package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 union, ++
Performs the standard set operation: A ∪ B

 */
object UnionExample {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize (1 to 3 , 1)
    val b = sc . parallelize (5 to 7 , 1)
    ( a ++ b ) . collect

  }
}
