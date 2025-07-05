package cn.itcast

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object Demo04_Query {


  def main(args: Array[String]): Unit = {

    //1.准备spark环境
    val spark: SparkSession = SparkSession.builder().appName("sparksql")
      .master("local[*]").getOrCreate()

    val sc: SparkContext = spark.sparkContext


    //2.从RDD数据加载DataFrame
    val lines: RDD[String] = sc.textFile("hdfs://node1:8020/data/input/person.txt")

    //3.处理数据
    val personRDD: RDD[Person] = lines.map(line => {
      val arr: Array[String] = line.split(" ")
      Person(arr(0).toInt, arr(1), arr(2).toInt)
    })

    import spark.implicits._ //引入spark的隐式转换类
    //转换 RDD --> DF
    val personDF: DataFrame = personRDD.toDF()

//    personDF.printSchema()
//    personDF.show()

    /**
      * root
      * |-- id: integer (nullable = false)
      * |-- name: string (nullable = true)
      * |-- age: integer (nullable = false)
      *
      * +---+--------+---+
      * | id|    name|age|
      * +---+--------+---+
      * |  1|zhangsan| 30|
      * |  2|    lisi| 50|
      * |  3|  wangwu| 69|
      * +---+--------+---+
      */

    //*****************************SQL编程***********************************/

    // 1.注册表名
    //personDF.registerTempTable()//过期了
    //personDF.createGlobalTempView() //创建全局视图,SparkSession可以扩线程,生命周期和程序进程一起
    personDF.createOrReplaceTempView("t_person") //创建或复盖临时视图,SparkSession当前线程可以调用,生命周期和程序进程一起

    // 2.编写sql查询
      //=1.查看name字段的数据
//    spark.sql("select name from t_person").show()
//      //=2.查看 name 和age字段数据
//    spark.sql("select name,age from t_person").show()
//      //=3.查询所有的name和age，并将age+1
//    spark.sql("select name,age+1 from t_person").show()
//      //=4.过滤age大于等于50的
//    spark.sql("select id,name,age from t_person where age >=50").show()
//      //=5.统计年龄大于50的人数
//    spark.sql("select count(*) from t_person where age >=50").show()
//      //=6.按年龄进行分组并统计相同年龄的人数
//    spark.sql("select age,count(age) from t_person group by age").show()
//      //=7.查询姓名=张三的
//    spark.sql("select id,name,age from t_person where name='zhangsan'").show()
//
//
//    //*****************************DSL编程***********************************/
//
//    //=1.查看name字段的数据
//    personDF.select("name").show()
//    //=2.查看 name 和age字段数据
//    personDF.select("name","age").show()
//    //=3.查询所有的name和age，并将age+1
//    //personDF.select("name","age+1").show()// cannot resolve '`age+1`' given input columns: [age, id, name];
//    //'Project [name#5, 'age+1]
//    personDF.select($"name",$"age",$"age"+1).show()
//    //=4.过滤age大于等于50的
//    personDF.filter("age >=50").show()
//
//    //=6.按年龄进行分组并统计相同年龄的人数
//    personDF.groupBy("age").count().show()

    //=7.查询姓名=张三的
    personDF.filter("name='zhangsan'").show()
    personDF.filter($"name"==="zhangsan").show()
    personDF.filter('name==="zhangsan").show()

    //5.关闭资源
    spark.stop()

  }

  //定义样例类
  case class Person(id:Int,name:String,age:Int)
}
