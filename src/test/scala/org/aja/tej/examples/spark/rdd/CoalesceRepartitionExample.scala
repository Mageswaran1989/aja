package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Coalesces the associated data into a given number of partitions. repartition(numPartitions)
is simply an abbreviation for coalesce(numPartitions, shuffle = true).

The coalesce method is a good method to pack and rebalance your RDDs (for example, after a filter operation where you
have less data after the action).
New Partition > Old partition : shuffle=True
New Partition < Old partition : shuffle=False
 */
object CoalesceRepartitionExample{

  def useCases(sc: SparkContext): Unit = {
    val y = sc.parallelize (1 to 10 , 10)
    val z = y.coalesce(2, false )
    println("CoalesceRepartitionExample: " + z.partitions.length)
  }

}
