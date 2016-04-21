package org.aja.tej.examples.spark.rdd.dataOrganization.partitioner

import org.aja.tej.utils.TejUtils
import org.apache.spark.Partitioner

/**
 * Created by mageswaran on 15/8/15.
 */

/*
partitionBy[Pair]
Repartitions as key-value RDD using its keys. The partitioner implementation can be
supplied as the first argument.

 partitioner
Specifies a function pointer to the default partitioner that will be used for groupBy,
subtract, reduceByKey (from PairedRDDFunctions), etc. functions.

 partitions
Returns an array of the partition objects associated with this RDD.


 */
//
//Problem: Given a set of user information, partition the records based on the year of last
//access date, one partition per year.


class ModulusPartitioner(partitions: Int) extends Partitioner {
  implicit def int2Boolean(x: Int) = if(x == 0) true else false

  require(partitions == 5, s"Number of $partitions should be 5")
  override def numPartitions: Int = partitions

  override def getPartition(key: Any): Int = key match {
    case key if(key.hashCode%2) =>  println("key = " + key + " 0 "); 0
    case key if(key.hashCode%3) =>  println("key = " + key + " 1 ");1
    case key if(key.hashCode%5) =>  println("key = " + key + " 2 ");2
    case key if(key.hashCode%11) => println("key = " + key + " 3 ");3
    case key if(key.hashCode%13) => println("key = " + key + " 4 ");4
  }

  override def equals(other: Any) = other match {
    case that: ModulusPartitioner => that.numPartitions == numPartitions
    case _ => false
  }
  override def hashCode = numPartitions
}

object PartitionExample  extends App {


  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val nums = List((2,1),(4,1),(8,1),(3,1),(9,1),(5,1),(11,1),(13,1))
  val rdd = sc.parallelize(nums)

  val rddWith2Partitions = rdd.partitionBy(new ModulusPartitioner(5))

  var partition = 0
  val partitionAccumulator = sc.accumulator(0)
  rddWith2Partitions.foreachPartition{iter =>
    println(s"Data in $partition")
    iter.foreach(println)
    //partition += 1
    partitionAccumulator += 1
  }
}
