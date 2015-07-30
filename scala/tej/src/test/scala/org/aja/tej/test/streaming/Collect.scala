package org.aja.tej.test.streaming

/**
 * Created by mageswaran on 29/7/15.
 */
import java.io.File

import com.google.gson.Gson
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Collect at least the specified number of tweets into json text files.
 *  %  ${YOUR_SPARK_HOME}/bin/spark-submit \
     --class "com.databricks.apps.twitter_classifier.Collect" \
     --master ${YOUR_SPARK_MASTER:-local[4]} \
     target/scala-2.10/spark-twitter-lang-classifier-assembly-1.0.jar \
     ${YOUR_OUTPUT_DIR:-/tmp/tweets} \
     ${NUM_TWEETS_TO_COLLECT:-10000} \
     ${OUTPUT_FILE_INTERVAL_IN_SECS:-10} \
     ${OUTPUT_FILE_PARTITIONS_EACH_INTERVAL:-1} \
     --consumerKey ${YOUR_TWITTER_CONSUMER_KEY} \
     --consumerSecret ${YOUR_TWITTER_CONSUMER_SECRET} \
     --accessToken ${YOUR_TWITTER_ACCESS_TOKEN}  \
     --accessTokenSecret ${YOUR_TWITTER_ACCESS_SECRET}

--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
 */
object Collect {
  private var numTweetsCollected = 0L
  private var partNum = 0
  private var gson = new Gson()

  def main(args: Array[String]) {
    // Process program arguments and set properties
    if (args.length < 3) {
      System.err.println("Usage: " + this.getClass.getSimpleName +
        "<outputDirectory> <numTweetsToCollect> <intervalInSeconds> <partitionsEachInterval>")
      System.exit(1)
    }
    val Array(outputDirectory, Utils.IntParam(numTweetsToCollect),  Utils.IntParam(intervalSecs), Utils.IntParam(partitionsEachInterval)) =
      Utils.parseCommandLineWithTwitterCredentials(args)
    val outputDir = new File(outputDirectory.toString)
    if (outputDir.exists()) {
      System.err.println("Found - %s already exists: deleting...".format(
        outputDirectory))
      outputDir.delete()
      System.exit(1)
    }
    outputDir.mkdirs()

    println("Initializing Streaming Spark Context...")
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[4]")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(intervalSecs))

    val tweetStream = TwitterUtils.createStream(ssc, Utils.getAuth)
      .map(gson.toJson(_))

    tweetStream.foreachRDD((rdd, time) => {
      val count = rdd.count()
      if (count > 0) {
        val outputRDD = rdd.repartition(partitionsEachInterval)
        outputRDD.saveAsTextFile(outputDirectory + "/tweets_" + time.milliseconds.toString)
        numTweetsCollected += count
        if (numTweetsCollected > numTweetsToCollect) {
          System.exit(0)
        }
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}