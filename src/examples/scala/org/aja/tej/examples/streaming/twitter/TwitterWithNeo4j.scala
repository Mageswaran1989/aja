package org.aja.tej.examples.streaming.twitter

import com.google.gson.Gson
import org.aja.tej.utils.{TejUtils, TejTwitterUtils}
import org.anormcypher.{Cypher, Neo4jREST}
import org.apache.spark.sql.{AnalysisException, Row, SQLContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import play.api.libs.ws.ning

import scala.collection.mutable

/**
 * Created by mageswaran on 24/4/16.
 */

/*
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
*/

//case class HashTag(tag: String)

case class UserTag(name: String, tag: String, location: String) {

  def executeCQL = {
    println("executing CQL : " )
    println(s"""CREATE(u:USER {name:'${name}'}) -[:TWEETS]-> (t:TAG {name:'${tag}'})""")
    println(s"""CREATE(u:USER {name:'${name}'}) -[:LOCATION]-> (t:LOCATION{name:'${location}'})""")

    implicit val wsclient = ning.NingWSClient()

    // Setup the Rest Client
    implicit val connection = Neo4jREST("localhost", 7474, "/db/data/", "neo4j", "aja")//Neo4jREST()(wsclient)

    // Provide an ExecutionContext
    implicit val ec = scala.concurrent.ExecutionContext.global

//    CREATE(u:USER{name:'Lalita de corazón'})-[:TWEETS]->(t:Location{name:'SoyLali40GlobalShow'})
    Cypher(s"""CREATE(u:USER {name:'${name}'}) -[:TWEETS]-> (t:TAG {name:'${tag}'})""").execute()
    Cypher(s"""CREATE(u:USER {name:'${name}'}) -[:LOCATION]-> (t:LOCATION{name:'${location}'})""").execute()
  }
}

object TwitterWithNeo4j {

  var count = 0L
  private var gson = new Gson()

  def main(args: Array[String]) {

    var printSchema = true
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


    //**************Streaming Processing********************//
    //Each RDD can have either 0 or N tweets, since each stream is an continuous sequence of RDD
    twitterStream.foreachRDD(rdd => {

      //Retrieve each DStream and print tweets in that
      count += 1
      // Get the singleton instance of SQLContext
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._

      //val jsonRDD = gson.toJson(rdd)
      val df = sqlContext.read.json(rdd)
      df.registerTempTable("tweets")

      if (count > numbeOfTweetsToPrint.toString.toLong)
        System.exit(0)

      println("1.>>>>>>>>>>>" + " Count: " + count + " Size: " + rdd.count() )

      try {

        val users = df.select("user.name", "hashtagEntities", "user.location").explode($"hashtagEntities"){
          //case Row(hashtags: mutable.WrappedArray[String]) => println(hashtags); hashtags.map(HashTag(_))
          case Row(hashtags: Seq[Row]) => hashtags.map(tagRow => HashTag(tagRow(2).asInstanceOf[String]))
        }.select("name", "tag", "location").rdd.map(r =>
          UserTag(r(0).asInstanceOf[String],r(1).asInstanceOf[String],r(2).asInstanceOf[String])).foreach(_.executeCQL)

      } catch {
        case e: AnalysisException =>
          println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! Something wrong with incoming tweet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + e.toString)
      }
    })

    ssc.start()
    ssc.awaitTermination()

  }
}
