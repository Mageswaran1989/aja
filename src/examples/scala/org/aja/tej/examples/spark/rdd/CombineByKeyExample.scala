package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Very efficient implementation that combines the values of a RDD consisting of two-
component tuples by applying multiple aggregators one after another.

[Pair]

Turns an RDD[(K, V)] into a result of type RDD[(K, C)]
RDD[(K, V)] -> RDD[(K, C)]

   * - `createCombiner`, which turns a V into a C (e.g., creates a one-element list)
   * - `mergeValue`, to merge a V into a C (e.g., adds it to the end of a list)
   * - `mergeCombiners`, to combine two C's into a single one.

 */
object CombineByKeyExample extends App{

  def useCases(sc:SparkContext) = {
    println(this.getClass.getSimpleName)
    val value = sc . parallelize ( List ("dog","cat","gnu","salmon","rabbit","turkey","wolf","bear","bee"),3)
    println("value:" )
    value.foreach(println)
    val key = sc.parallelize(List(1 ,1 ,2 ,2 ,2 ,1 ,2 ,2 ,2), 3)
    val keyValuePairs = key.zip(value)
    keyValuePairs.foreach(println) //RDD[(Int, String)]
    //(2,wolf)
    //(2,bear)
    //(2,salmon)
    //(1,dog)
    //(2,rabbit)
    //(1,turkey)
    //(2,bee)
    //(1,cat)
    //(2,gnu)

    // Rdd[(Int, List[String])]
    val d = keyValuePairs.combineByKey(List ( _ ) , //createCombiner i.e for each partition
      (x: List[String], y: String ) => y :: x , //mergeValue i.e mege all values in current partition
      (x: List[String] , y: List[String]) => x ::: y ) //mergeCombiners i.e combine all partition

    println("d partitions :" + d.partitions.length)
    println("Lets see what each partition has: " )

    try {
      d.mapPartitions(iter => Iterator(iter.map(_._2))).collect.foreach(println)
    } catch {
      case e: java.io.NotSerializableException => {
        //??? not working! excepting below prints :)
        println("Caught NotSerializableException")
        d.mapPartitions(iter => Iterator(iter.map(_._2).toList)).collect.foreach(println)
      }
      case exception => { //Found the exception to be SparkException
        println("Unknown exception : " + exception.getClass)
        d.mapPartitions(iter => Iterator(iter.map(_._2).toList)).collect.foreach(println)
        //.toList is necessary as iter.map produces Iterator which is not Serializable
      }
    }
    //    List()
    //    List(List(cat, dog, turkey))
    //    List(List(gnu, rabbit, salmon, bee, bear, wolf))

    d.collect.foreach(println)
    //(1,List(cat, dog, turkey))
    //(2,List(gnu, rabbit, salmon, bee, bear, wolf))
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}

