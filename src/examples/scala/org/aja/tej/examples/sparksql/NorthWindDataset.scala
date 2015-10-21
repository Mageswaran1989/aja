package org.aja.tej.examples.sparksql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
 * Created by mageswaran on 18/10/15.
 */

/*
Lets take one record from the NW-Employees-NoHdr.csv and refer it for creating case calss
1,Fuller,Andrew,Sales Representative,12/6/48,4/29/92,Seattle,WA,98122,USA,2
*/

case class Employee(EmployeeId: Int, LastName : String, FirstName : String, Title : String,
                    BirthDate : String, HireDate : String,
                    City : String, State : String, Zip : String, Country : String,
                    ReportsTo : String)
/*
Lets take one record from NW-Orders-NoHdr.csv and refer it for creating case calss
10248,VINET,5,7/2/96,France
 */
case class Orders(OrderID: String, CustomerID: String, EmployeeID: String,
                   OrderData: String, ShipCountry: String)

/*
Lets take one record from NW-Order-Details-NoHdr.csv and refer it for creating case calss
10248,11,14,12,0
 */
case class OrderDetails(OrderID : String, ProductID : String, UnitPrice : Float,
                        Qty : Int, Discount : Float)

object NorthWindDataset {

  def main(args: Array[String]) {

    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    //Enable this to know the back end processing stages
    //Don't be afraid of the logs, you would see some code been generated for the SQL query!
    //That's how SQL queries are been executed across the Spark nodes.
    //Arbitrary code generation for given SQL query based on Spark Catalyst optimizer.
    //sc.setLogLevel("ALL")
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val nwEmployeeRecord = sc.textFile("data/northwind/NW-Employees-NoHdr.csv")
    println("NW-Employees-NoHdr.csv file has " + nwEmployeeRecord.count() + " lines")

    //single file no need to use flatMap()
    val employees = nwEmployeeRecord.map(_.split(","))
    .map(e => Employee(e(0).trim.toInt, e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8), e(9), e(10)))
    .toDF //Converts the RDD to DataFrame
    employees.registerTempTable("Employees")
    println("Number of employees : " + employees.count())
    println("\n******************************************************************\n")

    //Run the SQL query to select all the employees from the table
    var result = sqlContext.sql("SELECT * FROM Employees")
    result.foreach(println)
    println("\n******************************************************************\n")

    //Select employees from 'WA' only
    result = sqlContext.sql(" SELECT * FROM Employees WHERE State = 'WA'")
    result.foreach(println)
    println("\n******************************************************************\n")

    val orders = sc.textFile("data/northwind/NW-Orders-NoHdr.csv")
                   .map(_.split(","))
                   .map(o => Orders(o(0), o(1), o(2), o(3), o(4)))
                   .toDF
    orders.registerTempTable("Orders")
    println("Number of orders : " + orders.count)
    println("\n******************************************************************\n")

    val orderDetails = sc.textFile("data/northwind/NW-Order-Details-NoHdr.csv")
                         .map(_.split(","))
                         .map(e => OrderDetails( e(0), e(1), e(2).trim.toFloat,e(3).trim.toInt, e(4).trim.toFloat ))
                         .toDF
    orderDetails.registerTempTable("OrderDetails")
    println("Number of OrderDetails : " + orderDetails.count)
    sqlContext.sql("SELECT * FROM OrderDetails").take(10).foreach(println)
    println("\n******************************************************************\n")

    result = sqlContext.sql("SELECT OrderDetails.OrderID,ShipCountry,UnitPrice,Qty,Discount FROM Orders " +
      "INNER JOIN OrderDetails ON Orders.OrderID = OrderDetails.OrderID")
    result.take(10).foreach(println)
    result.take(10).foreach(e=>println("%s | %15s | %5.2f | %d | %5.2f |".format(e(0),e(1),e(2),e(3),e(4))))
    println("\n******************************************************************\n")

    result = sqlContext.sql("SELECT ShipCountry, Sum(OrderDetails.UnitPrice * Qty * Discount) AS ProductSales FROM " +
      "Orders INNER JOIN OrderDetails ON Orders.OrderID = OrderDetails.OrderID GROUP BY ShipCountry")
    result.take(10).foreach(println)
    // Need to try this
    // println(result.take(30).mkString(" | "))
    // result.take(30).foreach(e -> ... mkString(" | ")
    // probably this would work
    result.take(30).foreach(e=>println("%15s | %9.2f |".format(e(0),e(1))))
  }
}