package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 reduce
This function provides the well-known reduce functionality in Spark. Please note that
any function f you provide, should be commutative in order to generate reproducible
results.

 reduceByKey[Pair] , reduceByKeyLocally[Pair] ,
reduceByKeyToDriver[Pair]
Very similar to reduce, but performs the reduction separately for each key of the RDD.
This function is only available if the RDD consists of two-component tuples.

 */
object ReduceExample {

  def useCases(sc: SparkContext) = {
    val a = sc.parallelize (1 to 100 , 3)
    a.reduce ( _ + _ )

    val a1 = sc . parallelize ( List (" dog " , " cat " , " owl " , " gnu " , " ant ") ,2)
    val b1 = a1 . map ( x => ( x . length , x ) )
    b1 . reduceByKey ( _ + _ ) . collect

    val a2 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " , " panther " , "eagle ") , 2)
    val b2 = a2 . map ( x => ( x . length , x ) )
    b2 . reduceByKey ( _ + _ ) . collect




  }
}
