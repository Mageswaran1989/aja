package org.aja.tej.examples.streaming.twitter

import com.google.gson.Gson
import org.aja.tej.utils.{TejUtils, TejTwitterUtils}
import org.apache.spark.sql.{Row, AnalysisException, SQLContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

/**
 * Created by mageswaran on 18/10/15.
 */

/*
--consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw
--consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3
--accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE
--accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5
 */

case class HashTag(tag: String)

object PrintTweets {

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

    //Each RDD can have either 0 or N tweets, since each stream is an continuous sequence of RDD
    twitterStream.foreachRDD(rdd => {
      println()
      println()
      println()
      println()
      println()
      println()
      println()

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

      println("1.>>>>>>>>>>>  Raw Tweets")
      rdd.foreach(println)

      println("2.>>>>>>>>>>>" + " Count: " + count + " Size: " + rdd.count() )
      if (printSchema && rdd.count() > 0) {
        println("3.>>>>>>>>>>> Tweet scema")
        df.printSchema()
        printSchema = false
      } else {
        println("3.>>>>>>>>>>> Tweet schema skipped")
      }

      println("4.>>>>>>>>>>> Print Tweet tabel")
      df.show()

      try {
        println("5.>>>>>>>>>>> Hashtags")
        df.select("hashtagEntities").foreach(println)

        println("6.>>>>>>>>>>> TagTabel")
        df.explode($"hashtagEntities"){
          //case Row(hashtags: mutable.WrappedArray[String]) => println(hashtags); hashtags.map(HashTag(_))
          case Row(hashtags: Seq[Row]) => hashtags.map(tagRow => HashTag(tagRow(2).asInstanceOf[String]))
        }.select("tag").show()
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
Sample tweet

{
"createdAt":"Feb 6, 2016 7:17:56 PM",
"id":695967162570133504,
"text":"@KPhoner1208 \n頭悪いは根本的、アホは意識すればなおる",
"source":"\u003ca href\u003d\"http://twitter.com/download/iphone\" rel\u003d\"nofollow\"\u003eTwitter for iPhone\u003c/a\u003e",
"isTruncated":false,
"inReplyToStatusId":695966811141984256,
"inReplyToUserId":3073171866,
"isFavorited":false,
"inReplyToScreenName":"KPhoner1208",
"retweetCount":0,
"isPossiblySensitive":false,
"contributorsIDs":[],
"userMentionEntities":[{"name":"けー ご","screenName":"KPhoner1208","id":3073171866,"start":0,"end":12}],
"urlEntities":[],
"hashtagEntities":[],
"mediaEntities":[],
"currentUserRetweetId":-1,
"user":{"id":1896320876,
        "name":"純はアホ毛でジョン",
        "screenName":"IJun0523",
        "location":"岡高受かって東大行きます",
        "description":"愛知の中3音ゲーマー。岡高志望。メンサとやらに興味を持ち始めた。相棒→@MOB_Torara",
        "descriptionURLEntities":[],
        "isContributorsEnabled":false,
        "profileImageUrl":"http://pbs.twimg.com/profile_images/683580733214998529/xAvqNbEL_normal.jpg",
        "profileImageUrlHttps":"https://pbs.twimg.com/profile_images/683580733214998529/xAvqNbEL_normal.jpg",
        "isProtected":false,"followersCount":4932,"profileBackgroundColor":"C0DEED","profileTextColor":"333333",
        "profileLinkColor":"0084B4",
        "profileSidebarFillColor":"DDEEF6",
        "profileSidebarBorderColor":"C0DEED",
        "profileUseBackgroundImage":true,
        "showAllInlineMedia":false,
        "friendsCount":3958,
        "createdAt":"Sep 23, 2013 11:35:18 AM",
        "favouritesCount":73608,"utcOffset":-1,
        "profileBackgroundImageUrl":"http://abs.twimg.com/images/themes/theme1/bg.png",
        "profileBackgroundImageUrlHttps":"https://abs.twimg.com/images/themes/theme1/bg.png",
        "profileBannerImageUrl":"https://pbs.twimg.com/profile_banners/1896320876/1430910972",
        "profileBackgroundTiled":false,"lang":"ja","statusesCount":81371,"isGeoEnabled":true,
        "isVerified":false,"translator":false,"listedCount":47,
        "isFollowRequestSent":false
        }
}
 */

/*
Tweet Schema: That's a big list indeed!

root
 |-- contributorsIDs: array (nullable = true)
 |    |-- element: string (containsNull = true)
 |-- createdAt: string (nullable = true)                    "Feb 6, 2016 7:17:56 PM"
 |-- currentUserRetweetId: long (nullable = true)
 |-- geoLocation: struct (nullable = true)
 |    |-- latitude: double (nullable = true)
 |    |-- longitude: double (nullable = true)
 |-- hashtagEntities: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- end: long (nullable = true)
 |    |    |-- start: long (nullable = true)
 |    |    |-- text: string (nullable = true)
 |-- id: long (nullable = true)                            695967162570133504
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