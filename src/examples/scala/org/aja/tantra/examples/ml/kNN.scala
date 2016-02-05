//package org.aja.tantra.examples.ml
//
///**
// * Created by mageswaran on 29/1/16.
// */
//object kNN {
//
//  case class DataSet[U,V](features: Vector[Array[U]], label: Vector[V])
//
//  def createDataset() = {
//    val features = Vector(Array(1.0,1.1),Array(1.0,1.0), Array(0.0,0.0),Array(0.0,0.1))
//    val output = Vector('A','A','B','B')
//    DataSet(features, output)
//  }
//
//  def printDataSet(dataSet: DataSet) = {
//
//    dataSet.features.zip(dataSet.label).foreach{
//      lp => lp._1.foreach(pt => print(" " + pt)); print(" " + lp._2)
//        println
//    }
//
//  }
//  /*
//  For every point in our dataset:
//    calculate the distance between inX and the current point
//    sort the distances in increasing order
//    take k items with lowest distances to inX
//    find the majority class among these items
//  return the majority class as our prediction for the class of inX
//  */
//
//  //val inX = Array(0.0,0.0)
//  //val dataSet = createDataset()
//  def kNN[U](dataSet: DataSet, inX: Array[U], n: Int) = {
//    require(inX.length == dataSet.features(0).length)
//
//    val diff = dataSet.features.map(feature => feature diff inX)
//    val squareDifference = diff.map(feature => feature.map(pt => pt * pt))
//    val distance = squareDifference.map(sqDiff => sqDiff.sum).map(Math.sqrt(_))
//
//    val distanceWithLabels = distance.zip(dataSet.label).sorted
//
//    val topKClasses = distanceWithLabels.take(3)
//
//    val d = topKClasses.map(lp => lp._2)
//
//  }
//  def main(args: Array[String]) {
//
//  }
//}
