package cn.itcast

import cn.itcast.Demo02_RDD2DataFrame1.Person
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object Demo02_RDD2DataFrame2 {

  def main(args: Array[String]): Unit = {

    //1.准备spark环境
    val spark: SparkSession = SparkSession.builder().appName("sparksql")
      .master("local[*]").getOrCreate()

    val sc: SparkContext = spark.sparkContext


    //2.从RDD数据加载DataFrame
    val lines: RDD[String] = sc.textFile("hdfs://node1:8020/data/input/person.txt")

    //3.处理数据
    val tupleRDD: RDD[(Int, String, Int)] = lines.map(line => {
      val arr: Array[String] = line.split(" ")
      (arr(0).toInt, arr(1), arr(2).toInt)
    })


    //RDD 转成 DataFrame
    import spark.implicits._  //引入spark的隐式转换类

    val personDF: DataFrame = tupleRDD.toDF("id","name","age")



    //4.输出结果
    personDF.printSchema()
    personDF.show()


    //5.关闭资源
    spark.stop()

  }

}
