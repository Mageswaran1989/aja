package org.aja.tej.examples.sparksql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
 * Created by mageswaran on 18/10/15.
 *
 * Dataset: Url: https://northwinddatabase.codeplex.com/releases/view/71634
 */

/*
Lets take one record from the NW-Employees-NoHdr.csv and refer it for creating case calss
1,Fuller,Andrew,Sales Representative,12/6/48,4/29/92,Seattle,WA,98122,USA,2

Note: case classes will have "extractors" created by the compiler, which is very usefull in the match...case{}
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
    //NW-Employees-NoHdr.csv file has 9 lines

    //single file no need to use flatMap()
    val employees = nwEmployeeRecord.map(_.split(","))
    .map(e => Employee(e(0).trim.toInt, e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8), e(9), e(10)))
    .toDF //Converts the RDD to DataFrame, which comes from last import
    employees.registerTempTable("Employees")
    println("Number of employees : " + employees.count()) //Number of employees : 9
    println("\n******************************************************************\n")

    //Run the SQL query to select all the employees from the table
    var result = sqlContext.sql("SELECT * FROM Employees")
    result.foreach(println)
    println("\n******************************************************************\n")
//    [1,Fuller,Andrew,Sales Representative,12/6/48,4/29/92,Seattle,WA,98122,USA,2]
//    [6,Suyama,Michael,Sales Representative,6/30/63,10/15/93,London,,EC2 7JR,UK,5]
//    [7,King,Robert,Sales Representative,5/27/60,12/31/93,London,,RG1 9SP,UK,5]
//    [2,Davolio,Nancy,"Vice President, Sales",2/17/52,8/12/92,Tacoma,WA,98401,USA]
//    [8,Callahan,Laura,Inside Sales Coordinator,1/7/58,3/3/94,Seattle,WA,98105,USA,2]
//    [3,Leverling,Janet,Sales Representative,8/28/63,3/30/92,Kirkland,WA,98033,USA,2]
//    [9,Buchanan,Steven,Sales Representative,1/25/66,11/13/94,London,,WG2 7LT,UK,5]
//    [4,Peacock,Margaret,Sales Representative,9/17/37,5/1/93,Redmond,WA,98052,USA,2]
//    [5,Dodsworth,Anne,Sales Manager,3/2/55,10/15/93,London,,SW1 8JR,UK,2]


    //Select employees from 'WA' only
    result = sqlContext.sql(" SELECT * FROM Employees WHERE State = 'WA'")
    result.foreach(println)
    println("\n******************************************************************\n")
//     [1,Fuller,Andrew,Sales Representative,12/6/48,4/29/92,Seattle,WA,98122,USA,2]
//    [8,Callahan,Laura,Inside Sales Coordinator,1/7/58,3/3/94,Seattle,WA,98105,USA,2]
//    [3,Leverling,Janet,Sales Representative,8/28/63,3/30/92,Kirkland,WA,98033,USA,2]
//    [4,Peacock,Margaret,Sales Representative,9/17/37,5/1/93,Redmond,WA,98052,USA,2]

    val orders = sc.textFile("data/northwind/NW-Orders-NoHdr.csv")
                   .map(_.split(","))
                   .map(o => Orders(o(0), o(1), o(2), o(3), o(4)))
                   .toDF
    orders.registerTempTable("Orders")
    println("Number of orders : " + orders.count) //Number of orders : 830
    println("\n******************************************************************\n")

    val orderDetails = sc.textFile("data/northwind/NW-Order-Details-NoHdr.csv")
                         .map(_.split(","))
                         .map(e => OrderDetails( e(0), e(1), e(2).trim.toFloat,e(3).trim.toInt, e(4).trim.toFloat ))
                         .toDF
    orderDetails.registerTempTable("OrderDetails")
    println("Number of OrderDetails : " + orderDetails.count)
    sqlContext.sql("SELECT * FROM OrderDetails").take(10).foreach(println)
    println("\n******************************************************************\n")
//    Number of OrderDetails : 2155
//      [10248,11,14.0,12,0.0]
//    [10248,42,9.8,10,0.0]
//    [10248,72,34.8,5,0.0]
//    [10249,14,18.6,9,0.0]
//    [10249,51,42.4,40,0.0]
//    [10250,41,7.7,10,0.0]
//    [10250,51,42.4,35,0.15]
//    [10250,65,16.8,15,0.15]
//    [10251,22,16.8,6,0.05]
//    [10251,57,15.6,15,0.05]

    result = sqlContext.sql("SELECT OrderDetails.OrderID,ShipCountry,UnitPrice,Qty,Discount FROM Orders " +
      "INNER JOIN OrderDetails ON Orders.OrderID = OrderDetails.OrderID")
    //result.take(10).foreach(println)
    result.take(10).foreach(e=>println("%s | %15s | %5.2f | %d | %5.2f |".format(e(0),e(1),e(2),e(3),e(4))))
    println("\n******************************************************************\n")
//      10315 |              UK | 11.20 | 14 |  0.00 |
//      10315 |              UK | 12.00 | 30 |  0.00 |
//      10766 |         Germany | 19.00 | 40 |  0.00 |
//      10766 |         Germany | 30.00 | 35 |  0.00 |
//      10766 |         Germany | 12.50 | 40 |  0.00 |
//      10810 |          Canada |  6.00 | 7 |  0.00 |
//      10810 |          Canada | 14.00 | 5 |  0.00 |
//      10810 |          Canada | 15.00 | 5 |  0.00 |
//      10928 |           Spain |  9.50 | 5 |  0.00 |
//      10928 |           Spain | 18.00 | 5 |  0.00 |

    result = sqlContext.sql("SELECT ShipCountry, Sum(OrderDetails.UnitPrice * Qty * Discount) AS ProductSales FROM " +
      "Orders INNER JOIN OrderDetails ON Orders.OrderID = OrderDetails.OrderID GROUP BY ShipCountry")
    result.take(10).foreach(println)
    // Need to try this
    // println(result.take(30).mkString(" | "))
    // result.take(30).foreach(e -> ... mkString(" | ")
    // probably this would work
    result.take(30).foreach(e=>println("%15s | %9.2f |".format(e(0),e(1))))

//       Mexico  |    491.37 |
//      Denmark  |   2121.23 |
//      Portugal |    996.29 |
//      Poland   |      0.00 |
//      Norway   |      0.00 |
//      Canada   |   5137.81 |
//      UK       |   1645.20 |
//      Belgium  |   1310.13 |
//      Brazil   |   8029.76 |
//      Ireland  |   7337.49 |
//      USA      |  17982.37 |
//      Argentina |      0.00 |
//      Sweden   |   5028.56 |
//      France   |   4140.44 |
//      Germany  |  14356.00 |
//      Spain    |   1448.69 |
//      Finland  |    968.40 |
//      Venezuela |   4004.26 |
//      Austria  |  11492.79 |
//      Switzerland |   1226.84 |
//      04876-786' |     12.94 |
//      Italy    |    935.00 |


  }
}