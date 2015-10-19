package org.aja.tej.examples.sparksql

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by mdhandapani on 29/7/15.
 */

//define schema using a case class
case class Person(name: String, age: Int)

// Two methods of creating the DataFrame
// 1. Allowing the Spark to manage itself by using SQL Context
// 2. Creating schema using case class and registering it with the sql context
object HelloWorld {
  def main (args: Array[String]) {
    val conf = new SparkConf().setAppName("Simple SparkSQL Application").setMaster("local[2]" /*"spark://myhost:7077"*/)
    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val df = sqlContext.read.json("data/people.json")
    //{"name":"Michael"}
    //{"name":"Andy", "age":30}
    //{"name":"Justin", "age":19}
    df.show()
    df.printSchema()

    // Select everybody, but increment the age by 1
    df.select(df("name"), df("age") + 1).show()
    // Select people older than 21
    df.filter(df("age") > 21).show()
    // Count people by age
    df.groupBy("age").count().show()

    //Method 2
    //    people.txt
    //    Michael, 29
    //    Andy, 30
    //    Justin, 19
    // create an RDD of Person objects and register it as a table
    val people = sc.textFile("data/people.txt").map(_.split(",")).map(p => Person(p(0), p(1).trim.toInt)).toDF()
    people.registerTempTable("people")

    //SQL statement can be run using the SQL methods provided by sqlcontext
    val teenagers = sqlContext.sql("SELECT name, age FORM people WHERE age >= 13 AND age <= 19")

    // The results of SQL queries are DataFrames and support all the normal RDD operations.
    // The columns of a row in the result can be accessed by field index:
    teenagers.map(t => "Name: " + t(0)).collect().foreach(println)

    // or by field name:
    teenagers.map(t => "Name: " + t.getAs[String]("name")).collect().foreach(println)

    // row.getValuesMap[T] retrieves multiple columns at once into a Map[String, T]
    teenagers.map(_.getValuesMap[Any](List("name", "age"))).collect().foreach(println)
    // Map("name" -> "Justin", "age" -> 19)
  }
}
