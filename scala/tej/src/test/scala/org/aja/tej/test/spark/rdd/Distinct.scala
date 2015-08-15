package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
Returns a new RDD that contains each unique value only once.

 */
object Distinct {

  def useCases(sc: SparkContext) = {
    val c = sc . parallelize ( List (" Gnu " , " Cat " , " Rat " ," Dog " , " Gnu " , " Rat ") ,2)
    c . distinct . collect

    val a = sc . parallelize( List (1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9 ,10) )
    a . distinct (2) . partitions . length

    a . distinct (3) . partitions . length

  }

}
