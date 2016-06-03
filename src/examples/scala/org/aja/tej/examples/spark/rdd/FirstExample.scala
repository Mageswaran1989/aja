package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Looks for the very first data item of the RDD and returns it.

 */
object FirstExample extends App{

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)
    val c = sc.parallelize(List("Gnu","Cat","Rat","Dog"), 2)
    println(c.first)//Gnu
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}
