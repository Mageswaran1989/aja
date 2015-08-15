package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
Allows a RDD to be tagged with a custom name.
 */
object NameExample {

  def useCases(sc: SparkContext) = {
    val y = sc . parallelize (1 to 10 , 10)
    y . name

    y . setName (" Fancy RDD Name ")
    y . name

  }
}
