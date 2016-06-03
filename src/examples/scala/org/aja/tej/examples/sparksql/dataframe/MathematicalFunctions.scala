package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.examples.sparksql.datasets.DataFrameEnv

import scala.util.Random

/**
 * Created by mageswaran on 27/11/15.
 *
 * Reference: https://databricks.com/blog/2015/06/02/statistical-and-mathematical-functions-with-dataframes-in-spark.html
 */
object MathematicalFunctions extends App with DataFrameEnv {

  val df = sqlContext.range(0, 10)
  df.printSchema()
  df.show()

  df.select("id")


}
