package org.aja.tej.examples.spark.rdd

/**
 * Created by mageswaran on 25/11/15.
 */

/*
Standard reduce is taking a wrapped version of the function and using it to mapPartitions. After that results are
collected and reduced locally on a driver. If number of the partitions is large and/or function you use is expensive it
places a significant load on a single machine.

The first phase of the treeReduce is pretty much the same as above but after that partial results are merged in parallel
 and only the final aggregation is performed on the driver.

depth is suggested depth of the tree and since depth of the node in tree is defined as number of edges between the root
and the node it should you give you more or less an expected pattern although it looks like a distributed aggregation
can be stopped early in some cases.

It is worth to note that what you get with treeReduce is not a binary tree. Number of the partitions is adjusted on each
 level and most likely more than a two partitions will be merged at once.

Compared to the standard reduce, tree based version performs reduceByKey with each iteration and it means a lot of data
shuffling. If number of the partitions is relatively small it will be much cheaper to use plain reduce. If you suspect
that the final phase of the reduce is a bottleneck tree* version could be worth trying.
 */

/*
 treeAggregate with depth = 2 should run in
O(sqrt(n)) time, where n is the number of partitions.

Git history: https://github.com/apache/spark/pull/1110
 */
object TreeReduceExample {

}
