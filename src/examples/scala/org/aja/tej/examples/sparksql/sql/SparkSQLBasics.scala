package org.aja.tej.examples.sparksql.sql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
 * Created by mageswaran on 24/1/16.
 */
object SparkSQLBasics extends App{

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  //Lets us defice the scehme for table using case classes
  //Let us skip the units for purchase
  case class CustomerDetails(id: Long, name: String, state: String, purchaseAmt: Double, discountAmt: Double)

  val customers = Seq(
  CustomerDetails(1, "Mageswaran", "TN", 15000.00, 150),
    CustomerDetails(2, "Michael", "JR", 24000.00, 300),
    CustomerDetails(3, "Antony Leo", "TN", 10000.00, 50),
    CustomerDetails(4, "Arun", "TN", 18000.00, 90),
    CustomerDetails(5, "Venkat", "ANDRA", 5000.00, 0),
    CustomerDetails(6, "Sathis", "TN", 150000.00, 3000)
  )

  val customerDF = sc.parallelize(customers, 4).toDF()

  println("*** See the DataFrame contents")
  customerDF.show()

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
  val caseInsensitive =
    sqlContext.sql("SELECT * FROM CUSTOMER")
  caseInsensitive.show()
  sqlContext.setConf("spark.sql.caseSensitive", "true")


}
