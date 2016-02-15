package org.aja.tej.examples.sparksql.dataframe


import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

/**
  * Created by mdhandapani on 3/2/16.
  */
object DataFrameBasics  extends App {


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
    CustomerDetails(6, "Sathis", null, 150000.00, 3000)
  )

  val customerDF = sc.parallelize(customers, 4).toDF()

  println("*** toString() just gives you the schema")
//[id: bigint, name: string, state: string, purchaseAmt: double, discountAmt: double]
  println(customerDF.toString())

  println("*** It's better to use printSchema()")
//  root
//  |-- id: long (nullable = false)
//  |-- name: string (nullable = true)
//  |-- state: string (nullable = true)
//  |-- purchaseAmt: double (nullable = false)
//  |-- discountAmt: double (nullable = false)
  customerDF.printSchema()

  println("*** show() gives you neatly formatted data")
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
  customerDF.show()

  println("*** use select() to choose one column")
//  +---+
//  | id|
//  +---+
//  |  1|
//  |  2|
//  |  3|
//  |  4|
//  |  5|
//  |  6|
//  +---+
  customerDF.select("id").show()

  println("*** use select() for multiple columns")
//  +-----------+-----+
//  |purchaseAmt|state|
//  +-----------+-----+
//  |    15000.0|   TN|
//  |    24000.0|   JR|
//  |    10000.0|   TN|
//  |    18000.0|   TN|
//  |     5000.0|ANDRA|
//  |   150000.0|   TN|
//  +-----------+-----+

  customerDF.select("purchaseAmt", "state").show()

  println("*** use filter() to choose rows")
//  +---+----------+-----+-----------+-----------+
//  | id|      name|state|purchaseAmt|discountAmt|
//  +---+----------+-----+-----------+-----------+
//  |  1|Mageswaran|   TN|    15000.0|      150.0|
//  |  3|Antony Leo|   TN|    10000.0|       50.0|
//  |  4|      Arun|   TN|    18000.0|       90.0|
//  |  6|    Sathis|   TN|   150000.0|     3000.0|
//  +---+----------+-----+-----------+-----------+

  customerDF.filter($"state".equalTo("TN")).show()


  //DataFrame APIs in Action for select

  println("*** use * to select() all columns")
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
  customerDF.select("*").show()

  println("*** select multiple columns")
//  +---+-----------+
//  | id|discountAmt|
//  +---+-----------+
//  |  1|      150.0|
//  |  2|      300.0|
//  |  3|       50.0|
//  |  4|       90.0|
//  |  5|        0.0|
//  |  6|     3000.0|
//  +---+-----------+

  customerDF.select("id", "discountAmt").show()

  println("*** use apply() on DataFrame to create column objects, and select through them")
//  +---+-----------+
//  | id|discountAmt|
//  +---+-----------+
//  |  1|      150.0|
//  |  2|      300.0|
//  |  3|       50.0|
//  |  4|       90.0|
//  |  5|        0.0|
//  |  6|     3000.0|
//  +---+-----------+
  customerDF.select(customerDF("id"), customerDF("discountAmt")).show()

  println("*** use as() on Column to rename")
//  +-----------+--------------+
//  |Customer ID|Total Discount|
//  +-----------+--------------+
//  |          1|         150.0|
//  |          2|         300.0|
//  |          3|          50.0|
//  |          4|          90.0|
//  |          5|           0.0|
//  |          6|        3000.0|
//  +-----------+--------------+
  customerDF.select(customerDF("id").as("Customer ID"),
    customerDF("discountAmt").as("Total Discount")).show()

  println("*** $ as shorthand to obtain Column")
//  +-----------+--------------+
//  |Customer ID|Total Discount|
//  +-----------+--------------+
//  |          1|         150.0|
//  |          2|         300.0|
//  |          3|          50.0|
//  |          4|          90.0|
//  |          5|           0.0|
//  |          6|        3000.0|
//  +-----------+--------------+

  customerDF.select($"id".as("Customer ID"), $"discountAmt".as("Total Discount")).show()

  println("*** use DSL to manipulate values")

//  +---------------+
//  |Double Discount|
//    +---------------+
//  |          300.0|
//  |          600.0|
//  |          100.0|
//  |          180.0|
//  |            0.0|
//  |         6000.0|
//  +---------------+
  customerDF.select(($"discountAmt" * 2).as("Double Discount")).show()

//  +--------------+
//  |After Discount|
//  +--------------+
//  |       14850.0|
//  |       23700.0|
//  |        9950.0|
//  |       17910.0|
//  |        5000.0|
//  |      147000.0|
//  +--------------+
  customerDF.select(
    ($"purchaseAmt" - $"discountAmt").as("After Discount")).show()

  println("*** use * to select() all columns and add more")
//  +---+----------+-----+-----------+-----------+-----+
//  | id|      name|state|purchaseAmt|discountAmt|newID|
//  +---+----------+-----+-----------+-----------+-----+
//  |  1|Mageswaran|   TN|    15000.0|      150.0|    1|
//  |  2|   Michael|   JR|    24000.0|      300.0|    2|
//  |  3|Antony Leo|   TN|    10000.0|       50.0|    3|
//  |  4|      Arun|   TN|    18000.0|       90.0|    4|
//  |  5|    Venkat|ANDRA|     5000.0|        0.0|    5|
//  |  6|    Sathis|   TN|   150000.0|     3000.0|    6|
//  +---+----------+-----+-----------+-----------+-----+
  customerDF.select(customerDF("*"), $"id".as("newID")).show()

  println("*** use lit() to add a literal column")
//  +---+----------+--------+
//  | id|      name|FortyTwo|
//  +---+----------+--------+
//  |  1|Mageswaran|      42|
//  |  2|   Michael|      42|
//  |  3|Antony Leo|      42|
//  |  4|      Arun|      42|
//  |  5|    Venkat|      42|
//  |  6|    Sathis|      42|
//  +---+----------+--------+
  customerDF.select($"id", $"name", lit(42).as("FortyTwo")).show()

  println("*** use array() to combine multiple results into a single array column")
//  +---+--------------------+
//  | id|               Stuff|
//  +---+--------------------+
//  |  1|[Mageswaran, TN, ...|
//  |  2|[Michael, JR, hello]|
//  |  3|[Antony Leo, TN, ...|
//  |  4|   [Arun, TN, hello]|
//  |  5|[Venkat, ANDRA, h...|
//  |  6| [Sathis, TN, hello]|
//    +---+--------------------+
  customerDF.select($"id", array($"name", $"state", lit("hello")).as("Stuff")).show()

  println("*** use rand() to add random numbers between 0.0 and 1.0 inclusive ")
//  +---+-------------------+
//  | id|                  r|
//  +---+-------------------+
//  |  1| 0.7572661909962807|
//  |  2|0.03528305261129272|
//  |  3|  0.766871164475284|
//  |  4|0.21273894980149388|
//  |  5|0.26240350624366104|
//  |  6| 0.7924567526350429|
//  +---+-------------------+
  customerDF.select($"id", rand().as("r")).show()
}

