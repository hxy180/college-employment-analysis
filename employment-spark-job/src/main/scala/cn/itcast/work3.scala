package cn.itcast

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, regexp_replace, trim}

object work3 {
  def main(args: Array[String]): Unit = {
    // 0. 准备环境（启用Hive支持）
    val spark: SparkSession = SparkSession.builder()
      .appName("job_data_processing")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
      .config("spark.hadoop.dfs.client.use.datanode.hostname", "false")
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

      // 4. 筛选北京数据
      println("\n正在筛选北京数据...")
      val beijingDF = cleanedDF.filter(col("location") === "北京市")

      // 5. MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val dbName = "job_analysis"
      val tableName = "beijing_jobs"

      // 6. 创建数据库连接属性（添加字符编码设置）
      val connectionProperties = new java.util.Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")
      connectionProperties.put("characterEncoding", "UTF-8")
      connectionProperties.put("useUnicode", "true")

      // 7. 创建数据库并设置字符集（UTF-8）
      println("\n正在创建MySQL数据库并设置UTF-8编码...")
      val conn = java.sql.DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)
      val stmt = conn.createStatement()
      stmt.execute(s"CREATE DATABASE IF NOT EXISTS $dbName DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci")
      stmt.close()
      conn.close()

      // 8. 将数据写入MySQL（添加字符编码参数）
      println("\n正在将数据写入MySQL...")
      beijingDF.write
        .mode("overwrite")
        .option("createTableOptions", "ENGINE=InnoDB DEFAULT CHARSET=utf8")
        .jdbc(s"$mysqlUrl$dbName?useUnicode=true&characterEncoding=UTF-8",
          tableName,
          connectionProperties)

      // 9. 验证MySQL数据
      println("\n验证MySQL中的数据...")
      val mysqlDF = spark.read
        .jdbc(s"$mysqlUrl$dbName?useUnicode=true&characterEncoding=UTF-8",
          tableName,
          connectionProperties)

      println(s"MySQL表中的记录数: ${mysqlDF.count()}")
      mysqlDF.show(10, truncate = false)

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