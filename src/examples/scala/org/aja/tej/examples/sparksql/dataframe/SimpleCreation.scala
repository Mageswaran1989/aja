package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}

/**
  * Created by mdhandapani on 3/2/16.
  */
object SimpleCreation extends App{

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  val customers = Seq(
    (1, "Mageswaran", "TN", 15000.00, 150),
    (2, "Michael", "JR", 24000.00, 300),
    (3, "Antony Leo", "TN", 10000.00, 50),
    (4, "Arun", "TN", 18000.00, 90),
    (5, "Venkat", "ANDRA", 5000.00, 0),
    (6, "Sathis", "TN", 150000.00, 3000)
  )

  val customerRDDRows = sc.parallelize(customers, 4)

  //Method 1: By specifying the column names
  val customerDF = customerRDDRows.toDF("id", "name", "state", "purchaseAmt", "discountAmt")

  customerDF.printSchema()

  customerDF.show()


  //Method 2: Create DataFrame from an RDD[Row] and a syntehtic schema
  val customersRows = Seq(
    Row(1, "Mageswaran", "TN", 15000.00, 150.00),
    Row(2, "Michael", "JR", 24000.00, 300.0),
    Row(3, "Antony Leo", "TN", 10000.00, 50.00),
    Row(4, "Arun", "TN", 18000.00, 90.00),
    Row(5, "Venkat", "ANDRA", 5000.00, 0.00),
    Row(6, "Sathis", "TN", 150000.00, 3000.00)
  )

  val schema = StructType (
    Seq(
      StructField("id", IntegerType, true),
      StructField("name", StringType, true),
      StructField("state", StringType, true),
      StructField("purchaseAmt", DoubleType, true),
      StructField("discountAmt", DoubleType, true)
    )
  )

  val customersRowsRDD = sc.parallelize(customersRows, 4)
  val customerDF2 = sqlContext.createDataFrame(customersRowsRDD, schema)

  customerDF2.printSchema()

  customerDF2.show()

}
