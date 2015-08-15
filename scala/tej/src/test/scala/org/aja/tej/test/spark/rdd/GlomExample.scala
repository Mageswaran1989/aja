package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 glom:
Assembles an array that contains all elements of the partition and embeds it in an RDD.

 */
object GlomExample {

  def useCases(sc: SparkContext) = {

    val a = sc . parallelize (1 to 100 , 3)
    a . glom . collect // Array [ Array [ Int ]]

  }
}
