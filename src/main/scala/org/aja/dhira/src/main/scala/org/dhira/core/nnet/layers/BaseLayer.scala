/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */
package org.dhira.core.nnet.layers

import org.dhira.core.containers.Pair
import org.dhira.core.nnet.api.Layer
import org.dhira.core.nnet.api.Updater
import org.dhira.core.nnet.gradient.DefaultGradient
import org.dhira.core.nnet.gradient.Gradient
import org.dhira.core.nnet.params.DefaultParamInitializer
import org.dhira.core.nnet.params.PretrainParamInitializer
import org.dhira.core.optimize.Solver
import org.dhira.core.optimize.api.ConvexOptimizer
import org.dhira.core.optimize.api.IterationListener
import org.deeplearning4j.util.Dropout
import org.dhira.core.nnet.conf.NeuralNetConfiguration
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import java.lang.reflect.Constructor
import java.util._

/**
 * A layer with a bias
 * and activation function
 * @author Adam Gibson
 */
abstract class BaseLayer[LayerConfT <: Layer](var conf:NeuralNetConfiguration) extends Layer {
  protected var inputArray: INDArray = null
  protected var paramsFlattened: INDArray = null
  protected var gradientsFlattened: INDArray = null
  protected var params: Map[String, INDArray] = null
  @transient
  protected var gradientViews: Map[String, INDArray] = null
//  protected var conf: NeuralNetConfiguration = null
  protected var dropoutMask: INDArray = null
  protected var dropoutApplied: Boolean = false
  protected var score: Double = 0.0
  protected var optimizer: ConvexOptimizer = null
  protected var gradient: Gradient = null
  protected var iterationListeners: Collection[IterationListener] = new ArrayList[IterationListener]
  protected var index: Int = 0
  protected var maskArray: INDArray = null
  protected var solver: Solver = null

  def this(conf: NeuralNetConfiguration, inputArray: INDArray) {
    this()
    this.inputArray = inputArray
    this.conf = conf
  }

  protected def layerConf: LayerConfT = {
    return this.conf.getLayer.asInstanceOf[LayerConfT]
  }

  def getInput: INDArray = {
    return input
  }

  def setInput(input: INDArray) {
    this.inputArray = input
    dropoutApplied = false
  }

  def getIndex: Int = {
    return index
  }

  def setIndex(index: Int) {
    this.index = index
  }

  def getListeners: Collection[IterationListener] = {
    return iterationListeners
  }

  def setListeners(listeners: Collection[IterationListener]) {
    this.iterationListeners = if (listeners != null) listeners else new ArrayList[IterationListener]
  }

  def setListeners(listeners: IterationListener*) {
    this.iterationListeners = new ArrayList[IterationListener]
    for (l <- listeners) iterationListeners.add(l)
  }

  def error(errorSignal: INDArray): Gradient = {
    val W: INDArray = getParam(DefaultParamInitializer.WEIGHT_KEY)
    val nextLayerGradient: Gradient = new DefaultGradient
    val wErrorSignal: INDArray = errorSignal.mmul(W.transpose)
    nextLayerGradient.gradientForVariable.put(DefaultParamInitializer.WEIGHT_KEY, wErrorSignal)
    return nextLayerGradient
  }

