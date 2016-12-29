package org.dhira.core.gradientcheck

import org.deeplearning4j.berkeley.Pair
import org.deeplearning4j.nn.api.Updater
import org.deeplearning4j.nn.gradient.Gradient
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.layers.BaseOutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.updater.UpdaterCreator
import org.deeplearning4j.nn.updater.graph.ComputationGraphUpdater
import org.nd4j.linalg.api.ndarray.INDArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** A utility for numerically checking gradients. <br>
  * Basic idea: compare calculated gradients with those calculated numerically,
  * to check implementation of backpropagation gradient calculation.<br>
  * See:<br>
  * - http://cs231n.github.io/neural-networks-3/#gradcheck<br>
  * - http://ufldl.stanford.edu/wiki/index.php/Gradient_checking_and_advanced_optimization<br>
  * - https://code.google.com/p/cuda-convnet/wiki/CheckingGradients<br>
  *
  *
  * Is C is cost function, then dC/dw ~= (C(w+epsilon)-C(w-epsilon)) / (2*epsilon).<br>
  * Method checks gradient calculation for every parameter separately by doing 2 forward pass
  * calculations for each parameter, so can be very time consuming for large networks.
  *
  * @author Alex Black
  */
object GradientCheckUtil {
  private var log: Logger = LoggerFactory.getLogger(classOf[GradientCheckUtil])

