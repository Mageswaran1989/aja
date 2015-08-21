package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
keyBy
Constructs two-component tuples (key-value pairs) by applying a function on each data
item. The result of the function becomes the key and the original data item becomes
the value of the newly created tuples.

 keys[Pair]
Extracts the keys from all contained tuples and returns them in a new RDD.

 */
object KeyExample {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize ( List (" dog " , " salmon " ," salmon " , " rat " , " elephant") , 3)
    val b = a . keyBy ( _ . length )
    b . collect

    val a1 = sc . parallelize ( List (" dog " , " tiger " ," lion " , " cat " , " panther " , "eagle ") , 2)
    val b1 = a1 . map ( x => ( x . length , x ) )
    b1 . keys . collect
  }
}
