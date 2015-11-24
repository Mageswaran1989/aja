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
    println(this.getClass.getSimpleName)
    sc.setCheckpointDir("data/CheckPointExampleDir")
    //Check in tej/data/
    val a = sc.parallelize(1 to 500 , 5)
    val b = a ++ a ++ a ++ a ++ a

    println("a.isCheckpointed : " + a.isCheckpointed) //  a.isCheckpointed : false
    //enable checkpoin ton this rdd
    println("a.checkpoint : " + a.checkpoint) //  a.checkpoint : ()
    println("a.count : " + a.count) //  a.count : 500
    println("a.isCheckpointed : " + a.isCheckpointed) //  a.isCheckpointed : true
    println("a.getCheckpointFile : " + a.getCheckpointFile) //  a.getCheckpointFile : Some(file:/opt/aja/data/CheckPointExampleDir/ee7a927e-3a48-412f-adcd-8e79c20e860c/rdd-0)
    println("----------------------------------------------------------")

    println("b.isCheckpointed : " + b.isCheckpointed) //  b.isCheckpointed : false
    println("b.collect : " + b.collect) //  b.collect : [I@2dad7733
    println("b.isCheckpointed : " + b.isCheckpointed) //  b.isCheckpointed : false
    println("b.getCheckpointFile : " + b.getCheckpointFile) //    b.getCheckpointFile : None


  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
