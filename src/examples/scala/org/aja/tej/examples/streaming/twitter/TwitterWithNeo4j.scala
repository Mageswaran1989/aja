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

//TODO: should check for any issues in sharing below details
/*
20
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
*/

//For each tweet we are creating a REST connection. Very bad indeed!
case class Tweets(name: String, tag: String, location: String, inReplyToScreenName: String) {

  def executeCQL = {
    println("executing CQL : " )
    //Not an efficient way of creating nodes and relationship, however this
    //gives us a head start for learning CQL in the browser
    val cql =
      s"""MERGE (u:User {name:'${name}'})
         |MERGE (l:Location {name: '${location}'})
         |MERGE (t:Tag {name: '${tag}'})
         |MERGE (u) -[:LIVES_IN]-> (l)
         |MERGE (u) -[:TWEETS]-> (t)
       """.stripMargin

    println(cql)
    implicit val wsclient = ning.NingWSClient()

    // Setup the Rest Client
    implicit val connection = Neo4jREST("localhost", 7474, "/db/data/", "neo4j", "aja")

    // Provide an ExecutionContext
    implicit val ec = scala.concurrent.ExecutionContext.global

    Cypher(cql).execute()
  }
}

object TwitterWithNeo4j {

  var count = 0L
  private var gson = new Gson()

  def main(args: Array[String]) {

    var printSchema = true
    if (args.length < 3) {
      println("Usage : " + this.getClass.getSimpleName + " <numbeOfRuns> --consumerKey <key> --consumerSecret <key> " +
        "--accessToken <key> --accessTokenSecret <key>")
      System.exit(1)
    }

    val Array(numbeOfRuns) = TejTwitterUtils.parseCommandLineWithTwitterCredentials(args)
    println(numbeOfRuns)

    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    sc.setLogLevel("OFF")

    val ssc = new StreamingContext(sc, Seconds(1))

    val twitterStream = TwitterUtils.createStream(ssc, TejTwitterUtils.getAuth)
      .map(gson.toJson(_))


    //**************Streaming Processing********************//
    //Each RDD can have either 0 or N tweets, since each stream is an continuous sequence of RDD
    twitterStream.foreachRDD(rdd => {

      count += 1

      // Get the singleton instance of SQLContext
      val sqlContext = SQLContext.getOrCreate(rdd.sparkContext)
      import sqlContext.implicits._

      //val jsonRDD = gson.toJson(rdd)
      val df = sqlContext.read.json(rdd)
      df.registerTempTable("tweets")

      if (count > numbeOfRuns.toString.toLong)
        System.exit(0)

      println("1.>>>>>>>>>>>" + " Run: " + count + " Size: " + rdd.count() )

      try {

        val tweetDF = df.select("user.name", "user.friendsCount", "user.lang", "user.screenName",
          "hashtagEntities", "user.location", "inReplyToScreenName").explode($"hashtagEntities"){
          //
          case Row(content: Seq[Row]) => content.map(tags => HashTag(tags(2).asInstanceOf[String]))
        }.select("name", "friendsCount", "tag", "lang", "screenName", "location","inReplyToScreenName")

        println("1.>>>>>>>>>>>" + tweetDF.show())
        tweetDF.rdd.map(r =>
          //(name: String, tag: String, location: String, inReplyToScreenName: String)
        Tweets(r(0).asInstanceOf[String],r(2).asInstanceOf[String],r(5).asInstanceOf[String], r(6).asInstanceOf[String])).foreach(_.executeCQL)

      } catch {
        case e: AnalysisException =>
          println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! Something wrong with incoming tweet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + e.toString)
      }
    })

    ssc.start()
    ssc.awaitTermination()

  }
}


