package org.aja.tej.examples.streaming.twitter

import org.aja.tej.utils.{TejUtils, TejTwitterUtils}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mageswaran on 17/10/15.
 */

/*

India
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
 */
object PopularTags {

  private var totalTime = 0L

  def main (args: Array[String]){

    if ( args.length < 3) {
      System.err.print("Usage: " + this.getClass.getSimpleName + " <popular tag to be filtered>")
      System.exit(1)
    }

    val Array(filterTag) = TejTwitterUtils.parseCommandLineWithTwitterCredentials(args)

    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    val ssc = new StreamingContext(sc, Seconds(1))

    val twitterStream = TwitterUtils.createStream(ssc, TejTwitterUtils.getAuth)

    val hashTags = twitterStream.flatMap(status => status.getText.split(" ")).filter(_.startsWith("#"))

    val topCountdFor60Secs = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))

    val topCountsFor10Seconds = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(10))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))

    // Print popular hashtags
    topCountdFor60Secs.foreachRDD(rdd => {
      val topList = rdd.take(5)
      println("\nPopular topics in last 60 seconds (%s total):".format(rdd.count()))
      topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
    })

    topCountsFor10Seconds.foreachRDD(rdd => {
      val topList = rdd.take(5)
      println("\nPopular topics in last 10 seconds (%s total):".format(rdd.count()))
      topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
    })

    ssc.start()
    ssc.awaitTermination()

    //https://github.com/prabeesh/SparkTwitterAnalysis/blob/master/src/main/scala/TwitterPopularTags.scala
  }
}
