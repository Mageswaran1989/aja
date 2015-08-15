package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 *
 */

/*
Similar to map, but allows emitting more than one item in the map function.

 flatMapValues[Pair]
Very similar to mapValues, but collapses the inherent structure of the values during
mapping.

flatMapWith
Similar to flatMap, but allows accessing the partition index or a derivative of the partition
index from within the flatMap-function.



 */
object FlatMapExample {

  def useCases(sc: SparkContext) = {

    val a = sc . parallelize (1 to 10 , 5)
    a . flatMap (1 to _ ) . collect

    sc . parallelize ( List (1 , 2 , 3) , 2) . flatMap ( x => List (x , x , x ) ) . collect

    // The program below generates a random number of copies ( up to 10) of
    //the items in the list .
    val x = sc . parallelize (1 to 10 , 3)
    x . flatMap ( List . fill ( scala . util . Random . nextInt (10) ) ( _ ) ) . collect

    val a1 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " , " panther " , " eagle ") , 2)
    val b = a1.map ( x => ( x . length , x ) )
    b . flatMapValues (" x " + _ + " x ") . collect

    val a2 = sc . parallelize ( List (1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9) , 3)
    //use mapPartitionsWithIndex and flatMap
    a2 . flatMapWith ( x => x , true ) (( x , y ) => List (y , x ) ) . collect



  }
}