/*
Tweet Schema: That's a big list indeed!

root
 |-- contributorsIDs: array (nullable = true)
 |    |-- element: string (containsNull = true)
 |-- createdAt: string (nullable = true)
 |-- currentUserRetweetId: long (nullable = true)
 |-- geoLocation: struct (nullable = true)
 |    |-- latitude: double (nullable = true)
 |    |-- longitude: double (nullable = true)
 |-- hashtagEntities: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- end: long (nullable = true)
 |    |    |-- start: long (nullable = true)
 |    |    |-- text: string (nullable = true)
 |-- id: long (nullable = true)
 |-- inReplyToScreenName: string (nullable = true)
 |-- inReplyToStatusId: long (nullable = true)
 |-- inReplyToUserId: long (nullable = true)
 |-- isFavorited: boolean (nullable = true)
 |-- isPossiblySensitive: boolean (nullable = true)
 |-- isTruncated: boolean (nullable = true)
 |-- mediaEntities: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- displayURL: string (nullable = true)
 |    |    |-- end: long (nullable = true)
 |    |    |-- expandedURL: string (nullable = true)
 |    |    |-- id: long (nullable = true)
 |    |    |-- mediaURL: string (nullable = true)
 |    |    |-- mediaURLHttps: string (nullable = true)
 |    |    |-- sizes: struct (nullable = true)
 |    |    |    |-- 0: struct (nullable = true)
 |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |-- width: long (nullable = true)
 |    |    |    |-- 1: struct (nullable = true)
 |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |-- width: long (nullable = true)
 |    |    |    |-- 2: struct (nullable = true)
 |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |-- width: long (nullable = true)
 |    |    |    |-- 3: struct (nullable = true)
 |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |-- width: long (nullable = true)
 |    |    |-- start: long (nullable = true)
 |    |    |-- type: string (nullable = true)
 |    |    |-- url: string (nullable = true)
 |-- place: struct (nullable = true)
 |    |-- boundingBoxCoordinates: array (nullable = true)
 |    |    |-- element: array (containsNull = true)
 |    |    |    |-- element: struct (containsNull = true)
 |    |    |    |    |-- latitude: double (nullable = true)
 |    |    |    |    |-- longitude: double (nullable = true)
 |    |-- boundingBoxType: string (nullable = true)
 |    |-- country: string (nullable = true)
 |    |-- countryCode: string (nullable = true)
 |    |-- fullName: string (nullable = true)
 |    |-- id: string (nullable = true)
 |    |-- name: string (nullable = true)
 |    |-- placeType: string (nullable = true)
 |    |-- url: string (nullable = true)
 |-- retweetCount: long (nullable = true)
 |-- retweetedStatus: struct (nullable = true)
 |    |-- contributorsIDs: array (nullable = true)
 |    |    |-- element: string (containsNull = true)
 |    |-- createdAt: string (nullable = true)
 |    |-- currentUserRetweetId: long (nullable = true)
 |    |-- hashtagEntities: array (nullable = true)
 |    |    |-- element: struct (containsNull = true)
 |    |    |    |-- end: long (nullable = true)
 |    |    |    |-- start: long (nullable = true)
 |    |    |    |-- text: string (nullable = true)
 |    |-- id: long (nullable = true)
 |    |-- inReplyToScreenName: string (nullable = true)
 |    |-- inReplyToStatusId: long (nullable = true)
 |    |-- inReplyToUserId: long (nullable = true)
 |    |-- isFavorited: boolean (nullable = true)
 |    |-- isPossiblySensitive: boolean (nullable = true)
 |    |-- isTruncated: boolean (nullable = true)
 |    |-- mediaEntities: array (nullable = true)
 |    |    |-- element: struct (containsNull = true)
 |    |    |    |-- displayURL: string (nullable = true)
 |    |    |    |-- end: long (nullable = true)
 |    |    |    |-- expandedURL: string (nullable = true)
 |    |    |    |-- id: long (nullable = true)
 |    |    |    |-- mediaURL: string (nullable = true)
 |    |    |    |-- mediaURLHttps: string (nullable = true)
 |    |    |    |-- sizes: struct (nullable = true)
 |    |    |    |    |-- 0: struct (nullable = true)
 |    |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |    |-- width: long (nullable = true)
 |    |    |    |    |-- 1: struct (nullable = true)
 |    |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |    |-- width: long (nullable = true)
 |    |    |    |    |-- 2: struct (nullable = true)
 |    |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |    |-- width: long (nullable = true)
 |    |    |    |    |-- 3: struct (nullable = true)
 |    |    |    |    |    |-- height: long (nullable = true)
 |    |    |    |    |    |-- resize: long (nullable = true)
 |    |    |    |    |    |-- width: long (nullable = true)
 |    |    |    |-- start: long (nullable = true)
 |    |    |    |-- type: string (nullable = true)
 |    |    |    |-- url: string (nullable = true)
 |    |-- place: struct (nullable = true)
 |    |    |-- boundingBoxCoordinates: array (nullable = true)
 |    |    |    |-- element: array (containsNull = true)
 |    |    |    |    |-- element: struct (containsNull = true)
 |    |    |    |    |    |-- latitude: double (nullable = true)
 |    |    |    |    |    |-- longitude: double (nullable = true)
 |    |    |-- boundingBoxType: string (nullable = true)
 |    |    |-- country: string (nullable = true)
 |    |    |-- countryCode: string (nullable = true)
 |    |    |-- fullName: string (nullable = true)
 |    |    |-- id: string (nullable = true)
 |    |    |-- name: string (nullable = true)
 |    |    |-- placeType: string (nullable = true)
 |    |    |-- url: string (nullable = true)
 |    |-- retweetCount: long (nullable = true)
 |    |-- source: string (nullable = true)
 |    |-- text: string (nullable = true)
 |    |-- urlEntities: array (nullable = true)
 |    |    |-- element: struct (containsNull = true)
 |    |    |    |-- displayURL: string (nullable = true)
 |    |    |    |-- end: long (nullable = true)
 |    |    |    |-- expandedURL: string (nullable = true)
 |    |    |    |-- start: long (nullable = true)
 |    |    |    |-- url: string (nullable = true)
 |    |-- user: struct (nullable = true)
 |    |    |-- createdAt: string (nullable = true)
 |    |    |-- description: string (nullable = true)
 |    |    |-- descriptionURLEntities: array (nullable = true)
 |    |    |    |-- element: string (containsNull = true)
 |    |    |-- favouritesCount: long (nullable = true)
 |    |    |-- followersCount: long (nullable = true)
 |    |    |-- friendsCount: long (nullable = true)
 |    |    |-- id: long (nullable = true)
 |    |    |-- isContributorsEnabled: boolean (nullable = true)
 |    |    |-- isFollowRequestSent: boolean (nullable = true)
 |    |    |-- isGeoEnabled: boolean (nullable = true)
 |    |    |-- isProtected: boolean (nullable = true)
 |    |    |-- isVerified: boolean (nullable = true)
 |    |    |-- lang: string (nullable = true)
 |    |    |-- listedCount: long (nullable = true)
 |    |    |-- location: string (nullable = true)
 |    |    |-- name: string (nullable = true)
 |    |    |-- profileBackgroundColor: string (nullable = true)
 |    |    |-- profileBackgroundImageUrl: string (nullable = true)
 |    |    |-- profileBackgroundImageUrlHttps: string (nullable = true)
 |    |    |-- profileBackgroundTiled: boolean (nullable = true)
 |    |    |-- profileBannerImageUrl: string (nullable = true)
 |    |    |-- profileImageUrl: string (nullable = true)
 |    |    |-- profileImageUrlHttps: string (nullable = true)
 |    |    |-- profileLinkColor: string (nullable = true)
 |    |    |-- profileSidebarBorderColor: string (nullable = true)
 |    |    |-- profileSidebarFillColor: string (nullable = true)
 |    |    |-- profileTextColor: string (nullable = true)
 |    |    |-- profileUseBackgroundImage: boolean (nullable = true)
 |    |    |-- screenName: string (nullable = true)
 |    |    |-- showAllInlineMedia: boolean (nullable = true)
 |    |    |-- statusesCount: long (nullable = true)
 |    |    |-- timeZone: string (nullable = true)
 |    |    |-- translator: boolean (nullable = true)
 |    |    |-- url: string (nullable = true)
 |    |    |-- utcOffset: long (nullable = true)
 |    |-- userMentionEntities: array (nullable = true)
 |    |    |-- element: struct (containsNull = true)
 |    |    |    |-- end: long (nullable = true)
 |    |    |    |-- id: long (nullable = true)
 |    |    |    |-- name: string (nullable = true)
 |    |    |    |-- screenName: string (nullable = true)
 |    |    |    |-- start: long (nullable = true)
 |-- source: string (nullable = true)
 |-- text: string (nullable = true)
 |-- urlEntities: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- displayURL: string (nullable = true)
 |    |    |-- end: long (nullable = true)
 |    |    |-- expandedURL: string (nullable = true)
 |    |    |-- start: long (nullable = true)
 |    |    |-- url: string (nullable = true)
 |-- user: struct (nullable = true)
 |    |-- createdAt: string (nullable = true)
 |    |-- description: string (nullable = true)
 |    |-- descriptionURLEntities: array (nullable = true)
 |    |    |-- element: string (containsNull = true)
 |    |-- favouritesCount: long (nullable = true)
 |    |-- followersCount: long (nullable = true)
 |    |-- friendsCount: long (nullable = true)
 |    |-- id: long (nullable = true)
 |    |-- isContributorsEnabled: boolean (nullable = true)
 |    |-- isFollowRequestSent: boolean (nullable = true)
 |    |-- isGeoEnabled: boolean (nullable = true)
 |    |-- isProtected: boolean (nullable = true)
 |    |-- isVerified: boolean (nullable = true)
 |    |-- lang: string (nullable = true)
 |    |-- listedCount: long (nullable = true)
 |    |-- location: string (nullable = true)
 |    |-- name: string (nullable = true)
 |    |-- profileBackgroundColor: string (nullable = true)
 |    |-- profileBackgroundImageUrl: string (nullable = true)
 |    |-- profileBackgroundImageUrlHttps: string (nullable = true)
 |    |-- profileBackgroundTiled: boolean (nullable = true)
 |    |-- profileBannerImageUrl: string (nullable = true)
 |    |-- profileImageUrl: string (nullable = true)
 |    |-- profileImageUrlHttps: string (nullable = true)
 |    |-- profileLinkColor: string (nullable = true)
 |    |-- profileSidebarBorderColor: string (nullable = true)
 |    |-- profileSidebarFillColor: string (nullable = true)
 |    |-- profileTextColor: string (nullable = true)
 |    |-- profileUseBackgroundImage: boolean (nullable = true)
 |    |-- screenName: string (nullable = true)
 |    |-- showAllInlineMedia: boolean (nullable = true)
 |    |-- statusesCount: long (nullable = true)
 |    |-- timeZone: string (nullable = true)
 |    |-- translator: boolean (nullable = true)
 |    |-- url: string (nullable = true)
 |    |-- utcOffset: long (nullable = true)
 |-- userMentionEntities: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- end: long (nullable = true)
 |    |    |-- id: long (nullable = true)
 |    |    |-- name: string (nullable = true)
 |    |    |-- screenName: string (nullable = true)
 |    |    |-- start: long (nullable = true)

 */

