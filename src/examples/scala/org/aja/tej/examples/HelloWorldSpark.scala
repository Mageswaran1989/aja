package org.aja.tej.examples


import java.io.File

import org.aja.tej.utils.TejUtils
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by mageswaran on 26/7/15.
 */

/** Executor:
  *
  * Spark Cluster -> Worker Node 1 (Slave Machines) -> Runs Executor 1 with n slots/cores -> Runs application task/slot
  *                                                                                         send from SparkContext
  *                                                -> Runs Executor 2 with n slots/cores
  *                                                -> Runs Executor ...
  *               -> Worker Node 2 (Slave Machines)
  *               -> Worker Node ...(Slave Machines)
  *
  *                       ----------------------------------------------------
  *                       \                                                  \
  *                       \                                      <----->  Spark Cluster(s)
  * Driver Program (SparkContext)    <----->   Cluster Manager   <----->  Spark Cluster(s)
  *                       \                                       <-----> Spark Cluster(s)
  *                       \                                                  \
  *                       ----------------------------------------------------
  *
  */

/**
Job:- A piece of code which reads some input  from HDFS or local, performs some computation on the data and writes
some output data.

Stages:-Jobs are divided into stages. Stages are classified as a Map or reduce stages(Its easier to understand if you
have worked on Hadoop and want to correlate). Stages are divided based on computational boundaries, all computations
(operators) cannot be Updated in a single Stage. It happens over many stages.

Tasks:- Each stage has some tasks, one task per partition. One task is executed on one partition of data on one
executor(machine).

DAG - DAG stands for Directed Acyclic Graph, in the present context its a DAG of operators.

Executor - The process responsible for executing a task.

Driver - The program/process responsible for running the Job over the Spark Engine

Master - The machine on which the Driver program runs

Slave - The machine on which the Executor program runs

 All jobs in spark comprise a series of operators and  run on a set of data. All the operators in a job are used to
construct a DAG (Directed Acyclic Graph). The DAG is optimized by rearranging and combining operators where possible.
 */

/*
Four stages
RDD Objects          \    DAGScheduler         \      TaskScheduler         \    Worker
-----------------------------------------------------------------------------------------
Build operator DAG   \ Split graph into stages \ Launch tasks via ckuster   \ Execute tasks
                     \ of tasks                \ manager                    \ Store and serve blocks
                     \ Submit eacg stage as    \ Retry failed or straggling \
                     \ ready                   \ tasks                      \l
                     \ Agnostics to operators  \ Doesn't know about stages


1 . The first layer is the interpreter/main program, Spark uses a Scala interpreter, with some modifications.
    As  you enter your code in spark console(creating RDD's and applying operators), Spark creates a operator graph.
2.  When the user runs an action(like collect), the Graph is submitted to a DAG Scheduler. The DAG scheduler divides
    operator graph into (map and reduce) stages.
    A stage is comprised of tasks based on partitions of the input data. The DAG scheduler pipelines operators together
    to optimize the graph. For e.g. Many map operators can be scheduled in a single stage. This optimization is key to
    Sparks performance. The final result of a DAG scheduler is a set of stages.
3.  The stages are passed on to the Task Scheduler. The task scheduler launches tasks via cluster manager.
    ( Spark Standalone/Yarn/Mesos). The task scheduler doesn't know about dependencies among stages.
4.  The Worker executes the tasks on the Slave. A new JVM is started per JOB. The worker knows only about the code that
    is passed to it.
 */

object HelloWorldSpark {
  def main(args: Array[String]) {
    val outputPath = "output/hw_wordcount_output/"
    val logFile = "data/datascience.stackexchange.com/Posts.xml" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]" /*"spark://myhost:7077"*/)
    val sc = new SparkContext(conf)

    try {

      val logData = sc.textFile(logFile, 2).cache()
      val numAs = logData.filter(line => line.contains("a")).count()
      val numBs = logData.filter(line => line.contains("b")).count()
      println("$$$$$$$$$Lines with a: %s, Lines with b: %s".format(numAs, numBs))

      val dir = new File(outputPath)
      if (dir.exists)
        dir.delete()

      sc.textFile(logFile)
        .flatMap(_.split(" "))
        .map((_, 1))
        .reduceByKey(_ + _)
      //    .saveAsTextFile(outputPath)

      //////////////////////////////////////////////////////////////////////////////////////////////////

      //20090505-000000 af.b Tuisblad 1 36236
      val pagecount = sc.textFile("data/pagecounts/")
      sc.setLogLevel("INFO")
      pagecount.take(10).foreach(println)

      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + pagecount.count())

      //Lets count the
      val engPages = pagecount.filter(_.split(" ")(1) == "en").count

      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + engPages)
    }
    finally {
      TejUtils.waitForSparkUI(sc)
    }
  }
}


/*
Datasets:
https://dumps.wikimedia.org/other/pagecounts-raw/
https://wikitech.wikimedia.org/wiki/Analytics/Data/Pagecounts-raw

 */