package org.aja.tantra.examples.ml

import java.io.{IOException, FileNotFoundException}
import scala.math.{ min, max, Ordering }
import scala.util.Random

/**
 * Created by mageswaran on 29/1/16.
 *
 */
/**
 * Main class to test kNN implementation
 * Pros: High Accuracy
 * Cons: High Memory and computation
 * DataSet: Numerical, Categorical
 */
object kNN {

  val DEBUG = false
  /**
   * Case class to define our data,
   * @param features Array of Feature of Array
   * @param label Array of labels
   * @tparam V Inferred based on the user data
   */
  case class DataSet[V](features: Array[Array[Double]], label: Array[V]) {
    /**
     * Normalize the DataSet features Array
     */
    def normalize() = {
      val featuresColDim = features(0).size
      if(DEBUG) println("featuresDim :" + featuresColDim)

      val maxColValues = Array.ofDim[Double](featuresColDim) //By default intialized to 0.0
      val minColValues = Array.fill[Double](featuresColDim)(999999999.9) //Let us fill with some max values

      for ( i <- 0 until featuresColDim)
        features.foreach{
          featureArray => {
            if (maxColValues(i) < featureArray(i))
              maxColValues(i) = featureArray(i)
            if (minColValues(i) > featureArray(i))
              minColValues(i) = featureArray(i)

            featureArray(i) = (featureArray(i) - minColValues(i)) / (maxColValues(i) - minColValues(i))
          }
        }

      if(DEBUG) {
        println("maxColValues :" )
        maxColValues.foreach(println)
        println("maxColValues :" )
        minColValues.foreach(println)
      }
    }
  }

  /**
   *
   * @return Creates a sample DataSet
   */
  def createDataset() = {
    val features = Array(Array(1.0,1.1),Array(1.0,1.0), Array(0.0,0.0),Array(0.0,0.1))
    val output = Array('A','A','B','B')
    DataSet(features, output)
  }

  /**
   * Print the DataSet class
   * @param dataSet DataSet
   * @tparam V
   */
  def printDataSet[V](dataSet: DataSet[V]) = {
    //Combine both features and labels and orint it line by line
    dataSet.features.zip(dataSet.label).foreach{
      lp => lp._1.foreach(pt => print(" " + pt)); print(" " + lp._2)
        if(DEBUG) println
    }
    if(DEBUG) println
  }

  /*
  For every point in our dataset:
    calculate the distance between inX and the current point
    sort the distances in increasing order
    take k items with lowest distances to inX
    find the majority class among these items
  return the majority class as our prediction for the class of inX
  */

  /*
  val inX = Array(0.0,0.0)
  val dataSet = createDataset()
  val n =3

  */
  /**
   *
   * @param dataSet
   * @param inX Input feature Array
   * @param n Number of Nearest Neighbour
   * @tparam V Inferred based on the user data
   * @return Returns class label of type V
   */
  def kNN[V](dataSet: DataSet[V], inX: Array[Double], n: Int): V  = {
    require(inX.length == dataSet.features(0).length)

    if(DEBUG) printDataSet(dataSet)

    //d= Sqrt((x2 - x1)^2 + (y2 - y1)^2)

    val squareDifference = dataSet.features.map(feature => feature.zip(inX)
      .map{case(a,b) =>
        val diff = (a-b)
        diff * diff})
    val distance = squareDifference.map(sqDiff => Math.sqrt(sqDiff.sum))

    val distanceWithLabels = distance.zip(dataSet.label).sortBy(_._1)
    if(DEBUG) distanceWithLabels.foreach(println)

    if(DEBUG) println()

    val topKClasses = distanceWithLabels.take(n)
    if(DEBUG) topKClasses.foreach(println)

    /*
    type V = Char

     */
    val classMap: scala.collection.mutable.Map[V, Int] = scala.collection.mutable.Map[V, Int]()

    topKClasses.map{
      lp => {
        if(DEBUG) println(lp._2)
        classMap += (lp._2 -> (classMap.getOrElse((lp._2), 0) + 1))
      }
    }

    if(DEBUG) println("Top " + n + " calsses are :" )
    if(DEBUG) classMap.foreach(println)
    classMap.last._1
  }

  /**
   *
   * @return Creates DataSet from datingTestSet.txt
   */
  def createDatingDataSet() = {
    import scala.io._

    var file = Source.fromFile("/opt/aja/data/datingTestSet.txt")
    val numberOfSamples = file.getLines().size
    //    file = Source.fromFile("/opt/aja/data/datingTestSet.txt")
    //    val featureArraySize = file.getLines()
    //      .take(1)
    //      .toArray
    //      .flatMap(_.split("\t"))
    //      .map(firstLine => try {firstLine.toDouble} catch {case _: java.lang.NumberFormatException => -1.0} )
    //      .filter(_ > 0.0)
    //      .size

    //reset the buffer
    file = Source.fromFile("/opt/aja/data/datingTestSet.txt")

    val featureArray = Array.ofDim[Array[Double]](numberOfSamples)
    val labelArray = Array.ofDim[String](numberOfSamples)
    try {
      var index = 0
      for (line <- file.getLines()) {
        //println(line)
        val features = line.split("\t")
        featureArray(index) = Array(features(0).toDouble, features(1).toDouble, features(2).toDouble)
        labelArray(index) = features(3).stripLineEnd
        index += 1
      }
    }
    catch {
      case e: FileNotFoundException => println("FileNotFoundException")
      case e: IOException => println("IOException")
      case _ => "Unknown Error"
    }
    file.close

    DataSet(featureArray, labelArray)
  }

  /**
   *
   * @param args
   */
  def main(args: Array[String]) {
    val dataSet = createDataset()
    println( "Predicted class for Array(0.0, 0.0) is : " + kNN(dataSet, Array(0.0,0.0), 3))

    val datingDataSet = createDatingDataSet()
    datingDataSet.normalize

    //Dirty way of printing the values and predictions
    printDataSet(datingDataSet)
        print( "Predicted class for " )

    var errorCount: Int = 0
    for ( i <- 0 until 100)
    {
      val rndGnr = Random
      val index = rndGnr.nextInt(100)
            datingDataSet.features(index).foreach(print)
            println(" is\t" + kNN(datingDataSet,  datingDataSet.features(index), 3) + "\t Actual is " + datingDataSet.label(index))

      if (kNN(datingDataSet,  datingDataSet.features(index), 3) != datingDataSet.label(index))
        errorCount+= 1
    }

    println("Errorcount : " + errorCount)
    println("the total error rate is : " + (errorCount/(10).toDouble))
  }
}
