package org.aja.tej.examples.streaming.twitter

/**
 * Created by mageswaran on 30/7/15.
 */
/**
Examine the collected tweets and trains a model based on them.
The second program examines the data found in tweets and trains a language classifier using K-Means
clustering on the tweets:

Examine - Spark SQL is used to gather data about the tweets -- to look at a few of them, and to count the total number
of tweets for the most common languages of the user.
Train - Spark MLLib is used for applying the K-Means algorithm for clustering the tweets. The number of clusters and the number of iterations of algorithm are configurable.
After training the model, some sample tweets from the different clusters are shown.
 */

//%  ${YOUR_SPARK_HOME}/bin/spark-submit \
//--class "com.databricks.apps.twitter_classifier.ExamineAndTrain" \
//--master ${YOUR_SPARK_MASTER:-local[4]} \
//target/scala-2.10/spark-twitter-lang-classifier-assembly-1.0.jar \
//"${YOUR_TWEET_INPUT:-/tmp/tweets/tweets*/part-*}" \
//${OUTPUT_MODEL_DIR:-/tmp/tweets/model} \
//${NUM_CLUSTERS:-10} \
//${NUM_ITERATIONS:-20}

//Program Arguments:
// data/tweets/tweets*/part-* data/tweets/model 25 20

import com.google.gson.{GsonBuilder, JsonParser}
import org.aja.tej.utils.TejTwitterUtils
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object ExamineAndTrain {
  val jsonParser = new JsonParser()
  val gson = new GsonBuilder().setPrettyPrinting().create()

  def main(args: Array[String]) {
    // Process program arguments and set properties
    if (args.length < 3) {
      System.err.println("Usage: " + this.getClass.getSimpleName +
        " <tweetInput> <outputModelDir> <numClusters> <numIterations>")
      System.exit(1)
    }
    val Array(tweetInput, outputModelDir, TejTwitterUtils.IntParam(numClusters), TejTwitterUtils.IntParam(numIterations)) = args

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    // Pretty print some of the tweets.
    val tweets = sc.textFile(tweetInput)
    println("------------Sample JSON Tweets-------")
    for (tweet <- tweets.take(5)) {
      println(gson.toJson(jsonParser.parse(tweet)))
    }

    /*
    Spark SQL can load JSON files and infer the schema based on that data. Here is the code to load the json
    files, register the data in the temp table called "tweetTable" and print out the schema based on that.
     */
    val tweetTable = sqlContext.read.json(tweetInput).cache()
    tweetTable.registerTempTable("tweetTable")

    println("------Tweet table Schema---")
    tweetTable.printSchema()

    //Now, look at the text of 10 sample tweets.
    println("----Sample Tweet Text-----")
    sqlContext.sql("SELECT text FROM tweetTable LIMIT 10").collect().foreach(println)

    //View the user language, user name, and text for 10 sample tweets.
    println("------Sample Lang, Name, text---")
    sqlContext.sql("SELECT user.lang, user.name, text FROM tweetTable LIMIT 1000").collect().foreach(println)

    //Finally, show the count of tweets by user language. This can help determine the number of clusters that is ideal
    // for this dataset of tweets.
    println("------Total count by languages Lang, count(*)---")
    sqlContext.sql("SELECT user.lang, COUNT(*) as cnt FROM tweetTable GROUP BY user.lang ORDER BY cnt DESC LIMIT 25").collect.foreach(println)

    println("--- Training the model and persist it")
    val texts = sqlContext.sql("SELECT text from tweetTable").map(_.toString)

    // Cache the vectors RDD since it will be used for all the KMeans iterations.
    //First, we need to featurize the Tweet text. MLLib has a HashingTF class that does that:
    val vectors = texts.map(TejTwitterUtils.featurize).cache()

    vectors.count()  // Calls an action on the RDD to populate the vectors cache.

    val model = KMeans.train(vectors, numClusters, numIterations)
    sc.makeRDD(model.clusterCenters, numClusters).saveAsObjectFile(outputModelDir)

    val some_tweets = texts.take(100)
    println("----Example tweets from the clusters")
    for (i <- 0 until numClusters) {
      println(s"\nCLUSTER $i:")
      some_tweets.foreach { t =>
        if (model.predict(TejTwitterUtils.featurize(t)) == i) {
          println(t)
        }
      }
    }
  }
}




