package org.aja.tej.examples.mllib.regression

import org.aja.tej.utils.Utils
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionModel, LinearRegressionWithSGD}

/**
 * Created by mageswaran on 14/9/15.
 */


object LinearReg extends App{

  val sc = Utils.getSparkContext("Simple LR")
  // Load and parse the data
  val data = sc.textFile("data/lpsa.data")

  val parsedData = data.map { line =>
    val parts = line.split(',')
    LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
  }.cache()

  // Building the model
  val numIterations = 100
  val model = LinearRegressionWithSGD.train(parsedData, numIterations)

  // Evaluate model on training examples and compute training error
  val valuesAndPreds = parsedData.map { point =>
    val prediction = model.predict(point.features)
    (point.label, prediction)
  }

  val MSE = valuesAndPreds.map{case(v, p) => math.pow((v - p), 2)}.mean()
  println("training Mean Squared Error = " + MSE)

  // Save and load model
  model.save(sc, "myModelPath")
  val sameModel = LinearRegressionModel.load(sc, "myModelPath")

//  import scala.reflect.runtime.universe._
//  typeOf(LinearRegressionWithSGD.type).baseClasses.map(_.name).mkString("extends")
}


