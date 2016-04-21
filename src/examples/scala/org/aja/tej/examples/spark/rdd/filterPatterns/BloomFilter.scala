package org.aja.tej.examples.spark.rdd.filterPatterns

import breeze.util.BloomFilter
import org.aja.tej.utils.TejUtils

/**
  * Created by mdhandapani on 11/3/16.
  */
object BloomFilterExample extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val nums = List(1 to 20: _*).map(_.toString)
  val rdd = sc.parallelize(nums,5)


  val bf = rdd.mapPartitions{ iter =>
    val bf = BloomFilter.optimallySized[String](10000, 0.0001)
    iter.foreach(i => bf += i)
    Iterator(bf)
  }.reduce(_ | _)

  println(bf.contains("5"))
  println(bf.contains("31"))
}
