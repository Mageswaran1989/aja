package org.aja.tej.examples.mllib.regression

import org.aja.tej.utils.TejUtils
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils

/**
 * Created by mageswaran on 14/9/15.
 */
object LogReg extends App {

  val sc = TejUtils.getSparkContext("Simple LogReg")

  // Load training data in LIBSVM format.
  val data = MLUtils.loadLibSVMFile(sc, "data/sample_libsvm_data.txt")

  // Split data into training (60%) and test (40%).
  val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
  val training = splits(0).cache()
  val test = splits(1)

  // Run training algorithm to build the model
  val model = new LogisticRegressionWithLBFGS()
    .setNumClasses(10)
    .run(training)

  // Compute raw scores on the test set.
  val predictionAndLabels = test.map { case LabeledPoint(label, features) =>
    val prediction = model.predict(features)
    (prediction, label)
  }

  // Get evaluation metrics.
  val metrics = new MulticlassMetrics(predictionAndLabels)
  val precision = metrics.precision
  println("Precision = " + precision)

  // Save and load model
  model.save(sc, "myModelPath")
  val sameModel = LogisticRegressionModel.load(sc, "myModelPath")
}
