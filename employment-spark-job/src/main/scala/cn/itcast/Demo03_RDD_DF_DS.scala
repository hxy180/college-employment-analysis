package cn.itcast

import cn.itcast.Demo02_RDD2DataFrame1.Person
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object Demo03_RDD_DF_DS {

  def main(args: Array[String]): Unit = {

    //1.准备spark环境
    val spark: SparkSession = SparkSession.builder().appName("sparksql")
      .master("local[*]").getOrCreate()

    val sc: SparkContext = spark.sparkContext


    //2.从RDD数据加载DataFrame
    val lines: RDD[String] = sc.textFile("hdfs://node1:8020/data/input/person.txt")

    //3.处理数据
    //3.处理数据
    val personRDD: RDD[Person] = lines.map(line => {
      val arr: Array[String] = line.split(" ")
      Person(arr(0).toInt, arr(1), arr(2).toInt)
    })

    import spark.implicits._ //引入spark的隐式转换类
    //转换 RDD --> DF
    val personDF: DataFrame = personRDD.toDF()
    // DF --> RDD
    val personRDD1: RDD[Row] = personDF.rdd

    // RDD --> DS
    val personDS: Dataset[Person] = personRDD.toDS()
    // DS --> RDD
    val personRDD2: RDD[Person] = personDS.rdd

    // DF --> DS
    val personDS2: Dataset[Person] = personDF.as[Person]

    // DS --> DF
    val personDF2 = personDS.toDF()

    //4.输出结果
    personDF.printSchema()
    personDF.show()

    personDS.printSchema()
    personDS.show()


    //5.关闭资源
    spark.stop()

  }

  //定义样例类
  case class Person(id:Int,name:String,age:Int)
}
