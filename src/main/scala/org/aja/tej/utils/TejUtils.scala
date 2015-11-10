package org.aja.tej.utils

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by mageswaran on 26/7/15.
 */
object TejUtils {

  def getSparkContext(applicationName: String, master: String = "local[4]") = {
    val conf = new SparkConf().setAppName(applicationName).setMaster(master /*"spark://myhost:7077"*/)
    new SparkContext(conf)
  }

  def getRawData(sc: SparkContext, filePath: String, numPartions: Int = 2) = {
    sc.textFile(filePath, numPartions)
  }

  def tokenizeFolder(sc: SparkContext, path: String): (Long, RDD[String]) = {
    val folderContents = sc.wholeTextFiles(path) //Returns Tuple(String,String) -> (FileName, Contents)
    val numFiles = folderContents.count()
    //rec._1 => fileName, rec._2 => file contents
    val features = folderContents.flatMap(rec => {
      val strArray = rec._2.split(" ")
      strArray
    }
    )
    (numFiles, features)
  }

  /*
      val sum = (1 to 10) sum
      val sum = getExecutionTime { (1 to 10) sum }

      In REPL:
      :power
      :wrap time
   */
  def getExecutionTime[A] (block: => A) : A = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    println("Elapsed time is " + (t1 - t0) + "ns" )
    result
  }

  def waitForSparkUI(sc: SparkContext) = {
    println("""
              |========================================================================
              |
              |    Before closing down the SparkContext, open the Spark Web Console
              |    http://localhost:4040 and browse the information about the tasks
              |    run for this example.
              |
              |    When finished, hit the <return> key to exit.
              |
              |========================================================================
            """.stripMargin)
    Console.in.read()

    sc.stop()
  }
  //def printClassLinearization(className: Any)
  //To print class Linearization
  //import scala.reflect.runtime.universe._
  //val tpe = typeOf[LinearRegressionModel]
  //  val tpe = typeOf[className]
  //  tpe.baseClasses foreach { s => println(s.fullName) }
}
