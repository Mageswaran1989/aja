package org.aja.tej.examples.spark

import org.aja.tej.utils.TejUtils

/**
  * Created by mdhandapani on 6/1/16.
  */

//  Join of two or more data sets is one of the most widely used operations you do with your data, but in distributed
// systems it can be a huge headache. In general, since your data are distributed among many nodes, they have to be
// shuffled before a join that causes significant network I/O and slow performance.
//Fortunately, if you need to join a large table (fact) with relatively small tables (dimensions) i.e. to perform a
// star-schema join you can avoid sending all data of the large table over the network. This type of join is called
// map-side join in Hadoop community. In other distributed systems, it is often called replicated or broadcast join.

object BroadCastExample extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  // Fact table
  val flights = sc.parallelize(List(
    ("SEA", "JFK", "DL", "418",  "7:00"),
    ("SFO", "LAX", "AA", "1250", "7:05"),
    ("SFO", "JFK", "VX", "12",   "7:05"),
    ("JFK", "LAX", "DL", "424",  "7:10"),
    ("LAX", "SEA", "DL", "5737", "7:10")))

  // Dimension table
  val airports = sc.parallelize(List(
    ("JFK", "John F. Kennedy International Airport", "New York", "NY"),
    ("LAX", "Los Angeles International Airport", "Los Angeles", "CA"),
    ("SEA", "Seattle-Tacoma International Airport", "Seattle", "WA"),
    ("SFO", "San Francisco International Airport", "San Francisco", "CA")))

  // Dimension table
  val airlines = sc.parallelize(List(
    ("AA", "American Airlines"),
    ("DL", "Delta Airlines"),
    ("VX", "Virgin America")))

  val airportsMap = sc.broadcast(airports.map{case(a, b, c, d) => (a, c)}.collectAsMap)
  val airlinesMap = sc.broadcast(airlines.collectAsMap)

  val joinedFacts = flights.map{case(a, b, c, d, e) =>
    (airportsMap.value.get(a).get,
      airportsMap.value.get(b).get,
      airlinesMap.value.get(c).get, d, e)}.collect

  //This approach allows us not to shuffle the fact table, and to get quite good join performance.

  joinedFacts.foreach(println)
}
