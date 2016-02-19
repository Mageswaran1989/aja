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

    val numOfSamples = features.size
    val numOfFeatures = features(0).size

    //Breeze does column major filling, hence transpose numOfFeatures x numOfSamples -> numOfSamples x numOfFeatures
    def getDenseMatFeatureMatrix = new DenseMatrix(numOfFeatures, numOfSamples, features.flatten).t
    def getDenseMatLabels = new DenseMatrix(numOfSamples, 1, labels) //TODO: DenseVector?
  }

  def loadDataSet(path: String = "data/svmTestSet.txt") = {
    import scala.io._
    println("Loading DataSet @ " + path)

    var file = Source.fromFile(path)
    val numberOfSamples = file.getLines().size //Number of numOfSamples/samples

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
    if (aj > highLimit)
      highLimit
    else if (aj >= lowLimit && aj <= highLimit)
      aj
    else
      lowLimit
    //    else (aj < lowLimit)  lowLimit
  }
  clipAlpha(4,2,-2)

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

  /**
   * @param dataSet
   * @param constant
   * @param tolerance
   * @param maxIterations
   * @return
   */
  def simpleSMO(dataSet: SVMDataSet, constant: Double = 0.6, tolerance: Double = 0.01, maxIterations: Int = 40) = {
    import util.control.Breaks._

    val featuresMat = dataSet.getDenseMatFeatureMatrix //numOfSamples x numOfFeatures
    val labelsMat = dataSet.getDenseMatLabels //numOfSamples x 1
    val numOfSamples = featuresMat.rows
    val numOfFeatures = featuresMat.cols

    val alphas = DenseMatrix.fill(numOfSamples, 1)(0.0) //numOfSamples X 1 TODO: DenseVector?
    var b = 0.0
    var currentIteration = 0

    var L = 0.0
    var H = 0.0

    while (currentIteration < maxIterations) {
      var alphaPairsChanged = 0
      for (i <- 0 until numOfSamples) {
        //println("!!!!!!!!!!!!!!!!!!!!! Next i :" + i)
        breakable {
          //element wise multiply
          //(numOfSamples x 1).t * (numOfSamples x numOfFeatures * numOfFeatures x 1)
          //(1 x numOfSamples * numOfSamples x 1)
          val fXiDenseMat = (alphas :* labelsMat).t * (featuresMat * featuresMat(i, ::).t)
          //Extract scalar value from Matrix
          val Ei = fXiDenseMat(0) + b - labelsMat(i,0)

          val calculatedTolerance = labelsMat(i, 0) * Ei
          //Some circus to extract scalar value from breeze mat
          var alphaI = alphas(i,0)

          //if checks if an example violates KKT conditions
          if ((calculatedTolerance < -tolerance) && (alphaI < constant) ||
            (calculatedTolerance > tolerance) && (alphaI > 0)) {

            val j = selectJrand(i, numOfSamples)
            val fXjDenseMat = (alphas :* labelsMat).t * (featuresMat * featuresMat(j, ::).t)
            val Ej = fXjDenseMat(0) + b - labelsMat(j,0)

            var alphaJ = alphas(j,0)

            val alphaIold = alphaI
            val alphaJold = alphaJ

            if (labelsMat(i,0) != labelsMat(j,0)) {
              L = max(0.0, alphaJ - alphaI)
              H = min(constant, constant + alphaJ - alphaI)
            } else {
              L = max(0.0, alphaJ + alphaI - constant)
              H = min(constant, alphaJ + alphaI)
            }

            if (L == H) {
              println(" alphaI: " +alphaI + " alphaJ: " +alphaJ + " Ei: " + Ei + " Ej: " + Ej)
              println("L==H BREAK >>>>>>>>>>>>>>>>>>>>")
              break //break the current for loop i.e continue
              // kind of throwing exception and escaping out of breakable
            }

            val etaVec = (2.0 * featuresMat(i, ::) * featuresMat(j, ::).t) - (featuresMat(i, ::) * featuresMat(i, ::).t) - (featuresMat(j, ::) * featuresMat(j, ::).t)
            val eta = etaVec(0)

            if (eta >= 0.0) {
              println("eta>=0 BREAK!!!!!!!!!!!!!!!!!!!!")
              break
            }

            val labelJ = labelsMat(j,0)
            val labelI = labelsMat(i,0)
            alphaJ = alphaJ -  ( (labelJ * (Ei - Ej)) / eta )
            alphaJ= clipAlpha(alphaJ, H, L)
            alphas(j,0) = alphaJ

            if (abs(alphaJ - alphaJold) < 0.00001) {
              println("j not moving enough BREAK!!!!!!!!!!!!!!!!!!!!")
              break
            }

            alphaI = alphaI + (labelJ * labelI * (alphaJold - alphaJ)) //update i by the same amount as j
            alphas(i,0) = alphaI

            //the update is in the oppostie direction
            val b1 = b - Ei - labelI * (alphaI - alphaIold) * (featuresMat(i, ::) * featuresMat(i, ::).t) -
              labelJ * (alphaJ - alphaJold) * (featuresMat(i, ::) * featuresMat(j, ::).t)

            val b2 = b - Ej - labelI * (alphaI - alphaIold) * (featuresMat(i, ::) * featuresMat(j, ::).t) -
              labelJ * (alphaJ - alphaJold) * (featuresMat(j, ::) * featuresMat(j, ::).t)

            if ((0 < alphaI) && (constant > alphaI)) {
              b = b1
              //println(">>>>>>>>>>>> Value of b1: " + b + " eta: " + eta + " abs(alphaJ - alphaJold): "+ abs(alphaJ - alphaJold))
            } else if ((0 < alphaJ) && (constant > alphaJ)) {
              b = b2              //println(">>>>>>>>>>>> Value of b2: " + b + " eta: " + eta + " abs(alphaJ - alphaJold): "+ abs(alphaJ - alphaJold))
            } else {
              b = (b1 + b2) / 2.0
              //println(">>>>>>>>>>>> Value of b3: " + b1(0) + " " +b2(0) + " eta: " + eta + " abs(alphaJ - alphaJold): "+ abs(alphaJ - alphaJold))
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

    println(pair._1)
    println(pair._2)

  }

}