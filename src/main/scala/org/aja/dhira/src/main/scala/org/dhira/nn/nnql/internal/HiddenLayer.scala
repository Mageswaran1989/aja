package org.aja.dhira.nnql.internal

/**
  * Created by mdhandapani on 10/6/16.
  */
import scala.util.Random
import scala.math

class HiddenLayer(val N: Int, val numInputNeurons: Int, val numOutputNeurons: Int, weights: Array[Array[Double]],
                  bias: Array[Double], var rng: Random=null) {


  def uniform(min: Double, max: Double): Double = {
    return rng.nextDouble() * (max - min) + min
  }

  def binomial(n: Int, p: Double): Int = {
    if(p < 0 || p > 1) return 0

    var c: Int = 0
    var r: Double = 0.0

    var i: Int = 0

    for(i <- 0 until n) {
      r = rng.nextDouble()
      if(r < p) c += 1
    }

    return c
  }

  def sigmoid(x: Double): Double = {
    return 1.0 / (1.0 + math.pow(math.E, -x))
  }


  if(rng == null) rng = new Random(1234)

  var a: Double = 0.0
  var W: Array[Array[Double]] = Array.ofDim[Double](numOutputNeurons, numInputNeurons)
  var b: Array[Double] = new Array[Double](numOutputNeurons)

  var i: Int = 0
  if(weights == null) {
    a = 1.0 / numInputNeurons

    for(i <- 0 until numOutputNeurons) {
      for(j <- 0 until numInputNeurons) {
        W(i)(j) = uniform(-a, a)
      }
    }
  } else {
    W = weights
  }

  if(bias != null) b = bias


  def output(input: Array[Int], w: Array[Double], b: Double): Double = {
    var linear_output: Double = 0.0

    var j: Int = 0
    for(j <- 0 until numInputNeurons) {
      linear_output += w(j) * input(j)
    }
    linear_output += b

    return sigmoid(linear_output)
  }

  def sample_h_given_v(input: Array[Int], sample: Array[Int]) {
    var i: Int = 0

    for(i <- 0 until numOutputNeurons) {
      sample(i) = binomial(1, output(input, W(i), b(i)))
    }
  }
}
