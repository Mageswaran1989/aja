package org.aja.tej.examples.streaming.twitter.analysis

/**
 * Created by mageswaran on 30/7/15.
 */

import org.aja.tej.utils.TejTwitterUtils
import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Pulls live tweets and filters them for tweets in the chosen cluster.
 */

//${YOUR_SPARK_HOME}/bin/spark-submit \
//--class "com.databricks.apps.twitter_classifier.Predict" \
//--master ${YOUR_SPARK_MASTER:-local[4]} \
//target/scala-2.10/spark-twitter-lang-classifier-assembly-1.0.jar \
//${YOUR_MODEL_DIR:-/tmp/tweets/model} \
//${CLUSTER_TO_FILTER:-7} \
//--consumerKey ${YOUR_TWITTER_CONSUMER_KEY} \
//--consumerSecret ${YOUR_TWITTER_CONSUMER_SECRET} \
//--accessToken ${YOUR_TWITTER_ACCESS_TOKEN}  \
//--accessTokenSecret ${YOUR_TWITTER_ACCESS_SECRET}

/*
 data/tweets/model
21
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
 */


object Predict {
  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println("Usage: " + this.getClass.getSimpleName + " <modelDirectory>  <clusterNumber>")
      System.exit(1)
    }

    val Array(modelFile, TejTwitterUtils.IntParam(clusterNumber)) =
      TejTwitterUtils.parseCommandLineWithTwitterCredentials(args)

    println("Initializing Streaming Spark Context...")
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[4]")
    val ssc = new StreamingContext(conf, Seconds(5))

    println("Initializing Twitter stream...")
    val tweets = TwitterUtils.createStream(ssc, TejTwitterUtils.getAuth)
    val statuses = tweets.map(_.getText)

    println("Initalizaing the the KMeans model...")
    val model = new KMeansModel(ssc.sparkContext.objectFile[Vector](modelFile.toString).collect())

    val filteredTweets = statuses
      .filter(t => model.predict(TejTwitterUtils.featurize(t)) == clusterNumber)
    filteredTweets.print()

    // Start the streaming computation
    println("Initialization complete.")
    ssc.start()
    ssc.awaitTermination()
  }
}