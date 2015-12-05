package org.aja.tej.examples.ml

import org.aja.tej.utils.TejUtils
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.sql.SQLContext

/**
 * Created by mageswaran on 25/9/15.
 *
 * Link: http://www.cakesolutions.net/teamblogs/apache-spark-machine-learning-pipelines
 */
object ANNet extends App {

  val sc = TejUtils.getSparkContext("Artifical Neural Network")
  try {
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    // Load training data
    // 1 1:-0.222222 2:0.5 3:-0.762712 4:-0.833333 => (1, Vector(-0.222222, 0.5, -0.762712, -0.833333))
    // 0 1:0.166667 2:-0.416667 3:0.457627 4:0.5 => (0, Vector(0.166667, -0.416667, 0.457627, 0.5))
    // 2 1:-0.222222 2:-0.333333 3:0.0508474 4:-4.03573e-08 => (2, Vector(-0.222222, -0.333333, 0.0508474, -4.03573e-08))
    val data = MLUtils.loadLibSVMFile(sc, "data/sample_multiclass_classification_data.txt").toDF()

    data.printSchema()
    data.show()

    // Split the data into train and test
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)
    // specify layers for the neural network:
    // input layer of size 4 (features), two intermediate of size 5 and 4 and output of size 3 (classes)
    val layers = Array[Int](4, 5, 4, 3)
    // create the trainer and set its parameters
    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(128)
      .setSeed(1234L)
      .setMaxIter(100)

    // train the model
    //This becomes Estimator
    // DataFrame -> fit() -> Model(Transformer)
    //Prepare the data -> Set the parameters -> Create the Topology -> FeedForwardTrainer
    //                                                -> train -> MultilayerPerceptronClassificationModel
    val model = trainer.fit(train)

    // compute precision on the test set
    //This becomes Transformer
    // Feature DataFrame ->  transformer() -> Predicted Data
    val result = model.transform(test)

    val predictionAndLabels = result.select("prediction", "label")
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("precision")

    println("Precision:" + evaluator.evaluate(predictionAndLabels))
  }
  finally  {
    TejUtils.waitForSparkUI(sc)
  }
}


//Gist: https://gist.github.com/Mageswaran1989/312a9751fcba5d335abe