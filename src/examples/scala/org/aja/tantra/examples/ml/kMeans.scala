package org.aja.tantra.examples.ml

import breeze.linalg._
import breeze.numerics._
import  breeze.stats._


/**
 * Created by mageswaran on 24/2/16.
 */

case class kMeansDataSet(features: Array[Array[Double]]) {
  val rows = features.size
  val cols = features(0).size
  //Breeze does column major filling, hence transpose numOfFeatures x numOfSamples -> numOfSamples x numOfFeatures
  def toDenseMatrix = new DenseMatrix[Double](cols,rows, features.flatten).t
}

object kMeans {


  def loadDataSet(path: String = "data/kMeansTestSet.txt") = {
    import scala.io._
    println("Loading DataSet @ " + path)

    var file = Source.fromFile(path)
    val numberOfSamples = file.getLines().size //Number of numOfSamples/samples

    //Reset the buffer, seems scala has bug! :)
    file = Source.fromFile(path)

    //Create an array for input dataset sample size
    val featuresArray = Array.ofDim[Array[Double]](numberOfSamples)

    var index = 0
    for (line <- file.getLines()) {
      val features = line.split("\t")
      //if(DEBUG) println("*** " +  features(0) + "," + features(1) + "," + features(2))
      //Now create the column data!
      featuresArray(index) = Array(features(0).toDouble, features(1).toDouble)

      index += 1
    }
    kMeansDataSet(featuresArray)
  }

  def distanceEclud(vecA: DenseVector[Double], vecB: DenseVector[Double]) = {
    val diff = (vecA - vecB)
    sqrt(diff.dot(diff))
  }

  def createCentds(dataSet: kMeansDataSet, k: Int) = {
    val numFeatures = dataSet.features(0).size
    val dataMat = dataSet.toDenseMatrix

    var centroids = DenseMatrix.fill[Double](k,numFeatures)(0)

    for (j <- 0 until numFeatures) {
      val minV = min(dataMat(::, j))
      val range = (max(dataMat(::,j)) - minV)
      centroids(::,j) := minV * range * DenseVector.rand[Double](k)
    }
    centroids
  }

  /*
  val dataSet = loadDataSet()
  val k = 4
  val distMeasure:(DenseVector[Double], DenseVector[Double]) => Double = distanceEclud
  val createCentroids: (kMeansDataSet, Int) => DenseMatrix[Double] = createCentds
  val i=0
  val j=0
  val c=0
   */
  def kMeans(dataSet: kMeansDataSet, k: Int = 4,
             distMeasure:(DenseVector[Double], DenseVector[Double]) => Double,
             createCentroids: (kMeansDataSet, Int) => DenseMatrix[Double] ) = {

    val dmFeatures = dataSet.toDenseMatrix
    val m = dmFeatures.rows //80
    val n = dmFeatures.cols //2

    val clusterAssment = DenseMatrix.fill[Double](m,n)(0)

    val centroids = createCentroids(dataSet, k)
    var clusterChanged = true

    while(clusterChanged) {
      clusterChanged = false

      for (i <- 0 until m) {
        var minDist = Double.PositiveInfinity
        var minIndex = -1

        for ( j <- 0 until k) {
          val distJI = distMeasure(centroids(j,::).t, dmFeatures(i,::).t)
          if(distJI < minDist) {
            minDist = distJI
            minIndex = j
          }
        }//end of inner for loop

        if (clusterAssment(i,0) != minIndex)
          clusterChanged = true

        clusterAssment(i,::).t := DenseVector[Double](minIndex,minDist*minDist)
        //println(clusterAssment(i,0)+ " != " + minIndex + " i = " + i + " " + clusterAssment(i,::).t)

      } //end of outer for loop

      println(centroids)
      println()
      println

      for (c <- 0 until k) {
        val numPointsInKCluster = clusterAssment(::, 0).toArray.map(cluster => if (cluster == c) 1 else 0).sum
        val pointsInClusterK = DenseMatrix.fill[Double](numPointsInKCluster,n)(0)
        var clusterIndexIndex = 0
        if (pointsInClusterK.rows != 0) {
          clusterAssment(::, 0).toArray.zipWithIndex.map { case (assignedCluster,index) =>
              if (assignedCluster == c) {
                pointsInClusterK(clusterIndexIndex, ::).t := dmFeatures(index, ::).t
                clusterIndexIndex += 1
              }
          }
          println("Cluster : " + c + " " + mean(pointsInClusterK(::, *)).t)
          centroids(c, ::).t := mean(pointsInClusterK(::, *)).t //Mean for all rows on each column
        }
      }

    } //End of while loop
    (centroids,clusterAssment)
  }

  def main(args: Array[String]) {

    val dataSet = loadDataSet()
    val res = kMeans(dataSet, 4, distanceEclud, createCentds)
    println("----> \n" + res._1)
  }
}
