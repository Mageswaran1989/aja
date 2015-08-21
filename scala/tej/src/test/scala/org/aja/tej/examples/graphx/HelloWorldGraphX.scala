package org.aja.tej.examples.graphx

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
/**
 * Created by mdhandapani on 4/8/15.
 */
object HelloWorldGraphX {

  /**
   *    Vertex Table
   *    ============
   *  ID  Property(V)
   *  3   (rxin,student)
   *  7   (jgonzal, postdoc)
   *  5   (franklin, professor)
   *  2   (istoica,professor)
   */

  /**
   *     Edge Table
   *   SrcId  DstId  Property(E)
   *   3      7      Collabrator
   *   5      3      Advisor
   *   2      5      Colleague
   *   5      7       PI
   */

  /** *
    *
    * @param args
    */
  //http://www.snee.com/bobdc.blog/2015/03/spark-and-sparql-rdf-graphs-an.html
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Simple GraphX Application").setMaster("local[4]" /*"spark://myhost:7077"*/)
    val sc = new SparkContext(conf)
    val baseURI = "http://snee.com/xpropgraph#"

    // Create an RDD for the vertices
    val users: RDD[(VertexId, (String, String))] =
      sc.parallelize(Array(
        (3L, ("rxin", "student")),
        (7L, ("jgonzal", "postdoc")),
        (5L, ("franklin", "prof")),
        (2L, ("istoica", "prof")),
        // Following lines are new data
        (8L, ("bshears", "student")),
        (9L, ("nphelge", "student")),
        (10L, ("asmithee", "student")),
        (11L, ("rmutt", "student")),
        (12L, ("ntufnel", "student"))
      ))
    // Create an RDD for edges
    val relationships: RDD[Edge[String]] =
      sc.parallelize(Array(
        Edge(3L, 7L, "collab"),
        Edge(5L, 3L, "advisor"),
        Edge(2L, 5L, "colleague"),
        Edge(5L, 7L, "pi"),
        // Following lines are new data
        Edge(5L, 8L, "advisor"),
        Edge(2L, 9L, "advisor"),
        Edge(5L, 10L, "advisor"),
        Edge(2L, 11L, "advisor")
      ))
    // Build the initial Graph
    val graph = Graph(users, relationships)

    // Output object property triples
    graph.triplets.foreach( t => println(
      s"<$baseURI${t.srcAttr._1}> <$baseURI${t.attr}> <$baseURI${t.dstAttr._1}> ."
    ))

    // Output literal property triples
    users.foreach(t => println(
      s"""<$baseURI${t._2._1}> <${baseURI}role> \"${t._2._2}\" ."""
    ))

    sc.stop
  }

}
