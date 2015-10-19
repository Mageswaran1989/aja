package org.aja.tej.examples.streaming.twitter

import com.google.gson.Gson
import org.aja.tej.utils.{TejUtils, TejTwitterUtils}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mageswaran on 18/10/15.
 */

/*
10
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
 */

object PrintTweets {

  var count = 0L
  private var gson = new Gson()

  def main(args: Array[String]) {

    if (args.length < 3) {
      println("Usage : " + this.getClass.getSimpleName + " <numbeOfTweetsToPrint> --consumerKey <key> --consumerSecret <key> " +
        "--accessToken <key> --accessTokenSecret <key>")
      System.exit(1)
    }

    val Array(numbeOfTweetsToPrint) = TejTwitterUtils.parseCommandLineWithTwitterCredentials(args)
    println(numbeOfTweetsToPrint)
    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    val ssc = new StreamingContext(sc, Seconds(1))

    val twitterStream = TwitterUtils.createStream(ssc, TejTwitterUtils.getAuth)
      .map(gson.toJson(_)) //Disable this and see the raw data

    //Each RDD can have either 0 or N tweets, since each stream is an continuous sequence of RDD
    twitterStream.foreachRDD(rdd => { //Retrieve each DStream and print tweets in that
    count += 1
    if (count > numbeOfTweetsToPrint.toString.toLong)
      System.exit(0)
      rdd.foreach(println)
      println("**********************************************************************")
    })

    ssc.start()
    ssc.awaitTermination()

  }

}
