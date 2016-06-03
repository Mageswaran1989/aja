package org.aja.dataset

import org.apache.spark.SparkContext
import org.apache.spark.graphx._
import org.apache.spark.rdd._



/**
 * Created by mageswaran on 25/3/16.
 */

/*
A bipartite graph in the sense that the nodes are divided into two disjoint sets: the ingredient nodes and
the compound nodes. Each link connects an ingredient to a compound when
the chemical compound is present in the food ingredient. From the ingredient-
compound network, it is also possible to create what is called a flavor network.
Instead of connecting food ingredients to compounds, the flavor network links pairs
of ingredients whenever a pair of ingredients shares at least one chemical compound.
 The flavor network can also help food scientists or amateur cooks create
new recipes. The datasets that we will use consist of ingredient-compound data
and the recipes collected from http://www.epicurious.com/, allrecipes.com,
and http://www.menupan.com/. The datasets are available at http://yongyeol.
com/2011/12/15/paper-flavor-network.html.

 */

object FlavorNetworkDS {

  class FNNode(val name: String) extends Serializable
  case class Ingredient(override val name: String, category: String) extends FNNode(name)
  case class Compound(override val name: String, cas: String) extends FNNode(name)

  //Assuming the application has already initialized the context
  var sc:SparkContext = SparkContext.getOrCreate()

  val path = "data/graphx/food_network/"

  val ingredients: RDD[(VertexId, FNNode)] =
    sc.textFile(path + "/ingr_info.tsv").
      filter(! _.startsWith("#")).
      map {line =>
        val row = line split '\t'
        (row(0).toLong, Ingredient(row(1), row(2)))
      }

  def compounds: RDD[(VertexId, FNNode)] =
    sc.textFile(path + "/comp_info.tsv").
      filter(! _.startsWith("#")).
      map {line =>
        val row = line split '\t'
        (10000L + row(0).toInt, Compound(row(1), row(2)))
      }

  def links: RDD[Edge[Int]] =
    sc.textFile(path + "/ingr_comp.tsv").
      filter(! _.startsWith("#")).
      map {line =>
        val row = line split '\t'
        Edge(row(0).toInt, 10000L + row(1).toInt, 1)
      }

  def nodes = ingredients ++ compounds
  val foodNetwork = Graph(nodes, links)

  def showTriplet(t: EdgeTriplet[FNNode,Int]): String = "The ingredient " ++ t.srcAttr.name ++ " contains " ++ t.dstAttr.name
}
