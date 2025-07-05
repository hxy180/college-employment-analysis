package data.proj

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object salaryInfo {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("job_salary_processing")
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

    println("========== 开始薪资数据处理 ==========")

    try {
      // 1. 从Hive表读取职位数据
      println("\n从Hive表aa.job_position读取数据...")
      val jobDF = spark.sql("SELECT * FROM aa.job_position")

      // 调试：查看数据结构
      println("职位数据Schema:")
      jobDF.printSchema()
      println("职位数据示例（前5行）:")
      jobDF.show(5, truncate = false)

      // 2. 数据转换：生成薪资记录
      println("\n正在转换薪资数据...")
      val salaryDF = transformSalaryData(jobDF)

      println("转换后的薪资数据示例:")
      salaryDF.show(10, truncate = false)
      println(s"共生成 ${salaryDF.count()} 条薪资记录")

      // 3. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "salary_info"

      // 4. 创建数据库连接属性
      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")

      // 5. 安全写入MySQL
      writeToMySQL(salaryDF, mysqlUrl, tableName, connectionProperties)

      // 6. 验证MySQL数据
      println("\n验证MySQL中的薪资数据...")
      val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connectionProperties)
      println(s"薪资信息表记录数: ${mysqlDF.count()}")
      mysqlDF.show(10, truncate = false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 薪资数据处理完成 ==========")
    }
  }

  /** 薪资数据转换逻辑 */
  def transformSalaryData(jobDF: DataFrame): DataFrame = {
    // 生成连续递增的salary_id
    val idWindow = Window.orderBy("job_id")

    jobDF
      .select(
        // 生成从1开始递增的salary_id
        row_number().over(idWindow).as("salary_id"),
        // 生成随机的employment_id (1-1000000范围)
        (rand() * 1000000 + 1).cast("int").as("employment_id"),
        // 使用原始薪资值
        $"salary".as("salary_amount"),
        // 保留原始薪资类型
        $"salary_type"
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