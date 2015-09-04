package org.aja.tej.examples.usecases.audioscrobbler.recommendations

import org.apache.spark.mllib.recommendation._
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by mageswaran on 22/8/15.
 */
object ALSExample extends App{

  val getSparkContext = {
    val conf = new SparkConf().setAppName("MLLib ALS").setMaster("local[4]" /*"spark://myhost:7077"*/)
    new SparkContext(conf)
  }

  val sc = getSparkContext

  //userID artistID play_count
  val rawUserArtistData = sc.textFile("data/profiledata_06-May-2005/xaa", 4 )
  //val rawUserArtistData = sc.textFile("data/profiledata_06-May-2005/user_artist_data.txt", 4 ) //Uncomment me if you have a cluster!
  //Changing the user_artist_data.txt => xaa, to manage heap size on personal computer
  //Linux utility "split" is run on user_artist_data.txt and splitted into major 4 parts
  // From out stats: 24296858 / 4 => 6074214 lines / split
  // $split -l 6074214 user_artist_data.txt
  //Changing the user_artist_data.txt => xaa

  val rawArtistData = sc.textFile("data/profiledata_06-May-2005/artist_data.txt", 4 )
  val rawArtistAlias = sc.textFile("data/profiledata_06-May-2005/artist_alias.txt",4)


  rawUserArtistData.top(10).foreach(println)
  //  9875 979 41
  //  9875 976 3
  //  9875 949 29
  //  9875 930 1
  //  9875 929 1
  //  9875 92 1
  //  9875 910 1
  //  9875 891 32
  //  9875 868 12

  println("Some statistics on user_artist_data.txt")

  println(rawUserArtistData.map(_.split(" ")(0).toDouble).stats())
  println(rawUserArtistData.map(_.split(" ")(1).toDouble).stats())

//  Some statistics on user_artist_data.txt
//  (count: 24296858, mean: 1947573.265353, stdev: 496000.544975, max: 2443548.000000, min: 90.000000)
//  (count: 24296858, mean: 1718704.093757, stdev: 2539389.040171, max: 10794401.000000, min: 1.000000)

  val artistById = rawArtistData.map(_.split("\t")).map(artistData => (artistData(0).toInt, artistData(1).trim))
  artistById.take(10).foreach(println)

  //Doint it in a complicated way...oh sorry the correct way!
  val artistByID = rawArtistData.flatMap { line =>
    val (id, name) = line.span(_ != '\t')
    if (name.isEmpty) {
      None
    } else {
      try {
        Some((id.toInt, name.trim))
      } catch {
        case e: NumberFormatException => None
      }
    }
  }

  println()
  artistByID.take(10).foreach(println)

  val artistAlias = rawArtistAlias.flatMap { line =>
    val tokens = line.split('\t')
    if (tokens(0).isEmpty) {
      None
    } else {
      Some((tokens(0).toInt, tokens(1).toInt))
    }
  }.collectAsMap()

  println(artistByID.lookup(6803336).head)
  println(artistByID.lookup(1000010).head)

  //make the artistAlias as broadcast variable so that each executor gets a copy and stores as a raw Java
  //objects for fast access on any task it run thus reducing the network traffic and memory
  val bArtistAlias = sc.broadcast(artistAlias)

  val trainData = rawUserArtistData.map { line =>
    val Array(userID, artistID, count) = line.split(' ').map(_.toInt)
    val finalArtistID =
      bArtistAlias.value.getOrElse(artistID, artistID)
    Rating(userID, finalArtistID, count)
  }.cache()
  //cached on each executor, need not to be computed again

  //This constructs model as a MatrixFactorizationModel.
  /*
    • rank = 10: the number of latent factors in the model, or equivalently, the number
  of columns k in the user-feature and product-feature matrices. In non-trivial cases,
  this is also their rank.
  • iterations = 5: the number of iterations that the factorization runs. More iterations
  take more time but may produce a better factorization.
  • lambda = 0.01: a standard overfitting parameter. Higher values resist overfitting,
  but values that are too high hurt the factorization’s accuracy.
  • alpha = 1.0: controls the relative weight of observed versus unobserved user-
  product interactions in the factorization.
   */
  val model = ALS.trainImplicit(trainData, 10, 5, 0.01, 1.0)
  println(model.userFeatures.mapValues(_.mkString(", ")).first)

  val rawArtistsForUser = rawUserArtistData.map(_.split(' ')).
    filter { case Array(user,_,_) => user.toInt == 1000057 }

  val existingProducts =
    rawArtistsForUser.map { case Array(_,artist,_) => artist.toInt }.
      collect().toSet

  artistByID.filter { case (id, name) =>
    existingProducts.contains(id)
  }.values.collect().foreach(println)

  val recommendations = model.recommendProducts(1000057, 5)
  recommendations.foreach(println)

  val recommendedProductIDs = recommendations.map(_.product).toSet
  artistByID.filter { case (id, name) =>
    recommendedProductIDs.contains(id)
  }.values.collect().foreach(println)

}

