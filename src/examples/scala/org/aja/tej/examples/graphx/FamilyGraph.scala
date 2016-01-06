package org.aja.tej.examples.graphx

import org.aja.tej.utils.TejUtils
import org.apache.spark.graphx.{Graph, Edge, VertexId}
import org.apache.spark.rdd.RDD

/**
 * Created by mageswaran on 31/12/15.
 */
object FamilyGraph extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  //  1,Mike,48
  //  2,Sarah,45
  //  3,John,25
  //  4,Jim,53
  //  5,Kate,22
  //  6,Flo,52
  val familyVerticesData = "data/graph1_vertex.csv"
  val vertices: RDD[(VertexId, (String, String))] = sc.textFile(familyVerticesData).map { line =>
    val fields = line.split(",")
    //(id, (name, age)
    (fields(0).toLong, (fields(1), fields(2)))
  }
  //  6,1,Sister
  //  1,2,Husband
  //  2,1,Wife
  //  5,1,Daughter
  //  5,2,Daughter
  //  3,1,Son
  //  3,2,Son
  //  4,1,Friend
  //  1,5,Father
  //  1,3,Father
  //  2,5,Mother
  //  2,3,Mother
  val familyEdgesData = "data/graph1_edges.csv"
  val edges: RDD[Edge[String]] = sc.textFile(familyEdgesData).map { line =>
    val fields = line.split(",")
    //(from, to, property)
    Edge(fields(0).toLong, fields(1).toLong, fields(2))
  }

  //create graph from edges and vertices
  val default = ("Unknown", "Missing")
  val graph = Graph(vertices, edges, default)

  //Count objects
  println("vertices : " + graph.vertices.count)
  println("edges    : " + graph.edges.count)

  println("------------------------------------------------------------")
  //Filtering
  val filteredVertices = graph.vertices.filter {
    case (id, (name, age)) => age.toLong > 40
  }.count

  val filteredEdges = graph.edges.filter {
    case Edge(from, to, property) => property == "Father" | property == "Mother"
  }.count

  println("Vertices count : " + filteredVertices)
  println("Edges    count : " + filteredEdges)

  println("------------------------------------------------------------")

  //  The PageRank algorithm provides a ranking value for each of the vertices in a graph.
  //    It makes the assumption that the vertices that are connected to the most edges are the
  //    most important ones. Search engines use PageRank to provide ordering for the page
  //    display during a web search:

  val tolrence = 0.0001
  val ranking = graph.pageRank(tolrence).vertices

  val rankByPerson = vertices.join(ranking).map {
    case (id, ((person, age), rank)) => (rank, id, person)
  }

  // Print the result
  rankByPerson.collect().foreach {
    case (rank, id, person) =>
      println(f"Rank $rank%1.2f id $id person $person")
  }

  println("------------------------------------------------------------")
  //  The triangle count algorithm provides a vertex-based count of the number of
  //    triangles, associated with this vertex. For instance, vertex Mike (1) is connected
  //  to Kate (5), who is connected to Sarah (2); Sarah is connected to Mike (1) and
  //    so, a triangle is formed. This can be useful for route finding, where minimum,
  //  triangle-free, spanning tree graphs need to be generated for route planning.

  val tCount = graph.triangleCount().vertices
  println(tCount.collect().mkString("\n"))

  println("------------------------------------------------------------")

  // lets get connected components

  val iterations = 1000
  val connected = graph.connectedComponents().vertices
  val connectedS = graph.stronglyConnectedComponents(iterations).vertices

  // join with the original graph vertices
  val connByPerson = vertices.join(connected).map {
    case (id, ((person, age), conn)) => (conn, id, person)
  }

  val connByPersonS = vertices.join(connectedS).map {
    case (id, ((person, age), conn)) => (conn, id, person)
  }

  // print the result
  connByPerson.collect().foreach {
    case (conn, id, person) =>
      println(f"Weak $conn  $id $person")
  }

  connByPersonS.collect().foreach {
    case (conn, id, person) =>
      println(f"Strong $conn  $id $person")
  }
}