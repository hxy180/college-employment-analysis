package cn.itcast

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object Demo09_Hive {

  def main(args: Array[String]): Unit = {

    //准备环境
    val spark: SparkSession = SparkSession.builder().appName("sparksqlAndHive").master("local[*]")
      //开启支持hive
      .config("spark.sql.shuffle.partitions", "4")//本次测试时将分区数设置小一点,实际开发中可以根据集群规模调整大小,默认200
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")//指定Hive数据库在HDFS上的位置
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .enableHiveSupport()//开启对hive语法的支持
      .getOrCreate()

    val sc: SparkContext = spark.sparkContext

    //引入spark框架的隐式转换类形
    import spark.implicits._


    spark.sql("show databases").show()

    spark.sql("show tables").show()

    spark.sql("create table if not exists person(id int,name string,age int) row format delimited fields terminated by ' '").show(false)

    spark.sql("load data inpath 'hdfs://192.168.71.201:8020/data/input/person.txt' into table person").show(false)

    spark.sql("select * from person").show()


    //释放资源
    spark.stop()

  }
}
