package org.aja.tej.examples.ml

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.Row


/**
 * Created by mageswaran on 27/9/15.
 */
object Pipeline extends App {

  val sc = TejUtils.getSparkContext("Pipeline")

  val sqlContext= new SQLContext(sc)

  // Prepare training documents from a list of (id, text, label) tuples.
  val training = sqlContext.createDataFrame(Seq(
    (0L, "a b c d e spark", 1.0),
    (1L, "b d", 0.0),
    (2L, "spark f g h", 1.0),
    (3L, "hadoop mapreduce", 0.0)
  )).toDF("id", "text", "label")

  // Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
  val tokenizer = new Tokenizer()
    .setInputCol("text")
    .setOutputCol("words")
  val hashingTF = new HashingTF()
    .setNumFeatures(1000)
    .setInputCol(tokenizer.getOutputCol)
    .setOutputCol("features")
  val lr = new LogisticRegression()
    .setMaxIter(10)
    .setRegParam(0.01)
  val pipeline = new Pipeline()
    .setStages(Array(tokenizer, hashingTF, lr))

  // Fit the pipeline to training documents.
  val model = pipeline.fit(training)

  // Prepare test documents, which are unlabeled (id, text) tuples.
  val test = sqlContext.createDataFrame(Seq(
    (4L, "spark i j k"),
    (5L, "l m n"),
    (6L, "mapreduce spark"),
    (7L, "apache hadoop")
  )).toDF("id", "text")

  // Make predictions on test documents.
  model.transform(test.toDF)
    .select("id", "text", "probability", "prediction")
    .collect()
    .foreach { case Row(id: Long, text: String, prob: Vector, prediction: Double) =>
    println(s"($id, $text) --> prob=$prob, prediction=$prediction")
  }
}