/*

------------Sample JSON Tweets-------
{
  "createdAt": "Sep 9, 2015 9:38:27 AM",
  "id": 641463150978666497,
  "text": "RT @louisquads: @ToriKelly \nyou\u0027re so genuine and kind.\nyou always inspire me to be\na better person. thank you.\nfollow me? lot\u0027s of love. 6",
  "source": "\u003ca href\u003d\"https://about.twitter.com/products/tweetdeck\" rel\u003d\"nofollow\"\u003eTweetDeck\u003c/a\u003e",
  "isTruncated": false,
  "inReplyToStatusId": -1,
  "inReplyToUserId": -1,
  "isFavorited": false,
  "retweetCount": 0,
  "isPossiblySensitive": false,
  "contributorsIDs": [],
  "retweetedStatus": {
    "createdAt": "Sep 9, 2015 9:36:28 AM",
    "id": 641462652439527424,
    "text": "@ToriKelly \nyou\u0027re so genuine and kind.\nyou always inspire me to be\na better person. thank you.\nfollow me? lot\u0027s of love. 6",
    "source": "\u003ca href\u003d\"http://twitter.com\" rel\u003d\"nofollow\"\u003eTwitter Web Client\u003c/a\u003e",
    "isTruncated": false,
    "inReplyToStatusId": -1,
    "inReplyToUserId": 19017237,
    "isFavorited": false,
    "inReplyToScreenName": "ToriKelly",
    "retweetCount": 68,
    "isPossiblySensitive": false,
    "contributorsIDs": [],
    "userMentionEntities": [
      {
        "name": "Tori Kelly",
        "screenName": "ToriKelly",
        "id": 19017237,
        "start": 0,
        "end": 10
      }
    ],
    "urlEntities": [],
    "hashtagEntities": [],
    "mediaEntities": [],
    "currentUserRetweetId": -1,
    "user": {
      "id": 1303440043,
      "name": "Thanks Hayes! / H.",
      "screenName": "louisquads",
      "location": "",
      "descriptionURLEntities": [],
      "isContributorsEnabled": false,
      "profileImageUrl": "http://pbs.twimg.com/profile_images/641445611502481408/bP4lX9uc_normal.png",
      "profileImageUrlHttps": "https://pbs.twimg.com/profile_images/641445611502481408/bP4lX9uc_normal.png",
      "isProtected": false,
      "followersCount": 1005,
      "profileBackgroundColor": "A30834",
      "profileTextColor": "333333",
      "profileLinkColor": "000000",
      "profileSidebarFillColor": "DDFFCC",
      "profileSidebarBorderColor": "FFFFFF",
      "profileUseBackgroundImage": true,
      "showAllInlineMedia": false,
      "friendsCount": 659,
      "createdAt": "Mar 26, 2013 4:05:53 PM",
      "favouritesCount": 1786,
      "utcOffset": -25200,
      "timeZone": "Pacific Time (US \u0026 Canada)",
      "profileBackgroundImageUrl": "http://abs.twimg.com/images/themes/theme16/bg.gif",
      "profileBackgroundImageUrlHttps": "https://abs.twimg.com/images/themes/theme16/bg.gif",
      "profileBackgroundTiled": true,
      "lang": "pt",
      "statusesCount": 4004,
      "isGeoEnabled": false,
      "isVerified": false,
      "translator": false,
      "listedCount": 3,
      "isFollowRequestSent": false
    }
  },
  "userMentionEntities": [
    {
      "name": "Thanks Hayes! / H.",
      "screenName": "louisquads",
      "id": 1303440043,
      "start": 3,
      "end": 14
    },
    {
      "name": "Tori Kelly",
      "screenName": "ToriKelly",
      "id": 19017237,
      "start": 16,
      "end": 26
    }
  ],
  "urlEntities": [],
  "hashtagEntities": [],
  "mediaEntities": [],
  "currentUserRetweetId": -1,
  "user": {
    "id": 2591454660,
    "name": "dani",
    "screenName": "pokebello",
    "location": "",
    "descriptionURLEntities": [],
    "isContributorsEnabled": false,
    "profileImageUrl": "http://pbs.twimg.com/profile_images/638796470402056192/WYRpTjVh_normal.jpg",
    "profileImageUrlHttps": "https://pbs.twimg.com/profile_images/638796470402056192/WYRpTjVh_normal.jpg",
    "isProtected": false,
    "followersCount": 7,
    "profileBackgroundColor": "C0DEED",
    "profileTextColor": "333333",
    "profileLinkColor": "000000",
    "profileSidebarFillColor": "DDEEF6",
    "profileSidebarBorderColor": "C0DEED",
    "profileUseBackgroundImage": true,
    "showAllInlineMedia": false,
    "friendsCount": 10,
    "createdAt": "Jun 27, 2014 8:07:49 PM",
    "favouritesCount": 1,
    "utcOffset": -10800,
    "timeZone": "Santiago",
    "profileBackgroundImageUrl": "http://abs.twimg.com/images/themes/theme1/bg.png",
    "profileBackgroundImageUrlHttps": "https://abs.twimg.com/images/themes/theme1/bg.png",
    "profileBackgroundTiled": false,
    "lang": "pt",
    "statusesCount": 375,
    "isGeoEnabled": false,
    "isVerified": false,
    "translator": false,
    "listedCount": 0,
    "isFollowRequestSent": false
  }
}

  ------Tweet table Schema---
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
 |    |-- geoLocation: struct (nullable = true)
 |    |    |-- latitude: double (nullable = true)
 |    |    |-- longitude: double (nullable = true)
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


----Sample Tweet Text-----
[RT @louisquads: @ToriKelly
you're so genuine and kind.
you always inspire me to be
a better person. thank you.
follow me? lot's of love. 6]

------Sample Lang, Name, text---
[pt,dani,RT @louisquads: @ToriKelly
you're so genuine and kind.
you always inspire me to be
a better person. thank you.
follow me? lot's of love. 6]

------Total count by languages Lang, count(*)---
[en,4947]
[ja,1931]
[es,1778]
[pt,634]
[ar,256]
[ko,162]
[id,129]
[ru,119]
[th,80]
[fr,58]
[en-gb,39]
[tr,37]
[pl,32]
[de,30]
[it,16]
[nl,14]
[zh-tw,6]
[es-MX,6]
[vi,5]
[hu,5]
[zh-cn,3]
[el,3]
[en-GB,3]
[fi,3]
[sv,2]

--- Training the model and persist it
15/09/09 09:49:00 WARN BLAS: Failed to load implementation from: com.github.fommil.netlib.NativeSystemBLAS
15/09/09 09:49:00 WARN BLAS: Failed to load implementation from: com.github.fommil.netlib.NativeRefBLAS
----Example tweets from the clusters

CLUSTER 0:

CLUSTER 1:
[OMG !1! YOUR BIGGEST FAN RIGHT HERE !!1!11  https://t.co/fh8e9ebgyJ]
[RT @ashishbadshah: ABVP ensured FYUP was scrapped .. #ABVP1144 http://t.co/dXHwwVl903]
[RT @el_viejopaulino: GOOOOOOOOOOL HIJOS DE SU PUTA MADGHE!!! http://t.co/X0ekx7N0yN]
[RT @rosebear424: カミュさんがいちばんだいすきちゃん！
愛知県の大学１年生(*´・ω・`*)
プリライ行かれる方もぜひ！

RT or ✩
 */