package org.aja.tantra.examples.ml

import breeze.linalg._
import breeze.numerics._
import com.google.common.cache.Weigher
import org.jfree.chart.{ChartFrame, ChartFactory}
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}

//For inbuilt sigmoid function
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
    val alpha = 0.001
    val maxCycles = 500
    var weights = DenseMatrix.fill(cols,1){1.0} //2 X 1
    for (i <- 0 until maxCycles) {
      val predictedOutputH = sigmoid(dmFeatures * weights)
      val error = (dmLabels - predictedOutputH) //Gradient
      weights = weights + alpha * dmFeatures.t * error
    }
    weights
  }


  def plotLRDataSet(dataSet: DataSet, weightsLR: Array[Double]) = {
    val features = dataSet.features
    val labels = dataSet.label
    val positiveSeries = new XYSeries("Positive Features")
    val negativeSeries = new XYSeries("Negative Features")
    val weightsSeries = new XYSeries("Weights")

    features.zip(labels).foreach{feature =>
      if(feature._2 == 1)
        positiveSeries.add(feature._1(1), feature._1(2))
      else
        negativeSeries.add(feature._1(1), feature._1(2))
    }

    val weightsForX = -3.0 to +3.0 by 0.1 toArray
    val weightsForY = weightsForX.map(x => (-weightsLR(0)-weightsLR(1)*x)/weightsLR(2))
    weightsForX.zip(weightsForY).foreach(xy => weightsSeries.add(xy._1, xy._2))

    val linePlotDataSet : XYSeriesCollection = new XYSeriesCollection()

    val scatterPlotDataSet : XYSeriesCollection = new XYSeriesCollection()
    scatterPlotDataSet.addSeries(positiveSeries)
    scatterPlotDataSet.addSeries(negativeSeries)
    scatterPlotDataSet.addSeries(weightsSeries)

    val scaterChart = ChartFactory.createScatterPlot(
      "Scatter Plot",
      "X",
      "Y",
      scatterPlotDataSet,
      PlotOrientation.VERTICAL,
      true, //include legent
      true, //tooltips
      false //urls
    )

    val scatterFrame = new ChartFrame(
      "Scatter Frame",
      scaterChart
    )
    scatterFrame.pack()
    scatterFrame.setVisible(true)
  }

  def main(args: Array[String]) {

    val dataSet = loadDataSet()

    //printDataSet(dataSet)
    //Small improvement
    //println(dataSet)

    val weights = LR(dataSet)

    println(weights)

    plotLRDataSet(dataSet, weights.toArray)

  }
}
