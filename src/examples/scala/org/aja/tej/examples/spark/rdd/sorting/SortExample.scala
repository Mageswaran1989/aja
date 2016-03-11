package org.aja.tej.examples.spark.rdd.sorting

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
sortByKey[Ordered]
This function sorts the input RDD’s data and stores it in a new RDD. The output RDD
is a shuffled RDD because it stores data that is output by a reducer which has been
shuffled. The implementation of this function is actually very clever. First, it uses a
range partitioner to partition the data in ranges within the shuffled RDD. Then it sorts
these ranges individually with mapPartitions using standard sort mechanisms.

 */

/**
 * Refere org.aja.tej.examples.usecases.wordcount.BibleWordCount for usage
 */

object SortExample  extends App {

  def useCases(sc: SparkContext) = {

    val a = sc.parallelize(List("dog","cat","owl","gnu","ant"), 2)
    val b = sc.parallelize(1 to a.count.toInt, 2)
    val c = a.zip(b)
    c.sortByKey(true).collect
    c.sortByKey(false).collect

    val a1 = sc.parallelize(1 to 100, 5)
    val b1 = a1.cartesian(a1)
    val c1 = sc.parallelize(b1.takeSample(true, 5, 13), 2)
    val d1 = c1.sortByKey(false)

  }
  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
