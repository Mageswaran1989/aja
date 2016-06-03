package org.aja.tej.examples.graphx.neo4j

import org.anormcypher._
import play.api.libs.ws._

/**
 * Created by mageswaran on 13/1/16.
 */
object HelloNeo4j extends App {

  // Provide an instance of WSClient
  implicit val wsclient = ning.NingWSClient()

  // Setup the Rest Client (Works in Ubuntu only!)
  implicit val connection = Neo4jREST("localhost", 7474, "/db/data/", "neo4j", "aja")

  // Provide an ExecutionContext
  implicit val ec = scala.concurrent.ExecutionContext.global

  val someString = "SomeString"
  // create some test nodes
  Cypher(s"""create (anorm {name:"Node-1: Aja"}), (test {name:"Node 2: ${someString}"})""").execute()

  // a simple query
  val req = Cypher("start n=node(*) return n.name")

  // get a stream of results back
  val stream = req()

  // get the results and put them into a list
  println(stream.map(row => {row[String]("n.name")}).toList)

  // shut down WSClient
  wsclient.close()

}
