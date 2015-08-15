package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
subtract
Performs the well known standard set subtraction operation: A \ B

 subtractByKey[Pair]
Very similar to subtract, but instead of supplying a function, the key-component of each
pair will be automatically used as criterion for removing items from the first RDD.

 sum[Double] , sumApprox[Double]
Computes the sum of all values contained in the RDD. The approximate version of the
function can finish somewhat faster in some scenarios. However, it trades accuracy for
speed.

 */
object SubractSumExample {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize (1 to 9 , 3)
    val b = sc . parallelize (1 to 3 , 3)
    val c = a . subtract ( b )
    c . collect

    val a1 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " ," spider " , "eagle ") , 2)

    val b1 = a1 . keyBy ( _ . length )
    val c1 = sc . parallelize ( List (" ant " , " falcon " , " squid ") , 2)
    val d1 = c1 . keyBy ( _ . length )
    b1.subtractByKey ( d1 ) . collect


    val x = sc . parallelize ( List (1.0 , 2.0 , 3.0 , 5.0 , 20.0 , 19.02 , 19.29 ,
      11.09 , 21.0) , 2)
    x . sum


  }
}
