package org.aja.tej.examples


import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by mageswaran on 26/7/15.
 */

/** Excursus:
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
object HelloWorldSpark extends App {
  //def main(args: Array[String]) {
  val logFile = "data/datascience.stackexchange.com/Posts.xml" // Should be some file on your system
  val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]" /*"spark://myhost:7077"*/)
  val sc = new SparkContext(conf)
  val logData = sc.textFile(logFile, 2).cache()
  val numAs = logData.filter(line => line.contains("a")).count()
  val numBs = logData.filter(line => line.contains("b")).count()
  println("$$$$$$$$$Lines with a: %s, Lines with b: %s".format(numAs, numBs))

  sc.textFile(logFile)
    .flatMap(_.split(" "))
    .map((_, 1))
    .reduceByKey(_ + _)
    .saveAsTextFile("data/hw_wordcount_output/")

  //}
}
