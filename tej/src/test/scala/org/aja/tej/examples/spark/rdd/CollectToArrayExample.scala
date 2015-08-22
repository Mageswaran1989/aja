package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Converts the RDD into a Scala array and returns it. If you provide a standard map-
function (i.e. f = T ⇒ U ) it will be applied before inserting the values into the result
array.

 */
object CollectToArrayExample {

  def useCases(sc: SparkContext) = {
    val c = sc . parallelize ( List (" Gnu " , " Cat " , " Rat " , " Dog " , " Gnu " , " Rat ") ,
      2)
    c . collect

  }
}
