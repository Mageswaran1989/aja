package org.aja.tej.examples.streaming.twitter

import com.google.gson.Gson
import org.aja.tej.utils.{TejUtils, TejTwitterUtils}
import org.anormcypher.{Cypher, Neo4jREST}
import org.apache.spark.sql.{AnalysisException, Row, SQLContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import play.api.libs.ws.ning

/**
 * Created by mageswaran on 15/5/16.
 */

/*
20
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
*/

object Neo4jRestConnection {
  implicit val wsclient = ning.NingWSClient()
  // Setup the Rest Client
  implicit val connection = Neo4jREST("localhost", 7474, "/db/data/", "neo4j", "aja")
  // Provide an ExecutionContext
  implicit val ec = scala.concurrent.ExecutionContext.global
}

/*
{
  "createdAt": "May 15, 2016 11:58:12 AM",
  "id": 731732898773012500,
  "text": ". . .\nHahaha! https://t.co/Vz9Ie4Lw5U",
  "source": "<a href=\"http://twitter.com/download/iphone\" rel=\"nofollow\">Twitter for iPhone</a>",
  "isTruncated": false,
  "inReplyToStatusId": -1,
  "inReplyToUserId": -1,
  "isFavorited": false,
  "isRetweeted": false,
  "favoriteCount": 0,
  "place": {
    "name": "Zaragosa",
    "countryCode": "PH",
    "id": "01e938de0821321d",
    "country": "Republika ng Pilipinas",
    "placeType": "city",
    "url": "https://api.twitter.com/1.1/geo/id/01e938de0821321d.json",
    "fullName": "Zaragosa, Central Luzon",
    "boundingBoxType": "Polygon",
    "boundingBoxCoordinates": [
      [
        {
          "latitude": 15.394902,
          "longitude": 120.743542
        },
        {
          "latitude": 15.509868,
          "longitude": 120.743542
        },
        {
          "latitude": 15.509868,
          "longitude": 120.852049
        },
        {
          "latitude": 15.394902,
          "longitude": 120.852049
        }
      ]
    ]
  },
  "retweetCount": 0,
  "isPossiblySensitive": false,
  "lang": "tl",
  "contributorsIDs": [],
  "userMentionEntities": [],
  "urlEntities": [],
  "hashtagEntities": [],
  "mediaEntities": [
    {
      "id": 731732875096162300,
      "url": "https://t.co/Vz9Ie4Lw5U",
      "mediaURL": "http://pbs.twimg.com/media/CiejLEqVAAAjfHc.jpg",
      "mediaURLHttps": "https://pbs.twimg.com/media/CiejLEqVAAAjfHc.jpg",
      "expandedURL": "http://twitter.com/XuilienIsMyName/status/731732898773012480/photo/1",
      "displayURL": "pic.twitter.com/Vz9Ie4Lw5U",
      "sizes": {
        "0": {
          "width": 150,
          "height": 150,
          "resize": 101
        },
        "1": {
          "width": 340,
          "height": 340,
          "resize": 100
        },
        "2": {
          "width": 600,
          "height": 600,
          "resize": 100
        },
        "3": {
          "width": 640,
          "height": 640,
          "resize": 100
        }
      },
      "type": "photo",
      "start": 14,
      "end": 37
    }
  ],
  "extendedMediaEntities": [
    {
      "videoAspectRatioWidth": 0,
      "videoAspectRatioHeight": 0,
      "videoDurationMillis": 0,
      "videoVariants": [],
      "id": 731732875096162300,
      "url": "https://t.co/Vz9Ie4Lw5U",
      "mediaURL": "http://pbs.twimg.com/media/CiejLEqVAAAjfHc.jpg",
      "mediaURLHttps": "https://pbs.twimg.com/media/CiejLEqVAAAjfHc.jpg",
      "expandedURL": "http://twitter.com/XuilienIsMyName/status/731732898773012480/photo/1",
      "displayURL": "pic.twitter.com/Vz9Ie4Lw5U",
      "sizes": {
        "0": {
          "width": 150,
          "height": 150,
          "resize": 101
        },
        "1": {
          "width": 340,
          "height": 340,
          "resize": 100
        },
        "2": {
          "width": 600,
          "height": 600,
          "resize": 100
        },
        "3": {
          "width": 640,
          "height": 640,
          "resize": 100
        }
      },
      "type": "photo",
      "start": 14,
      "end": 37
    }
  ],
  "symbolEntities": [],
  "currentUserRetweetId": -1,
  "user": {
    "id": 710053480971444200,
    "name": "Xuilien",
    "screenName": "XuilienIsMyName",
    "description": "I'm not perfect. I'm not your ideal girl. Im not you first. but i am preparing my self to be you last",
    "descriptionURLEntities": [],
    "isContributorsEnabled": false,
    "profileImageUrl": "http://pbs.twimg.com/profile_images/729439092987940864/UBMCTreI_normal.jpg",
    "profileImageUrlHttps": "https://pbs.twimg.com/profile_images/729439092987940864/UBMCTreI_normal.jpg",
    "isDefaultProfileImage": false,
    "isProtected": false,
    "followersCount": 13,
    "profileBackgroundColor": "F5F8FA",
    "profileTextColor": "333333",
    "profileLinkColor": "2B7BB9",
    "profileSidebarFillColor": "DDEEF6",
    "profileSidebarBorderColor": "C0DEED",
    "profileUseBackgroundImage": true,
    "isDefaultProfile": true,
    "showAllInlineMedia": false,
    "friendsCount": 5,
    "createdAt": "Mar 16, 2016 4:11:56 PM",
    "favouritesCount": 83,
    "utcOffset": -1,
    "profileBackgroundImageUrl": "",
    "profileBackgroundImageUrlHttps": "",
    "profileBannerImageUrl": "https://pbs.twimg.com/profile_banners/710053480971444224/1462746807",
    "profileBackgroundTiled": false,
    "lang": "en",
    "statusesCount": 128,
    "isGeoEnabled": true,
    "isVerified": false,
    "translator": false,
    "listedCount": 0,
    "isFollowRequestSent": false
  },
  "quotedStatusId": -1
}
 */

case class UserNode(name: String, tag: String, favoritesCount: Long,
                    followersCount: Long, friendsCount: Long, lang: String, location: String,
                    protectedTweets: Boolean, screenName: String, statusCount: Int, timeZone: String)
case class TweetNode(contributerScreenName: String, coordinates: Map[String, String], createdAtUTC: String,
                     favoritesCount: Int, inReplyToScreenName: String, lang: String, sensitive: Boolean,
                     retweetCount: Int)

object Neo4jConnector {

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
    //sc.setLogLevel("OFF")

    val ssc = new StreamingContext(sc, Seconds(1))

    val twitterStream = TwitterUtils.createStream(ssc, TejTwitterUtils.getAuth)
      .map(gson.toJson(_))


    //twitterStream.mapPartitions()

    twitterStream.foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        import Neo4jRestConnection._
        partitionOfRecords.foreach(tweet => {
          //InsertEachTweetToGraphDB.executeCQL(_)
          import play.api.libs.json._

          val jsonTweet = Json.parse(tweet)

          val tId = (jsonTweet \"user" \ "id").as[Long]
          val userName = (jsonTweet \ "user" \ "name").as[String]
          val userScreenName = (jsonTweet \"user" \ "screenName").as[String]
          val hashTagsArray = (jsonTweet \ "hashtagEntities").as[JsArray]
          var userHashTags = hashTagsArray.\\("text")
          val userFavouritesCount = (jsonTweet \ "user" \ "favouritesCount").as[Int]
          val userLang =  (jsonTweet \ "user" \ "lang").as[String]
          val userStatusesCount = (jsonTweet \ "user" \ "statusesCount").as[Long]
          val userFriendsCount = (jsonTweet \ "user" \ "friendsCount").as[Long]
          val userFollowersCount = (jsonTweet \ "user" \ "followersCount").as[Long]
          val retweetCount = (jsonTweet \ "retweetCount").as[Long]
          val isPossiblySensitive = (jsonTweet \ "isPossiblySensitive").as[Boolean]


          val userCQL =
            s"""MERGE (u:TestUser {uId: '${tId}',
               |uName: '${userName.replace("'", "`")}',
               |screenName : '${userScreenName}',
               |favouritesCount : '${userFavouritesCount}',
               |statusesCount : '${userStatusesCount}',
               |friendsCount : '${userFriendsCount}'})""".stripMargin

          if (!Cypher(userCQL).execute())  {
            println(userCQL)
            println("userCQL failed to execute!")
            System.exit(-1)
          }

          if (!userHashTags.isEmpty) {
            userHashTags.foreach(tag => {
              val tagCQL =
                s"""MERGE (t:Tag {tName: '${tag}'})
                   |MATCH (u:TestUser {uId: '${tId}'})
                   |MERGE (u)-[:TWEETS_ON_TAG]-> (t)
                 """.stripMargin

              println(tagCQL)
              if (!Cypher(tagCQL).execute())  {
                println(tagCQL)
                println("tagCQL failed to execute!")
                System.exit(-1)
              }
            })
          }

          try {
            val pId = (jsonTweet \"place" \ "id").as[Long]
            val placeName = (jsonTweet \ "place" \ "name").as[String]
            val placeCountryCode = (jsonTweet \ "place" \ "countryCode").as[String]
            val placeId = (jsonTweet \ "place" \ "id").as[String]
            val placeCountry = (jsonTweet \ "place" \ "country").as[String]
            val placePlaceType = (jsonTweet \ "place" \ "placeType").as[String]
            val placeFullName = (jsonTweet \ "place" \ "fullName").as[String]
            val geoLocationLatitude = (jsonTweet \ "geoLocation" \ "latitude").as[Double]
            val geoLocationLongitude = (jsonTweet \ "geoLocation" \ "longitude").as[Double]


            val placeCQL =
              s"""MERGE (l:TestPlace {pId: '${pId}',
                 |pName: '${placeName}',
                 |placeCountryCode : '${placeCountryCode}',
                 |placeCountry : '${placeCountry}',
                 |placePlaceType : '${placePlaceType}',
                 |placeFullName : '${placeFullName}',
                 |geoLocationLatitude : '${geoLocationLatitude}',
                 |geoLocationLongitude : '${geoLocationLongitude}'})
                 |MATCH ((u:TestUser {uId: '${tId}'})
                 |MERGE (u) -[:LIVES_IN]-> (l)""".stripMargin


            if (!Cypher(placeCQL).execute()) {
              println(placeCQL)
              println("placeCQL failed to execute!")
              System.exit(-1)
            }

          } catch {
            case e: JsResultException => println(" !!! User place is not avaiable !!!")
          }

          try {
            val inReplyToScreenName = (jsonTweet \ "inReplyToScreenName").as[String]
            val inReplyToStatusId = (jsonTweet \ "inReplyToStatusId").as[Long]
            val inReplyToUserId = (jsonTweet \ "inReplyToUserId").as[Long]


            val replyCQL =
              s"""MATCH ((u2:TestUser {uId: '${tId}'})
                 |MATCH ((u1:TestUser {uId: '${inReplyToUserId}'})
                 |MERGE (u1) -[:REPLYS_TO]-> (u2)""".stripMargin

            if (!Cypher(replyCQL).execute()) {
              println(replyCQL)
              println("replyCQL failed to execute!")
              System.exit(-1)
            }
          } catch {
            case e: JsResultException => println("!!! No reply details for this tweet !!!")
          }
        })
      })
    })

    ssc.start()
    ssc.awaitTermination()

  }
}