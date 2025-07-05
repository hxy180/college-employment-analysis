package data.proj

import java.sql.DriverManager
import java.util.Properties
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object employmentInfo {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("employment_info_processing")
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

    println("========== 开始就业信息处理 ==========")

    try {
      // 1. 从Hive表读取职位数据
      println("\n从Hive表aa.job_position读取数据...")
      val jobDF = spark.sql("SELECT position_name FROM aa.job_position")

      // 调试：查看数据结构
      println("职位数据Schema:")
      jobDF.printSchema()
      println("职位数据示例（前5行）:")
      jobDF.show(5, truncate = false)

      // 2. 数据清洗：处理空值
      val cleanedDF = jobDF
        .filter($"position_name".isNotNull)
        .filter(length(trim($"position_name")) > 0)

      println(s"\n清洗后职位记录数: ${cleanedDF.count()}")
      cleanedDF.show(5, truncate = false)

      // 3. 数据转换：生成就业信息
      println("\n正在转换就业信息数据...")
      val employmentDF = transformEmploymentData(cleanedDF)

      println("转换后的就业信息示例:")
      employmentDF.show(10, truncate = false)
      println(s"共生成 ${employmentDF.count()} 条就业记录")

      // 4. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "employment_info"

      // 5. 创建数据库连接属性
      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")
      connectionProperties.put("rewriteBatchedStatements", "true") // 批量写入优化

      // 6. 安全写入MySQL
      writeToMySQL(employmentDF, mysqlUrl, tableName, connectionProperties)

      // 7. 验证MySQL数据
      println("\n验证MySQL中的就业信息...")
      val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connectionProperties)
      println(s"就业信息表记录数: ${mysqlDF.count()}")
      mysqlDF.show(10, truncate = false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 就业信息处理完成 ==========")
    }
  }

  /** 就业数据转换逻辑 */
  def transformEmploymentData(df: DataFrame): DataFrame = {
    // 生成连续递增的employment_id
    val idWindow = Window.orderBy("position_name")

    // 注册UDF用于生成随机日期
    val randomDateUDF = udf(() => {
      val startDate = LocalDate.of(2022, 1, 1)
      val endDate = LocalDate.of(2024, 12, 31)
      val randomDays = scala.util.Random.nextInt(endDate.getDayOfYear - startDate.getDayOfYear + 365*2)
      val randomDate = startDate.plusDays(randomDays)
      randomDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    })

    df
      .withColumn("employment_id", row_number().over(idWindow))
      .withColumn("student_id", (rand() * 100000 + 1).cast("int")) // 1-100000随机学生ID
      .withColumn("is_employed", (rand() > 0.5).cast("int")) // 50%就业概率
      .withColumn("company_id", (rand() * 100 + 1).cast("int")) // 1-100随机公司ID
      .withColumn("job_title", $"position_name")
      .withColumn("employment_date", randomDateUDF())
      .select(
        $"employment_id",
        $"student_id",
        $"is_employed",
        $"company_id",
        $"job_title",
        $"employment_date"
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