  def derivativeActivation(input: INDArray): INDArray = {
    val deriv: INDArray = Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(conf.getLayer.getActivationFunction, input).derivative)
    return deriv
  }

  def calcGradient(layerError: Gradient, activation: INDArray): Gradient = {
    val ret: Gradient = new DefaultGradient
    val weightErrorSignal: INDArray = layerError.getGradientFor(DefaultParamInitializer.WEIGHT_KEY)
    val weightError: INDArray = weightErrorSignal.transpose.mmul(activation).transpose
    ret.gradientForVariable.put(DefaultParamInitializer.WEIGHT_KEY, weightError)
    val biasGradient: INDArray = weightError.mean(0)
    ret.gradientForVariable.put(DefaultParamInitializer.BIAS_KEY, biasGradient)
    return ret
  }

  def backpropGradient(epsilon: INDArray): Nothing = {
    val z: INDArray = preOutput(true)
    val activationDerivative: INDArray = Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(conf.getLayer.getActivationFunction, z).derivative)
    val delta: INDArray = epsilon.muli(activationDerivative)
    if (maskArray != null) {
      delta.muliColumnVector(maskArray)
    }
    val ret: Gradient = new DefaultGradient
    val weightGrad: INDArray = gradientViews.get(DefaultParamInitializer.WEIGHT_KEY)
    Nd4j.gemm(input, delta, weightGrad, true, false, 1.0, 0.0)
    val biasGrad: INDArray = gradientViews.get(DefaultParamInitializer.BIAS_KEY)
    biasGrad.assign(delta.sum(0))
    ret.gradientForVariable.put(DefaultParamInitializer.WEIGHT_KEY, weightGrad)
    ret.gradientForVariable.put(DefaultParamInitializer.BIAS_KEY, biasGrad)
    val epsilonNext: INDArray = params.get(DefaultParamInitializer.WEIGHT_KEY).mmul(delta.transpose).transpose
    return new Nothing(ret, epsilonNext)
  }

  def fit {
    fit(this.input)
  }

  def computeGradientAndScore {
    if (this.input == null) return
    val output: INDArray = activate(true)
    setScoreWithZ(output)
  }

  protected def setScoreWithZ(z: INDArray) {
  }

  def preOutput(x: INDArray, training: Nothing): INDArray = {
    return preOutput(x, training eq TrainingMode.TRAIN)
  }

  def activate(training: Nothing): INDArray = {
    return activate(training eq TrainingMode.TRAIN)
  }

  def activate(input: INDArray, training: Nothing): INDArray = {
    return activate(input, training eq TrainingMode.TRAIN)
  }

  /**
   * Objective function:  the specified objective
   * @return the score for the objective
   */
  def score: Double = {
    return score
  }

  def gradient: Gradient = {
    return gradient
  }

  /**
   * iterate one iteration of the network
   *
   * @param input  the input to iterate on
   */
  def iterate(input: INDArray) {
    setInput(input.dup)
    applyDropOutIfNecessary(true)
    val gradient: Gradient = gradient
    import scala.collection.JavaConversions._
    for (paramType <- gradient.gradientForVariable.keySet) {
      update(gradient.getGradientFor(paramType), paramType)
    }
  }

  def update(gradient: Gradient) {
    import scala.collection.JavaConversions._
    for (paramType <- gradient.gradientForVariable.keySet) {
      update(gradient.getGradientFor(paramType), paramType)
    }
  }

  def update(gradient: INDArray, paramType: String) {
    setParam(paramType, getParam(paramType).addi(gradient))
  }

  def getOptimizer: Nothing = {
    if (optimizer == null) {
      val solver: Solver = new Solver.Builder().model(this).configure(conf).build
      this.optimizer = solver.getOptimizer
    }
    return optimizer
  }

  def setConf(conf: Nothing) {
    this.conf = conf
  }

  /** Returns the parameters of the neural network as a flattened row vector
    * @return the parameters of the neural network
    */
  def params: INDArray = {
    return Nd4j.toFlattened('f', params.values)
  }

  def getParam(param: String): INDArray = {
    return params.get(param)
  }

  def setParam(key: String, `val`: INDArray) {
    if (params.containsKey(key)) params.get(key).assign(`val`)
    else params.put(key, `val`)
  }

  def setParams(params: INDArray) {
    if (params eq paramsFlattened) return
    setParams(params, 'f')
  }

  protected def setParams(params: INDArray, order: Char) {
    val parameterList: List[String] = conf.variables
    var length: Int = 0
    import scala.collection.JavaConversions._
    for (s <- parameterList) length += getParam(s).length
    if (params.length != length) throw new IllegalArgumentException("Unable to set parameters: must be of length " + length)
    var idx: Int = 0
    val paramKeySet: Set[String] = this.params.keySet
    import scala.collection.JavaConversions._
    for (s <- paramKeySet) {
      val param: INDArray = getParam(s)
      val get: INDArray = params.get(NDArrayIndex.point(0), NDArrayIndex.interval(idx, idx + param.length))
      if (param.length != get.length) throw new IllegalStateException("Parameter " + s + " should have been of length " + param.length + " but was " + get.length)
      param.assign(get.reshape(order, param.shape))
      idx += param.length
    }
  }

  def setParamsViewArray(params: INDArray) {
    if (this.params != null && params.length != numParams) throw new IllegalArgumentException("Invalid input: expect params of length " + numParams + ", got params of length " + params.length)
    this.paramsFlattened = params
  }

  def setBackpropGradientsViewArray(gradients: INDArray) {
    if (this.params != null && gradients.length != numParams(true)) throw new IllegalArgumentException("Invalid input: expect gradients array of length " + numParams(true) + ", got params of length " + gradients.length)
    this.gradientsFlattened = gradients
    this.gradientViews = conf.getLayer.initializer.getGradientsFromFlattened(conf, gradients)
  }

  def setParamTable(paramTable: Map[String, INDArray]) {
    this.params = paramTable
  }

  def initParams {
    throw new UnsupportedOperationException("Not yet implemented")
  }

  def paramTable: Map[String, INDArray] = {
    return params
  }

  def preOutput(x: INDArray, training: Boolean): INDArray = {
    if (x == null) throw new IllegalArgumentException("No null input allowed")
    setInput(x)
    return preOutput(training)
  }

  def preOutput(training: Boolean): INDArray = {
    applyDropOutIfNecessary(training)
    val b: INDArray = getParam(DefaultParamInitializer.BIAS_KEY)
    var W: INDArray = getParam(DefaultParamInitializer.WEIGHT_KEY)
    if (conf.isUseDropConnect && training && conf.getLayer.getDropOut > 0) {
      W = Dropout.applyDropConnect(this, DefaultParamInitializer.WEIGHT_KEY)
    }
    val ret: INDArray = input.mmul(W).addiRowVector(b)
    if (maskArray != null) {
      ret.muliColumnVector(maskArray)
    }
    return ret
  }

  def activate(training: Boolean): INDArray = {
    val z: INDArray = preOutput(training)
    val ret: INDArray = Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(conf.getLayer.getActivationFunction, z, conf.getExtraArgs))
    if (maskArray != null) {
      ret.muliColumnVector(maskArray)
    }
    return ret
  }

  def activate(input: INDArray): INDArray = {
    setInput(input)
    return activate(true)
  }

  def activate(input: INDArray, training: Boolean): INDArray = {
    setInput(input)
    return activate(training)
  }

  def activate: INDArray = {
    return activate(false)
  }

  /**
   * Classify input
   * @param x the input (can either be a matrix or vector)
   *          If it's a matrix, each row is considered an example
   *          and associated rows are classified accordingly.
   *          Each row will be the likelihood of a label given that example
   * @return a probability distribution for each row
   */
  def preOutput(x: INDArray): INDArray = {
    return preOutput(x, true)
  }

  def calcL2: Double = {
    if (!conf.isUseRegularization || conf.getLayer.getL2 <= 0.0) return 0.0
    val l2Norm: Double = getParam(DefaultParamInitializer.WEIGHT_KEY).norm2Number.doubleValue
    return 0.5 * conf.getLayer.getL2 * l2Norm * l2Norm
  }

  def calcL1: Double = {
    if (!conf.isUseRegularization || conf.getLayer.getL1 <= 0.0) return 0.0
    return conf.getLayer.getL1 * getParam(DefaultParamInitializer.WEIGHT_KEY).norm1Number.doubleValue
  }

  def batchSize: Int = {
    return input.size(0)
  }

  def activationMean: INDArray = {
    val b: INDArray = getParam(DefaultParamInitializer.BIAS_KEY)
    val W: INDArray = getParam(DefaultParamInitializer.WEIGHT_KEY)
    return input.mmul(W).addiRowVector(b)
  }

  def conf: Nothing = {
    return conf
  }

  def clear {
    if (input != null) {
      input.data.destroy
      input = null
    }
  }

  protected def applyDropOutIfNecessary(training: Boolean) {
    if (conf.getLayer.getDropOut > 0 && !conf.isUseDropConnect && training && !dropoutApplied) {
      Dropout.applyDropout(input, conf.getLayer.getDropOut)
      dropoutApplied = true
    }
  }

  /**
   * Averages the given logistic regression from a mini batch into this layer
   * @param l the logistic regression layer to average into this layer
   * @param batchSize  the batch size
   */
  def merge(l: Nothing, batchSize: Int) {
    setParams(params.addi(l.params.divi(batchSize)))
    computeGradientAndScore
  }

  override def clone: Nothing = {
    var layer: Nothing = null
    try {
      val c: Constructor[_] = getClass.getConstructor(classOf[Nothing])
      layer = c.newInstance(conf).asInstanceOf[Nothing]
      val linkedTable: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
      import scala.collection.JavaConversions._
      for (entry <- params.entrySet) {
        linkedTable.put(entry.getKey, entry.getValue.dup)
      }
      layer.setParamTable(linkedTable)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    return layer
  }

  def `type`: Nothing = {
    return Type.FEED_FORWARD
  }

  /**
   * The number of parameters for the model
   *
   * @return the number of parameters for the model
   */
  def numParams: Int = {
    var ret: Int = 0
    import scala.collection.JavaConversions._
    for (`val` <- params.values) ret += `val`.length
    return ret
  }

  def numParams(backwards: Boolean): Int = {
    if (backwards) {
      var ret: Int = 0
      import scala.collection.JavaConversions._
      for (entry <- params.entrySet) {
        if (this.isInstanceOf[Nothing] && (PretrainParamInitializer.VISIBLE_BIAS_KEY == entry.getKey)) continue //todo: continue is not supported
        ret += entry.getValue.length
      }
      return ret
    }
    else return numParams
  }

  def fit(input: INDArray) {
    if (input != null) {
      setInput(input.dup)
      applyDropOutIfNecessary(true)
    }
    if (solver == null) {
      solver = new Solver.Builder().model(this).configure(conf).listeners(getListeners).build
      val updater: Nothing = solver.getOptimizer.getUpdater
      val updaterStateSize: Int = updater.stateSizeForLayer(this)
      if (updaterStateSize > 0) updater.setStateViewArray(this, Nd4j.createUninitialized(Array[Int](1, updaterStateSize), Nd4j.order), true)
    }
    this.optimizer = solver.getOptimizer
    solver.optimize
  }

  def gradientAndScore: Nothing = {
    return new Nothing(gradient, score)
  }

  def input(): INDArray = {
    inputArray
  }

  def validateInput {
  }

  /**
   * Create a gradient list based on the passed in parameters.
   * Will throw an IllegalArgumentException if the number of gradient matrices
   * isn't equal to the number of keys in the parameter list
   * @param gradients the gradients to create from
   * @return the create based on the passed in ndarrays
   */
  protected def createGradient(gradients: INDArray*): Gradient = {
    val ret: Gradient = new DefaultGradient
    if (gradients.length != conf.variables.size) throw new IllegalArgumentException("Unable to create gradients...not equal to number of parameters")
    {
      var i: Int = 0
      while (i < gradients.length) {
        {
          val paramI: INDArray = getParam(conf.variables.get(i))
          if (!Arrays.equals(paramI.shape, gradients(i).shape)) throw new IllegalArgumentException("Gradient at index " + i + " had wrong gradient size of " + Arrays.toString(gradients(i).shape) + " when should have been " + Arrays.toString(paramI.shape))
          ret.gradientForVariable.put(conf.variables.get(i), gradients(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  override def toString: String = {
    return getClass.getName + "{" + "conf=" + conf + ", dropoutMask=" + dropoutMask + ", score=" + score + ", optimizer=" + optimizer + ", listeners=" + iterationListeners + '}'
  }

  def transpose: Nothing = {
    if (!(conf.getLayer.isInstanceOf[Nothing])) throw new UnsupportedOperationException("unsupported layer type: " + conf.getLayer.getClass.getName)
    val w: INDArray = getParam(DefaultParamInitializer.WEIGHT_KEY)
    val b: INDArray = getParam(DefaultParamInitializer.BIAS_KEY)
    val vb: INDArray = getParam(PretrainParamInitializer.VISIBLE_BIAS_KEY)
    var layer: Nothing = null
    try {
      val clone: Nothing = conf.clone
      val clonedLayerConf: Nothing = clone.getLayer.asInstanceOf[Nothing]
      val nIn: Int = clonedLayerConf.getNOut
      val nOut: Int = clonedLayerConf.getNIn
      clonedLayerConf.setNIn(nIn)
      clonedLayerConf.setNOut(nOut)
      var newB: INDArray = null
      var newVB: INDArray = null
      if (vb != null) {
        newB = vb.dup
        newVB = b.dup
      }
      else {
        newB = Nd4j.create(1, nOut)
      }
      val paramsView: INDArray = Nd4j.create(1, w.length + nOut)
      layer = clone.getLayer.instantiate(clone, iterationListeners, this.index, paramsView, true)
      layer.setParam(DefaultParamInitializer.WEIGHT_KEY, w.transpose.dup)
      layer.setParam(DefaultParamInitializer.BIAS_KEY, newB)
      if (vb != null) layer.setParam(PretrainParamInitializer.VISIBLE_BIAS_KEY, newVB)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException("unable to construct transposed layer", e)
      }
    }
    return layer
  }

  def accumulateScore(accum: Double) {
    score += accum
  }

  def setInputMiniBatchSize(size: Int) {
  }

  def getInputMiniBatchSize: Int = {
    return input.size(0)
  }

  def applyLearningRateScoreDecay {
    import scala.collection.JavaConversions._
    for (lrPair <- conf.getLearningRateByParam.entrySet) conf.setLearningRateByParam(lrPair.getKey, lrPair.getValue * (conf.getLrPolicyDecayRate + Nd4j.EPS_THRESHOLD))
  }

  def setMaskArray(maskArray: INDArray) {
    this.maskArray = maskArray
  }
}