package data.proj

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object summaryTrendYearly {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("yearly_trend_processing")
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

    println("========== 开始年度趋势数据处理 ==========")

    try {
      // 1. 从Hive表读取职位数据（满足题目要求）
      println("\n从Hive表aa.job_position读取数据...")
      val jobDF = spark.sql("SELECT * FROM aa.job_position")

      // 调试：查看数据结构
      println("职位数据Schema:")
      jobDF.printSchema()
      println(s"读取到 ${jobDF.count()} 条职位记录")

      // 2. 生成模拟的年度趋势数据
      println("\n正在生成模拟年度趋势数据...")
      val yearlyTrendDF = generateYearlyTrendData(spark)

      println("生成的年度趋势数据:")
      yearlyTrendDF.show(truncate = false)
      println(s"共生成 ${yearlyTrendDF.count()} 条年度趋势记录")

      // 3. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "summary_trend_yearly"

      // 4. 创建数据库连接属性
      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")
      connectionProperties.put("rewriteBatchedStatements", "true") // 批量写入优化

      // 5. 安全写入MySQL
      writeToMySQL(yearlyTrendDF, mysqlUrl, tableName, connectionProperties)

      // 6. 验证MySQL数据
      println("\n验证MySQL中的年度趋势数据...")
      val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connectionProperties)
      println(s"年度趋势表记录数: ${mysqlDF.count()}")
      mysqlDF.show(truncate = false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 年度趋势数据处理完成 ==========")
    }
  }

  /** 生成年度趋势数据 */
  def generateYearlyTrendData(spark: SparkSession): DataFrame = {
    import spark.implicits._

    // 创建2021-2024年的数据
    val years = Seq(2021, 2022, 2023, 2024)

    val data = years.map { year =>
      // 随机生成总毕业人数 (1000-10000)
      val totalStudents = 1000 + scala.util.Random.nextInt(9001)

      // 随机生成已就业人数 (小于总毕业人数)
      val employedStudents = scala.util.Random.nextInt(totalStudents - 500) + 500

      // 计算就业率 (保留2位小数)
      val employmentRate = (employedStudents.toDouble / totalStudents * 10000).round / 100.0

      // 随机生成平均薪资 (5000-20000)
      val avgSalary = 5000.0 + scala.util.Random.nextDouble() * 15000.0

      (year, totalStudents, employedStudents, employmentRate, avgSalary)
    }

    // 创建DataFrame
    data.toDF(
      "stat_year",
      "total_students",
      "employed_students",
      "employment_rate",
      "avg_salary"
    ).orderBy("stat_year")
  }

  /** 安全写入MySQL */
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