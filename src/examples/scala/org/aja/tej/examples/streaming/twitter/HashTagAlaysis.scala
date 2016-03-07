package org.aja.tej.examples.streaming.twitter

import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date

import com.google.gson.Gson
import org.aja.tej.utils.{TejUtils, TejTwitterUtils}
import org.apache.spark.sql.{SaveMode, AnalysisException, SQLContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils

/**
 * Created by mageswaran on 6/3/16.
 */

/*
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
 */

case class Tweet(createdAt: Time, inReplyToScreenName: String, hashtagEntities: Array[String],
                 screenName: String, location: String, friendsCount: Long)
//case class User(screenName: String, location: String, friendsCount: Long)

object HashTagAlaysis {

  private var gson = new Gson()

  def main(args: Array[String]) {

    if (args.length < 3) {
      println("Usage : " + this.getClass.getSimpleName + " --consumerKey <key> --consumerSecret <key> " +
        "--accessToken <key> --accessTokenSecret <key>")
      System.exit(1)
    }

    val dummyArray = TejTwitterUtils.parseCommandLineWithTwitterCredentials(args)

    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    val ssc = new StreamingContext(sc, Seconds(1))

    val twitterStream = TwitterUtils.createStream(ssc, TejTwitterUtils.getAuth)
    .map(gson.toJson(_))

    val formatter = new SimpleDateFormat

    twitterStream.foreachRDD(rdd => {
      // Get the singleton instance of SQLContext
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._

      val df = sqlContext.read.json(rdd)
      df.registerTempTable("tweets")

      try {
        println("Total of " + rdd.count + " tweets received in this batch ")
        println(df.select(df("createdAt"), df("inReplyToScreenName"), df("hashtagEntities.text").as("hashtag"),
          df("text"), df("user.screenName"), df("user.location"), df("user.friendsCount")).show())
        df.write.mode(SaveMode.Append).parquet("tweets_table")
      }
      catch {
        case e: AnalysisException =>
          println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! Something wrong with incoming tweet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + e.toString)
      }

    })

    ssc.start()
    ssc.awaitTermination()

  }
}