/*
Sample Data
===========

{
  "createdAt": "Oct 18, 2015 1:01:47 PM",
  "id": 655647448245866500,
  "text": "RT @Thabo_Shingange: Asina mali asisebenzi #UPrising @TuksUPrising #TuksFeesMustFall",
  "source": "<a href=\"http://twitter.com\" rel=\"nofollow\">Twitter Web Client</a>",
  "isTruncated": false,
  "inReplyToStatusId": -1,
  "inReplyToUserId": -1,
  "isFavorited": false,
  "retweetCount": 0,
  "isPossiblySensitive": false,
  "contributorsIDs": [],
  "retweetedStatus": {
    "createdAt": "Oct 18, 2015 12:57:42 PM",
    "id": 655646420905644000,
    "text": "Asina mali asisebenzi #UPrising @TuksUPrising #TuksFeesMustFall",
    "source": "<a href=\"http://twitter.com/download/android\" rel=\"nofollow\">Twitter for Android</a>",
    "isTruncated": false,
    "inReplyToStatusId": -1,
    "inReplyToUserId": -1,
    "isFavorited": false,
    "place": {
      "name": "Pretoria",
      "countryCode": "ZA",
      "id": "0e587c59401d0a27",
      "country": "South Africa",
      "placeType": "city",
      "url": "https://api.twitter.com/1.1/geo/id/0e587c59401d0a27.json",
      "fullName": "Pretoria, South Africa",
      "boundingBoxType": "Polygon",
      "boundingBoxCoordinates": [
        [
          {
            "latitude": -25.915773,
            "longitude": 27.948304
          },
          {
            "latitude": -25.589432,
            "longitude": 27.948304
          },
          {
            "latitude": -25.589432,
            "longitude": 28.419829
          },
          {
            "latitude": -25.915773,
            "longitude": 28.419829
          }
        ]
      ]
    },
    "retweetCount": 1,
    "isPossiblySensitive": false,
    "contributorsIDs": [],
    "userMentionEntities": [
      {
        "name": "#UPrising",
        "screenName": "TuksUPrising",
        "id": 3973033815,
        "start": 32,
        "end": 45
      }
    ],
    "urlEntities": [],
    "hashtagEntities": [
      {
        "text": "UPrising",
        "start": 22,
        "end": 31
      },
      {
        "text": "TuksFeesMustFall",
        "start": 46,
        "end": 63
      }
    ],
    "mediaEntities": [],
    "currentUserRetweetId": -1,
    "user": {
      "id": 57374087,
      "name": "#TuksFeesMustFall",
      "screenName": "Thabo_Shingange",
      "location": "South Africa",
      "description": "#IWriteWhatILike\nStudent Activist\nPan Africanist\nSecretary: SASCO Tukkies Branch",
      "descriptionURLEntities": [],
      "isContributorsEnabled": false,
      "profileImageUrl": "http://pbs.twimg.com/profile_images/652389061592764416/NWvjzj6A_normal.jpg",
      "profileImageUrlHttps": "https://pbs.twimg.com/profile_images/652389061592764416/NWvjzj6A_normal.jpg",
      "isProtected": false,
      "followersCount": 386,
      "profileBackgroundColor": "090A0A",
      "profileTextColor": "333333",
      "profileLinkColor": "94D487",
      "profileSidebarFillColor": "DDFFCC",
      "profileSidebarBorderColor": "000000",
      "profileUseBackgroundImage": true,
      "showAllInlineMedia": false,
      "friendsCount": 56,
      "createdAt": "Jul 16, 2009 9:55:54 PM",
      "favouritesCount": 220,
      "utcOffset": 7200,
      "timeZone": "Pretoria",
      "profileBackgroundImageUrl": "http://pbs.twimg.com/profile_background_images/378800000162805661/OEdMK8VQ.jpeg",
      "profileBackgroundImageUrlHttps": "https://pbs.twimg.com/profile_background_images/378800000162805661/OEdMK8VQ.jpeg",
      "profileBannerImageUrl": "https://pbs.twimg.com/profile_banners/57374087/1444376646",
      "profileBackgroundTiled": true,
      "lang": "en",
      "statusesCount": 9343,
      "isGeoEnabled": true,
      "isVerified": false,
      "translator": false,
      "listedCount": 3,
      "isFollowRequestSent": false
    }
  },
  "userMentionEntities": [
    {
      "name": "#TuksFeesMustFall",
      "screenName": "Thabo_Shingange",
      "id": 57374087,
      "start": 3,
      "end": 19
    },
    {
      "name": "#UPrising",
      "screenName": "TuksUPrising",
      "id": 3973033815,
      "start": 53,
      "end": 66
    }
  ],
  "urlEntities": [],
  "hashtagEntities": [
    {
      "text": "UPrising",
      "start": 43,
      "end": 52
    },
    {
      "text": "TuksFeesMustFall",
      "start": 67,
      "end": 84
    }
  ],
  "mediaEntities": [],
  "currentUserRetweetId": -1,
  "user": {
    "id": 989746208,
    "name": "#TuksFeesMustFall",
    "screenName": "Amogelangkk",
    "location": "Ninapark",
    "description": "Better to die for an idea that shall live, than to live for an idea that shall die! #EFF #Orlando_
    Pirates BSc(Hons) Actuarial Science #AspiringActuary @EFFSCUP",
    "descriptionURLEntities": [],
    "isContributorsEnabled": false,
    "profileImageUrl": "http://pbs.twimg.com/profile_images/655364161283624960/fkpKRfyo_normal.jpg",
    "profileImageUrlHttps": "https://pbs.twimg.com/profile_images/655364161283624960/fkpKRfyo_normal.jpg",
    "url": "http://effighters.org.za",
    "isProtected": false,
    "followersCount": 236,
    "profileBackgroundColor": "7D237D",
    "profileTextColor": "333333",
    "profileLinkColor": "11E80E",
    "profileSidebarFillColor": "DDEEF6",
    "profileSidebarBorderColor": "FFFFFF",
    "profileUseBackgroundImage": true,
    "showAllInlineMedia": false,
    "friendsCount": 289,
    "createdAt": "Dec 5, 2012 4:24:27 AM",
    "favouritesCount": 451,
    "utcOffset": -1,
    "profileBackgroundImageUrl": "http://pbs.twimg.com/profile_background_images/378800000161345839/9VmfmCVm.jpeg",
    "profileBackgroundImageUrlHttps": "https://pbs.twimg.com/profile_background_images/378800000161345839/9VmfmCVm.jpeg",
    "profileBannerImageUrl": "https://pbs.twimg.com/profile_banners/989746208/1388690204",
    "profileBackgroundTiled": false,
    "lang": "en",
    "statusesCount": 3622,
    "isGeoEnabled": false,
    "isVerified": false,
    "translator": false,
    "listedCount": 1,
    "isFollowRequestSent": false
  }
}

 */