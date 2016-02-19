package org.aja.tej.examples.sparksql.dataframe


import org.aja.tej.examples.sparksql.datasets.NorthWindUtility
import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

/**
  * Created by mdhandapani on 3/2/16.
  */
object Basics  extends App with NorthWindUtility {

  import sqlContext.implicits._

  /**
    * Schema & Data Display API's
    * */

  println(employeesDF.toString()) //just gives you the schema
  //    [EmployeeID: int, LastName: string, FirstName: string, Title: string, BirthDate: string, HireDate: string, City: string, State: string, Zip: string, Country: string, ReportsTo: string]

  employeesDF.printSchema() //formatted schema layout
  //  root
  //  |-- EmployeeID: integer (nullable = true)
  //  |-- LastName: string (nullable = true)
  //  |-- FirstName: string (nullable = true)
  //  |-- Title: string (nullable = true)
  //  |-- BirthDate: string (nullable = true)
  //  |-- HireDate: string (nullable = true)
  //  |-- City: string (nullable = true)
  //  |-- State: string (nullable = true)
  //  |-- Zip: string (nullable = true)
  //  |-- Country: string (nullable = true)
  //  |-- ReportsTo: string (nullable = true)

  employeesDF.show(5) //formatted data in tabular form
  //  +----------+---------+---------+--------------------+---------+--------+--------+-----+-------+-------+---------+
  //  |EmployeeID| LastName|FirstName|               Title|BirthDate|HireDate|    City|State|    Zip|Country|ReportsTo|
  //  +----------+---------+---------+--------------------+---------+--------+--------+-----+-------+-------+---------+
  //  |         1|   Fuller|   Andrew|Sales Representative|  12/6/48| 4/29/92| Seattle|   WA|  98122|    USA|        2|
  //  |         2|  Davolio|    Nancy|Vice President, S...|  2/17/52| 8/12/92|  Tacoma|   WA|  98401|    USA|        0|
  //  |         3|Leverling|    Janet|Sales Representative|  8/28/63| 3/30/92|Kirkland|   WA|  98033|    USA|        2|
  //  |         4|  Peacock| Margaret|Sales Representative|  9/17/37|  5/1/93| Redmond|   WA|  98052|    USA|        2|
  //  |         5|Dodsworth|     Anne|       Sales Manager|   3/2/55|10/15/93|  London|     |SW1 8JR|     UK|        2|
  //  +----------+---------+---------+--------------------+---------+--------+--------+-----+-------+-------+---------+

  /**
    * Selection
    * Column Name options : "col1 name", col2 name" or "*" or df("col name) or $"col name"
    */
  employeesDF.select("FirstName", "LastName", "BirthDate").show(5)
  //  +---------+---------+---------+
  //  |FirstName| LastName|BirthDate|
  //  +---------+---------+---------+
  //  |   Andrew|   Fuller|  12/6/48|
  //  |    Nancy|  Davolio|  2/17/52|
  //  |    Janet|Leverling|  8/28/63|
  //  | Margaret|  Peacock|  9/17/37|
  //  |     Anne|Dodsworth|   3/2/55|
  //  +---------+---------+---------+

  employeesDF.select(employeesDF("EmployeeID"), employeesDF("FirstName").as("Name")).show(5)
  //  +----------+--------+
  //  |EmployeeID|    Name|
  //  +----------+--------+
  //  |         1|  Andrew|
  //  |         2|   Nancy|
  //  |         3|   Janet|
  //  |         4|Margaret|
  //  |         5|    Anne|
  //  +----------+--------+

  employeesDF.select(employeesDF("EmployeeID"), array($"FirstName", $"LastName").as("Name")).show(5)
  //  +----------+-------------------+
  //  |EmployeeID|               Name|
  //  +----------+-------------------+
  //  |         1|   [Andrew, Fuller]|
  //  |         2|   [Nancy, Davolio]|
  //  |         3| [Janet, Leverling]|
  //  |         4|[Margaret, Peacock]|
  //  |         5|  [Anne, Dodsworth]|
  //  +----------+-------------------+


