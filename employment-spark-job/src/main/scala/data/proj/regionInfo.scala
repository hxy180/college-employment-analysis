package data.proj

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, row_number}

object regionInfo {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("region_info_processing")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
      .config("spark.hadoop.dfs.client.use.datanode.hostname", "false")
      .enableHiveSupport()
      .getOrCreate()

    val sc: SparkContext = spark.sparkContext
    sc.setLogLevel("WARN")
    import spark.implicits._

    println("========== 开始地区数据处理 ==========")

    try {
      // 1. 从Hive表aa.job_position读取数据
      println("\n从Hive表aa.job_position读取数据...")
      val jobPositionDF = spark.sql("SELECT * FROM aa.job_position")

      // 显示表结构和示例数据
      println("职位数据Schema:")
      jobPositionDF.printSchema()
      println("职位数据示例（前5行）:")
      jobPositionDF.show(5, truncate = false)

      // 2. 提取省份和城市信息
      println("\n提取省份和城市信息...")
      val regionDF = jobPositionDF
        .select(
          col("location_province").as("province"),
          col("location_urban").as("city")
        )
        .distinct()
        .filter($"province".isNotNull && $"city".isNotNull) // 过滤空值

      println("去重后的地区数据（前10条）:")
      regionDF.show(10, truncate = false)
      println(s"提取到 ${regionDF.count()} 条地区记录")

      // 3. 生成region_id
      println("\n正在生成地区ID...")
      val windowSpec = Window.orderBy("province", "city")
      val regionWithID = regionDF
        .withColumn("region_id", row_number().over(windowSpec))
        .select("region_id", "province", "city")
        .orderBy("region_id")

      println("带ID的地区数据（前10条）:")
      regionWithID.show(10, truncate = false)
      println(s"生成 ${regionWithID.count()} 个地区ID")

      // 4. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "region_info"

      // 5. 创建数据库连接属性
      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")

      // 6. 安全写入MySQL（处理外键约束）
      println("\n安全写入MySQL（处理外键约束）...")

      // 获取MySQL连接
      Class.forName("com.mysql.jdbc.Driver")
      val conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)

      try {
        // 禁用外键检查
        val disableFK = conn.createStatement()
        disableFK.execute("SET FOREIGN_KEY_CHECKS = 0")
        disableFK.close()
        println("已禁用外键检查")

        // 清空表数据
        val truncateTable = conn.createStatement()
        truncateTable.execute(s"DELETE FROM $tableName")
        truncateTable.close()
        println(s"已清空表: $tableName")

        // 将数据写入MySQL（追加模式）
        println("正在将地区数据写入MySQL...")
        regionWithID.write
          .mode("append")
          .jdbc(mysqlUrl, tableName, connectionProperties)

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

      // 7. 验证MySQL数据
      println("\n验证MySQL中的地区数据...")
      val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connectionProperties)
      println(s"地区信息表记录数: ${mysqlDF.count()}")
      mysqlDF.show(10, truncate = false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 地区数据处理完成 ==========")
    }
  }
}