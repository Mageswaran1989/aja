//package org.aja.tej.test.usecases
//
//import org.apache.spark.mllib.classification.SVMWithSGD
//import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
//import org.apache.spark.mllib.util.MLUtils
//
///**
// * Created by mdhandapani on 29/7/15.
// */
//object EventDetection {
//
//  TwitterUtils.createStream(...)
//  .filter(_.getText.contains("earthquake") || _.getText.contains("shaking"))
//
//  // We would prepare some earthquake tweet data and load it in LIBSVM format.
//  val data = MLUtils.loadLibSVMFile(sc, "sample_earthquate_tweets.txt")
//
//  // Split data into training (60%) and test (40%).
//  val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
//  val training = splits(0).cache()
//  val test = splits(1)
//
//  // Run training algorithm to build the model
//  val numIterations = 100
//  val model = SVMWithSGD.train(training, numIterations)
//
//  // Clear the default threshold.
//  model.clearThreshold()
//
//  // Compute raw scores on the test set.
//  val scoreAndLabels = test.map { point =>
//    val score = model.predict(point.features)
//    (score, point.label)
//  }
//
//  // Get evaluation metrics.
//  val metrics = new BinaryClassificationMetrics(scoreAndLabels)
//  val auROC = metrics.areaUnderROC()
//
//  println("Area under ROC = " + auROC)
//
//  // sc is an existing SparkContext.
//  val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
//  // sendEmail is a custom function
//  sqlContext.sql("FROM earthquake_warning_users SELECT firstName, lastName, city, email")
//    .collect().foreach(sendEmail)
//}
