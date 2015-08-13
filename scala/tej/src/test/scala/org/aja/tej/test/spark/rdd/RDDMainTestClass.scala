package org.aja.tej.test.spark.rdd

import org.aja.tej.utils.Utils._
/**
 * Created by mageswaran on 12/8/15.
 */
object RDDMainTestClass {

  def main(args: Array[String]) {
    val sc = getSparkContext("RDDMainTestClass")
    val rddRawData = getRawData(sc, "data/datascience.stackexchange.com/Posts.xml")

    val aggregateExample = new AggregateExample(sc)
    val cartesianExample = new CartesianExample(sc)
    CheckPointExample.useCases(sc)
  }
}
