package cn.itcast

import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, regexp_replace, trim}
import java.util.Properties

object JobDataProcessing {
  def main(args: Array[String]): Unit = {
    // 0. 准备环境（启用Hive支持）
    val spark: SparkSession = try {
      SparkSession.builder()
        .appName("job_data_processing")
        .master("local[*]")
        .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
        .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
        // 强制覆盖所有HDFS地址为IP
        .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
        .config("spark.hadoop.dfs.client.use.datanode.hostname", "false")
        // 解决HDFS权限问题
        .config("spark.hadoop.hadoop.security.authentication", "simple")
        .enableHiveSupport()
        .getOrCreate()
    } catch {
      case e: Exception =>
        println(s"SparkSession创建失败: ${e.getMessage}")
        System.exit(1)
        null
    }

    val sc: SparkContext = spark.sparkContext
    sc.setLogLevel("WARN")
    import spark.implicits._

    println("========== 开始数据处理 ==========")

    try {
      // 1. 检查并修改表位置（如果使用Hive表）
      try {
        spark.sql("USE aa")
        println("当前表位置:")
        spark.sql("DESCRIBE FORMATTED job_position").filter($"col_name" === "Location").show(truncate = false)

        // 临时修改表位置为IP地址（生产环境建议在Hive中永久修改）
        spark.sql("ALTER TABLE job_position SET LOCATION 'hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/job_position'")
      } catch {
        case e: Exception => println(s"表位置修改警告: ${e.getMessage}")
      }

      // 2. 从Hive表读取数据
      println("\n从Hive表读取数据...")
      val rawDF = try {
        spark.sql("SELECT * FROM aa.job_position")
      } catch {
        case e: Exception =>
          println(s"从Hive读取失败，尝试从HDFS直接读取: ${e.getMessage}")
          // 备用方案：直接从HDFS读取文本文件
          spark.read
            .option("sep", "\t")
            .option("inferSchema", "true")
            .csv("hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/job_position")
            .toDF("job_id", "position_name", "college", "major", "education",
              "location", "industry", "company", "salary", "dummy")
      }

      // 3. 展示原始数据
      println("\n原始数据结构：")
      rawDF.printSchema()
      println("\n原始数据前20条记录：")
      rawDF.show(20, truncate = false)
      println(s"总记录数: ${rawDF.count()}")

      // 4. 数据清洗转换
      println("\n正在进行数据清洗转换...")
      val cleanedDF = rawDF
        .drop("dummy")  // 删除多余列（如果有）
        .select(rawDF.columns.filter(_ != "dummy").map { colName =>
          if (colName == "salary") col(colName)  // 薪资列保持原样
          else trim(regexp_replace(col(colName), "'", "")).as(colName)  // 去除单引号和首尾空格
        }: _*)
        .withColumn("salary", col("salary").cast("int"))  // 转换薪资为整数

      println("\n清洗后数据结构：")
      cleanedDF.printSchema()
      println("\n清洗后数据前20条记录：")
      cleanedDF.show(20, truncate = false)
      println(s"清洗后记录数: ${cleanedDF.count()}")

      // 5. 配置MySQL连接
      val url = "jdbc:mysql://192.168.71.201:3306/bigdata?characterEncoding=UTF-8"
      val properties = new Properties()
      properties.setProperty("user", "root")
      properties.setProperty("password", "030106")

      // 6. 写入MySQL（覆盖模式）
      println("\n正在写入MySQL数据库...")
      cleanedDF.write.mode(SaveMode.Overwrite)
        .option("createTableColumnTypes",
          "job_id VARCHAR(10), position_name VARCHAR(50), college VARCHAR(20), " +
            "major VARCHAR(30), education VARCHAR(10), location VARCHAR(20), " +
            "industry VARCHAR(20), company VARCHAR(30), salary INT")
        .jdbc(url, "job", properties)
      println("数据成功写入MySQL job表")

      // 7. 从MySQL读取验证
      println("\n从MySQL读取数据进行验证...")
      val jobDF = spark.read.jdbc(url, "job", properties)
      println("\nMySQL中的数据结构：")
      jobDF.printSchema()
      println("\nMySQL中的数据前20条记录：")
      jobDF.show(20, truncate = false)
      println(s"MySQL中的记录数: ${jobDF.count()}")

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 数据处理完成 ==========")
    }
  }
}