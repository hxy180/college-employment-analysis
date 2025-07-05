package data.proj

import java.sql.DriverManager
import java.util.Properties
import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object summaryMajor {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName("summary_major_analysis")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
      .config("spark.hadoop.dfs.client.use.datanode.hostname", "false")
      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    // 在Hadoop配置中直接设置主机名映射
    spark.sparkContext.hadoopConfiguration.set("dfs.client.use.datanode.hostname", "false")
    spark.sparkContext.hadoopConfiguration.set("dfs.namenode.rpc-address.ns1.nn1", "192.168.71.201:8020")

    try {
      // 0. 修改Hive表的存储位置为IP地址
      println("修改Hive表存储位置为IP地址...")
      spark.sql("USE aa")

      // 获取当前数据库的所有表
      val tables = spark.sql("SHOW TABLES").select("tableName").as[String].collect()

      // 更新每个表的存储位置
      tables.foreach { tableName =>
        try {
          // 获取表的原始存储位置
          val describeResult = spark.sql(s"DESCRIBE FORMATTED $tableName")
          val locationRow = describeResult.filter(col("col_name") === "Location").select("data_type").head()
          val originalLocation = locationRow.getString(0)

          // 替换主机名为IP地址
          val fixedLocation = originalLocation.replace("node1", "192.168.71.201")

          // 只有当位置包含node1时才更新
          if (fixedLocation != originalLocation) {
            println(s"更新表 $tableName 的存储位置:")
            println(s"原始位置: $originalLocation")
            println(s"更新位置: $fixedLocation")

            // 修改表存储位置
            spark.sql(s"ALTER TABLE $tableName SET LOCATION '$fixedLocation'")
          }
        } catch {
          case e: Exception =>
            println(s"无法更新表 $tableName 的存储位置: ${e.getMessage}")
        }
      }

      // 1. 从Hive读取原始数据
      println("从Hive读取数据...")
      val studentRecords = spark.sql("SELECT * FROM student_records")
      val jobPosition = spark.sql("SELECT * FROM job_position")
      val studentJobRelation = spark.sql("SELECT * FROM student_job_relation")

      println("学生记录表示例:")
      studentRecords.show(2, truncate = false)
      println("职位表示例:")
      jobPosition.show(2, truncate = false)
      println("关系表示例:")
      studentJobRelation.show(2, truncate = false)

      // 2. 计算专业维度指标
      println("开始计算专业维度指标...")

      // 2.1 专业总人数
      val totalStudents = studentRecords
        .groupBy("major")
        .agg(count("*").alias("total_students"))

      // 2.2 已就业学生数
      val employedStudents = studentRecords
        .filter(col("employment_status") === "已就业")
        .groupBy("major")
        .agg(count("*").alias("employed_students"))

      // 2.3 就业率计算
      val employmentRate = totalStudents.join(employedStudents, Seq("major"), "left")
        .withColumn("employment_rate",
          when(col("total_students") > 0, col("employed_students") / col("total_students"))
            .otherwise(0.0))
        .select("major", "total_students", "employed_students", "employment_rate")

      // 2.4 修正后的平均薪资计算（按教育水平分组计算）
      // 步骤：
      // a. 计算每个专业+教育水平的薪资总和
      // b. 计算每个专业+教育水平的人数
      // c. 按专业汇总：总薪资 / 总人数
      val salaryByEducation = studentJobRelation.alias("rel")
        .join(jobPosition.alias("job"), $"rel.position_id" === $"job.job_id")
        .join(studentRecords.alias("stu"), $"rel.student_id" === $"stu.student_id")
        .filter($"stu.employment_status" === "已就业")
        .groupBy($"stu.major", $"job.education")
        .agg(
          sum($"job.salary").alias("total_salary"),
          count($"stu.student_id").alias("student_count")
        )

      val avgSalary = salaryByEducation.groupBy("major")
        .agg(
          sum("total_salary").alias("total_salary"),
          sum("student_count").alias("total_students")
        )
        .withColumn("avg_salary",
          when($"total_students" > 0, $"total_salary" / $"total_students")
            .otherwise(0.0))
        .select("major", "avg_salary")

      // 2.5 修正后的主要就业行业（直接从job_position中获取）
      // 步骤：
      // a. 关联职位表和关系表
      // b. 按职位表中的专业(major)分组
      // c. 统计每个专业的行业分布
      // d. 取每个专业最常见的行业
      val industryData = studentJobRelation.alias("rel")
        .join(jobPosition.alias("job"), $"rel.position_id" === $"job.job_id")
        .select(
          $"job.major",  // 直接使用职位表中的专业
          $"job.industry"
        )

      val topIndustry = industryData.groupBy("major", "industry")
        .agg(count("*").alias("industry_count"))
        .withColumn("rn", row_number().over(
          Window.partitionBy("major").orderBy(col("industry_count").desc, col("industry"))
        ))
        .filter(col("rn") === 1)
        .select("major", "industry")
        .withColumnRenamed("industry", "top_industry")

      // 3. 整合所有指标
      val summary = employmentRate
        .join(avgSalary, Seq("major"), "left")
        .join(topIndustry, Seq("major"), "left")
        .withColumn("stat_date", to_date(lit("2024-09-01")))
        .select(
          col("major").alias("major_name"),
          col("total_students"),
          col("employed_students"),
          round(col("employment_rate") * 100, 2).alias("employment_rate"),
          coalesce(round(col("avg_salary"), 2), lit(0.0)).alias("avg_salary"),
          coalesce(col("top_industry"), lit("未知")).alias("top_industry"),
          col("stat_date")
        )
        .na.fill(0, Seq("employed_students", "employment_rate", "avg_salary"))
        .na.fill("未知", Seq("top_industry"))

      println("专业维度分析结果（前10条）:")
      summary.show(10, truncate = false)
      println(s"总共生成 ${summary.count()} 条专业分析记录")

      // 4. 写入MySQL数据库
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "summary_major"

      val connProperties = new Properties()
      connProperties.put("user", mysqlUser)
      connProperties.put("password", mysqlPassword)
      connProperties.put("driver", "com.mysql.jdbc.Driver")
      connProperties.put("characterEncoding", "UTF-8")
      connProperties.put("useUnicode", "true")

      println(s"写入MySQL数据库: $mysqlUrl/$tableName")

      // 禁用外键检查并清空表
      Class.forName("com.mysql.jdbc.Driver")
      val conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)
      try {
        conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0")
        conn.createStatement().execute(s"TRUNCATE TABLE $tableName")

        // 写入数据
        summary.write
          .mode("append")
          .jdbc(mysqlUrl, tableName, connProperties)

        conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1")
        println(s"数据成功写入MySQL表: $tableName")

        // 验证写入结果
        val mysqlDF = spark.read.jdbc(mysqlUrl, tableName, connProperties)
        println(s"MySQL表记录数: ${mysqlDF.count()}")
        mysqlDF.show(5, truncate = false)
      } finally {
        if (conn != null) conn.close()
      }

    } catch {
      case e: Exception =>
        println(s"处理失败: ${e.getMessage}")
        e.printStackTrace()
        sys.exit(1)
    } finally {
      spark.stop()
      println("作业执行完成")
    }
  }
}