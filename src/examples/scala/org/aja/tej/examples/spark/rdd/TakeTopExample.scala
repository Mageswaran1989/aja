package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
take
Extracts the first n items of the RDD and returns them as an array. (Note: This sounds
very easy, but it is actually quite a tricky problem for the implementors of Spark because
the items in question can be in many different partitions.)

takeOrdered
Orders the data items of the RDD using their inherent implicit ordering function and
returns the first n items as an array.

takeSample
Behaves different from sample in the following respects:
• It will return an exact number of samples (Hint: 2nd parameter).
• It returns an Array instead of RDD.
• It internally randomizes the order of the items returned.

 top
Utilizes the implicit ordering of T to determine the top k values and returns them as an
array.

 */
object TakeTopExample {

  def useCases(sc: SparkContext) = {
    val b = sc.parallelize(List(" dog ", " cat ", " ape ", " salmon ", " gnu "), 2)
    b.take(2)

    val b1 = sc . parallelize ( List (" dog " , " cat " , " ape " , " salmon " , " gnu ") , 2)
    b1. takeOrdered (2)

    val x = sc . parallelize (1 to 1000 , 3)
    x . takeSample ( true , 100 , 1)

    val c = sc . parallelize ( Array (6 , 9 , 4 , 7 , 5 , 8) , 2)
    c . top (2)

  }
}
