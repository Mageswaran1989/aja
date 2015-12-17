package org.aja.tej.examples.streaming.HelloWorld

import breeze.linalg.DenseVector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, StreamingLinearRegressionWithSGD}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mdhandapani on 17/12/15.
 */
object StreamingLRModel {

  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[2]", "First Streaming App", Seconds(10))
    val stream = ssc.socketTextStream("localhost", 9999)

    val NumFeatures = 100
    val zeroVector = DenseVector.zeros[Double](NumFeatures)
    val model = new StreamingLinearRegressionWithSGD()
      .setInitialWeights(Vectors.dense(zeroVector.data))
      .setNumIterations(1)
      .setStepSize(0.01)

    // create a stream of labeled points
    val labeledStream = stream.map { event =>
      val split = event.split("\t")
      val y = split(0).toDouble
      val features = split(1).split(",").map(_.toDouble)
      LabeledPoint(label = y, features = Vectors.dense(features))
    }

    // create a stream of labeled points
    val vectorStream = stream.map { event =>
      val split = event.split("\t")
      val y = split(0).toDouble
      val features = split(1).split(",").map(_.toDouble)
      Vectors.dense(features)
    }

    // train and test model on the stream, and print predictions
    // for illustrative purposes
    model.trainOn(labeledStream)
    model.predictOn(vectorStream).print()
    ssc.start()
    ssc.awaitTermination()
  }
}


//Note that because we are using the same MLlib model classes for
//streaming as we did for batch processing, we can, if we choose, perform
//multiple iterations over the training data in each batch (which is just an
//RDD of LabeledPoint instances).
//Here, we will set the number of iterations to 1 to simulate purely online
//learning. In practice, you can set the number of iterations higher, but
//note that the training time per batch will go up. If the training time per
//batch is much higher than the batch interval, the streaming model will
//start to lag behind the velocity of the data stream.
//This can be handled by decreasing the number of iterations, increasing
//the batch interval, or increasing the parallelism of our streaming
//program by adding more Spark workers.