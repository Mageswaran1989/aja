package org.aja.tej.examples.sparksql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
  * Created by mdhandapani on 29/7/15.
  */

// Two methods of creating the DataFrame
// 1. Allowing the Spark to manage itself by using SQL Context
// 2. Creating schema using case class and registering it with the sql context
object HelloWorldSparkSQL extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  //def main (args: Array[String]) { //TODO: Spark SQL Implicits are not working win main function

  //Infers the scehma from json
  val jsonDataFrame = sqlContext.read.json("data/people.json")
  //{"name":"Michael"}
  //{"name":"Andy", "age":30}
  //{"name":"Justin", "age":19}
  jsonDataFrame.show()
  //    +----+-------+
  //    | age|   name|
  //    +----+-------+
  //    |null|Michael|
  //    |  30|   Andy|
  //    |  19| Justin|
  //    +----+-------+
  jsonDataFrame.printSchema()
  //root
  //    |-- age: long (nullable = true)
  //    |-- name: string (nullable = true)

  // Select everybody, but increment the age by 1
  jsonDataFrame.select(jsonDataFrame("name"), jsonDataFrame("age") + 1).show()
  //    +-------+---------+
  //    |   name|(age + 1)|
  //    +-------+---------+
  //    |Michael|     null|
  //    |   Andy|       31|
  //    | Justin|       20|
  //    +-------+---------+
  // Select people older than 21
  jsonDataFrame.filter(jsonDataFrame("age") > 21).show()
  //    +---+----+
  //    |age|name|
  //    +---+----+
  //    | 30|Andy|
  //    +---+----+
  // Count people by age
  jsonDataFrame.groupBy("age").count().show()
  //    +----+-----+
  //    | age|count|
  //    +----+-----+
  //    |null|    1|
  //    |  19|    1|
  //    |  30|    1|
  //    +----+-----+

  /*********************************************************************************************/

  println("Method 2")
  //    people.txt
  //    Michael, 29
  //    Andy, 30
  //    Justin, 19

  //define schema using a case class
  case class Person(name: String, age: Int)

  // create an RDD of Person objects and register it as a table
  val people = sc.textFile("data/people.txt").map(_.split(",")).map(p => Person(p(0), p(1).trim.toInt)).toDF()
  people.registerTempTable("people")

  //SQL statement can be run using the SQL methods provided by sqlcontext
  val teenagers = sqlContext.sql("SELECT name, age FROM people WHERE age >= 13 AND age <= 19")

  // The results of SQL queries are DataFrames and support all the normal RDD operations.
  // The columns of a row in the result can be accessed by field index:
  teenagers.map(t => "Name: " + t(0)).collect().foreach(println)
  //Name: Justin

  // or by field name:
  teenagers.map(t => "Name: " + t.getAs[String]("name")).collect().foreach(println)
  //Name: Justin

  // row.getValuesMap[T] retrieves multiple columns at once into a Map[String, T]
  teenagers.map(_.getValuesMap[Any](List("name", "age"))).collect().foreach(println)
  // Map("name" -> "Justin", "age" -> 19)

  /*********************************************************************************************/

  println("Method 3")

  //Lets us define the scehme for table using case classes
  //Let us skip the units for purchase
  case class CustomerDetails(id: Long, name: String, state: String, purchaseAmt: Double, discountAmt: Double)

  val customers = Seq(
    CustomerDetails(1, "Mageswaran", "TN",    15000.00,  150),
    CustomerDetails(2, "Michael",    "JR",    24000.00,  300),
    CustomerDetails(3, "Antony Leo", "TN",    10000.00,  50),
    CustomerDetails(4, "Arun",       "TN",    18000.00,  90),
    CustomerDetails(5, "Venkat",     "ANDRA", 5000.00,   0),
    CustomerDetails(6, "Sathis",     "TN",    150000.00, 3000)
  )

  val customerDF = sc.parallelize(customers, 4).toDF()

  println("*** See the DataFrame contents")
  customerDF.show()
  //  +---+----------+-----+-----------+-----------+
  //  | id|      name|state|purchaseAmt|discountAmt|
  //  +---+----------+-----+-----------+-----------+
  //  |  1|Mageswaran|   TN|    15000.0|      150.0|
  //  |  2|   Michael|   JR|    24000.0|      300.0|
  //  |  3|Antony Leo|   TN|    10000.0|       50.0|
  //  |  4|      Arun|   TN|    18000.0|       90.0|
  //  |  5|    Venkat|ANDRA|     5000.0|        0.0|
  //  |  6|    Sathis|   TN|   150000.0|     3000.0|
  //  +---+----------+-----+-----------+-----------+

  println("*** See the first few lines of the DataFrame contents")
  customerDF.show(2)

  println("*** See the first few lines of the DataFrame contents")
  customerDF.printSchema()

  println("*** Statistics for the numerical columns")
  customerDF.describe("purchaseAmt","discountAmt").show()

  // Register with a table name for SQL queries
  customerDF.registerTempTable("customer")

  println("*** Very simple query")
  val allCust = sqlContext.sql("SELECT id, name FROM customer")
  allCust.show()

  println("*** The result has a schema too")
  allCust.printSchema()

  //
  // more complex query: note how it's spread across multiple lines
  //
  println("*** Very simple query with a filter")
  val tnCust =
    sqlContext.sql(
      s"""
         | SELECT id, name, purchaseAmt
         | FROM customer
         | WHERE state = 'TN'
         """.stripMargin)
  tnCust.show()
  tnCust.printSchema()

  println("*** Queries are case sensitive by default, but this can be disabled")

  sqlContext.setConf("spark.sql.caseSensitive", "false")
  //
  // the capitalization of "CUSTOMER" here would normally make the query fail
  // with "Table not found"
  //
  val caseInsensitive = sqlContext.sql("SELECT * FROM CUSTOMER")
  caseInsensitive.show()
  sqlContext.setConf("spark.sql.caseSensitive", "true")
  //}
}
