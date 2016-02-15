package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.examples.sparksql.dataframe.DataFrameBasics.CustomerDetails
import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.{SQLContext,Column}
import org.apache.spark.sql.GroupedData
import org.apache.spark.sql.functions._

/**
  * Created by mdhandapani on 6/1/16.
  */
object Aggregation  extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._ //To convert RDD -> DF

  val sessionDF = Seq(
    ("day1", "user1", "session1", 100.0),
    ("day1", "user1", "session2", 200.0),
    ("day1", "user1", "session3", 300.0),
    ("day1", "user1", "session4", 400.0),
    ("day2", "user1", "session4", 90.0)
  ).toDF("day", "userId", "sessionId", "purchaseTotal")

  val groupedSessions = sessionDF.groupBy("day", "userId", "sessionId")

  val groupedSessionsDF = groupedSessions.agg(countDistinct("sessionId"), sum("purchaseTotal"))

  //val groupedSessionsDF1 = groupedSessions.agg($"day", $"userId", countDistinct("sessionId") -> "count", sum("purchaseTotal") -> "sum")

  groupedSessionsDF.foreach(println)
//    [Stage 1:==============>                                         (52 + 4) / 200][day2,user1,session4,1,90.0]
//  [Stage 1:======================>                                 (80 + 4) / 200][day1,user1,session1,1,100.0]
//  [day1,user1,session3,1,300.0]
//  [day1,user1,session2,1,200.0]
//  [Stage 1:============================>                          (103 + 4) / 200][day1,user1,session4,1,400.0]

  ////////////////////////////////////////////////////////

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

  // groupBy() produces a GroupedData, and you can't do much with
  // one of those other than aggregate it -- you can't even print it

  // basic form of aggregation assigns a function to
  // each non-grouped column -- you map each column you want
  // aggregated to the name of the aggregation function you want
  // to use
  //
  // automatically includes grouping columns in the DataFrame

  println("*** basic form of aggregation")
  customerDF.groupBy("state").agg("discountAmt" -> "max").show()
//  +-----+----------------+
//  |state|max(discountAmt)|
//  +-----+----------------+
//  |   TN|          3000.0|
//  |ANDRA|             0.0|
//  |   JR|           300.0|
//  +-----+----------------+

  // you can turn of grouping columns using the SQL context's
  // configuration properties

  println("*** this time without grouping columns")
  sqlContext.setConf("spark.sql.retainGroupColumns", "false")
  customerDF.groupBy("state").agg("discountAmt" -> "max").show()
//  +----------------+
//  |max(discountAmt)|
//  +----------------+
//  |          3000.0|
//  |             0.0|
//  |           300.0|
//  +----------------+

  //
  // When you use $"somestring" to refer to column names, you use the
  // very flexible column-based version of aggregation, allowing you to make
  // full use of the DSL defined in org.apache.spark.sql.functions --
  // this version doesn't automatically include the grouping column
  // in the resulting DataFrame, so you have to add it yourself.
  //

  println("*** Column based aggregation")
  // you can use the Column object to specify aggregation
  customerDF.groupBy("state").agg(max($"discountAmt")).show()

//  +----------------+
//  |max(discountAmt)|
//  +----------------+
//  |          3000.0|
//  |             0.0|
//  |           300.0|
//  +----------------+

  println("*** Column based aggregation plus grouping columns")
  // but this approach will skip the grouped columns if you don't name them
  customerDF.groupBy("state").agg($"state", max($"discountAmt")).show()

//  +-----+----------------+
//  |state|max(discountAmt)|
//  +-----+----------------+
//  |   TN|          3000.0|
//  |ANDRA|             0.0|
//  |   JR|           300.0|
//  +-----+----------------+


  // Think of this as a user-defined aggregation function -- written in terms
  // of more primitive aggregations
  def stddevFunc(c: Column): Column =
    sqrt(avg(c * c) - (avg(c) * avg(c)))

  println("*** Sort-of a user-defined aggregation function")
  customerDF.groupBy("state").agg($"state", stddevFunc($"discountAmt")).show()

//  +-----+--------------------------------------------------------------------------------+
//  |state|SQRT((avg((discountAmt * discountAmt)) - (avg(discountAmt) * avg(discountAmt))))|
//  +-----+--------------------------------------------------------------------------------+
//  |   TN|                                                              1257.6838831757366|
//  |ANDRA|                                                                             0.0|
//  |   JR|                                                                             0.0|
//  +-----+--------------------------------------------------------------------------------+

  // there are some special short cuts on GroupedData to aggregate
  // all numeric columns
  println("*** Aggregation short cuts")
  customerDF.groupBy("state").count().show()
//  +-----+
//  |count|
//  +-----+
//  |    4|
//  |    1|
//  |    1|
//  +-----+


}


/*
set.
[Stage 1:==================>                                     (67 + 4) / 200][day2,user1,session4,1,90.0]
[Stage 1:========================>                               (89 + 5) / 200][day1,user1,session1,1,100.0]
[day1,user1,session2,1,200.0]
[day1,user1,session3,1,300.0]
[day1,user1,session4,1,400.0]
*** basic form of aggregation
+-----+----------------+
|state|max(discountAmt)|
+-----+----------------+
|   TN|          3000.0|
|ANDRA|             0.0|
|   JR|           300.0|
+-----+----------------+

*** this time without grouping columns
[Stage 9:=====================================================> (194 + 4) / 199]+----------------+
|max(discountAmt)|
+----------------+
|          3000.0|
|             0.0|
|           300.0|
+----------------+

*** Column based aggregation
[Stage 13:==============================================>       (173 + 4) / 199]+----------------+
|max(discountAmt)|
+----------------+
|          3000.0|
|             0.0|
|           300.0|
+----------------+

*** Column based aggregation plus grouping columns
+-----+----------------+
|state|max(discountAmt)|
+-----+----------------+
|   TN|          3000.0|
|ANDRA|             0.0|
|   JR|           300.0|
+-----+----------------+

*** Sort-of a user-defined aggregation function
+-----+--------------------------------------------------------------------------------+
|state|SQRT((avg((discountAmt * discountAmt)) - (avg(discountAmt) * avg(discountAmt))))|
+-----+--------------------------------------------------------------------------------+
|   TN|                                                              1257.6838831757366|
|ANDRA|                                                                             0.0|
|   JR|                                                                             0.0|
+-----+--------------------------------------------------------------------------------+

*** Aggregation short cuts
+-----+
|count|
+-----+
|    4|
|    1|
|    1|
+-----+
 */
