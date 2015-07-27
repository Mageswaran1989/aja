#! /opt/spark/bin/spark-shell -deiver-memory 4g

// user id, movie id, rating, timestamp
val PATH = "/opt/datasets/ml-100k"

val rawData = sc.textFile(PATH + "/u.data")

rawData.first()

val rawRatings = rawData.map(_.split("\t").take(3))

import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating

val ratings = rawRatings.map {
                case Array(userID, movieID, rating) => 
                  Rating(userID.toInt, movieID.toInt, rating.toDouble)
              }

val model = ALS.train(ratings, 50, 10, 0.01)

model.userFeatures
model.userFeatures.count

model.productFeatures.count

// User Recomendations
// user item
val predictedRating = model.predict(789, 123)

val userId = 789
val K = 10
val topKRecs = model.recommendProducts(userId, K)

println(topKRecs.mkString("\n"))

/*
              movie id | movie title | release date | video release date |
              IMDb URL | unknown | Action | Adventure | Animation |
              Children's | Comedy | Crime | Documentary | Drama | Fantasy |
              Film-Noir | Horror | Musical | Mystery | Romance | Sci-Fi |
              Thriller | War | Western |
*/

val movies = sc.textFile(PATH + "/u.item")

val titles = movies.map(line => line.split("\\|").take(2)).map(array => (array(0).toInt, array(1))).collectAsMap()

//1682|Scream of Stone (Schrei aus Stein) (1991)|08-Mar-1996||http://us.imdb.com/M/title-exact?Schrei%20aus%20Stein%20(1991)|0|0|0|0|0|0|0|0|1|0|0|0|0|0|0|0|0|0|0
titles(1682)

val moviesForUser = ratings.keyBy(_.user).lookup(789)
println(moviesForUser.size)

moviesForUser.sortBy(-_.rating).take(10).map(rating => (titles(rating.product), rating.rating)).foreach(println)

topKRecs.map(rating => (titles(rating.product), rating.rating)).foreach(println)

//Item Based Recomendations

import org.jblas.DoubleMatrix
val aMatrix = new DoubleMatrix(Array(1.0, 2.0, 3.0))

def cosineSimilarity(vec1: DoubleMatrix, vec2: DoubleMatrix): Double =
{
  vec1.dot(vec2) / (vec1.norm2() * vec2.norm2())
}

val itemId = 567
val itemFactor = model.productFeatures.lookup(itemId).head
val itemVector = new DoubleMatrix(itemFactor)
cosineSimilarity(itemVector, itemVector)

val sims = model.productFeatures.map{ case (id, factor) =>
  val factorVector = new DoubleMatrix(factor)
  val sim = cosineSimilarity(factorVector, itemVector)
  (id, sim)
}

// recall we defined K = 10 earlier
val sortedSims = sims.top(K)(Ordering.by[(Int, Double), Double] { 
case(id, similarity) => similarity })

println(sortedSims.take(10).mkString("\n))


