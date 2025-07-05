package cn.itcast

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

object Demo01 {

  def main(args: Array[String]): Unit = {

    //1.准备spark环境
    val spark: SparkSession = SparkSession.builder().appName("sparksql")
      .master("local[*]").getOrCreate()

    val sc: SparkContext = spark.sparkContext


    //2.加载数据
    val df1: DataFrame = spark.read.text("hdfs://192.168.71.201:8020/data/input/text")
    val df2: DataFrame = spark.read.json("hdfs://192.168.71.201:8020/data/input/json")
    val df3: DataFrame = spark.read.csv("hdfs://192.168.71.201:8020/data/input/csv")

    //3.处理数据

    //4.输出结果
    df1.printSchema()
    df2.printSchema()
    df3.printSchema()

    df1.show()
    df2.show()
    df3.show()

    //5.关闭资源
    spark.stop()

  }
}
