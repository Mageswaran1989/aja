package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Very efficient implementation that combines the values of a RDD consisting of two-
component tuples by applying multiple aggregators one after another.

[Pair]
 */
object CombineByKeyExample {

  def useCases(sc:SparkContext) = {
    val a = sc . parallelize ( List (" dog " ," cat " ," gnu " ," salmon " ," rabbit " ," turkey " ," wolf " ," bear " ," bee ") , 3)
    val b = sc . parallelize ( List (1 ,1 ,2 ,2 ,2 ,1 ,2 ,2 ,2) , 3)
    val c = b . zip ( a )
    val d = c.combineByKey(List ( _ ) , (x: List [ String ], y : String ) => y :: x ,(x : List [ String ] , y : List [ String ]) => x ::: y )

    d . collect

  }
}
