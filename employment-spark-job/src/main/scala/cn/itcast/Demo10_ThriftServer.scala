package cn.itcast

import java.sql.{Connection, DriverManager}


object Demo10_ThriftServer {

  def main(args: Array[String]): Unit = {
    //加载hive ThriftServer 驱动
    Class.forName("org.apache.hive.jdbc.HiveDriver")

    //获取连接
    val conn: Connection = DriverManager.getConnection(
      "jdbc:hive2://node1:10000/default",
      "root",
      "030106"
    )


    //查询
    val sql= "select id,name,age from person"

    //执行SQL
    val ps = conn.prepareStatement(sql)
    val resultSet = ps.executeQuery()

    //遍历结果
    while (resultSet.next()){
      val id = resultSet.getInt("id")
      val name = resultSet.getString("name")
      val age =resultSet.getInt("age")
      println(s"id = ${id},name=${name},age=${age}")
    }

    //关闭资源
    if (resultSet!=null) resultSet.close()
    if (ps !=null) ps.close()
    if (conn!=null) conn.close()
  }
}
