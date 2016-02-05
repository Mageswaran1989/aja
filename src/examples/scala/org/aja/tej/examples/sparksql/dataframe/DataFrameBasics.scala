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
    CustomerDetails(6, "Sathis", "TN", 150000.00, 3000)
  )

  val customerDF = sc.parallelize(customers, 4).toDF()

  println("*** toString() just gives you the schema")

  println(customerDF.toString())

  println("*** It's better to use printSchema()")

  customerDF.printSchema()

  println("*** show() gives you neatly formatted data")

  customerDF.show()

  println("*** use select() to choose one column")

  customerDF.select("id").show()

  println("*** use select() for multiple columns")

  customerDF.select("purchaseAmt", "state").show()

  println("*** use filter() to choose rows")

  customerDF.filter($"state".equalTo("TN")).show()


  //DataFrame APIs in Action for select

  println("*** use * to select() all columns")

  customerDF.select("*").show()

  println("*** select multiple columns")

  customerDF.select("id", "discountAmt").show()

  println("*** use apply() on DataFrame to create column objects, and select though them")

  customerDF.select(customerDF("id"), customerDF("discountAmt")).show()

  println("*** use as() on Column to rename")

  customerDF.select(customerDF("id").as("Customer ID"),
    customerDF("discountAmt").as("Total Discount")).show()

  println("*** $ as shorthand to obtain Column")

  customerDF.select($"id".as("Customer ID"), $"discountAmt".as("Total Discount")).show()

  println("*** use DSL to manipulate values")

  customerDF.select(($"discountAmt" * 2).as("Double Discount")).show()

  customerDF.select(
    ($"purchaseAmt" - $"discountAmt").as("After Discount")).show()

  println("*** use * to select() all columns and add more")

  customerDF.select(customerDF("*"), $"id".as("newID")).show()

  println("*** use lit() to add a literal column")

  customerDF.select($"id", $"name", lit(42).as("FortyTwo")).show()

  println("*** use array() to combine multiple results into a single array column")

  customerDF.select($"id", array($"name", $"state", lit("hello")).as("Stuff")).show()

  println("*** use rand() to add random numbers between 0.0 and 1.0 inclusive ")

  customerDF.select($"id", rand().as("r")).show()
}