  employeesDF.select($"EmployeeID".as("ID")).show(5)
  //  +---+
  //  | ID|
  //  +---+
  //  |  1|
  //  |  2|
  //  |  3|
  //  |  4|
  //  |  5|
  //  +---+

  /**
    * Filter
    */
  employeesDF.filter($"Country".equalTo("UK")).show(5)
  //  +----------+---------+---------+--------------------+---------+--------+------+-----+-------+-------+---------+
  //  |EmployeeID| LastName|FirstName|               Title|BirthDate|HireDate|  City|State|    Zip|Country|ReportsTo|
  //  +----------+---------+---------+--------------------+---------+--------+------+-----+-------+-------+---------+
  //  |         5|Dodsworth|     Anne|       Sales Manager|   3/2/55|10/15/93|London|     |SW1 8JR|     UK|        2|
  //  |         6|   Suyama|  Michael|Sales Representative|  6/30/63|10/15/93|London|     |EC2 7JR|     UK|        5|
  //  |         7|     King|   Robert|Sales Representative|  5/27/60|12/31/93|London|     |RG1 9SP|     UK|        5|
  //  |         9| Buchanan|   Steven|Sales Representative|  1/25/66|11/13/94|London|     |WG2 7LT|     UK|        5|
  //  +----------+---------+---------+--------------------+---------+--------+------+-----+-------+-------+---------+

  /**
    * Data Manipulation using DSL
    */

  orderDetailsDF.show(5)
  //  +-------+---------+---------+---+--------+
  //  |OrderID|ProductID|UnitPrice|Qty|Discount|
  //  +-------+---------+---------+---+--------+
  //  |  10248|       11|     14.0| 12|     0.0|
  //  |  10248|       42|      9.8| 10|     0.0|
  //  |  10248|       72|     34.8|  5|     0.0|
  //  |  10249|       14|     18.6|  9|     0.0|
  //  |  10249|       51|     42.4| 40|     0.0|
  //  +-------+---------+---------+---+--------+

  orderDetailsDF.select($"UnitPrice" * $"Qty").as("TotalPrice").show(5)
  //  +-----------------+
  //  |(UnitPrice * Qty)|
  //  +-----------------+
  //  |            168.0|
  //  |             98.0|
  //  |            174.0|
  //  |            167.4|
  //  |           1696.0|
  //  +-----------------+

  //Lets add this TotalPRice to orderDetails

  orderDetailsDF.select(orderDetailsDF("*"), $"UnitPrice" * $"Qty".as("TotalPrice")).show(5)
  //  +-------+---------+---------+---+--------+----------------------------------+
  //  |OrderID|ProductID|UnitPrice|Qty|Discount|(UnitPrice * Qty AS TotalPrice#31)|
  //  +-------+---------+---------+---+--------+----------------------------------+
  //  |  10248|       11|     14.0| 12|     0.0|                             168.0|
  //  |  10248|       42|      9.8| 10|     0.0|                              98.0|
  //  |  10248|       72|     34.8|  5|     0.0|                             174.0|
  //  |  10249|       14|     18.6|  9|     0.0|                             167.4|
  //  |  10249|       51|     42.4| 40|     0.0|                            1696.0|
  //  +-------+---------+---------+---+--------+----------------------------------+


  orderDetailsDF.select($"*", lit(1).as("Min Discount"), rand().as("r")).show(5)
  //  +-------+---------+---------+---+--------+------------+--------------------+
  //  |OrderID|ProductID|UnitPrice|Qty|Discount|Min Discount|                   r|
  //  +-------+---------+---------+---+--------+------------+--------------------+
  //  |  10248|       11|     14.0| 12|     0.0|           1|0.020392164663888157|
  //  |  10248|       42|      9.8| 10|     0.0|           1|  0.4268059459785989|
  //  |  10248|       72|     34.8|  5|     0.0|           1|  0.4950364695956071|
  //  |  10249|       14|     18.6|  9|     0.0|           1|   0.730863746231909|
  //  |  10249|       51|     42.4| 40|     0.0|           1|  0.3231373463001569|
  //  +-------+---------+---------+---+--------+------------+--------------------+
}

