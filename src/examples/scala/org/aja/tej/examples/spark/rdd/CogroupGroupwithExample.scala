package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */

/*
A very powerful set of functions that allow grouping up to 3 key-value RDDs together
using their keys.
[Pair]
 */
object CogroupGroupwithExample extends App {

  def useCases(sc: SparkContext) = {

    val a = sc.parallelize( List (1 ,2 , 1 , 3) , 1)
    val b = a.map(( _ , " b "))
    val c = a.map(( _ , " c "))

    b.cogroup(c).collect

    println("CogroupGroupwithExample: ")
    println(b.cogroup(c).collect)

    val x = sc.parallelize(List ((1 , "apple"), (2 , "banana") , (3 , "orange"), (4 , "kiwi") ) , 2)
    val y = sc.parallelize ( List ((5 , "computer") , (1 , "laptop") , (1 , "desktop") , (4 , "iPad ") ) , 2)
    x . cogroup ( y ) . collect

    println(x . cogroup ( y ) . collect)
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
