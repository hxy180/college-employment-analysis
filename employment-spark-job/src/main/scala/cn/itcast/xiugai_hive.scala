package cn.itcast

import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, regexp_replace, trim}
import java.util.Properties

object xiugai_hive {
  def main(args: Array[String]): Unit = {
    // 0. 准备环境（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
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

        // 临时修改表位置为IP地址
        spark.sql("ALTER TABLE job_position SET LOCATION 'hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/job_position'")
      } catch {
        case e: Exception => println(s"表位置修改警告: ${e.getMessage}")
      }

      // 2. 从Hive表读取数据
      println("\n从Hive表读取数据...")
      val rawDF = spark.sql("SELECT * FROM aa.job_position")

      // 3. 数据清洗转换
      println("\n正在进行数据清洗转换...")
      val cleanedDF = rawDF
        .select(rawDF.columns.map { colName =>
          if (colName == "salary") col(colName).cast("int")
          else trim(regexp_replace(col(colName), "'", "")).as(colName)
        }: _*)

      // 4. 创建北京数据表（使用明确指定IP的路径）
      println("\n正在创建北京数据表...")
      val beijingDF = cleanedDF.filter(col("location") === "北京市")

      // 方法1：使用SQL方式创建（推荐）
      beijingDF.createOrReplaceTempView("beijing_temp")
      spark.sql("""
        CREATE TABLE IF NOT EXISTS aa.beijin
        USING hive
        OPTIONS (
          path 'hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/beijin'
        )
        AS SELECT * FROM beijing_temp
      """)

      // 验证新表
      println("\n北京数据表内容:")
      spark.sql("SELECT * FROM aa.beijin").show(20, truncate = false)
      println(s"北京数据记录数: ${spark.sql("SELECT COUNT(*) FROM aa.beijin").collect()(0)(0)}")

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