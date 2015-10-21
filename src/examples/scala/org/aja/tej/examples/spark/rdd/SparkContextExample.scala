package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
///Returns the SparkContext that was used to create the RDD.

object SparkContextExample {

  def useCases(sc: SparkContext) = {
    val c = sc . parallelize ( List (" Gnu " , " Cat " , " Rat " , " Dog ") ,2)
    c . context
  }

}
