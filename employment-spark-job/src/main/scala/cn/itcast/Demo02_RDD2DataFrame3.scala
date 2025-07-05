package cn.itcast

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Demo02_RDD2DataFrame3 {

  def main(args: Array[String]): Unit = {

    //1.准备spark环境
    val spark: SparkSession = SparkSession.builder().appName("sparksql")
      .master("local[*]").getOrCreate()

    val sc: SparkContext = spark.sparkContext


    //2.从RDD数据加载DataFrame
    val lines: RDD[String] = sc.textFile("hdfs://node1:8020/data/input/person.txt")

    //3.处理数据
    val rowRDD: RDD[Row] = lines.map(line => {
      val arr: Array[String] = line.split(" ")
      Row(arr(0).toInt, arr(1), arr(2).toInt)
    })




    //RDD 转成 DataFrame
    import spark.implicits._  //引入spark的隐式转换类

    //自定义Schema
    val schema: StructType = StructType(List(
      StructField("id", IntegerType, false),
      StructField("name", StringType, false),
      StructField("age", IntegerType, false)
    ))


    val personDF: DataFrame = spark.createDataFrame(rowRDD, schema)



    //4.输出结果
    personDF.printSchema()
    personDF.show()


    //5.关闭资源
    spark.stop()

  }
}
