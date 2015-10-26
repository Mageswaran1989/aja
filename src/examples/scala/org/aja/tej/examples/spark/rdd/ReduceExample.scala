package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 reduce
This function provides the well-known reduce functionality in Spark. Please note that
any function f you provide, should be commutative in order to generate reproducible
results.

 reduceByKey[Pair] , reduceByKeyLocally[Pair] ,
reduceByKeyToDriver[Pair]
Very similar to reduce, but performs the reduction separately for each key of the RDD.
This function is only available if the RDD consists of two-component tuples.

Avoid reduceByKey When the input and output value types are different
For example, consider writing a transformation that finds all the unique strings corresponding to each key.
One way would be to use map to transform each element into a Set and then combine the Sets with reduceByKey

  rdd.map(kv => (kv._1, new Set[String]() + kv._2))
      .reduceByKey(_ ++ _)
  This code results in tons of unnecessary object creation because a new set must be allocated for each record.
  It’s better to use aggregateByKey, which performs the map-side aggregation more efficiently:

  val zero = new collection.mutable.Set[String]()
     rdd.aggregateByKey(zero)(
    (set, v) => set += v,
    (set1, set2) => set1 ++= set2)
 */
object ReduceExample  extends App {

  def useCases(sc: SparkContext) = {
    val a = sc.parallelize (1 to 100 , 3)
    a.reduce ( _ + _ )

    val a1 = sc . parallelize ( List (" dog " , " cat " , " owl " , " gnu " , " ant ") ,2)
    val b1 = a1 . map ( x => ( x . length , x ) )
    b1 . reduceByKey ( _ + _ ) . collect

    val a2 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " , " panther " , "eagle ") , 2)
    val b2 = a2 . map ( x => ( x . length , x ) )
    b2 . reduceByKey ( _ + _ ) . collect

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}
