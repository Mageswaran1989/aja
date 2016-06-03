package org.aja.tej.examples.spark

import org.aja.tej.utils.FileUtils
import org.aja.tej.utils.TejUtils._
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 30/8/15.
 */

/**
 * All examples/exercises are executed/controlled from here
 */

object BibleWordCount {

  val inFile = "data/kjvdat.txt"
  val outDir = "output/kjv-bible-words-count"
  def run(sc: SparkContext) = {

    FileUtils.rmrf(outDir)

    implicit val caseInsensitiveOrdering = new Ordering[String] {
      override def compare(a: String, b: String) = a.toLowerCase.compare(b.toLowerCase)
    }

    //For those who pull your hair for local variable names, there goes our pipeline execution model.
    sc.textFile(inFile) //Read the file as a Array of line
      .map(line => line.toLowerCase()) //Convert each line to lowercase
      .flatMap(line => line.split("""[^\p{IsAlphabetic}]+""")) //Split each line into Array[Strings],
      // therefore we get Arrray of Array[Strings], complex isn't? flatten it
      .map(word => (word, 1))//Map each string/word to tuple of (xyz,1). Remember Hadoop Map
      .reduceByKey((count1, count2) => (count1 + count2)) //What comes after Map? Yes reduce!
      // Add the values of same key or word
      //.groupBy(tuple => tuple._2)   // group by the counts!
      .sortByKey()  //Sort the data
      .saveAsTextFile(outDir) //Time to use our lazy Discs!

    //Lets do some analysis on the sorted data
    // Exercise: Take the output from the previous exercise and count the number
    //   of words that start with each letter of the alphabet and each digit.
    // Exercise (Hard): Sort the output by count. You can't use the same
    //   approach as in the previous exercise. Hint: See RDD.keyBy
    //   (http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.rdd.RDD)
    //   What's the most frequent word that isn't a "stop word".
    // Exercise (Hard): Group the word-count pairs by count. In other words,
    //   All pairs where the count is 1 are together (i.e., just one occurrence
    //   of those words was found), all pairs where the count is 2, etc. Sort
    //   ascending or descending. Hint: Is there a method for grouping?
    // Exercise (Thought Experiment): Consider the size of each group created
    //   in the previous exercise and the distribution of those sizes vs. counts.
    //   What characteristics would you expect for this distribution? That is,
    //   which words (or kinds of words) would you expect to occur most
    //   frequently? What kind of distribution fits the counts (numbers)?

  }
}

object WordCountMain {

  def main (args: Array[String]) {
    val sc = getSparkContext("WordCountMain")

    //try..finally block to make sure we close SparkContext no matter what happens
    try {
      BibleWordCount.run(sc)
    } finally {
      sc.stop()
    }
  }



}
