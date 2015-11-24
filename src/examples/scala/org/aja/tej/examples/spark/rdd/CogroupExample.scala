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
object CogroupExample extends App {

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)
    val a = sc.parallelize(List(1,2,1,3), 1)
    val b = a.map((_,"b")) //((1,b), (2,b), (1,b), (3,b))
    val c = a.map((_,"c")) //((1,c), (2,c), (1,c), (3,c))

    b.cogroup(c).collect.foreach(println)
    //(1,(CompactBuffer(b, b),CompactBuffer(c, c)))
    //(3,(CompactBuffer(b),CompactBuffer(c)))
    //(2,(CompactBuffer(b),CompactBuffer(c)))

    println("----------------------------------------------------")

    val x = sc.parallelize(List((1,"apple"), (2,"banana"), (3,"orange"), (4, "kiwi")), 2)
    val y = sc.parallelize (List((5,"computer"), (1,"laptop"), (1,"desktop"), (4,"iPad")), 2)

    x.cogroup(y).collect.foreach(println)
    //(4,(CompactBuffer(kiwi),CompactBuffer(iPad)))
    //(2,(CompactBuffer(banana),CompactBuffer()))
    //(1,(CompactBuffer(apple),CompactBuffer(laptop, desktop)))
    //(3,(CompactBuffer(orange),CompactBuffer()))
    //(5,(CompactBuffer(),CompactBuffer(computer)))
    println("----------------------------------------------------")
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
