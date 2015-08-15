package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 leftOuterJoin[Pair]
Performs an left outer join using two key-value RDDs. Please note that the keys must
be generally comparable to make this work correctly.

 lookup[Pair]
Scans the RDD for all keys that match the provided value and returns their values as a
Scala sequence.

 */
object LeftOuterJoinExample {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize ( List (" dog " , " salmon " , " salmon " , " rat " , " elephant ") , 3)
    val b = a . keyBy ( _ . length )
    val c = sc . parallelize ( List (" dog " ," cat " ," gnu " ," salmon " ," rabbit " ," turkey" ," wolf " ," bear " ," bee ") , 3)
    val d = c . keyBy ( _ . length )
    b . leftOuterJoin ( d ) . collect


    val a1 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " , " panther " , " eagle ") , 2)
    val b1 = a1 . map ( x => ( x . length , x ) )
    b1 . lookup (5)


  }
}