  /**
   * Check backprop gradients for a MultiLayerNetwork.
   * @param mln MultiLayerNetwork to test. This must be initialized.
   * @param epsilon Usually on the order/ of 1e-4 or so.
   * @param maxRelError Maximum relative error. Usually < 0.01, though maybe more for deep networks
   * @param print Whether to print full pass/failure details for each parameter gradient
   * @param exitOnFirstError If true: return upon first failure. If false: continue checking even if
   *                         one parameter gradient has failed. Typically use false for debugging, true for unit tests.
   * @param input Input array to use for forward pass. May be mini-batch data.
   * @param labels Labels/targets to use to calculate backprop gradient. May be mini-batch data.
   * @param useUpdater Whether to put the gradient through Updater.update(...). Necessary for testing things
   *                   like l1 and l2 regularization.
   * @return true if gradients are passed, false otherwise.
   */
  def checkGradients(mln: Nothing, epsilon: Double, maxRelError: Double, print: Boolean, exitOnFirstError: Boolean, input: INDArray, labels: INDArray, useUpdater: Boolean): Boolean = {
    if (epsilon <= 0.0 || epsilon > 0.1) throw new IllegalArgumentException("Invalid epsilon: expect epsilon in range (0,0.1], usually 1e-4 or so")
    if (maxRelError <= 0.0 || maxRelError > 0.25) throw new IllegalArgumentException("Invalid maxRelativeError: " + maxRelError)
    if (!(mln.getOutputLayer.isInstanceOf[BaseOutputLayer[_ <: Nothing]])) throw new IllegalArgumentException("Cannot check backprop gradients without OutputLayer")
    mln.setInput(input)
    mln.setLabels(labels)
    mln.computeGradientAndScore
    val gradAndScore: Nothing = mln.gradientAndScore
    if (useUpdater) {
      val updater: Nothing = UpdaterCreator.getUpdater(mln)
      updater.update(mln, gradAndScore.getFirst, 0, mln.batchSize)
    }
    val gradientToCheck: INDArray = gradAndScore.getFirst.gradient.dup
    val originalParams: INDArray = mln.params.dup
    val nParams: Int = originalParams.length
    var totalNFailures: Int = 0
    var maxError: Double = 0.0
    {
      var i: Int = 0
      while (i < nParams) {
        {
          val params: INDArray = originalParams.dup
          params.putScalar(i, params.getDouble(i) + epsilon)
          mln.setParameters(params)
          mln.computeGradientAndScore
          val scorePlus: Double = mln.score
          params.putScalar(i, params.getDouble(i) - 2 * epsilon)
          mln.setParameters(params)
          mln.computeGradientAndScore
          val scoreMinus: Double = mln.score
          val scoreDelta: Double = scorePlus - scoreMinus
          val numericalGradient: Double = scoreDelta / (2 * epsilon)
          if (Double.isNaN(numericalGradient)) throw new IllegalStateException("Numerical gradient was NaN for parameter " + i + " of " + nParams)
          val backpropGradient: Double = gradientToCheck.getDouble(i)
          var relError: Double = Math.abs(backpropGradient - numericalGradient) / (Math.abs(numericalGradient) + Math.abs(backpropGradient))
          if (backpropGradient == 0.0 && numericalGradient == 0.0) relError = 0.0
          if (relError > maxError) maxError = relError
          if (relError > maxRelError || Double.isNaN(relError)) {
            if (print) log.info("Param " + i + " FAILED: grad= " + backpropGradient + ", numericalGrad= " + numericalGradient + ", relError= " + relError + ", scorePlus=" + scorePlus + ", scoreMinus= " + scoreMinus)
            if (exitOnFirstError) return false
            totalNFailures += 1
          }
          else if (print) {
            log.info("Param " + i + " passed: grad= " + backpropGradient + ", numericalGrad= " + numericalGradient + ", relError= " + relError)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (print) {
      val nPass: Int = nParams - totalNFailures
      log.info("GradientCheckUtil.checkGradients(): " + nParams + " params checked, " + nPass + " passed, " + totalNFailures + " failed. Largest relative error = " + maxError)
    }
    return totalNFailures == 0
  }

  /** Check backprop gradients for a ComputationGraph
    * @param graph ComputationGraph to test. This must be initialized.
    * @param epsilon Usually on the order of 1e-4 or so.
    * @param maxRelError Maximum relative error. Usually < 0.01, though maybe more for deep networks
    * @param print Whether to print full pass/failure details for each parameter gradient
    * @param exitOnFirstError If true: return upon first failure. If false: continue checking even if
    *                         one parameter gradient has failed. Typically use false for debugging, true for unit tests.
    * @param inputs Input arrays to use for forward pass. May be mini-batch data.
    * @param labels Labels/targets (output) arrays to use to calculate backprop gradient. May be mini-batch data.
    * @return true if gradients are passed, false otherwise.
    */
  def checkGradients(graph: ComputationGraph, epsilon: Double, maxRelError: Double, print: Boolean, exitOnFirstError: Boolean, inputs: Array[INDArray], labels: Array[INDArray]): Boolean = {
    if (epsilon <= 0.0 || epsilon > 0.1) throw new IllegalArgumentException("Invalid epsilon: expect epsilon in range (0,0.1], usually 1e-4 or so")
    if (maxRelError <= 0.0 || maxRelError > 0.25) throw new IllegalArgumentException("Invalid maxRelativeError: " + maxRelError)
    if (graph.getNumInputArrays != inputs.length) throw new IllegalArgumentException("Invalid input arrays: expect " + graph.getNumInputArrays + " inputs")
    if (graph.getNumOutputArrays != labels.length) throw new IllegalArgumentException("Invalid labels arrays: expect " + graph.getNumOutputArrays + " outputs")
    {
      var i: Int = 0
      while (i < inputs.length) {
        graph.setInput(i, inputs(i))
        ({
          i += 1; i - 1
        })
      }
    }
    {
      var i: Int = 0
      while (i < labels.length) {
        graph.setLabel(i, labels(i))
        ({
          i += 1; i - 1
        })
      }
    }
    graph.computeGradientAndScore
    val gradAndScore: Nothing = graph.gradientAndScore
    val updater: ComputationGraphUpdater = new ComputationGraphUpdater(graph)
    updater.update(graph, gradAndScore.getFirst, 0, graph.batchSize)
    val gradientToCheck: INDArray = gradAndScore.getFirst.gradient.dup
    val originalParams: INDArray = graph.params.dup
    val nParams: Int = originalParams.length
    var totalNFailures: Int = 0
    var maxError: Double = 0.0
    {
      var i: Int = 0
      while (i < nParams) {
        {
          val params: INDArray = originalParams.dup
          params.putScalar(i, params.getDouble(i) + epsilon)
          graph.setParams(params)
          graph.computeGradientAndScore
          val scorePlus: Double = graph.score
          params.putScalar(i, params.getDouble(i) - 2 * epsilon)
          graph.setParams(params)
          graph.computeGradientAndScore
          val scoreMinus: Double = graph.score
          val scoreDelta: Double = scorePlus - scoreMinus
          val numericalGradient: Double = scoreDelta / (2 * epsilon)
          if (Double.isNaN(numericalGradient)) throw new IllegalStateException("Numerical gradient was NaN for parameter " + i + " of " + nParams)
          val backpropGradient: Double = gradientToCheck.getDouble(i)
          var relError: Double = Math.abs(backpropGradient - numericalGradient) / (Math.abs(numericalGradient) + Math.abs(backpropGradient))
          if (backpropGradient == 0.0 && numericalGradient == 0.0) relError = 0.0
          if (relError > maxError) maxError = relError
          if (relError > maxRelError || Double.isNaN(relError)) {
            if (print) log.info("Param " + i + " FAILED: grad= " + backpropGradient + ", numericalGrad= " + numericalGradient + ", relError= " + relError + ", scorePlus=" + scorePlus + ", scoreMinus= " + scoreMinus)
            if (exitOnFirstError) return false
            totalNFailures += 1
          }
          else if (print) {
            log.info("Param " + i + " passed: grad= " + backpropGradient + ", numericalGrad= " + numericalGradient + ", relError= " + relError)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (print) {
      val nPass: Int = nParams - totalNFailures
      log.info("GradientCheckUtil.checkGradients(): " + nParams + " params checked, " + nPass + " passed, " + totalNFailures + " failed. Largest relative error = " + maxError)
    }
    return totalNFailures == 0
  }
}

class GradientCheckUtil {
  private def this() {
    this()
  }
}