package org.aja.tej.tej.test.mllib

/**
 * Created by mdhandapani on 29/7/15.
 */
/**
 * Created by mdhandapani on 16/7/15.
 */

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors

object LinearRegression {

  def loadData(sc: SparkContext) = { sc.textFile("data/ridge-data/lpsa.data") }

  def main (args: Array[String]) {

    val conf = new SparkConf().setAppName("MLLib LinearRegression Application").setMaster("local[4]")
    val sc = new SparkContext(conf)

    val file = loadData(sc)

    val parsedData = file.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }.cache()

    val numIterations = 100
    val model = LinearRegressionWithSGD.train(parsedData, numIterations)

    val valuesAndPredictions = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }

    val MSE = valuesAndPredictions.map{case(v,p) => math.pow((v-p), 2)}.mean()
    println("\n\n\n\n\n training Mean Squared Error = " + MSE + "\n\n\n\n\n\n")

    model.save(sc, "AjaLR")
    val testSave = LinearRegressionModel.load(sc, "AjaLR")
  }

}