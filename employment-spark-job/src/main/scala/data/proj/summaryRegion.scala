package data.proj

import java.sql.{Connection, Date, DriverManager, PreparedStatement}
import java.util.Properties
import scala.util.Random

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DecimalType, IntegerType}
import org.apache.spark.sql.{DataFrame, Row}

object summaryRegion {
  def main(args: Array[String]): Unit = {
    // 创建SparkSession
    val spark: SparkSession = SparkSession.builder()
      .appName("region_employment_summary")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "hdfs://192.168.71.201:8020/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://192.168.71.201:9083")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.71.201:8020")
      .enableHiveSupport()
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    import spark.implicits._

    println("========== 开始地区就业统计 ==========")

    try {
      val random = new Random()
      val fixedDate = Date.valueOf("2024-09-01")

      // 创建region_info数据
      val regionInfoData = Seq(
        ("上海市", "徐汇区"), ("上海市", "浦东新区"), ("上海市", "静安区"), ("上海市", "黄浦区"),
        ("云南省", "丽江市"), ("云南省", "保山市"), ("云南省", "大理市"), ("云南省", "昆明市"),
        ("云南省", "昭通市"), ("云南省", "普洱市"), ("内蒙古", "呼和浩特"), ("北京市", "朝阳区"),
        ("北京市", "海淀区"), ("吉林省", "吉林市"), ("吉林省", "四平市"), ("吉林省", "白山市"),
        ("吉林省", "辽源市"), ("吉林省", "通化市"), ("吉林省", "长春市"), ("四川省", "内江市"),
        ("四川省", "宜宾市"), ("四川省", "广元市"), ("四川省", "德阳市"), ("四川省", "成都市"),
        ("四川省", "泸州市"), ("四川省", "绵阳市"), ("天津市", "滨海新区"), ("宁夏", "中卫市"),
        ("宁夏", "吴忠市"), ("宁夏", "固原市"), ("宁夏", "石嘴山市"), ("宁夏", "银川市"),
        ("安徽省", "六安市"), ("安徽省", "合肥市"), ("安徽省", "安庆市"), ("安徽省", "芜湖市"),
        ("安徽省", "蚌埠市"), ("安徽省", "阜阳市"), ("山东省", "威海市"), ("山东省", "泰安市"),
        ("山东省", "济南市"), ("山东省", "济宁市"), ("山东省", "潍坊市"), ("山东省", "烟台市"),
        ("山东省", "青岛市"), ("山西省", "太原市"), ("广东省", "东莞市"), ("广东省", "中山市"),
        ("广东省", "佛山市"), ("广东省", "广州市"), ("广东省", "汕头市"), ("广东省", "深圳市"),
        ("广东省", "清远市"), ("广东省", "珠海市"), ("广东省", "肇庆市"), ("广西省", "南宁市"),
        ("新疆", "乌鲁木齐"), ("新疆", "克拉玛依"), ("新疆", "哈密市"), ("新疆", "喀什"),
        ("新疆", "昌吉"), ("新疆", "阿克苏"), ("江苏省", "南京市"), ("江苏省", "南通市"),
        ("江苏省", "常州市"), ("江苏省", "无锡市"), ("江苏省", "盐城市"), ("江苏省", "苏州市"),
        ("江苏省", "连云港"), ("江苏省", "镇江市"), ("江西省", "上饶市"), ("江西省", "九江市"),
        ("江西省", "南昌市"), ("江西省", "景德镇"), ("江西省", "萍乡市"), ("江西省", "赣州市"),
        ("河北省", "保定市"), ("河北省", "唐山市"), ("河北省", "张家口"), ("河北省", "石家庄市"),
        ("河北省", "邢台市"), ("河北省", "邯郸市"), ("河南省", "信阳市"), ("河南省", "安阳市"),
        ("河南省", "新乡市"), ("河南省", "洛阳市"), ("河南省", "许昌市"), ("河南省", "郑州市"),
        ("浙江省", "嘉兴市"), ("浙江省", "宁波市"), ("浙江省", "杭州市"), ("浙江省", "温州市"),
        ("浙江省", "绍兴市"), ("浙江省", "舟山市"), ("浙江省", "衢州市"), ("浙江省", "金华市"),
        ("海南省", "万宁市"), ("海南省", "三亚市"), ("海南省", "儋州市"), ("海南省", "文昌市"),
        ("海南省", "海口市"), ("海南省", "琼海市"), ("深圳市", "南山区"), ("湖北省", "孝感市"),
        ("湖北省", "宜昌市"), ("湖北省", "武汉市"), ("湖北省", "荆州市"), ("湖北省", "襄阳市"),
        ("湖北省", "鄂州市"), ("湖北省", "黄冈市"), ("湖南省", "岳阳市"), ("湖南省", "张家界"),
        ("湖南省", "株洲市"), ("湖南省", "衡阳市"), ("湖南省", "邵阳市"), ("湖南省", "长沙市"),
        ("甘肃省", "兰州市"), ("甘肃省", "天水市"), ("甘肃省", "庆阳市"), ("甘肃省", "张掖市"),
        ("甘肃省", "白银市"), ("甘肃省", "酒泉市"), ("福建省", "三明市"), ("福建省", "厦门市"),
        ("福建省", "泉州市"), ("福建省", "漳州市"), ("福建省", "福州市"), ("福建省", "龙岩市"),
        ("西藏", "山南市"), ("西藏", "拉萨市"), ("西藏", "日喀则"), ("西藏", "林芝市"),
        ("西藏", "那曲市"), ("西藏", "阿里地区"), ("贵州省", "六盘水"), ("贵州省", "安顺市"),
        ("贵州省", "毕节市"), ("贵州省", "贵阳市"), ("贵州省", "遵义市"), ("贵州省", "铜仁市"),
        ("辽宁省", "大连市"), ("辽宁省", "抚顺市"), ("辽宁省", "沈阳市"), ("辽宁省", "营口市"),
        ("辽宁省", "锦州市"), ("辽宁省", "阜新市"), ("辽宁省", "鞍山市"), ("重庆市", "渝北区"),
        ("陕西省", "安康市"), ("陕西省", "宝鸡市"), ("陕西省", "延安市"), ("陕西省", "榆林市"),
        ("陕西省", "汉中市"), ("陕西省", "西安市"), ("青海省", "格尔木"), ("青海省", "海东市"),
        ("青海省", "海南州"), ("青海省", "玉树市"), ("青海省", "西宁市"), ("黑龙江省", "佳木斯"),
        ("黑龙江省", "哈尔滨市"), ("黑龙江省", "大庆市"), ("黑龙江省", "牡丹江"), ("黑龙江省", "齐齐哈尔")
      )

      // 创建模拟数据DataFrame
      val simulatedData = regionInfoData.map { case (province, city) =>
        val employedStudents = 50 + random.nextInt(11) // 50-60
        val avgSalary = 15000 + random.nextInt(5001) // 15000-20000
        (province, city, employedStudents, BigDecimal(avgSalary).setScale(2, BigDecimal.RoundingMode.HALF_UP), fixedDate)
      }

      val finalDF = spark.createDataFrame(simulatedData)
        .toDF("province", "city", "employed_students", "avg_salary", "stat_date")
        .select(
          $"province",
          $"city",
          $"employed_students".cast(IntegerType),
          $"avg_salary".cast(DecimalType(20, 2)),
          $"stat_date"
        )
        .orderBy("province", "city")

      println("\n生成的地区就业统计数据:")
      finalDF.show(false)

      // MySQL配置
      val mysqlUrl = "jdbc:mysql://192.168.71.201:3306/job_analysis?useUnicode=true&characterEncoding=UTF-8"
      val mysqlUser = "root"
      val mysqlPassword = "030106"
      val tableName = "summary_region"

      println("\n写入MySQL数据库...")
      Class.forName("com.mysql.jdbc.Driver")

      var conn: Connection = null
      var stmt: PreparedStatement = null

      try {
        conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)

        // 1. 确保数据库使用utf8mb4
        conn.createStatement().execute("ALTER DATABASE job_analysis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")

        // 2. 创建表 - 根据图片中的表结构
        val createTableSQL =
          s"""
          CREATE TABLE IF NOT EXISTS $tableName (
            province VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
            city VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
            employed_students INT,
            avg_salary DECIMAL(20,2),
            stat_date DATE,
            PRIMARY KEY (province, city, stat_date)
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
          """
        conn.createStatement().execute(createTableSQL)

        // 3. 修改现有表的字符集（如果表已存在）
        conn.createStatement().execute(s"ALTER TABLE $tableName CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")

        // 4. 修改列的字符集
        conn.createStatement().execute(s"ALTER TABLE $tableName MODIFY province VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
        conn.createStatement().execute(s"ALTER TABLE $tableName MODIFY city VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")

        // 5. 清空表数据
        conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0")
        conn.createStatement().execute(s"TRUNCATE TABLE $tableName")
        conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1")

        // 6. 准备插入语句
        val insertSQL =
          s"INSERT INTO $tableName (province, city, employed_students, avg_salary, stat_date) " +
            "VALUES (?, ?, ?, ?, ?)"

        stmt = conn.prepareStatement(insertSQL)
        conn.setAutoCommit(false)

        // 7. 收集数据并写入
        val data = finalDF.collect()
        println(s"准备写入 ${data.length} 条记录到MySQL")

        for (row <- data) {
          stmt.setString(1, row.getAs[String]("province"))
          stmt.setString(2, row.getAs[String]("city"))
          stmt.setInt(3, row.getAs[Int]("employed_students"))
          stmt.setBigDecimal(4, row.getAs[java.math.BigDecimal]("avg_salary"))
          stmt.setDate(5, row.getAs[java.sql.Date]("stat_date"))
          stmt.addBatch()
        }

        // 8. 执行批处理
        stmt.executeBatch()
        conn.commit()

        println(s"数据成功写入MySQL表: $tableName")
      } catch {
        case e: Exception =>
          println(s"MySQL操作失败: ${e.getMessage}")
          e.printStackTrace()
          if (conn != null) conn.rollback()
      } finally {
        if (stmt != null) stmt.close()
        if (conn != null) conn.close()
      }

      // 9. 验证写入数据
      println("\n验证MySQL数据:")
      val mysqlDF = spark.read
        .format("jdbc")
        .option("url", mysqlUrl)
        .option("dbtable", tableName)
        .option("user", mysqlUser)
        .option("password", mysqlPassword)
        .option("characterEncoding", "UTF-8")
        .option("useUnicode", "true")
        .load()

      mysqlDF.show(false)

    } catch {
      case e: Exception =>
        println(s"数据处理失败: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
      println("\n========== 处理完成 ==========")
    }
  }
}