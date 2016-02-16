package org.aja.tej.examples.sparksql.sql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql._
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._
import org.apache.spark.{SparkConf, SparkContext}

//
// An example of the experimental User Defined Aggregation Function mechanism
// added in Spark 1.5.0.
//
/**
 * Created by mageswaran on 24/1/16.
 */
object UDAF {

  //
  // A UDAF that sums sales over $500
  //
  private class ScalaAggregateFunction extends UserDefinedAggregateFunction {

    // an aggregation function can take multiple arguments in general. but
    // this one just takes one
    def inputSchema: StructType =
      new StructType().add("sales", DoubleType)

    // the aggregation buffer can also have multiple values in general but
    // this one just has one: the partial sum
    def bufferSchema: StructType =
      new StructType().add("sumLargeSales", DoubleType)

    // returns just a double: the sum
    def dataType: DataType = DoubleType

    // always gets the same result
    def deterministic: Boolean = true

    // each partial sum is initialized to zero
    def initialize(buffer: MutableAggregationBuffer): Unit = {
      buffer.update(0, 0.0)
    }

    // an individual sales value is incorporated by adding it if it exceeds 500.0
    def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
      val sum = buffer.getDouble(0)
      if (!input.isNullAt(0)) {
        val sales = input.getDouble(0)
        if (sales > 500.0) {
          buffer.update(0, sum + sales)
        }
      }
    }

    // buffers are merged by adding the single values in them
    def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
      buffer1.update(0, buffer1.getDouble(0) + buffer2.getDouble(0))
    }

    // the aggregation buffer just has one value: so return it
    def evaluate(buffer: Row): Any = {
      buffer.getDouble(0)
    }
  }

  def main(args: Array[String]) {
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

    val customerDF = sc.parallelize(customers, 4).toDF("id", "name", "state", "purchaseAmt", "discountAmt")

    val mysum = new ScalaAggregateFunction()

    customerDF.printSchema()

    // register as a temporary table

    customerDF.registerTempTable("customers")

    sqlContext.udf.register("mysum", mysum)

    // now use it in a query
    val sqlResult =
      sqlContext.sql(
        s"""
           | SELECT state, mysum(purchaseAmt) AS bigsales
           | FROM customers
           | GROUP BY state
           """.stripMargin)
    sqlResult.
      printSchema()
    println()

    sqlResult.
      show()

  }
}
