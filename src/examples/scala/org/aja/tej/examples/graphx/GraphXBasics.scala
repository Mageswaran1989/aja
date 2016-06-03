package org.aja.tej.examples.graphx

import org.aja.dataset.{SocialEgoDS, FlavorNetworkDS, EnrollEmailDS, TinySocialGraphDS}
import org.aja.tej.utils.TejUtils
import org.apache.spark.graphx.{Graph, VertexId}
import org.apache.spark.rdd.RDD


/**
 * Created by mageswaran on 25/3/16.
 */

//Let us explore some formatted Graph datasets
object GraphXBasics extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  //--------------------------------------------------------------------------------------------

  TinySocialGraphDS(sc).tinySocial.cache()
  TinySocialGraphDS(sc).tinySocial.vertices.collect()
  TinySocialGraphDS(sc).tinySocial.edges.collect()

  // Extract links between coworkers and print their professional relationship
  val profLinks: List[String] = List("coworker", "boss", "employee","client", "supplier")

  TinySocialGraphDS(sc).tinySocial.subgraph(profLinks contains _.attr).
    triplets.foreach(t => println(t.srcAttr.name + " is a " + t.attr + " of " + t.dstAttr.name))

  //--------------------------------------------------------------------------------------------

  EnrollEmailDS.emailGraph.numVertices  //Number of people in the organization
  EnrollEmailDS.emailGraph.numEdges     //Number of emails shared

  EnrollEmailDS.emailGraph.vertices.take(5)
  EnrollEmailDS.emailGraph.edges.take(5)

  // collect the destination nodes that node 19021 communicates to:
  EnrollEmailDS.emailGraph.edges.filter(_.srcId == 19021).map(_.dstId).collect()
  // incoming edges  to 19021:
  EnrollEmailDS.emailGraph.edges.filter(_.dstId == 19021).map(_.srcId).collect()

  EnrollEmailDS.emailGraph.inDegrees.map(_._2).sum / EnrollEmailDS.emailGraph.numVertices
  EnrollEmailDS.emailGraph.outDegrees.map(_._2).sum / EnrollEmailDS.emailGraph.numVertices

  // If we want to find the person that has e-mailed to the largest number of people,
  // we can define and use the following max function:

  def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
    if (a._2 > b._2) a else b
  }
  def min(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
    if (a._2 < b._2) a else b
  }

  EnrollEmailDS.emailGraph.outDegrees.reduce(max)

  // let's check if there are some isolated groups of employees at Enron using the following code:
  EnrollEmailDS.emailGraph.outDegrees.filter(_._2 <= 1).count

  EnrollEmailDS.emailGraphPartitioned.triangleCount()
  val triangleCounts = EnrollEmailDS.emailGraphPartitioned.triangleCount().vertices

  def clusterCoeff(tup: (VertexId, (Int,Int))): (VertexId, Double) =
    tup match {case (vid, (t, d)) =>
      (vid, (2*t.toDouble/(d*(d-1))))
    }

  def clusterCoefficients(graph: Graph[Int,Int]):
  RDD[(VertexId, Double)] = {
    val gRDD: RDD[(VertexId, (Int, Int))] =
      graph.triangleCount().vertices join graph.degrees
    gRDD map clusterCoeff
  }

  val coeffs = clusterCoefficients(EnrollEmailDS.emailGraphPartitioned)
  coeffs.take(10)
  coeffs.filter (x => !x._2.isNaN).count

  // Calculate the adjusted global cluster coefficient
  val nonIsolatedNodes = coeffs.filter(x => !x._2.isNaN)
  val globalCoeff = nonIsolatedNodes.map(_._2).sum / nonIsolatedNodes.count

  //--------------------------------------------------------------------------------------------

  FlavorNetworkDS.foodNetwork.edges.take(5)
  FlavorNetworkDS.foodNetwork.triplets.take(5).foreach(FlavorNetworkDS.showTriplet _ andThen println _)

  // which compound is the most prevalent in our list of ingredients
  FlavorNetworkDS.foodNetwork.outDegrees.reduce(max)
  FlavorNetworkDS.foodNetwork.vertices.filter(_._1 == 908).collect()
  FlavorNetworkDS.foodNetwork.inDegrees.reduce(max)
  FlavorNetworkDS.foodNetwork.vertices.filter(_._1 == 10292).collect()

  val cc = FlavorNetworkDS.foodNetwork.connectedComponents()
  cc.vertices.map(_._2).distinct.collect
  cc.vertices.map(_._2).collect.distinct.size

  cc.vertices.groupBy(_._2).
    map((p => (p._1,p._2.size))).
    sortBy(x => x._2, false).collect()

  //largestComponent
  cc.vertices.map(x => (x._2,x._1)).
    groupBy(_._1).
    map(p => (p._1,p._2.size)).
    max()(Ordering.by(_._2))

  //--------------------------------------------------------------------------------------------
  SocialEgoDS.egoNetwork.edges.filter(_.attr == 3).count()
  SocialEgoDS.egoNetwork.edges.filter(_.attr == 2).count()
  SocialEgoDS.egoNetwork.edges.filter(_.attr == 1).count()

  //degrees of the connections
  SocialEgoDS.egoNetwork.degrees.take(5)
  SocialEgoDS.egoNetwork.degrees.reduce(max)
  SocialEgoDS.egoNetwork.degrees.reduce(min)

  //histogram data of the degrees
  SocialEgoDS.egoNetwork.degrees.
    map(t => (t._2,t._1)).
    groupByKey.map(t => (t._1,t._2.size)).
    sortBy(_._1).collect()
}
