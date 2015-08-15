package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Similar to collect, but works on key-value RDDs and converts them into Scala maps to
preserve their key-value structure.
[Pair]
 */
object CollectAsMapExample {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize ( List (1 , 2 , 1 , 3) , 1)
    val b = a . zip ( a )
    b . collectAsMap
  }
}
