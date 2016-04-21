package org.aja.tej.examples.streaming.HelloWorld.LR

import java.io.PrintWriter
import java.net.ServerSocket

import breeze.linalg.DenseVector

import scala.util.Random

/**
 * Created by mdhandapani on 17/12/15.
 */

//trainOn: This takes DStream[LabeledPoint] as its argument. This tells the
//model to train on every batch in the input DStream. It can be called multiple
//times to train on different streams.
//• predictOn: This also takes DStream[LabeledPoint]. This tells the model to
//make predictions on the input DStream, returning a new DStream[Double]
//that contains the model predictions.

//!!!!!!!!!!!!!!Start StreamProducer!!!!!!!!!!!!!!!!!!!!!!!!
object StreamingLRModelProducer {

  def main(args: Array[String]) {
    // Maximum number of events per second
    val MaxEvents = 100
    val NumFeatures = 100
    val random = new Random()
    /** Function to generate a normally distributed dense vector
      */
    def generateRandomArray(n: Int) = Array.tabulate(n)(_ =>
      random.nextGaussian())
    // Generate a fixed random model weight vector
    val w = new DenseVector(generateRandomArray(NumFeatures))
    val intercept = random.nextGaussian() * 10

    /** Generate a number of random data events */
    def generateNoisyData(n: Int) = {
      (1 to n).map { i =>
        val x = new DenseVector(generateRandomArray(NumFeatures))
        val y: Double = w.dot(x)
        val noisy = y + intercept
        (noisy, x)
      }
    }

    // create a network producer
    val listener = new ServerSocket(9999)
    println("Listening on port: 9999")
    while (true) {
      val socket = listener.accept()
      new Thread() {
        override def run = {
          println("Got client connected from: " + socket.getInetAddress)
          val out = new PrintWriter(socket.getOutputStream(), true)
          while (true) {
            Thread.sleep(1000)
            val num = random.nextInt(MaxEvents)
            val data = generateNoisyData(num)
            data.foreach { case (y, x) =>
              val xStr = x.data.mkString(",")
              val eventStr = s"$y\t$xStr"
              out.write(eventStr)
              out.write("\n")
            }
            out.flush()
            println(s"Created $num events...")
          }
          socket.close()
        }
      }.start()
    }
  }

}
