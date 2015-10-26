package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
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

 getCheckpointFile
Returns the path to the checkpoint file or null if RDD has not yet been checkpointed.

 */
object CheckPointExample extends App{

  def useCases(sc: SparkContext) = {
    sc.setCheckpointDir("data/CheckPointExampleDir")
    //Check tej/data/
    val a = sc . parallelize (1 to 500 , 5)
    val b = a ++ a ++ a ++ a ++ a
    a.isCheckpointed

    a.checkpoint
    a.count

    a.isCheckpointed

    b . getCheckpointFile
    b . checkpoint
    b . getCheckpointFile

    b.isCheckpointed


    b . collect
    b . getCheckpointFile

    b.isCheckpointed

  }


  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
