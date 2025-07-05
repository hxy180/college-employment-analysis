package data.proj

import java.sql.DriverManager
import java.util.Properties

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{regexp_replace, row_number}

object majorInfo {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("student_major_processing")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
      // 关键配置：强制使用IP而非主机名
      .config("spark.hadoop.dfs.client.use.datanode.hostname", "false")
      .enableHiveSupport()
      .getOrCreate()

    val sc: SparkContext = spark.sparkContext
    sc.setLogLevel("WARN")
    import spark.implicits._

    println("========== 开始专业数据处理 ==========")

    try {
      // 1. 直接从HDFS路径读取学生数据（使用文本格式）
      val hdfsPath = "hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/student_records"
      println(s"\n从HDFS路径读取学生数据: $hdfsPath")

      // 修复1：使用制表符分隔，禁用表头推断
      val rawDF = spark.read
        .option("delimiter", "\t")    // 制表符分隔
        .option("header", "false")    // 无表头
        .csv(hdfsPath + "/*")

      // 调试：查看原始数据结构
      println("原始数据Schema:")
      rawDF.printSchema()
      println("原始数据示例（前5行）:")
      rawDF.show(5, truncate = false)

      // 修复2：通过列索引选择数据 + 清洗单引号
      val studentDF = rawDF
        .selectExpr(
          "regexp_replace(_c4, \"'\", '') as major",   // 第5列是专业（索引4）
          "regexp_replace(_c5, \"'\", '') as college"  // 第6列是学院（索引5）
        )
        .distinct()

      println("\n清洗后的专业数据（前5条）:")
      studentDF.show(5, truncate = false)
      println(s"读取到 ${studentDF.count()} 条专业记录")

      // 2. 数据转换：生成专业ID
      println("\n正在生成专业ID并转换字段...")
      val windowSpec = Window.orderBy("college", "major")
      val majorDF = studentDF
        .withColumn("major_id", row_number().over(windowSpec))
        .select(
          $"major_id",
          $"major".as("major_name"),
          $"college".as("department")
        )
        .orderBy("major_id")

      majorDF.show(10, truncate = false)
      println(s"生成 ${majorDF.count()} 个专业ID")

      // 3. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "major_info"

      // 4. 创建数据库连接属性
      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")

      // 5. 安全写入MySQL（处理外键约束）
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
        println("正在将专业数据写入MySQL...")
        majorDF.write
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

      // 6. 验证MySQL数据
      println("\n验证MySQL中的专业数据...")
      val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connectionProperties)
      println(s"专业信息表记录数: ${mysqlDF.count()}")
      mysqlDF.show(10, truncate = false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 专业数据处理完成 ==========")
    }
  }
}