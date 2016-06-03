package org.aja.dataset

import org.apache.spark.SparkContext
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

/**
 * Created by mageswaran on 25/3/16.
 */

class TinySocialGraphDS(sc: SparkContext) {
  // Load some data into RDDs
  val people = sc.textFile("data/graphx/tiny_social_network/people.csv")
  val links = sc.textFile("data/graphx/tiny_social_network/links.csv")

  // Parse the csv files into new RDDs
  case class Person(name: String, age: Int)
  type Connection = String

  val peopleRDD: RDD[(VertexId, Person)] = people map { line =>
    val row = line split ','
    (row(0).toLong, Person(row(1), row(2).toInt))
  }
  val linksRDD: RDD[Edge[Connection]] = links map {line =>
    val row = line split ','
    Edge(row(0).toInt, row(1).toInt, row(2))
  }

  // Create the social graph of people
  val tinySocial: Graph[Person, Connection] = Graph(peopleRDD, linksRDD)
}

object TinySocialGraphDS {
def apply(sc: SparkContext) = new TinySocialGraphDS((sc))
}
