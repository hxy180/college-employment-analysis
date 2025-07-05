package cn.itcast

// 必须添加的Spark SQL导入
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._  // 导入所有内置函数（split/col/avg等）

object Movie {
  def main(args: Array[String]): Unit = {
    // 1. 创建SparkSession（修正了getOrCreate的调用方式）
    val spark = SparkSession.builder()
      .appName("MovieDataAnalysis")
      .master("local[*]")
      .config("spark.sql.shuffle.partitions", "4")
      .getOrCreate()  // 注意这里需要加括号

    // 设置日志级别为WARN
    spark.sparkContext.setLogLevel("WARN")

    try {
      // 2. 加载HDFS数据（路径需确保存在）
      val rawData = spark.read.text("hdfs://192.168.71.201:8020/data/input/u.data")

      // 3. 数据转换（使用tab分割）
      val movieDF = rawData
        .withColumn("split_col", split(col("value"), "\t"))  // 分割列
        .select(
          col("split_col").getItem(0).cast("int").alias("user_id"),
          col("split_col").getItem(1).cast("int").alias("movie_id"),
          col("split_col").getItem(2).cast("int").alias("score"),
          col("split_col").getItem(3).cast("long").alias("timestamp")
        )
        .drop("value", "split_col")  // 删除原始列

      // 打印数据结构
      println("=== 数据结构 ===")
      movieDF.printSchema()
      println("=== 数据样例（前6行）===")
      movieDF.show(6)

      // 4. 创建临时视图（用于SQL查询）
      movieDF.createOrReplaceTempView("movie_ratings")

      // 5. SQL方式分析（格式化后的SQL更易读）
      println("\n=== SQL方式：电影平均分Top10（评分次数>200） ===")
      spark.sql("""
        SELECT
          movie_id,
          ROUND(AVG(score), 2) AS avg_score,
          COUNT(*) AS rating_count
        FROM movie_ratings
        GROUP BY movie_id
        HAVING rating_count > 200
        ORDER BY avg_score DESC
        LIMIT 10
      """).show()

      // 6. DSL方式分析（链式调用）
      println("\n=== DSL方式：电影平均分Top10（评分次数>200） ===")
      movieDF.groupBy("movie_id")
        .agg(
          round(avg("score"), 2).alias("avg_score"),
          count("movie_id").alias("rating_count")
        )
        .filter(col("rating_count") > 200)  // 等价于 .gt(200)
        .orderBy(desc("avg_score"))         // 降序排列
        .limit(10)
        .show()

    } finally {
      // 7. 关闭SparkSession（确保资源释放）
      spark.stop()
    }
  }
}