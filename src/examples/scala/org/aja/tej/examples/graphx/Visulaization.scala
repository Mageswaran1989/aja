//package org.aja.tej.examples.graphx
//
//import org.aja.tej.examples.dataset.FlavorNetworkDS.FNNode
//import org.aja.tej.examples.dataset.{FlavorNetworkDS, SocialEgoDS}
//import org.aja.tej.utils.TejUtils
//import org.apache.spark.graphx._
//
//import org.graphstream.graph.{Graph => GraphStream}
//import org.graphstream.graph.implementations._
//import org.graphstream.ui.j2dviewer.J2DGraphRenderer
//
//import org.jfree.chart.axis.ValueAxis
//import breeze.linalg.{SparseVector, DenseVector}
//import breeze.plot._
//import scala.io.Source
//import scala.math.abs
//
///**
// * Created by mageswaran on 26/3/16.
// */
//object Visulaization extends App {
//
//  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
//
//  // Setup GraphStream settings
//  System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
//
///*
//
//  // Create a SingleGraph class for GraphStream visualization
//  val graph: SingleGraph = new SingleGraph("EgoSocial")
//
//  // Set up the visual attributes for graph visualization
//  graph.addAttribute("ui.stylesheet", "url(file:data/style/stylesheet)")
//  graph.addAttribute("ui.quality")
//  graph.addAttribute("ui.antialias")
//
//
//  // Load the graphX vertices into GraphStream nodes
//  for ((id,_) <- SocialEgoDS.egoNetwork.vertices.collect()) {
//    val node = graph.addNode(id.toString).asInstanceOf[SingleNode]
//  }
//  // Load the graphX edges into GraphStream edges
//  for (Edge(x,y,_) <- SocialEgoDS.egoNetwork.edges.collect()) {
//    val edge = graph.addEdge(x.toString ++ y.toString, x.toString, y.toString, true).asInstanceOf[AbstractEdge]
//  }
//
//  // Display the graph
//  graph.display()
//
//
//  // Function for computing degree distribution
//  def degreeHistogram(net: Graph[Int, Int]): Array[(Int, Int)] =
//    net.degrees.map(t => (t._2,t._1)).
//      groupByKey.map(t => (t._1,t._2.size)).
//      sortBy(_._1).collect()
//
//
//  val nn = SocialEgoDS.egoNetwork.numVertices
//  val egoDegreeDistribution = degreeHistogram(SocialEgoDS.egoNetwork).map({case (d,n) => (d,n.toDouble/nn)})
//
//  // Plot degree distribution with breeze-viz
//  val f = Figure()
//  val p = f.subplot(0)
//  val x = new DenseVector(egoDegreeDistribution map (_._1.toDouble))
//  val y = new DenseVector(egoDegreeDistribution map (_._2))
//  p.xlabel = "Degrees"
//  p.ylabel = "Degree distribution"
//  p += plot(x, y)
//  // f.saveas("/output/degrees-ego.png")
//
//*/
//
//  // Setup GraphStream settings
//  System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer")
//
//  // Create a SingleGraph class for GraphStream visualization
//  val graph: SingleGraph = new SingleGraph("FoodNetwork")
//
//  // Set up the visual attributes for graph visualization
//  graph.addAttribute("ui.stylesheet", "url(file:data/style/stylesheet-foodnetwork)")
//  graph.addAttribute("ui.quality")
//  graph.addAttribute("ui.antialias")
//
//  // Load the graphX vertices into GraphStream nodes
//  for ((id: VertexId, fnn: FNNode) <- FlavorNetworkDS.foodNetwork.vertices.collect()) {
//    val node = graph.addNode(id.toString).asInstanceOf[SingleNode]
//    node.addAttribute("name", fnn.name)
//    node.addAttribute("ui.label", fnn.name)
//  }
//  // Load the graphX edges into GraphStream edges
//  for (Edge(x,y,_) <- FlavorNetworkDS.foodNetwork.edges.collect()) {
//    val edge = graph.addEdge(x.toString ++ y.toString, x.toString, y.toString, true).asInstanceOf[AbstractEdge]
//  }
//  // Display the graph
//  graph.display()
//
//}
