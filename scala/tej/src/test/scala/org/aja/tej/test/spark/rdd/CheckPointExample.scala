package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 12/8/15.
 */
/*
Will create a checkpoint when the RDD is computed next. Checkpointed RDDs are
stored as a binary file within the checkpoint directory which can be specified using the
Spark context. (Warning: Spark applies lazy evaluation. Checkpointing will not occur
until an action is invoked.)
Important note: the directory ”my directory name” should exist in all slaves. As an
alternative you could use an HDFS directory URL as well.

 */
object CheckPointExample {

  def useCases(sc: SparkContext) = {
    sc.setCheckpointDir("data/CheckPointExampleDir")
    //Check tej/data/
    val a = sc.parallelize (1 to 4)
    a.checkpoint
    a.count
    }


}
