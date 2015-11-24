package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Similar to collect, but works on key-value RDDs and converts them into Scala maps to
preserve their key-value structure.
[Pair]
 */
object CollectAsMapExample extends  App{

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)
    val a = sc.parallelize(List(1,2,1,3), 1)
    val b = a.zip(a)
    println("b.collectAsMap : " + b.collectAsMap) //b.collectAsMap : Map(2 -> 2, 1 -> 1, 3 -> 3)
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
