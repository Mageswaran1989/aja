package org.aja.tantra.examples.ml

import breeze.linalg._
import breeze.numerics._ //For inbuilt sigmoid function
/**
 * Created by mageswaran on 31/1/16.
 */

/**
 * Case class to represent LR dataset of type Double
 * @param features Array of features which are in turn an array of points
 * @param label Array of labels
 */
case class DataSet(features: Array[Array[Double]], label: Array[Double]) {
  val rows = features.length
  val cols = features(0).size

  override def toString() = "" + features.zip(label).foreach(println)

  def toDenseMatrixFeatures = new DenseMatrix[Double](rows,cols, features.flatten)
  def toDenseMatrixLabels = new DenseMatrix[Double](rows,1, label)
}

/**
 * Main class to test Logistics Regression
 *
 * Pros:  Computationally inexpensive, easy to implement, knowledge representation
 *        easy to interpret
 * Cons:  Underfitting, LowAccuracy
 * DataSet: Numerical, Nominal
 */
object LogisticsRegression {

  val DEBUG = true

  /**
   * Print the DataSet class
   * @param dataSet DataSet
   */
  def printDataSet(dataSet: DataSet) = {
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
      featuresArray(index) = Array(1.0, features(0).toDouble, features(1).toDouble)
      labelArray(index)  = features(2).toDouble
      index += 1
    }

    DataSet(featuresArray, labelArray)
  }

  /**
   * Start with the weights all set to 1
   * Repeat R number of times:
   *   Calculate the gradient of the entire dataset
   *   Update the weights vector by alpha*gradient
   *   Return the weights vector
   */
  def LR(dataSet: DataSet) = {
    val dmFeatures = dataSet.toDenseMatrixFeatures //100x2 2X1
    val dmLabels = dataSet.toDenseMatrixLabels
    val rows = dmFeatures.rows
    val cols = dmFeatures.cols
    val alpha = 0.01
    val maxCycles = 500
    var weights = DenseMatrix.fill(cols,1){1.0} //2 X 1
    for (i <- 0 until maxCycles) {
      val predictedOutputH = sigmoid(dmFeatures * weights)
      val error = (dmLabels - predictedOutputH) //Gradient
      weights = weights + alpha * dmFeatures.t * error
    }
    weights
  }


  def plot(dataSetLR: DataSet) = {

    import org.sameersingh.scalaplot.Implicits._

    val x = 0.0 until 2.0 * math.Pi by 0.1
    output(GUI, xyChart(x ->(math.sin(_), math.cos(_))))


  }
  def main(args: Array[String]) {

    val dataSet = loadDataSet()

    val dm = dataSet.toDenseMatrixFeatures
    println()
    //printDataSet(dataSet)
    //Small improvement
    //println(dataSet)

    val weights = LR(dataSet)

    println(weights)

    plot(dataSet)

  }
}
