package cn.itcast

import java.util.Properties

import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

//JDBC数据源
object Demo06_DataSource {

  def main(args: Array[String]): Unit = {
    //TODO 0.准备环境
    val spark: SparkSession = SparkSession.builder().appName("sparksql").master("local[*]").getOrCreate()
    val sc: SparkContext = spark.sparkContext
    sc.setLogLevel("WARN")
    import spark.implicits._

//    //TODO 1.加载数据
//    val df: DataFrame = spark.read.json("hdfs://node1:8020/data/input/json")//底层format("json").load(paths : _*)
//    //val df: DataFrame = spark.read.csv("hdfs://node1:8020/data/input/csv")//底层format("csv").load(paths : _*)
//    df.printSchema()
//    df.show()
//
//    //df保存到mysql表中
    val url = "jdbc:mysql://node1:3306/bigdata?characterEncoding=UTF-8"
    val properties = new Properties()
    properties.setProperty("user","root")
    properties.setProperty("password","123456")
//
//
//    df.coalesce(1).write.mode(SaveMode.Overwrite).jdbc(url,"person",properties)//表不存在会自动创建

    //从mysql表中读取内容
    val personDF: DataFrame = spark.read.jdbc(url,"person",properties)

    personDF.show()

    spark.stop()

  }
}
