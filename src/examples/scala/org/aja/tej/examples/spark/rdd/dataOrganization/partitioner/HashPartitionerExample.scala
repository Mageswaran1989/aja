package org.aja.tej.examples.spark.rdd.dataOrganization.partitioner

import org.aja.tej.utils.TejUtils
import org.apache.spark.{HashPartitioner, SparkContext}

/**
 * Created by mageswaran on 21/11/15.
 */
/*
RDD is distributed this means it is split on some number of parts. Each of this partitions is potentially on different
machine. Hash partitioner with arument numPartitions choses on what partition to place pair (key, value)
in following way:
1. Creates exactly numPartitions partitions.
2. Places (key, value) in partition with number Hash(key) % numPartition

 */

/*
Why Use a Partitioner?
In cluster computing, the central challenge is to minimize network traffic.
When the data is key-value oriented, partitioning becomes imperative because
for subsequent transformations on the RDD, there’s a fair amount of shuffling
of data across the network. If similar keys or range of keys are stored in the
same partition then the shuffling is minimized and the processing becomes substantially fast.

Transformations that require shuffling of data across worker nodes greatly
benefit from partitioning. Such transformations are cogroup, groupWith, join,
leftOuterJoin, rightOuterJoin, groupByKey, reduceByKey, combineByKey andlookup.
 */
object HashPartitionerExample extends App {

  def useCases(sc: SparkContext): Unit = {

    println(this.getClass.getSimpleName) //HashPartitionExample$

    val rddData = sc.parallelize(for {
      x <- 1 to 3
      y <- 1 to 2
    } yield (x, None), 8) //(key, value) with 8 partitions

    println("rddData partitons: " + rddData.partitions.length) //rdd partitons: 8
    println("rddData contents: " + rddData.collect.foreach(print)) // (1,None)(1,None)(2,None)(2,None)(3,None)(3,None)
    println("rddData partitioner: " + rddData.partitioner) //rddData partitioner: None


    //mapPartitions U -> T of iterator
    val numElementsPerPartition = rddData.mapPartitions(iter => Iterator(iter.length))

    println("numElementsPerPartition : " + numElementsPerPartition.collect().foreach(print)) //0 1 1 1 0 1 1 1
    //so 2 of 8 partition is empty

    println("----------------------------------------------------------")

    val rddDataWith1Partition = rddData.partitionBy(new HashPartitioner(1))
    println("rddDataWith1Partition partitons: " + rddDataWith1Partition.partitions.length) //rddDataWith1Partition partitons: 1
    println("rddDataWith1Partition contents: " + rddDataWith1Partition.collect.foreach(print))
    //(1,None)(1,None)(2,None)(2,None)(3,None)(3,None)rddDataWith1Partition conten
    //Since we have only one partition it contains all elements

    println("numElementsPerPartition : " +
      rddDataWith1Partition.mapPartitions(iter => Iterator(iter.length)).collect().foreach(println)) //6

    println("----------------------------------------------------------")

    val rddDataWith2Partition = rddData.partitionBy(new HashPartitioner(2))
    println("rddDataWith2Partition partitons: " + rddDataWith2Partition.partitions.length) //rddDataWith1Partition partitons: 2
    println("rddDataWith2Partition contents: " + rddDataWith2Partition.collect.foreach(print))
    //(2,None)(2,None)(1,None)(1,None)(3,None)(3,None)
    ////i.e partitionBy -> ShuffleRDD -> partition for current element is selected by (k.hashcode % numPartition)

    println("numElementsPerPartition : " +
      rddDataWith2Partition.mapPartitions(iter => Iterator(iter.length)).collect().foreach(println)) //2 4
    //Since rdd is partitioned by key data won't be distributed uniformly anymore:
    //Lets see why?
    //Because with have three keys and only two different values of hashCode mod numPartitions(2) there is nothing unexpected
    println("(1 to 3).map((k: Int) => (k, k.hashCode, k.hashCode % 2)) : " + (1 to 3).map((k: Int) => (k, k.hashCode, k.hashCode % 2)))
    //Vector((1,1,1), (2,2,0), (3,3,1))

    //Lets confirm in our rdd
    println("Keys in each partition" + rddDataWith2Partition.mapPartitions(iter => Iterator(iter.map(_._1).toSet)).collect.foreach(println))
    //Set(2)
    //Set(1,3)

    println("----------------------------------------------------------")

    val rddDataWith10Partition = rddData.partitionBy(new HashPartitioner(10))
    println("rddDataWith10Partition partitons: " + rddDataWith10Partition.partitions.length) //rddDataWith1Partition partitons: 10
    println("rddDataWith2Partition contents: " + rddDataWith10Partition.collect.foreach(print))
    //(1,None)(1,None)(2,None)(2,None)(3,None)(3,None)
    ////i.e partitionBy -> ShuffleRDD -> partition for current element is selected by (k.hashcode % numPartition)

    println("numElementsPerPartition : " +
      rddDataWith10Partition.mapPartitions(iter => Iterator(iter.length)).collect().foreach(println))
    //0   2     2    2    0    0    0    0    0    0

    println("(1 to 3).map((k: Int) => (k, k.hashCode, k.hashCode % 10)) : " + (1 to 10).map((k: Int) => (k, k.hashCode, k.hashCode % 10)))
    // Vector((1,1,1), (2,2,2), (3,3,3), (4,4,4), (5,5,5), (6,6,6), (7,7,7), (8,8,8), (9,9,9), (10,10,0))

    //Lets confirm in our rdd
    println("Keys in each partition" + rddDataWith10Partition.mapPartitions(iter => Iterator(iter.map(_._1).toSet)).collect.foreach(println))
    //Set()
    //Set(1)
    //Set(2)
    //Set(3)
    //Set()
    //Set()
    //Set()
    //Set()
    //Set()
    //Set()
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
