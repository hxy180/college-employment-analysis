package cn.itcast

import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

object Demo05_WordCount {

  def main(args: Array[String]): Unit = {
    //1.准备spark环境
    val spark: SparkSession = SparkSession.builder().appName("sparksql")
      .master("local[*]").getOrCreate()

    val sc: SparkContext = spark.sparkContext

    import spark.implicits._ //引入spark的隐式转换类


    //加载数据
    val df: DataFrame = spark.read.text("hdfs://node1:8020/data/input/words.txt")
    val ds: Dataset[String] = spark.read.textFile("hdfs://node1:8020/data/input/words.txt")

    df.printSchema()
    df.show()

    ds.printSchema()
    ds.show()


    //df.flatMap(_.split(" ")) //不支持泛型操作
    val words: Dataset[String] = ds.flatMap(_.split(" "))
    words.printSchema()
    words.show()


    //***********************SQL编程方式**************************/
//    words.createOrReplaceTempView("t_words")
////    val sql = "select value,count(value) as count_value " +
////      "from t_words group by value " +
////      "order by count_value desc"
//
//
//    val sql =
//      """
//        |select value,count(value) as count_value from t_words
//        |group by value
//        |order by count_value desc
//      """.stripMargin
//
//    spark.sql(sql).show()

    //***********************DSL编程方式**************************/
    words.groupBy('value).count()
      .orderBy('count.desc)
      .show()




    spark.stop()
  }
}
