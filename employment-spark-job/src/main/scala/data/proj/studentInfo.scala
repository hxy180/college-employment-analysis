package data.proj

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{lit, monotonically_increasing_id, row_number, when}

object studentInfo {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("student_info_processing")
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

    println("========== 开始处理学生信息 ==========")

    try {
      // 1. 从Hive表读取学生数据
      val hdfsPath = "hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/student_records"
      println(s"\n从Hive表读取学生数据: $hdfsPath")

      // 使用制表符分隔，禁用表头推断
      val rawDF = spark.read
        .option("delimiter", "\t")
        .option("header", "false")
        .csv(hdfsPath + "/*")

      // 调试：查看原始数据结构
      println("原始数据Schema:")
      rawDF.printSchema()
      println("原始数据示例（前5行）:")
      rawDF.show(5, truncate = false)

      // 2. 数据转换：选择所需字段并添加新字段
      val studentDF = rawDF
        .select(
          $"_c0".as("student_id_raw"), // 原始student_id
          $"_c1".as("name"),          // name字段
          $"_c3".as("age"),           // age字段
          $"_c6".as("education_level") // education_level字段
        )
        .withColumn("row_id", monotonically_increasing_id()) // 添加临时行ID

      // 3. 生成新student_id和gender字段
      val windowSpec = Window.orderBy("row_id")
      val processedDF = studentDF
        .withColumn("student_id", row_number().over(windowSpec))
        .withColumn("gender",
          when($"student_id" <= 4000, "男")
            .otherwise("女")
        )
        .withColumn("major_id", lit(1))          // 固定值1
        .withColumn("graduation_year", lit(2024))  // 固定值2024
        .withColumn("region_id", lit(1))           // 固定值1
        .select(
          $"student_id",
          $"name",
          $"gender",
          $"age",
          $"education_level",
          $"major_id",
          $"graduation_year",
          $"region_id"
        )

      println("\n处理后的学生信息（前10条）:")
      processedDF.show(10, truncate = false)
      println(s"共处理 ${processedDF.count()} 条学生记录")

      // 4. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "student_info"

      // 5. 数据库连接属性
      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")

      // 6. 安全写入MySQL（处理外键约束）
      println("\n安全写入MySQL（处理外键约束）...")

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
        truncateTable.execute(s"TRUNCATE TABLE $tableName")
        truncateTable.close()
        println(s"已清空表: $tableName")

        // 将数据写入MySQL
        println("正在将学生信息写入MySQL...")
        processedDF.write
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
      println("\n验证MySQL中的学生信息...")
      val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connectionProperties)
      println(s"学生信息表记录数: ${mysqlDF.count()}")
      mysqlDF.show(10, truncate = false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 学生信息处理完成 ==========")
    }
  }
}