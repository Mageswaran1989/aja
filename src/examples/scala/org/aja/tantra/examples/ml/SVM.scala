package org.aja.tantra.examples.ml

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._

//Reference: http://cs229.stanford.edu/materials/smo.pdf
/**
 * Created by mageswaran on 13/2/16.
 */
//Support vector machines
//Pros: Low generalization error, computationally inexpensive, easy to interpret results
//Cons: Sensitive to tuning parameters and kernel choice; natively only handles binary
//classification
//Works with: Numeric values, nominal values


//N-1 dimensions is called a hyperplane
//SMO - Sequential Minimal Optimization,

object SVM {

  case class SVMDataSet(val features: Array[Array[Double]], val labels: Array[Double]) {

    val rows = features.size
    val cols = features(0).size

    //Breeze does column major filling, hence transpose cols x rows -> rows x cols
    def getDenseMatFeatureMatrix = new DenseMatrix(cols, rows, features.flatten).t

    def getDenseMatLabels = new DenseMatrix(rows, 1, labels)
  }

  def loadDataSet(path: String = "data/svmTestSet.txt") = {
    import scala.io._
    println("Loading DataSet @ " + path)
    import scala.io._

    var file = Source.fromFile(path)
    val numberOfSamples = file.getLines().size //Number of rows/samples

    //Reset the buffer, seems scala has bug! :)
    file = Source.fromFile(path)

    //Create an array for input dataset sample size
    val featuresArray = Array.ofDim[Array[Double]](numberOfSamples)
    val labelArray = Array.ofDim[Double](numberOfSamples)

    var index = 0
    for (line <- file.getLines()) {
      val features = line.split("\t")
      //if(DEBUG) println("*** " +  features(0) + "," + features(1) + "," + features(2))
      //Now create the column data!
      featuresArray(index) = Array(features(0).toDouble, features(1).toDouble)
      //Label data
      labelArray(index) = features(2).toDouble
      index += 1
    }
    SVMDataSet(featuresArray, labelArray)
  }

  //Some basic utilities

  def selectJrand(i: Int, m: Int) = {
    var j = i
    val uniformDist = Uniform(0, m)
    while (i == j)
      j = uniformDist.sample().toInt
    j
  }

  def clipAlpha(aj: Double, highLimit: Double, lowLimit: Double): Double = {
    var returnValue: Double = 0.0
    if (highLimit > aj)
      returnValue = highLimit
    if (lowLimit > aj)
      returnValue = lowLimit

    returnValue
  }

  //  Create an alphas vector filled with 0s
  //  While the number of iterations is less than MaxIterations:
  //    For every data vector in the dataset:
  //      If the data vector can be optimized:
  //         Select another data vector at random
  //         Optimize the two vectors together
  //         If the vectors can’t be optimized ➞ break
  //    If no vectors were optimized ➞ increment the iteration count

  /*
  For shell usage:
  val constant: Double = 0.6; val tolerance: Double = 0.01; val maxIterations: Int = 40
   */
  def simpleSMO(dataSet: SVMDataSet, constant: Double = 0.6, tolerance: Double = 0.01, maxIterations: Int = 40) = {
    import util.control.Breaks._

    var b = 0.0
    val featuresMat = dataSet.getDenseMatFeatureMatrix //numSampels x numFeatures
    val labelsMat = dataSet.getDenseMatLabels //numSamples x 1
    val rows = featuresMat.rows
    val cols = featuresMat.cols

    val alphas = DenseMatrix.fill(rows, 1)(0.0) //numSamples X 1
    var currentIteration = 0
    var alphaPairsChanged = 0

    var L = 0.0
    var H = 0.0

    while (currentIteration < maxIterations) {

      for (i <- 0 until rows) {
        breakable {
          //elementwise multiply
          //(numSamples x 1).t * (numSampels x numFeatures * numFeatures x 1)
          //(1 x numSamples * numSampels x 1)
          val fXiDenseMat = (alphas :* labelsMat).t * (featuresMat * featuresMat(i, ::).t)
          //Extract scalar value from Matrix
          val fXi = fXiDenseMat(0) + b
          val Ei = fXi - labelsMat(i, ::)
          val calculatedToleranceMat = labelsMat(i, ::) * Ei
          val calculatedTolerance = calculatedToleranceMat(0)
          //Some circus to extract scalar value from breeze mat
          val alphaIVect = alphas(i, ::)
          var alphaI = alphaIVect(0)
          //if checks if an example violates KKT conditions
          if ((calculatedTolerance < -tolerance) && (alphaI < constant) ||
            (calculatedTolerance > tolerance) && (alphaI > 0)
          ) {
            val j = selectJrand(i, rows)
            val fXjDenseMat = (alphas :* labelsMat).t * (featuresMat * featuresMat(j, ::).t)
            val fXj = fXjDenseMat(0) + b
            val Ej = fXj - labelsMat(j, ::)

            val alphaJVect = alphas(j, ::)
            var alphaJ = alphaJVect(0)

            val alphaIold = alphaI
            val alphaJold = alphaJ

            if (labelsMat(i, ::) == labelsMat(j, ::)) {
              L = max(0.0, alphaJ - alphaI)
              H = min(constant, constant + alphaJ - alphaI)
            } else {
              L = max(0.0, alphaJ + alphaI - constant)
              H = min(constant, alphaJ + alphaI)
            }

            if (L == H) {
              break //break the current for loop i.e continue
              // kind of throwing exception and escaping out of breakable
              println("L==H")
            }

            val etaVec = 2.0 * featuresMat(i, ::) * featuresMat(j, ::).t - featuresMat(i, ::) * featuresMat(i, ::).t - featuresMat(j, ::) * featuresMat(j, ::).t
            val eta = etaVec(0)
            if (eta >= 0.0) {
              println("eta>=0")
              break
            }

            val labelJ = labelsMat(j, ::)
            val labelI = labelsMat(i, ::)
            alphaJ = alphaJ - labelJ(0) * (Ei(0, 0) - Ej(0, 0)) / eta
            alphaJ = clipAlpha(alphaJ, H, L)

            if (abs(alphaJ - alphaJold) < 0.00001) {
              println("j not moving enough")
              break
            }

            alphaI = alphaI + labelJ(0) * labelI(0) * (alphaJold - alphaJ) //update i by the same amount as j

            //the update is in the oppostie direction
            val b1 = b - Ei(0, 0) - labelI(0) * (alphaI - alphaIold) * featuresMat(i, ::) * featuresMat(i, ::).t -
              labelJ(0) * (alphaJ - alphaJold) * featuresMat(i, ::) * featuresMat(j, ::).t

            val b2 = b - Ej(0, 0) - labelI(0) * (alphaI - alphaIold) * featuresMat(i, ::) * featuresMat(j, ::).t -
              labelJ(0) * (alphaJ - alphaJold) * featuresMat(j, ::) * featuresMat(j, ::).t

            if ((0 < alphaI) && (constant > alphaI)) {
              b = b1(0)
            } else if ((0 < alphaJ) && (constant > alphaJ)) {
              b = b2(0)
            } else {
              b = (b1(0) + b2(0)) / 2.0
            }
            alphaPairsChanged += 1

            println("iter: " + currentIteration + " i : " + i + ", pairs changed: " + alphaPairsChanged)
          }

        }
      }

      if (alphaPairsChanged == 0) {
        currentIteration += 1
      }
      else {
        currentIteration = 0
      }
      println("iteration number: " + currentIteration)
    }
    (alphas, b)
  }


  def main(args: Array[String]) {

    val dataSet = loadDataSet()
    val pair = simpleSMO(dataSet)
  }

}