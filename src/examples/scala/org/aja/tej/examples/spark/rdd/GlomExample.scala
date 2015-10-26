package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 glom:
Assembles an array that contains all elements of the partition and embeds it in an RDD.

 */
object GlomExample  extends App {

  def useCases(sc: SparkContext) = {

    val a = sc . parallelize (1 to 100 , 3)
    a . glom . collect // Array [ Array [ Int ]]

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
