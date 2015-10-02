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

  /*
      Class Linearization to avoid multiple inheritance
      =================================================
      For example "LinearRegressionModel"
      @Since("0.8.0")
     class LinearRegressionModel @Since("1.1.0") (
         @Since("1.0.0") override val weights: Vector,
         @Since("0.8.0") override val intercept: Double)
       extends GeneralizedLinearModel(weights, intercept) with RegressionModel with Serializable
       with Saveable with PMMLExportable

      output is
      org.apache.spark.mllib.regression.LinearRegressionModel
      org.apache.spark.mllib.pmml.PMMLExportable
      org.apache.spark.mllib.util.Saveable
      org.apache.spark.mllib.regression.RegressionModel
      org.apache.spark.mllib.regression.GeneralizedLinearModel
      scala.Serializable
      java.io.Serializable
      java.lang.Object
      scala.Any

      1. Start with the class declaration
         -> LinearRegressionModel
      2. Reverse the order of the list, except keep the first item  at the beginning, and drop the other keywords
         -> LinearRegressionModel PMMLExportable Saveable Serializable RegressionModel GeneralizedLinearModel
      3. Replace each item in the list except the first  with its linearization
         -> LinearRegressionModel PMMLExportable Saveable Serializable
              RegressionModel Serializable GeneralizedLinearModel Serializable
      4. Remove the classes on the left that appears twice
         -> LinearRegressionModel PMMLExportable Saveable
              RegressionModel GeneralizedLinearModel Serializable
      5. Insert a right-associative list-concatenation operator between each element in the list
         -> LinearRegressionModel +: PMMLExportable +: Saveable ...
      6. Append the standard Scala classes ScalaObject, AnyRef, Any
   */

  //To print class Linearization
  import scala.reflect.runtime.universe._
  //val tpe = typeOf[LinearRegressionModel]
  val tpe = typeOf[LinearRegressionWithSGD]
  tpe.baseClasses foreach { s => println(s.fullName) }


  //RIP GradientDescent.runMiniBatchSGD method
  val rddData = parsedData.map(p => (p.label, p.features))
  val sampledData = rddData.sample(false, 1, 42 + 0)
  println(sampledData.getClass)
  sampledData.foreach(data => println(data._1, data._2))

}


/**
 * LinearRegressionWithSGD.train->run->GeneralizedLinearAlgorithm.optimizer->ptimize
 *
 *
 */

/**
 * Class linearization analysis
 * ============================
 * org.apache.spark.mllib.regression.LinearRegressionWithSGD
     org.apache.spark.mllib.regression.GeneralizedLinearAlgorithm
     scala.Serializable
     java.io.Serializable
     org.apache.spark.Logging
     java.lang.Object
     scala.Any
 *
 * org.apache.spark.mllib.regression.LinearRegressionModel
      org.apache.spark.mllib.pmml.PMMLExportable
      org.apache.spark.mllib.util.Saveable
      org.apache.spark.mllib.regression.RegressionModel
      org.apache.spark.mllib.regression.GeneralizedLinearModel
      scala.Serializable
      java.io.Serializable
      java.lang.Object
      scala.Any
 */