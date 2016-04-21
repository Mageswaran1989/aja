package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.examples.sparksql.datasets.NorthWindUtility
import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.{SQLContext,Column}
import org.apache.spark.sql.GroupedData
import org.apache.spark.sql.functions._

/**
  * Created by mdhandapani on 6/1/16.
  */
object Aggregation  extends App with NorthWindUtility {


  ordersDF.show(5)
  //  +-------+----------+----------+---------+-----------+
  //  |OrderID|CustomerID|EmployeeID|OrderDate|ShipCountry|
  //  +-------+----------+----------+---------+-----------+
  //  |  10248|     VINET|         5|   7/2/96|     France|
  //  |  10249|     TOMSP|         6|   7/3/96|    Germany|
  //  |  10250|     HANAR|         4|   7/6/96|     Brazil|
  //  |  10251|     VICTE|         3|   7/6/96|     France|
  //  |  10252|     SUPRD|         4|   7/7/96|    Belgium|
  //  +-------+----------+----------+---------+-----------+

  /**
    * Aggregation functions:
    * count("col name)
    * countDistinct("col name")
    * sum("col name")
    */

  // groupBy() produces a GroupedData, and you can't do much with
  // one of those other than aggregate it -- you can't even print it

  ordersDF.groupBy("OrderID").count().show()


  ordersDF.groupBy("ShipCountry").agg(count("OrderID").as("TotalOrders")).show()
  //  +-----------+-----------+
  //  |ShipCountry|TotalOrders|
  //  +-----------+-----------+
  //  |     Mexico|         28|
  //  |    Denmark|         18|
  //  |   Portugal|         13|
  //  |     Poland|          7|
  //  |     Norway|          6|
  //  |     Canada|         30|
  //  |         UK|         56|
  //  |    Belgium|         19|
  //  |     Brazil|         82|
  //  |    Ireland|         19|
  //  |        USA|        122|
  //  |  Argentina|         16|
  //  |     Sweden|         37|
  //  |     France|         77|
  //  |    Germany|        122|
  //  |      Spain|         23|
  //  |    Finland|         22|
  //  |  Venezuela|         46|
  //  |    Austria|         40|
  //  |Switzerland|         18|
  //  +-----------+-----------+


  // basic form of aggregation assigns a function to
  // each non-grouped column -- you map each column you want
  // aggregated to the name of the aggregation function you want
  // to use
  //
  // automatically includes grouping columns in the DataFrame

  val temp = ordersDF.groupBy("ShipCountry").agg(count("OrderID").as("TotalOrders"))

  temp.select("ShipCountry").agg("TotalOrders" -> "max")
    .show()

  //  customerDF.groupBy("state").agg("discountAmt" -> "max").show()
  ////  +-----+----------------+
  ////  |state|max(discountAmt)|
  ////  +-----+----------------+
  ////  |   TN|          3000.0|
  ////  |ANDRA|             0.0|
  ////  |   JR|           300.0|
  ////  +-----+----------------+
  //
  //  // you can turn of grouping columns using the SQL context's
  //  // configuration properties
  //
  //  println("*** this time without grouping columns")
  //  sqlContext.setConf("spark.sql.retainGroupColumns", "false")
  //  customerDF.groupBy("state").agg("discountAmt" -> "max").show()
  ////  +----------------+
  ////  |max(discountAmt)|
  ////  +----------------+
  ////  |          3000.0|
  ////  |             0.0|
  ////  |           300.0|
  ////  +----------------+
  //
  //  //
  //  // When you use $"somestring" to refer to column names, you use the
  //  // very flexible column-based version of aggregation, allowing you to make
  //  // full use of the DSL defined in org.apache.spark.sql.functions --
  //  // this version doesn't automatically include the grouping column
  //  // in the resulting DataFrame, so you have to add it yourself.
  //  //
  //
  //  println("*** Column based aggregation")
  //  // you can use the Column object to specify aggregation
  //  customerDF.groupBy("state").agg(max($"discountAmt")).show()
  //
  ////  +----------------+
  ////  |max(discountAmt)|
  ////  +----------------+
  ////  |          3000.0|
  ////  |             0.0|
  ////  |           300.0|
  ////  +----------------+
  //
  //  println("*** Column based aggregation plus grouping columns")
  //  // but this approach will skip the grouped columns if you don't name them
  //  customerDF.groupBy("state").agg($"state", max($"discountAmt")).show()
  //
  ////  +-----+----------------+
  ////  |state|max(discountAmt)|
  ////  +-----+----------------+
  ////  |   TN|          3000.0|
  ////  |ANDRA|             0.0|
  ////  |   JR|           300.0|
  ////  +-----+----------------+
  //
  //
  //  // Think of this as a user-defined aggregation function -- written in terms
  //  // of more primitive aggregations
  //  def stddevFunc(c: Column): Column =
  //    sqrt(avg(c * c) - (avg(c) * avg(c)))
  //
  //  println("*** Sort-of a user-defined aggregation function")
  //  customerDF.groupBy("state").agg($"state", stddevFunc($"discountAmt")).show()
  //
  ////  +-----+--------------------------------------------------------------------------------+
  ////  |state|SQRT((avg((discountAmt * discountAmt)) - (avg(discountAmt) * avg(discountAmt))))|
  ////  +-----+--------------------------------------------------------------------------------+
  ////  |   TN|                                                              1257.6838831757366|
  ////  |ANDRA|                                                                             0.0|
  ////  |   JR|                                                                             0.0|
  ////  +-----+--------------------------------------------------------------------------------+
  //
  //  // there are some special short cuts on GroupedData to aggregate
  //  // all numeric columns
  //  println("*** Aggregation short cuts")
  //  customerDF.groupBy("state").count().show()
  ////  +-----+
  ////  |count|
  ////  +-----+
  ////  |    4|
  ////  |    1|
  ////  |    1|
  ////  +-----+


}