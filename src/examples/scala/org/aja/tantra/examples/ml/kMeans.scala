//package org.aja.tantra.examples.ml
//
//import breeze.linalg._
//import breeze.numerics.sqrt
//
//
///**
// * Created by mageswaran on 24/2/16.
// */
//
//case class kMeansDataSet(features: Array[Array[Double]]) {
//  val rows = features.size
//  val cols = features(0).size
//  //Breeze does column major filling, hence transpose numOfFeatures x numOfSamples -> numOfSamples x numOfFeatures
//  def toDenseMatrix = new DenseMatrix[Double](cols,rows, features.flatten).t
//}
//object kMeans {
//
//
//  def loadDataSet(path: String = "data/kMeansTestSet.txt") = {
//    import scala.io._
//    println("Loading DataSet @ " + path)
//
//    var file = Source.fromFile(path)
//    val numberOfSamples = file.getLines().size //Number of numOfSamples/samples
//
//    //Reset the buffer, seems scala has bug! :)
//    file = Source.fromFile(path)
//
//    //Create an array for input dataset sample size
//    val featuresArray = Array.ofDim[Array[Double]](numberOfSamples)
//
//    var index = 0
//    for (line <- file.getLines()) {
//      val features = line.split("\t")
//      //if(DEBUG) println("*** " +  features(0) + "," + features(1) + "," + features(2))
//      //Now create the column data!
//      featuresArray(index) = Array(features(0).toDouble, features(1).toDouble)
//
//      index += 1
//    }
//    kMeansDataSet(featuresArray)
//  }
//
//  def distanceEclud(vecA: DenseVector[Double], vecB: DenseVector[Double]) = {
//    val diff = (vecA - vecB)
//    sqrt(diff.dot(diff))
//  }
//
//  def createCentroids(dataSet: kMeansDataSet, k: Int) = {
//    val numFeatures = dataSet.features(0).size
//    val dataMat = dataSet.toDenseMatrix
//
//    var centroids = DenseMatrix.fill[Double](k,numFeatures)(0)
//
//    for (j <- 0 until numFeatures) {
//      val minV = min(dataMat(::, j))
//      val range = (max(dataMat(::,j)) - minV)
//      centroids(::,j) := minV * range * DenseVector.rand[Double](k)
//    }
//    centroids
//  }
//
//  def kMeans(dataSet: kMeansDataSet, k: Int = 3,
//             distMeasure:(DenseVector[Double], DenseVector[Double]) => Double,
//             createCentroids: (kMeansDataSet, Int) => DenseMatrix[Double] ) = {
//
//    val dmFeatures = dataSet.toDenseMatrix
//    val m = dmFeatures.rows
//    val n = dmFeatures.cols
//
//    var clusterAssment = DenseMatrix.fill[Double](m,n)(0)
//
//    var centroids = createCentroids(dataSet, 3)
//    var clusterChanged = true
//
//    while(clusterChanged) {
//      clusterChanged = false
//
//      for (i <- 0 until m) {
//        var minDist = Double.PositiveInfinity
//        var minIndex = -1
//        for ( j <- 0 until k) {
//          var distJI = distMeasure(centroids(j,::), dataSet(i,::))
//          if(distJI < minDist) {
//            minDist = distJI
//            minIndex = j
//          }
//
//          if (clusterAssment(i,::) != minIndex)
//            clusterChanged = true
//          clusterAssment(i,::) := DenseVector(minIndex,minDist*minDist)
//        }
//      }
//
//
//    }
//
////    print centroids
////    for cent in range(k):#recalculate centroids
////      ptsInClust = dataSet[nonzero(clusterAssment[:,0].A==cent)[0]]#get all the point in this cluster
////      centroids[cent,:] = mean(ptsInClust, axis=0) #assign centroid to mean
//
//    (centroids,clusterAssment)
//  }
//  def main(args: Array[String]) {
//
//    val dataSet = loadDataSet()
//
//
//    kMeans(dataSet, 3, distanceEclud, createCentroids)
//
//  }
//}
