package data.proj

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object companyInfo {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("company_info_processing")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
      .config("spark.hadoop.dfs.client.use.datanode.hostname", "false")
      .enableHiveSupport()
      .getOrCreate()

    val sc = spark.sparkContext
    sc.setLogLevel("WARN")
    import spark.implicits._

    println("========== 开始公司信息处理 ==========")

    try {
      // 1. 从Hive表读取职位数据
      println("\n从Hive表aa.job_position读取数据...")
      val jobDF = spark.sql("SELECT DISTINCT company, industry FROM aa.job_position")

      // 调试：查看数据结构
      println("原始公司数据Schema:")
      jobDF.printSchema()
      println("原始公司数据示例（前5行）:")
      jobDF.show(5, truncate = false)

      // 2. 数据清洗：处理空值和重复项
      val cleanedDF = jobDF
        .filter($"company".isNotNull && $"industry".isNotNull)
        .filter(length(trim($"company")) > 0)
        .distinct()

      println(s"\n清洗后公司记录数: ${cleanedDF.count()}")
      cleanedDF.show(5, truncate = false)

      // 3. 数据转换：生成公司信息
      println("\n正在转换公司数据...")
      val companyDF = transformCompanyData(cleanedDF)

      println("转换后的公司信息示例:")
      companyDF.show(10, truncate = false)
      println(s"共生成 ${companyDF.count()} 条公司记录")

      // 4. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "company_info"

      // 5. 创建数据库连接属性
      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")
      connectionProperties.put("rewriteBatchedStatements", "true") // 批量写入优化

      // 6. 安全写入MySQL
      writeToMySQL(companyDF, mysqlUrl, tableName, connectionProperties)

      // 7. 验证MySQL数据
      println("\n验证MySQL中的公司信息...")
      val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connectionProperties)
      println(s"公司信息表记录数: ${mysqlDF.count()}")
      mysqlDF.show(10, truncate = false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 公司信息处理完成 ==========")
    }
  }

  /** 公司数据转换逻辑 */
  def transformCompanyData(df: DataFrame): DataFrame = {
    // 生成连续递增的company_id
    val windowSpec = Window.orderBy("company", "industry")

    df
      .withColumn("company_id", row_number().over(windowSpec))
      .withColumn("region_id", (rand() * 100 + 1).cast("int")) // 生成1-100的随机region_id
      .select(
        $"company_id",
        $"company".as("company_name"),
        $"industry",
        $"region_id"
      )
  }

  /** 安全写入MySQL（处理外键约束） */
  def writeToMySQL(df: DataFrame, url: String, table: String, props: Properties): Unit = {
    println(s"\n安全写入MySQL表: $table")

    // 获取MySQL连接
    Class.forName("com.mysql.jdbc.Driver")
    val conn = DriverManager.getConnection(url, props.getProperty("user"), props.getProperty("password"))

    try {
      // 禁用外键检查
      val disableFK = conn.createStatement()
      disableFK.execute("SET FOREIGN_KEY_CHECKS = 0")
      disableFK.close()
      println("已禁用外键检查")

      // 清空表数据
      val truncateTable = conn.createStatement()
      truncateTable.execute(s"TRUNCATE TABLE $table")
      truncateTable.close()
      println(s"已清空表: $table")

      // 将数据写入MySQL（追加模式）
      println(s"正在写入 ${df.count()} 条记录...")
      df.write
        .mode("append")
        .jdbc(url, table, props)

      // 启用外键检查
      val enableFK = conn.createStatement()
      enableFK.execute("SET FOREIGN_KEY_CHECKS = 1")
      enableFK.close()
      println("已启用外键检查")

    } catch {
      case e: Exception =>
        println(s"MySQL操作失败: ${e.getMessage}")
        throw e
    } finally {
      if (conn != null) conn.close()
    }
  }
}