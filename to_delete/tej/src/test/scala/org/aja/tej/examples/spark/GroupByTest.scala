package org.aja.tej.tej.test.spark

/**
 * Created by mageswaran on 9/8/15.
 */

import java.util.Random

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Usage: GroupByTest [numMappers] [numKVPairs] [valSize] [numReducers]
 *
 * Avoid groupByKey when performing an associative reductive operation.
 * For example, rdd.groupByKey().mapValues(_.sum) will produce the same results as rdd.reduceByKey(_ + _).
 * However, the former will transfer the entire dataset across the network, while the latter will compute
 * local sums for each key in each partition and combine those local sums into larger sums after shuffling.
 */
object GroupByTest {
  def main(args: Array[String]) {
    //Initializing the SparCong to run on local machine
    val sparkConf = new SparkConf().setAppName("GroupBy Test").setMaster("local[4]")

    /**
     *  Set the parameters
     *  Number of mappers as 100, number of KeyValue paris as 10000, value size as 1000 and number of reducers as 36.
     */
    var numMappers = 100
    /**
     * Key -> Int, Values -> Array[Byte]
     * Number of Array[(Int, Array[Byte]]
     */
    var numKVPairs = 10000
    //Array[Byte] size
    var valSize = 1000
    var numReducers = 36

    val sc = new SparkContext(sparkConf)

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * Size(pairs):
     *   numMappers * Size(mapperArray)
     *   100 * 9.57MB = 957MB ~ 1000MB
     */
    val pairs = sc.parallelize(0 until numMappers, numMappers).flatMap { p =>
      val ranGen = new Random
      /**
       * Size(mapperArray):
       *   numKVPairs * (4 + valSize)
       *   10000 * (1004) = 10040000 Bytes = 9804.6875 KB = 9.57MB ~ 10MB
       */
      var mapperArray = new Array[(Int, Array[Byte])](numKVPairs)

      for (i <- 0 until numKVPairs) {
        val byteArr = new Array[Byte](valSize)
        ranGen.nextBytes(byteArr)
        mapperArray(i) = (ranGen.nextInt(Int.MaxValue), byteArr)
      }
      mapperArray
    }.cache //cache the mapperArray in each Mapper
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    println(s"Logical plan: ${pairs.toDebugString}")

    println(s"Totsl number of pairs: ${pairs.count}")

    /**
     * Number of pairs/Reducers:
     *   (numMappers * numKVPairs) / numReducers
     *   (100 * 10000) / 36 = 1000000 / 36 = 27777.7 ~ 27777 pairs
     * Size of each reducer:
     *   Size(pairs) / numReducers
     *   1000MB / 36 = 27.77 MB
     */
    println(s"Number of pairs with unique key: ${pairs.groupByKey(numReducers).count}")

    sc.stop()
  }
}