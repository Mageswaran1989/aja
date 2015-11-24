package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
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
 */
object ForEachExample  extends App {

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)
    val c = sc.parallelize ( List ("cat","dog","tiger","lion","gnu","crocodile","ant","whale","dolphin","spider") , 3)
    c.foreach(x => println(x+"'s are yummy"))
//    cat's are yummy
//    lion's are yummy
//    ant's are yummy
//    gnu's are yummy
//    crocodile's are yummy
//    dog's are yummy
//    whale's are yummy
//    dolphin's are yummy
//    tiger's are yummy
//    spider's are yummy

    val b = sc.parallelize(List(1,2,3,4,5,6,7,8,9),3)
    b.foreachPartition(x => println(x.reduce(_+_)))
//    6
//    24
//    15
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}
