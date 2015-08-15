package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
foreach
Executes an parameterless function for each data item.

foreachPartition
Executes an parameterless function for each partition. Access to the data items contained
in the partition is provided via the iterator argument.

 foreachWith
Similar to foreach, but allows accessing the partition index or a derivative of the partition
index from within the function.

 */
object ForEachExample {

  def useCases(sc: SparkContext) = {
    val c = sc . parallelize ( List (" cat " , " dog " , " tiger " , " lion " , " gnu " , " crocodile " , " ant " , " whale " , " dolphin " , " spider ") , 3)
    c . foreach ( x => println ( x + " s are yummy ") )


    val b = sc . parallelize ( List (1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 ,9) , 3)
    b . foreachPartition ( x => println ( x . reduce ( _ + _ ) ) )

    val a = sc . parallelize (1 to 9 , 3)
    //use mapPartitionsWithIndex and foreach
    a . foreachWith ( i => i ) (( x , i ) => if ( x % 2 == 1 && i % 2 == 0) println ( x ))

  }

}
