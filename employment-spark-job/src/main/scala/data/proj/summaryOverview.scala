package data.proj

import java.sql.{Connection, Date, DriverManager, Statement}
import java.util.Properties

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DecimalType, FloatType, IntegerType}

object summaryOverview {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession（使用IP地址）
    val spark: SparkSession = SparkSession.builder()
      .appName("education_employment_summary")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
      .config("spark.hadoop.dfs.client.use.datanode.hostname", "false")
      .config("spark.hadoop.dfs.namenode.rpc-address.node1", "192.168.71.201:8020")
      .config("spark.hadoop.fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
      .enableHiveSupport()
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    import spark.implicits._

    println("========== 开始学历就业统计 ==========")

    try {
      // 1. 覆盖Hive元数据中的主机名
      spark.sql("SET hive.metastore.warehouse.dir=hdfs://192.168.71.201:8020/user/hive/warehouse")
      spark.sql("SET hive.metastore.uris=thrift://192.168.71.201:9083")

      // 2. 直接从HDFS读取数据
      val studentDF = spark.read
        .option("delimiter", "\t")
        .option("header", "false")
        .csv("hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/student_records")
        .selectExpr(
          "_c0 as student_id",
          "_c1 as name",
          "_c2 as name_chinese",
          "_c3 as age",
          "_c4 as major",
          "_c5 as college",
          "_c6 as education_level",
          "_c7 as hometown",
          "_c8 as gpa",
          "_c9 as internship_count",
          "_c10 as research_count",
          "_c11 as competition_count",
          "_c12 as scholarship_count",
          "_c13 as english_cert",
          "_c14 as computer_cert",
          "_c15 as employment_status",
          "_c16 as enrollment_year",
          "_c17 as graduation_year"
        )

      val jobRelationDF = spark.read
        .option("delimiter", "\t")
        .option("header", "false")
        .csv("hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/student_job_relation")
        .selectExpr(
          "_c0 as relation_id",
          "_c1 as student_id",
          "_c2 as position_id"
        )

      val jobPositionDF = spark.read
        .option("delimiter", "\t")
        .option("header", "false")
        .csv("hdfs://192.168.71.201:8020/user/hive/warehouse/aa.db/job_position")
        .selectExpr(
          "_c0 as job_id",
          "_c1 as position_name",
          "_c2 as college",
          "_c3 as major",
          "_c4 as education",
          "_c5 as location_province",
          "_c6 as location_urban",
          "_c7 as industry",
          "_c8 as company",
          "_c9 as salary_type",
          "_c10 as salary"
        )

      // 3. 计算每个学历层次的学生统计数据
      val educationStats = studentDF
        .groupBy("education_level")
        .agg(
          count("*").as("total_students"),
          sum(when($"employment_status" === "已就业", 1).otherwise(0)).as("employed_students")
        )

      // 4. 计算每个学历层次的就业率
      val withEmploymentRate = educationStats
        .withColumn("employment_rate",
          round($"employed_students" / $"total_students", 4))

      // 5. 计算每个学历层次的平均薪资
      val studentSalaryDF = studentDF
        .join(jobRelationDF, "student_id")
        .join(jobPositionDF, jobRelationDF("position_id") === jobPositionDF("job_id"))
        .select(
          studentDF("education_level"),
          jobPositionDF("salary")
        )

      // 使用cast确保数据类型兼容性
      val avgSalaryDF = studentSalaryDF
        .groupBy("education_level")
        .agg(round(avg($"salary".cast("double")), 2).as("avg_salary"))

      // 6. 合并所有统计数据 - 根据目标表结构调整数据类型
      val finalResult = withEmploymentRate
        .join(avgSalaryDF, Seq("education_level"), "left")
        .na.fill(0.0, Seq("avg_salary"))
        .withColumn("stat_date", lit(Date.valueOf("2024-09-01"))) // 使用真实的Date类型
        .select(
          trim($"education_level").as("education_level"), // 去除前后空格
          $"total_students".cast(IntegerType).as("total_students"),
          $"employed_students".cast(IntegerType).as("employed_students"),
          $"employment_rate".cast(FloatType).as("employment_rate"), // 使用FloatType
          $"avg_salary".cast(DecimalType(20, 0)).as("avg_salary"), // 使用DECIMAL(20,0)
          $"stat_date"
        )
        .orderBy("education_level")

      // 检查education_level的最大长度
      println("\n学历层次最大长度检查:")
      val maxLength = finalResult.select(max(length($"education_level"))).as[Int].first()
      println(s"最大长度: $maxLength")
      if (maxLength > 20) {
        println(s"警告: 最大长度 $maxLength 超过20，将截断数据")
      }

      println("\n学历就业统计数据:")
      finalResult.show(false)

      // 7. MySQL配置 - 根据目标表结构调整
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "summary_overview"

      val connectionProperties = new Properties()
      connectionProperties.put("user", mysqlUser)
      connectionProperties.put("password", mysqlPassword)
      connectionProperties.put("driver", "com.mysql.jdbc.Driver")

      println("\n写入MySQL数据库...")
      Class.forName("com.mysql.jdbc.Driver")

      var conn: Connection = null
      var stmt: Statement = null

      try {
        conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)
        stmt = conn.createStatement()

        // 创建表 - 完全匹配目标表结构
        stmt.executeUpdate(
          s"""
          CREATE TABLE IF NOT EXISTS $tableName (
            education_level VARCHAR(20) NOT NULL,
            total_students INT,
            employed_students INT,
            employment_rate FLOAT,
            avg_salary DECIMAL(20,0),
            stat_date DATE,
            PRIMARY KEY (education_level, stat_date)
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8
          """)

        // 禁用外键检查
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0")

        // 清空表数据
        stmt.execute(s"TRUNCATE TABLE $tableName")

        // 数据预处理：确保字符串长度不超过20
        val preparedDF = finalResult
          .withColumn("education_level",
            when(length($"education_level") > 20, substring($"education_level", 1, 20))
              .otherwise($"education_level")
          )
          .withColumn("education_level", trim($"education_level")) // 再次确保去除空格

        // 写入数据 - 使用更稳定的写入方式
        preparedDF.write
          .format("jdbc")
          .option("url", mysqlUrl)
          .option("dbtable", tableName)
          .option("user", mysqlUser)
          .option("password", mysqlPassword)
          .option("driver", "com.mysql.jdbc.Driver")
          .option("batchsize", 10000) // 增加批处理大小提高性能
          .mode("append")
          .save()

        // 启用外键检查
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1")

        println(s"数据成功写入MySQL表: $tableName")
      } catch {
        case e: Exception =>
          println(s"MySQL操作失败: ${e.getMessage}")
          e.printStackTrace()
      } finally {
        if (stmt != null) stmt.close()
        if (conn != null) conn.close()
      }

      println("\n验证MySQL数据:")
      val mysqlDF = spark.read
        .format("jdbc")
        .option("url", mysqlUrl)
        .option("dbtable", tableName)
        .option("user", mysqlUser)
        .option("password", mysqlPassword)
        .load()

      mysqlDF.show(false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 处理完成 ==========")
    }
  }
}