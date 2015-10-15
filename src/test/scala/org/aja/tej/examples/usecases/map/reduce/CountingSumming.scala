package org.aja.tej.examples.usecases.map.reduce

import org.aja.tej.utils.TejUtils

/**
 * Created by mageswaran on 13/10/15.
 */

/**
  Counting and Summing
  ====================

  Problem Statement:
  ------------------
    There is a number of documents where each document is a set of terms. It is required to calculate a total number of
  occurrences of each term in all documents. Alternatively, it can be an arbitrary function of the terms. For
  instance, there is a log file where each record contains a response time and it is required to calculate an average
  response time.

  Applications:
  -------------
  Log Analysis, Data Querying
  */
object CountingSumming extends App {

  val sc = TejUtils.getSparkContext("MapReducePattern - CountingSumming")

  val hamFolder = sc.wholeTextFiles("data/email/ham")

  def listFilesNContent(record : (String, String)) = {
    println("File Name: " + record._1)
    println(" File Content: " + record._2)
  }
  hamFolder.foreach(listFilesNContent)

  //No need of map and reduce to find the number of files
  val numOfFiles = hamFolder.count

  //There is a problem here!
  val stringsFrequency = hamFolder.map(rec => (rec._2, 1)).reduceByKey(_ + _)
  stringsFrequency.foreach(rec => println(rec._1 + "--->" + rec._2))

  val stringsFrequency1 = hamFolder.flatMap(rec => {
    val strArray = rec._2.split(" ") //Convert one huge string as a Array of string
    strArray.map(str => (str.trim,1)) //Map it
  }).reduceByKey(_ + _) //Reduce it
  stringsFrequency1.foreach(println)

  //Lets put this as a utility function in TejUtils
  val (numFiles, tokens) = TejUtils.tokenizeFolder(sc, "data/email/ham")
  tokens.foreach(println)

  //Have a doubt on what we have created? Lets check
  println("Created tokesn is of type: " + tokens.getClass)

}
