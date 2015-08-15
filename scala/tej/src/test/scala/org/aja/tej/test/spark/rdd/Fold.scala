package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Fold:
Aggregates the values of each partition. The aggregation variable within each partition
is initialized with zeroValue.

 foldByKey[Pair]
Very similar to fold, but performs the folding separately for each key of the RDD. This
function is only available if the RDD consists of two-component tuples.


 */
object Fold {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize ( List (1 ,2 ,3) , 3)
    a . fold (0) ( _ + _ )

    val a1 = sc . parallelize ( List (" dog " , " cat " , " owl " , " gnu " ," ant ") , 2)
    val b1 = a1 . map ( x => ( x . length , x ) )
    b1 . foldByKey ("") ( _ + _ ) . collect

    val a2 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " , " panther " , " eagle ") , 2)
    val b2 = a2 . map ( x => ( x . length , x ) )
    b2 . foldByKey ("") ( _ + _ ) . collect
  }

}
