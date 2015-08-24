package org.aja.tej.examples.usecases.audioscrobbler.recommendations

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by mageswaran on 22/8/15.
 */
object ALSExample {

  val getSparkContext = {
    val conf = new SparkConf().setAppName("MLLib ALS").setMaster("local[4]" /*"spark://myhost:7077"*/)
    new SparkContext(conf)
  }

  val sc = getSparkContext

  //userID artistID count
  val rawUserArtistData = sc.textFile("data/ds/user_artist_data.txt", 4 )



}

