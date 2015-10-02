package org.aja.tej.examples.usecases.wordcount

import org.aja.tej.utils.TejUtils._

/**
 * Created by mageswaran on 30/8/15.
 */

/**
 * All examples/exercises are executed/controlled from here
 */

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
