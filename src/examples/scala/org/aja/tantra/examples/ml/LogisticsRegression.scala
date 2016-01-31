package org.aja.tantra.examples.ml

import breeze.linalg._
/**
 * Created by mageswaran on 31/1/16.
 */

case class DataSet[V](features: Array[Array[Double]], label: Array[V]) {
  val size = features.length
  override def toString() = "" + features.zip(label).foreach(println)

  def toDenseMatrix = new DenseMatrix[Double](100,2, features.flatten)
}

/**
 * Main class to test Logistics Regression
 *
 * Pros:  Computationally inexpensive, easy to implement, knowledge representation
 *        easy to interpret
 * Cons: Underfitting, LowAccuracy
 * DataSet: Numerical, Nominal
 */
object LogisticsRegression {

  val DEBUG = true

  //sigma = (1 / (1 + e^-z))
  //z = w0x0 + w1x1 + w2x2 + ... + wnxn
  def sigmoid() = {

  }

  /**
   * Start with the weights all set to 1
   * Repeat R number of times:
   *   Calculate the gradient of the entire dataset
   *   Update the weights vector by alpha*gradient
   *   Return the weights vector
   */
  def LR(dataSet: DataSet, inputVector: DenseVector) = {
    val dm = dataSet.toDenseMatrix //100x2 2X1
    val rows = dm.rows
    val cols = dm.cols
    val alpha = 0.01
    val maxCycles = 500
    var weights = DenseMatrix.fill(cols,1){1}

    
  }

  /**
   * Print the DataSet class
   * @param dataSet DataSet
   * @tparam V
   */
  def printDataSet[V](dataSet: DataSet[V]) = {
    //Combine both features and labels and orint it line by line
    dataSet.features.zip(dataSet.label).foreach{ lp =>
      lp._1.foreach(pt => print(" " + pt))
      print(" " + lp._2)
      if(DEBUG) println
    }
    if(DEBUG) println
  }

  def loadDataSet() = {
    import scala.io._

    var file = Source.fromFile("/opt/aja/data/LRTestSet.txt")
    val numberOfSamples = file.getLines().size

    //Reset the buffer
    file = Source.fromFile("/opt/aja/data/LRTestSet.txt")

    val featuresArray = Array.ofDim[Array[Double]](numberOfSamples)
    val labelArray = Array.ofDim[Double](numberOfSamples)

    var index = 0
    for (line <- file.getLines())
    {
      val features = line.split("\t")
      //if(DEBUG) println("*** " +  features(0) + "," + features(1) + "," + features(2))
      featuresArray(index) = Array(features(0).toDouble, features(1).toDouble)
      labelArray(index)  = features(2).toDouble
      index += 1
    }

    DataSet(featuresArray, labelArray)
  }


  def main(args: Array[String]) {

    val dataSet = loadDataSet()

    val dm = dataSet.toDenseMatrix
    println()
    //printDataSet(dataSet)
    //Small improvement
    //println(dataSet)


  }
}
