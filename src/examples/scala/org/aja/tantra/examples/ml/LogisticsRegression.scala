package org.aja.tantra.examples.ml


import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.jfree.chart.axis.{NumberAxis, ValueAxis}
import org.jfree.chart.renderer.xy.{XYLineAndShapeRenderer, XYItemRenderer}
import org.jfree.chart.{JFreeChart, ChartFrame, ChartFactory}
import org.jfree.chart.plot.{XYPlot, PlotOrientation}
import org.jfree.data.xy.{XYDataset, XYSeriesCollection, XYSeries}

import scala.util.Random

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

  //Breeze does column major filling, hence transpose cols x rows -> rows x cols
  def toDenseMatrixFeatures = new DenseMatrix[Double](cols,rows, features.flatten).t
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

  /**
   * Test DataSet used to validate the algorithm.
   * 100 rows 3 columns (2 features each and 1 result).
   * This is used to validate all Linear Algebra calculations
   * @return DataSet(...)
   */
  def loadDataSet() = {
    import scala.io._

    var file = Source.fromFile("/opt/aja/data/LRTestSet.txt")
    val numberOfSamples = file.getLines().size //Number of rows/samples

    //Reset the buffer, seems scala has bug! :)
    file = Source.fromFile("/opt/aja/data/LRTestSet.txt")

    //Create an array for input dataset sample size
    val featuresArray = Array.ofDim[Array[Double]](numberOfSamples)
    val labelArray = Array.ofDim[Double](numberOfSamples)

    var index = 0
    for (line <- file.getLines())
    {
      val features = line.split("\t")
      //if(DEBUG) println("*** " +  features(0) + "," + features(1) + "," + features(2))
      //Now create the column data!
      featuresArray(index) = Array(1.0, features(0).toDouble, features(1).toDouble)
      //Label data
      labelArray(index)  = features(2).toDouble
      index += 1
    }

    DataSet(featuresArray, labelArray)
  }

  def plotLRDataSet(dataSet: DataSet, weightsLR: Array[Double], title: String = "Scatter Plot") = {
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
    linePlotDataSet.addSeries(weightsSeries)

    val scatterPlotDataSet : XYSeriesCollection = new XYSeriesCollection()
    scatterPlotDataSet.addSeries(positiveSeries)
    scatterPlotDataSet.addSeries(negativeSeries)
    scatterPlotDataSet.addSeries(weightsSeries)

    val scaterChart = ChartFactory.createScatterPlot(
      title + " Plot",
      "X",
      "Y",
      scatterPlotDataSet,
      PlotOrientation.VERTICAL,
      true, //include legent
      true, //tooltips
      false //urls
    )

    val scatterFrame = new ChartFrame(
      title + " - Chart Method 1",
      scaterChart
    )
    scatterFrame.pack()
    scatterFrame.setVisible(true)



    ////////////////////////////////////
    // Create a single plot containing both the scatter and line
    val plot: XYPlot = new XYPlot();

    /* SETUP SCATTER */

    /*
    // Create the scatter data, renderer, and axis
    val collection1: XYDataset = scatterPlotDataSet
    val renderer1: XYItemRenderer = new XYLineAndShapeRenderer(false, true);   // Shapes only
    val domain1: ValueAxis = new NumberAxis("X");
    val range1: ValueAxis = new NumberAxis("Y");

    // Set the scatter data, renderer, and axis into plot
    plot.setDataset(0, collection1);
    plot.setRenderer(0, renderer1);
    plot.setDomainAxis(0, domain1);
    plot.setRangeAxis(0, range1);

    // Map the scatter to the first Domain and first Range
    plot.mapDatasetToDomainAxis(0, 0);
    plot.mapDatasetToRangeAxis(0, 0);

    /* SETUP LINE */

    // Create the line data, renderer, and axis
    val collection2: XYDataset = linePlotDataSet
    val renderer2: XYItemRenderer = new XYLineAndShapeRenderer(true, false);   // Lines only
    val domain2: ValueAxis = new NumberAxis("X1");
    val range2: ValueAxis = new NumberAxis("Y1");

    // Set the line data, renderer, and axis into plot
    plot.setDataset(1, collection2);
    plot.setRenderer(1, renderer2);
    plot.setDomainAxis(1, domain2);
    plot.setRangeAxis(1, range2);

    // Map the line to the second Domain and second Range
    plot.mapDatasetToDomainAxis(1, 1);
    plot.mapDatasetToRangeAxis(1, 1);

    // Create the chart with the plot and a legend
    val chart1: JFreeChart = new JFreeChart(title + " Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

    // Map the line to the FIRST Domain and second Range
    plot.mapDatasetToDomainAxis(1, 0);
    plot.mapDatasetToRangeAxis(1, 1);

    val scatterFrame1 = new ChartFrame(
      "LR Best Fit Line",
      chart1
    )
    scatterFrame1.pack()
    scatterFrame1.setVisible(true) */
  }

  /**
   * Start with the weights all set to 1
   * Repeat R number of times:
   *   Calculate the gradient of the "entire" dataset. What if dataset has billions of entries!?
   *   Update the weights vector by alpha*gradient
   *   Return the weights vector
   * @param dataSet of type DataSet()
   * @return Returns a weight Matrix of dimension (1 + number of features x 1)
   */
  def LR(dataSet: DataSet) = {
    val dmFeatures = dataSet.toDenseMatrixFeatures //100x3 3X1 fo for testdata
    val dmLabels = dataSet.toDenseMatrixLabels
    val rows = dmFeatures.rows
    val cols = dmFeatures.cols
    val alpha = 0.001
    val maxCycles = 500
    var weights = DenseMatrix.fill(cols,1){1.0} //3 X 1
    for (i <- 0 until maxCycles) {
      val predictedOutputH = sigmoid(dmFeatures * weights) // 100 x 3 * 3 x 1 => 100 x 1
      val error = (dmLabels - predictedOutputH) //Gradient
      weights = weights + (alpha * dmFeatures.t * error)
    }
    weights
  }

  /**
   * Start with the weights all set to 1
   * For each piece of data in the dataset:
   * Calculate the gradient of one piece of data
   *   Update the weights vector by alpha*gradient
   *   Return the weights vector
   * @param dataSet
   */
  def lrSGA(dataSet: DataSet) = {
    val dmFeatures = dataSet.toDenseMatrixFeatures
    val dmLabels = dataSet.toDenseMatrixLabels.toDenseVector
    val rows = dmFeatures.rows
    val cols = dmFeatures.cols
    val alpha = 0.01

    //var weights = DenseMatrix.fill[Double](1,cols)(1.0)
    var weights = DenseVector.fill[Double](cols)(1.0)

    for ( i <- 0 until rows) {
      //Gradients of one piece of data
      // 1 x 3 * 3 x 1 => 1 x 1
      //(i,::) => extract each row

      val h = sigmoid(dmFeatures(i,::).t.dot( weights))
      val error = dmLabels(i) - h
      weights = weights + (alpha * error *  dmFeatures(i,::).t)
    }
    weights
  }

  /**
   * Start with the weights all set to 1
   * For number of iterations given
   *     For each piece of data in the dataset:
   *        Calculate the gradient of one piece of data
   *        Update the weights vector by alpha*gradient
   * Return the weights vector
   * @param dataSet of type DataSet
   * @param numberOfIterations Nu,ber of iterations over the given dataset
   * @return
   */
  def lrSGA1(dataSet: DataSet, numberOfIterations: Int) = {
    val dmFeatures = dataSet.toDenseMatrixFeatures
    val dmLabels = dataSet.toDenseMatrixLabels.toDenseVector
    val rows = dmFeatures.rows
    val cols = dmFeatures.cols
    var alpha = 0.0

    //var weights = DenseMatrix.fill[Double](1,cols)(1) //1 x 3
    var weights = DenseVector.fill[Double](cols)(1.0)
    val rand = Random

    for ( j <- 0 until numberOfIterations) {
      var length = rows
      for ( i <- 0 until rows) {
        alpha = 4/(1.0+j+i)+0.0001
        val uniformDist = Uniform(0,length)
        val randomIndex = uniformDist.sample().toInt //rand.nextInt(length)
        //Gradients of one piece of data
        // 1 x 3 * 3 x 1 => 1 x 1
        //(i,::) => extract each row
        val h = sigmoid(dmFeatures(randomIndex,::).t.dot(weights))
        val error = dmLabels(randomIndex) - h
        weights = weights + alpha * error *  dmFeatures(randomIndex,::).t
        length = length - 1
      }
    }
    weights
  }

  def main(args: Array[String]) {

    val dataSet = loadDataSet()
    val weights = LR(dataSet)  //y = w^Tx + b  i.e (1 x 3 * 3 x 1) + scalar

    println("Normal LR weights: " + weights + "of size : " + weights.rows + " x " + weights.cols)
    plotLRDataSet(dataSet, weights.toArray, "First")

    val weights1 = lrSGA(dataSet)
    println("Stochastic Gradient LR weights: " + weights1)
    plotLRDataSet(dataSet, weights1.toArray, "Second")

    val weights2 = lrSGA1(dataSet, 150)
    println("Stochastic Gradient LR weights: " + weights2)
    plotLRDataSet(dataSet, weights2.toArray, "Third")

  }
}
