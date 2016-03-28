package org.aja.tej.examples.dataset

import org.apache.spark.SparkContext
import org.apache.spark.graphx._
import org.apache.spark.rdd._
import scala.math.abs
import breeze.linalg.SparseVector
import scala.io.Source



/**
 * Created by mageswaran on 25/3/16.
 */

/*
 Google+ data was collected by (McAuley and Leskovec, 2012)
from the users who had manually shared their social circles using the share circle
feature. The dataset includes the user profiles, their circles, and their ego networks
and can be downloaded from Stanford's SNAP project website at http://snap.
stanford.edu/data/egonets-Gplus.html.

 */

object SocialEgoDS {
  //Assuming the application has already initialized the context
  var sc:SparkContext = SparkContext.getOrCreate()

  type Feature = breeze.linalg.SparseVector[Int]

  val path = "data/graphx/ego_network"

  val featureMap: Map[Long, Feature] =
    Source.fromFile(path + "/ego.feat").
      getLines().
      map{line =>
        val row = line split ' '
        val key = abs(row.head.hashCode.toLong)
        val feat = SparseVector(row.tail.map(_.toInt))
        (key, feat)
      }.toMap

  val edges: RDD[Edge[Int]] =
    sc.textFile(path + "/ego.edges").
      map {line =>
        val row = line split ' '
        val srcId = abs(row(0).hashCode.toLong)
        val dstId = abs(row(1).hashCode.toLong)
        val srcFeat = featureMap(srcId)
        val dstFeat = featureMap(dstId)
        val numCommonFeats = srcFeat dot dstFeat
        Edge(srcId, dstId, numCommonFeats)
      }

  val egoNetwork: Graph[Int,Int] = Graph.fromEdges(edges, 1)
}
