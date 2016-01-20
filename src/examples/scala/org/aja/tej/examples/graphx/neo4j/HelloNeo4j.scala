package org.aja.tej.examples.graphx.neo4j

import org.anormcypher._
import play.api.libs.ws._

/**
 * Created by mageswaran on 13/1/16.
 */
object HelloNeo4j extends App {

  // Provide an instance of WSClient
  val wsclient = ning.NingWSClient()

  // Setup the Rest Client
  implicit val connection = Neo4jREST()(wsclient)

  // Provide an ExecutionContext
  implicit val ec = scala.concurrent.ExecutionContext.global

  // create some test nodes
  Cypher("""create (anorm {name:"AnormCypher"}), (test {name:"Test"})""").execute()

  // a simple query
  val req = Cypher("start n=node(*) return n.name")

  // get a stream of results back
  val stream = req()

  // get the results and put them into a list
  stream.map(row => {row[String]("n.name")}).toList

  // shut down WSClient
  wsclient.close()

}
