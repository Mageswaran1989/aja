//package org.aja.tej.examples.sparksql.sql
//
//import org.aja.tej.utils.TejUtils
//import org.apache.spark.sql.SQLContext
//
///**
// * Created by mageswaran on 24/1/16.
// */
//object UDF extends App {
//
//  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
//
//  val sqlContext = new SQLContext(sc)
//  import sqlContext.implicits._
//
//  //Lets us defice the scehme for table using case classes
//  //Let us skip the units for purchase
//  case class CustomerDetails(id: Long, name: String, state: String, purchaseAmt: Double, discountAmt: Double)
//
//  // an extra case class to show how UDFs can generate structure
//  case class PurchaseDisc(purchaseAmt: Double, discountAmt: Double)
//
//  val customers = Seq(
//    CustomerDetails(1, "Mageswaran", "TN", 15000.00, 150),
//    CustomerDetails(2, "Michael", "JR", 24000.00, 300),
//    CustomerDetails(3, "Antony Leo", "TN", 10000.00, 50),
//    CustomerDetails(4, "Arun", "TN", 18000.00, 90),
//    CustomerDetails(5, "Venkat", "ANDRA", 5000.00, 0),
//    CustomerDetails(6, "Sathis", "TN", 150000.00, 3000)
//  )
//
//  val customerTable = sc.parallelize(customers, 4).toDF()
//
//  // DSL usage -- query using a UDF but without SQL
//  // (this example has been repalced by the one in dataframe.UDF)
//
//  def westernState(state: String) = Seq("TN", "JR", "ANDRA", "KA").contains(state)
//
//  // for SQL usage  we need to register the table
//
//  customerTable.registerTempTable("customerTable")
//
//  // WHERE clause
//
//  sqlContext.udf.register("westernState", westernState _)
//
//  println("UDF in a WHERE")
//  val westernStates =
//    sqlContext.sql("SELECT * FROM customerTable WHERE westernState(state)")
//  westernStates.foreach(println)
//
//  // HAVING clause
//
//  def manyCustomers(cnt: Long) = cnt > 2
//
//  sqlContext.udf.register("manyCustomers", manyCustomers _)
//
//  println("UDF in a HAVING")
//  val statesManyCustomers =
//    sqlContext.sql(
//      s"""
//         |SELECT state, COUNT(id) AS custCount
//         |FROM customerTable
//         |GROUP BY state
//         |HAVING manyCustomers(custCount)
//         """.stripMargin)
//  statesManyCustomers.foreach(println)
//
//  // GROUP BY clause
//
//  def stateRegion(state:String) = state match {
//    case "TN" | "KL"  => "South"
//    case "KA" | "ANDRA" => "East"
//    case "JR" | "MP" => "North"
//  }
//
//  sqlContext.udf.register("stateRegion", stateRegion _)
//
//  println("UDF in a GROUP BY")
//  // note the grouping column repeated since it doesn't have an alias
//  val purchaseByRegion =
//    sqlContext.sql(
//      s"""
//         |SELECT SUM(purchaseAmt), stateRegion(state) AS totalpurchaseAmt
//         |FROM customerTable
//         |GROUP BY stateRegion(state)
//        """.stripMargin)
//  purchaseByRegion.foreach(println)
//
//  // we can also apply a UDF to the result columns
//
//  def discountRatio(purchaseAmt: Double, discountAmt: Double) = discountAmt/purchaseAmt
//
//  sqlContext.udf.register("discountRatio", discountRatio _)
//
//  println("UDF in a result")
//  val customerDiscounts =
//    sqlContext.sql(
//      s"""
//         |SELECT id, discountRatio(purchaseAmt, discountAmt) AS ratio
//         |FROM customerTable
//        """.stripMargin)
//  customerDiscounts.foreach(println)
//
//  // we can make the UDF create nested structure in the results
//
//
//  def makeStruct(purchase: Double, disc:Double) = PurchaseDisc(purchase, disc)
//
//  sqlContext.udf.register("makeStruct", makeStruct _)
//
//  // these failed in Spark 1.3.0 -- reported SPARK-6054 -- but work again in 1.3.1
//
//  println("UDF creating structured result")
//  val withStruct =
//    sqlContext.sql("SELECT makeStruct(purchaseAmt, discountAmt) AS pd FROM customerTable")
//  withStruct.foreach(println)
//
//  println("UDF with nested query creating structured result")
//  val nestedStruct =
//    sqlContext.sql("SELECT id, sd.purchaseAmt FROM (SELECT id, makeStruct(purchaseAmt, discountAmt) AS sd FROM customerTable) AS d")
//  nestedStruct.foreach(println)
//}
