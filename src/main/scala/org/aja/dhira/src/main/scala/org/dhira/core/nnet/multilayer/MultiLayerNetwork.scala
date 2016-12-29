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
package org.dhira.core.nnet.multilayer

import lombok.Setter
import org.deeplearning4j.berkeley.Pair
import org.deeplearning4j.berkeley.Triple
import org.deeplearning4j.datasets.iterator.AsyncDataSetIterator
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.api.Classifier
import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.api.Updater
import org.deeplearning4j.nn.api.layers.IOutputLayer
import org.deeplearning4j.nn.api.layers.RecurrentLayer
import org.deeplearning4j.nn.conf.BackpropType
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.gradient.DefaultGradient
import org.deeplearning4j.nn.gradient.Gradient
import org.deeplearning4j.nn.params.DefaultParamInitializer
import org.deeplearning4j.nn.updater.MultiLayerUpdater
import org.deeplearning4j.nn.updater.UpdaterCreator
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.Solver
import org.deeplearning4j.optimize.api.ConvexOptimizer
import org.deeplearning4j.optimize.api.IterationListener
import org.deeplearning4j.util.ModelSerializer
import org.deeplearning4j.util.MultiLayerUtil
import org.deeplearning4j.util.TimeSeriesUtils
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.heartbeat.Heartbeat
import org.nd4j.linalg.heartbeat.reports.Environment
import org.nd4j.linalg.heartbeat.reports.Event
import org.nd4j.linalg.heartbeat.reports.Task
import org.nd4j.linalg.heartbeat.utils.EnvironmentUtils
import org.nd4j.linalg.heartbeat.utils.TaskUtils
import org.nd4j.linalg.indexing.NDArrayIndex
import org.nd4j.linalg.ops.transforms.Transforms
import org.nd4j.linalg.util.FeatureUtil
import org.nd4j.linalg.util.LinAlgExceptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util._

/**
 * MultiLayerNetwork is a neural network with multiple layers in a stack, and usually an output layer.
 * For neural networks with a more complex connection architecture, use {@link org.deeplearning4j.nn.graph.ComputationGraph}
 * which allows for an arbitrary directed acyclic graph connection structure between layers.
 * MultiLayerNetwork is trainable via backprop, with optional pretraining, depending on the type of layers it contains.
 *
 * @author Adam Gibson
 */
object MultiLayerNetwork {
  private val log: Logger = LoggerFactory.getLogger(classOf[MultiLayerNetwork])
}

class MultiLayerNetwork extends Serializable with Classifier with Layer {
  protected var layers: Array[Nothing] = null
  protected var layerMap: LinkedHashMap[String, Nothing] = new LinkedHashMap[K, V]
  protected var input: INDArray = null
  protected var labels: INDArray = null
  protected var initCalled: Boolean = false
  private var listeners: Collection[Nothing] = new ArrayList[E]
  protected var defaultConfiguration: Nothing = null
  protected var layerWiseConfigurations: Nothing = null
  protected var gradient: Nothing = null
  protected var epsilon: INDArray = null
  protected var score: Double = .0
  @Setter protected var initDone: Boolean = false
  protected var flattenedParams: INDArray = null
  @transient
  protected var flattenedGradients: INDArray = null
  protected var mask: INDArray = null
  protected var layerIndex: Int = 0
  @transient
  protected var solver: Nothing = null

  def this(conf: Nothing) {
    this()
    this.layerWiseConfigurations = conf
    this.defaultConfiguration = conf.getConf(0).clone
  }

  /**
   * Initialize the network based on the configuration
   *
   * @param conf   the configuration json
   * @param params the parameters
   */
  def this(conf: String, params: INDArray) {
    this()
    `this`(MultiLayerConfiguration.fromJson(conf))
    init
    setParameters(params)
  }

  /**
   * Initialize the network based on the configuraiton
   *
   * @param conf   the configuration
   * @param params the parameters
   */
  def this(conf: Nothing, params: INDArray) {
    this()
    `this`(conf)
    init
    setParameters(params)
  }

  protected def intializeConfigurations {
    if (layerWiseConfigurations == null) layerWiseConfigurations = new Nothing().build
    if (layers == null) layers = new Array[Nothing](getnLayers)
    if (defaultConfiguration == null) defaultConfiguration = new Nothing().build
  }

  /**
   * This unsupervised learning method runs
   * contrastive divergence on each RBM layer in the network.
   *
   * @param iter the input to iterate on
   *             The typical tip is that the higher k is the closer to the model
   *             you will be approximating due to more sampling. K = 1
   *             usually gives very good results and is the default in quite a few situations.
   */
  def pretrain(iter: DataSetIterator) {
    if (!layerWiseConfigurations.isPretrain) return
    var layerInput: INDArray = null
    {
      var i: Int = 0
      while (i < getnLayers) {
        {
          if (i == 0) {
            while (iter.hasNext) {
              val next: DataSet = iter.next
              if (getLayerWiseConfigurations.getInputPreProcess(i) != null) {
                val features: INDArray = next.getFeatureMatrix
                layerInput = getLayerWiseConfigurations.getInputPreProcess(i).preProcess(features, features.size(0))
              }
              else layerInput = next.getFeatureMatrix
              setInput(layerInput)
              if (this.getInput == null || this.getLayers == null) initializeLayers(input)
              layers(i).fit(input)
              MultiLayerNetwork.log.info("Training on layer " + (i + 1) + " with " + input.size(0) + " examples")
            }
          }
          else {
            while (iter.hasNext) {
              val next: DataSet = iter.next
              layerInput = next.getFeatureMatrix
              {
                var j: Int = 1
                while (j <= i) {
                  layerInput = activationFromPrevLayer(j - 1, layerInput, true)
                  ({
                    j += 1; j - 1
                  })
                }
              }
              MultiLayerNetwork.log.info("Training on layer " + (i + 1) + " with " + layerInput.size(0) + " examples")
              getLayer(i).fit(layerInput)
            }
          }
          iter.reset
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /**
   * This unsupervised learning method runs
   * contrastive divergence on each RBM layer in the network.
   *
   * @param input the input to iterate on
   *              The typical tip is that the higher k is the closer to the model
   *              you will be approximating due to more sampling. K = 1
   *              usually gives very good results and is the default in quite a few situations.
   */
  def pretrain(input: INDArray) {
    if (!layerWiseConfigurations.isPretrain) return
    val miniBatchSize: Int = input.size(0)
    var layerInput: INDArray = null
    {
      var i: Int = 0
      while (i < getnLayers - 1) {
        {
          if (i == 0) if (getLayerWiseConfigurations.getInputPreProcess(i) != null) layerInput = getLayerWiseConfigurations.getInputPreProcess(i).preProcess(input, miniBatchSize)
          else layerInput = input
          else layerInput = activationFromPrevLayer(i - 1, layerInput, true)
          MultiLayerNetwork.log.info("Training on layer " + (i + 1) + " with " + layerInput.size(0) + " examples")
          getLayers(i).fit(layerInput)
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def batchSize: Int = {
    return input.size(0)
  }

  def conf: Nothing = {
    return defaultConfiguration
  }

  def setConf(conf: Nothing) {
    throw new UnsupportedOperationException
  }

  def input: INDArray = {
    return input
  }

  def validateInput {
  }

  def getOptimizer: Nothing = {
    throw new UnsupportedOperationException
  }

  def getParam(param: String): INDArray = {
    val idx: Int = param.indexOf('_')
    if (idx == -1) throw new IllegalStateException("Invalid param key: not have layer separator: \"" + param + "\"")
    val layerIdx: Int = param.substring(0, idx).toInt
    val newKey: String = param.substring(idx + 1)
    return layers(layerIdx).getParam(newKey)
  }

  def initParams {
    throw new UnsupportedOperationException
  }

  def paramTable: Map[String, INDArray] = {
    val allParams: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          val paramMap: Map[String, INDArray] = layers(i).paramTable
          import scala.collection.JavaConversions._
          for (entry <- paramMap.entrySet) {
            val newKey: String = i + "_" + entry.getKey
            allParams.put(newKey, entry.getValue)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return allParams
  }

  def setParamTable(paramTable: Map[String, INDArray]) {
    throw new UnsupportedOperationException
  }

  def setParam(key: String, `val`: INDArray) {
    val idx: Int = key.indexOf('_')
    if (idx == -1) throw new IllegalStateException("Invalid param key: not have layer separator: \"" + key + "\"")
    val layerIdx: Int = key.substring(0, idx).toInt
    val newKey: String = key.substring(idx + 1)
    layers(layerIdx).setParam(newKey, `val`)
  }

  def getLayerWiseConfigurations: Nothing = {
    return layerWiseConfigurations
  }

  def setLayerWiseConfigurations(layerWiseConfigurations: Nothing) {
    this.layerWiseConfigurations = layerWiseConfigurations
  }

  /**
   * Base class for initializing the neuralNets based on the input.
   * This is meant for capturing numbers such as input columns or other things.
   *
   * @param input the input matrix for training
   */
  def initializeLayers(input: INDArray) {
    if (input == null) throw new IllegalArgumentException("Unable to initialize neuralNets with empty input")
    this.input = input
    setInputMiniBatchSize(input.size(0))
    if (!initCalled) init
  }

  /**
   * Initialize the MultiLayerNetwork. This should be called once before the network is used.
   */
  def init {
    init(null, false)
  }

  /**
   * Initialize the MultiLayerNetwork, optionally with an existing parameters array.
   * If an existing parameters array is specified, it will be used (and the values will not be modified) in the network;
   * if no parameters array is specified, parameters will be initialized randomly according to the network configuration.
   *
   * @param parameters              Network parameter. May be null. If null: randomly initialize.
   * @param cloneParametersArray    Whether the parameter array (if any) should be cloned, or used directly
   */
  def init(parameters: INDArray, cloneParametersArray: Boolean) {
    if (layerWiseConfigurations == null || layers == null) intializeConfigurations
    if (initCalled) return
    val nLayers: Int = getnLayers
    if (nLayers < 1) throw new IllegalStateException("Unable to create network: number of layers is less than 1")
    if (this.layers == null || this.layers(0) == null) {
      if (this.layers == null) this.layers = new Array[Nothing](nLayers)
      var backpropParamLength: Int = 0
      val nParamsPerLayer: Array[Int] = new Array[Int](nLayers)
      {
        var i: Int = 0
        while (i < nLayers) {
          {
            val conf: Nothing = layerWiseConfigurations.getConf(i)
            nParamsPerLayer(i) = conf.getLayer.initializer.numParams(conf, true)
            backpropParamLength += nParamsPerLayer(i)
          }
          ({
            i += 1; i - 1
          })
        }
      }
      var initializeParams: Boolean = false
      if (parameters != null) {
        if (!parameters.isRowVector) throw new IllegalArgumentException("Invalid parameters: should be a row vector")
        if (parameters.length != backpropParamLength) throw new IllegalArgumentException("Invalid parameters: expected length " + backpropParamLength + ", got length " + parameters.length)
        if (cloneParametersArray) flattenedParams = parameters.dup
        else flattenedParams = parameters
        initializeParams = false
      }
      else {
        flattenedParams = Nd4j.create(1, backpropParamLength)
        initializeParams = true
      }
      var paramCountSoFar: Int = 0
      {
        var i: Int = 0
        while (i < nLayers) {
          {
            var paramsView: INDArray = null
            if (nParamsPerLayer(i) > 0) {
              paramsView = flattenedParams.get(NDArrayIndex.point(0), NDArrayIndex.interval(paramCountSoFar, paramCountSoFar + nParamsPerLayer(i)))
            }
            else {
              paramsView = null
            }
            paramCountSoFar += nParamsPerLayer(i)
            val conf: Nothing = layerWiseConfigurations.getConf(i)
            layers(i) = conf.getLayer.instantiate(conf, listeners, i, paramsView, initializeParams)
            layerMap.put(conf.getLayer.getLayerName, layers(i))
          }
          ({
            i += 1; i - 1
          })
        }
      }
      initCalled = true
      initMask
    }
    defaultConfiguration.clearVariables
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          import scala.collection.JavaConversions._
          for (s <- layers(i).conf.variables) {
            defaultConfiguration.addVariable(i + "_" + s)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def isInitCalled: Boolean = {
    return initCalled
  }

  /**
   * This method: initializes the flattened gradients array (used in backprop) and sets the appropriate subset in all layers.
   * As a general rule, this shouldn't ever need to be called manually when doing training via fit(DataSet) or fit(DataSetIterator)
   */
  def initGradientsView {
    if (layers == null) init
    val nLayers: Int = layers.length
    var backpropParamLength: Int = 0
    val nParamsPerLayer: Array[Int] = new Array[Int](nLayers)
    {
      var i: Int = 0
      while (i < nLayers) {
        {
          val conf: Nothing = layerWiseConfigurations.getConf(i)
          nParamsPerLayer(i) = layers(i).conf.getLayer.initializer.numParams(conf, true)
          backpropParamLength += nParamsPerLayer(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    flattenedGradients = Nd4j.createUninitialized(Array[Int](1, backpropParamLength), 'f')
    var backpropParamsSoFar: Int = 0
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          if (nParamsPerLayer(i) == 0) continue //todo: continue is not supported
        val thisLayerGradView: INDArray = flattenedGradients.get(NDArrayIndex.point(0), NDArrayIndex.interval(backpropParamsSoFar, backpropParamsSoFar + nParamsPerLayer(i)))
          layers(i).setBackpropGradientsViewArray(thisLayerGradView)
          backpropParamsSoFar += nParamsPerLayer(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /**
   * Triggers the activation of the last hidden layer ie: not logistic regression
   *
   * @return the activation of the last hidden layer given the last input to the network
   */
  def activate: INDArray = {
    return getLayers(getLayers.length - 1).activate
  }

  /**
   * Triggers the activation for a given layer
   *
   * @param layer the layer to activate on
   * @return the activation for a given layer
   */
  def activate(layer: Int): INDArray = {
    return getLayer(layer).activate
  }

  def activate(input: INDArray): INDArray = {
    throw new UnsupportedOperationException
  }

  /**
   * Triggers the activation of the given layer
   *
   * @param layer the layer to trigger on
   * @param input the input to the hidden layer
   * @return the activation of the layer based on the input
   */
  def activate(layer: Int, input: INDArray): INDArray = {
    return getLayer(layer).activate(input)
  }

  def activationMean: INDArray = {
    throw new UnsupportedOperationException
  }

  /**
   * Sets the input and labels from this dataset
   *
   * @param data the dataset to initialize with
   */
  def initialize(data: DataSet) {
    setInput(data.getFeatureMatrix)
    feedForward(getInput)
    this.labels = data.getLabels
    if (getOutputLayer.isInstanceOf[Nothing]) {
      val ol: Nothing = getOutputLayer.asInstanceOf[Nothing]
      ol.setLabels(labels)
    }
  }

  /**
   * Compute input linear transformation (z) from previous layer
   * Apply pre processing transformation where necessary
   *
   * @param curr  the current layer
   * @param input the input
   * @param training training or test mode
   * @return the activation from the previous layer
   */
  def zFromPrevLayer(curr: Int, input: INDArray, training: Boolean): INDArray = {
    if (getLayerWiseConfigurations.getInputPreProcess(curr) != null) input = getLayerWiseConfigurations.getInputPreProcess(curr).preProcess(input, input.size(0))
    val ret: INDArray = layers(curr).preOutput(input, training)
    return ret
  }

  /**
   * Calculate activation from previous layer including pre processing where necessary
   *
   * @param curr  the current layer
   * @param input the input
   * @return the activation from the previous layer
   */
  def activationFromPrevLayer(curr: Int, input: INDArray, training: Boolean): INDArray = {
    if (getLayerWiseConfigurations.getInputPreProcess(curr) != null) input = getLayerWiseConfigurations.getInputPreProcess(curr).preProcess(input, getInputMiniBatchSize)
    val ret: INDArray = layers(curr).activate(input, training)
    return ret
  }

  /**
   * Calculate activation for few layers at once. Suitable for autoencoder partial activation.
   *
   * In example: in 10-layer deep autoencoder, layers 0 - 4 inclusive are used for encoding part, and layers 5-9 inclusive are used for decoding part.
   *
   * @param from first layer to be activated, inclusive
   * @param to last layer to be activated, inclusive
   * @return the activation from the last layer
   */
  def activateSelectedLayers(from: Int, to: Int, input: INDArray): INDArray = {
    if (input == null) throw new IllegalStateException("Unable to perform activation; no input found")
    if (from < 0 || from >= layers.length || from >= to) throw new IllegalStateException("Unable to perform activation; FROM is out of layer space")
    if (to < 1 || to >= layers.length) throw new IllegalStateException("Unable to perform activation; TO is out of layer space")
    var res: INDArray = input
    {
      var l: Int = from
      while (l <= to) {
        {
          res = this.activationFromPrevLayer(l, res, false)
        }
        ({
          l += 1; l - 1
        })
      }
    }
    return res
  }

  /**
   * * Compute input linear transformation (z) of the output layer
   *
   * @return the list of activations for each layer
   */
  def computeZ(training: Boolean): List[INDArray] = {
    var currInput: INDArray = this.input
    val activations: List[INDArray] = new ArrayList[INDArray]
    activations.add(currInput)
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          currInput = zFromPrevLayer(i, currInput, training)
          activations.add(currInput)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return activations
  }

  /**
   * Compute activations from input to output of the output layer
   *
   * @return the list of activations for each layer
   */
  def computeZ(input: INDArray, training: Boolean): List[INDArray] = {
    if (input == null) throw new IllegalStateException("Unable to perform feed forward; no input found")
    else if (this.getLayerWiseConfigurations.getInputPreProcess(0) != null) setInput(getLayerWiseConfigurations.getInputPreProcess(0).preProcess(input, getInputMiniBatchSize))
    else setInput(input)
    return computeZ(training)
  }

  /**
   * Compute activations from input to output of the output layer
   *
   * @return the list of activations for each layer
   */
  def feedForward(input: INDArray, train: Boolean): List[INDArray] = {
    setInput(input)
    return feedForward(train)
  }

  /**
   * Compute activations from input to output of the output layer
   *
   * @return the list of activations for each layer
   */
  def feedForward(train: Boolean): List[INDArray] = {
    return feedForwardToLayer(layers.length - 1, train)
  }

  /** Compute the activations from the input to the specified layer.<br>
    * To compute activations for all layers, use feedForward(...) methods<br>
    * Note: output list includes the original input. So list.get(0) is always the original input, and
    * list.get(i+1) is the activations of the ith layer.
    * @param layerNum Index of the last layer to calculate activations for. Layers are zero-indexed.
    *                 feedForwardToLayer(i,input) will return the activations for layers 0..i (inclusive)
    * @param input Input to the network
    * @return list of activations.
    */
  def feedForwardToLayer(layerNum: Int, input: INDArray): List[INDArray] = {
    return feedForwardToLayer(layerNum, input, false)
  }

  /** Compute the activations from the input to the specified layer.<br>
    * To compute activations for all layers, use feedForward(...) methods<br>
    * Note: output list includes the original input. So list.get(0) is always the original input, and
    * list.get(i+1) is the activations of the ith layer.
    * @param layerNum Index of the last layer to calculate activations for. Layers are zero-indexed.
    *                 feedForwardToLayer(i,input) will return the activations for layers 0..i (inclusive)
    * @param input Input to the network
    * @param train true for training, false for test (i.e., false if using network after training)
    * @return list of activations.
    */
  def feedForwardToLayer(layerNum: Int, input: INDArray, train: Boolean): List[INDArray] = {
    setInput(input)
    return feedForwardToLayer(layerNum, train)
  }

  /** Compute the activations from the input to the specified layer, using the currently set input for the network.<br>
    * To compute activations for all layers, use feedForward(...) methods<br>
    * Note: output list includes the original input. So list.get(0) is always the original input, and
    * list.get(i+1) is the activations of the ith layer.
    * @param layerNum Index of the last layer to calculate activations for. Layers are zero-indexed.
    *                 feedForwardToLayer(i,input) will return the activations for layers 0..i (inclusive)
    * @param train true for training, false for test (i.e., false if using network after training)
    * @return list of activations.
    */
  def feedForwardToLayer(layerNum: Int, train: Boolean): List[INDArray] = {
    var currInput: INDArray = input
    val activations: List[INDArray] = new ArrayList[INDArray]
    activations.add(currInput)
    {
      var i: Int = 0
      while (i <= layerNum) {
        {
          currInput = activationFromPrevLayer(i, currInput, train)
          activations.add(currInput)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return activations
  }

  /**
   * Compute activations from input to output of the output layer
   *
   * @return the list of activations for each layer
   */
  def feedForward: List[INDArray] = {
    return feedForward(false)
  }

  /**
   * Compute activations from input to output of the output layer
   *
   * @return the list of activations for each layer
   */
  def feedForward(input: INDArray): List[INDArray] = {
    if (input == null) throw new IllegalStateException("Unable to perform feed forward; no input found")
    else if (this.getLayerWiseConfigurations.getInputPreProcess(0) != null) setInput(getLayerWiseConfigurations.getInputPreProcess(0).preProcess(input, input.size(0)))
    else setInput(input)
    return feedForward
  }

  /** Compute the activations from the input to the output layer, given mask arrays (that may be null)
    * The masking arrays are used in situations such an one-to-many and many-to-one rucerrent neural network (RNN)
    * designs, as well as for supporting time series of varying lengths within the same minibatch for RNNs.
    */
  def feedForward(input: INDArray, featuresMask: INDArray, labelsMask: INDArray): List[INDArray] = {
    setLayerMaskArrays(featuresMask, labelsMask)
    val list: List[INDArray] = feedForward(input)
    clearLayerMaskArrays
    return list
  }

  def gradient: Nothing = {
    return gradient
  }

  def epsilon: INDArray = {
    return epsilon
  }

  def gradientAndScore: Nothing = {
    return new Nothing(gradient, score)
  }

  protected def computeDeltasR(v: INDArray): List[INDArray] = {
    val deltaRet: List[INDArray] = new ArrayList[INDArray]
    val deltas: Array[INDArray] = new Array[INDArray](getnLayers + 1)
    val activations: List[INDArray] = feedForward
    val rActivations: List[INDArray] = feedForwardR(activations, v)
    val weights: List[INDArray] = new ArrayList[INDArray]
    val biases: List[INDArray] = new ArrayList[INDArray]
    val activationFunctions: List[String] = new ArrayList[String]
    {
      var j: Int = 0
      while (j < getLayers.length) {
        {
          weights.add(getLayers(j).getParam(DefaultParamInitializer.WEIGHT_KEY))
          biases.add(getLayers(j).getParam(DefaultParamInitializer.BIAS_KEY))
          activationFunctions.add(getLayers(j).conf.getLayer.getActivationFunction)
        }
        ({
          j += 1; j - 1
        })
      }
    }
    var rix: INDArray = rActivations.get(rActivations.size - 1).divi(input.size(0).toDouble)
    LinAlgExceptions.assertValidNum(rix)
    {
      var i: Int = getnLayers - 1
      while (i >= 0) {
        {
          deltas(i) = activations.get(i).transpose.mmul(rix)
          if (i > 0) rix = rix.mmul(weights.get(i).addRowVector(biases.get(i)).transpose).muli(Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(activationFunctions.get(i - 1), activations.get(i)).derivative))
        }
        ({
          i -= 1; i + 1
        })
      }
    }
    {
      var i: Int = 0
      while (i < deltas.length - 1) {
        {
          deltaRet.add(deltas(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return deltaRet
  }

  protected def computeDeltas2: List[Nothing] = {
    val deltaRet: List[Nothing] = new ArrayList[E]
    val activations: List[INDArray] = feedForward
    val deltas: Array[INDArray] = new Array[INDArray](activations.size - 1)
    val preCons: Array[INDArray] = new Array[INDArray](activations.size - 1)
    var ix: INDArray = activations.get(activations.size - 1).sub(labels).div(labels.size(0))
    val weights: List[INDArray] = new ArrayList[INDArray]
    val biases: List[INDArray] = new ArrayList[INDArray]
    val activationFunctions: List[String] = new ArrayList[String]
    {
      var j: Int = 0
      while (j < getLayers.length) {
        {
          weights.add(getLayers(j).getParam(DefaultParamInitializer.WEIGHT_KEY))
          biases.add(getLayers(j).getParam(DefaultParamInitializer.BIAS_KEY))
          activationFunctions.add(getLayers(j).conf.getLayer.getActivationFunction)
        }
        ({
          j += 1; j - 1
        })
      }
    }
    {
      var i: Int = weights.size - 1
      while (i >= 0) {
        {
          deltas(i) = activations.get(i).transpose.mmul(ix)
          preCons(i) = Transforms.pow(activations.get(i).transpose, 2).mmul(Transforms.pow(ix, 2)).muli(labels.size(0))
          if (i > 0) {
            ix = ix.mmul(weights.get(i).transpose).muli(Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(activationFunctions.get(i - 1), activations.get(i)).derivative))
          }
        }
        ({
          i -= 1; i + 1
        })
      }
    }
    {
      var i: Int = 0
      while (i < deltas.length) {
        {
          deltaRet.add(new Nothing(deltas(i), preCons(i)))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return deltaRet
  }

  override def clone: MultiLayerNetwork = {
    val conf: Nothing = this.layerWiseConfigurations.clone
    val ret: MultiLayerNetwork = new MultiLayerNetwork(conf)
    ret.init(this.params.dup, false)
    if (solver != null) {
      val u: Nothing = this.getUpdater
      val updaterState: INDArray = u.getStateViewArray
      if (updaterState != null) {
        ret.getUpdater.setStateViewArray(ret, updaterState.dup, false)
      }
    }
    return ret
  }

  /**
   * Returns a 1 x m vector where the vector is composed of
   * a flattened vector of all of the weights for the
   * various neuralNets(w,hbias NOT VBIAS) and output layer
   *
   * @return the params for this neural net
   */
  def params(backwardOnly: Boolean): INDArray = {
    if (backwardOnly) return params
    val params: List[INDArray] = new ArrayList[INDArray]
    for (layer <- getLayers) {
      val layerParams: INDArray = layer.params
      if (layerParams != null) params.add(layerParams)
    }
    return Nd4j.toFlattened('f', params)
  }

  /**
   * Returns a 1 x m vector where the vector is composed of
   * a flattened vector of all of the weights for the
   * various neuralNets(w,hbias NOT VBIAS) and output layer
   *
   * @return the params for this neural net
   */
  def params: INDArray = {
    return flattenedParams
  }

  /**
   * Set the parameters for this model.
   * This expects a linear ndarray
   * which then be unpacked internally
   * relative to the expected ordering of the model
   *
   * @param params the parameters for the model
   */
  def setParams(params: INDArray) {
    if (flattenedParams eq params) return
    if (flattenedParams != null && params.length == flattenedParams.length) {
      flattenedParams.assign(params)
    }
    else {
      if (flattenedParams == null) flattenedParams = params.dup
      var idx: Int = 0
      {
        var i: Int = 0
        while (i < getLayers.length) {
          {
            val layer: Nothing = getLayer(i)
            val range: Int = layer.numParams
            if (range <= 0) continue //todo: continue is not supported
          val get: INDArray = params.get(NDArrayIndex.point(0), NDArrayIndex.interval(idx, range + idx))
            layer.setParams(get)
            idx += range
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
  }

  def setParamsViewArray(params: INDArray) {
    throw new UnsupportedOperationException("Not yet implemented")
  }

  def setBackpropGradientsViewArray(gradients: INDArray) {
    var paramsSoFar: Int = 0
    for (layer <- layers) {
      if (layer.numParams eq 0) continue //todo: continue is not supported
      layer.setBackpropGradientsViewArray(gradients.get(NDArrayIndex.point(0), NDArrayIndex.interval(paramsSoFar, paramsSoFar + layer.numParams)))
      paramsSoFar += layer.numParams
    }
  }

  /**
   * Returns a 1 x m vector where the vector is composed of
   * a flattened vector of all of the weights for the
   * various neuralNets and output layer
   *
   * @return the params for this neural net
   */
  def numParams: Int = {
    if (isInitCalled) return numParams(false)
    else MultiLayerNetwork.log.info("Model is not initialized. Initialize net with init()")
    return 0
  }

  def numParams(backwards: Boolean): Int = {
    var length: Int = 0
    {
      var i: Int = 0
      while (i < layers.length) {
        length += layers(i).numParams(backwards)
        ({
          i += 1; i - 1
        })
      }
    }
    return length
  }

  /**
   * Packs a set of matrices in to one vector,
   * where the matrices in this case are the w,hbias at each layer
   * and the output layer w,bias
   *
   * @return a singular matrix of all of the neuralNets packed in to one matrix
   * @deprecated use
   */
  @deprecated def pack: INDArray = {
    return params
  }

  /**
   * Packs a set of matrices in to one vector
   *
   * @param layers the neuralNets to pack
   * @return a singular matrix of all of the neuralNets packed in to one matrix
   * @deprecated use { @link #params()}
   */
  @deprecated def pack(layers: List[Nothing]): INDArray = {
    val list: List[INDArray] = new ArrayList[INDArray]
    import scala.collection.JavaConversions._
    for (layer <- layers) {
      list.add(layer.getFirst)
      list.add(layer.getSecond)
    }
    return Nd4j.toFlattened(list)
  }

  /**
   * Sets the input and labels and returns a score for the prediction
   * wrt true labels
   *
   * @param data the data to score
   * @return the score for the given input,label pairs
   */
  def f1Score(data: DataSet): Double = {
    return f1Score(data.getFeatureMatrix, data.getLabels)
  }

  /**
   * Unpacks a parameter matrix in to a
   * transform of pairs(w,hbias)
   * triples with layer wise
   *
   * @param param the param vector
   * @return a segmented list of the param vector
   * @deprecated use { @link #setParameters(INDArray)}
   */
  @deprecated def unPack(param: INDArray): List[Nothing] = {
    if (param.size(0) != 1) param = param.reshape(1, param.length)
    val ret: List[Nothing] = new ArrayList[E]
    var curr: Int = 0
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          val layerLength: Int = layers(i).getParam(DefaultParamInitializer.WEIGHT_KEY).length + layers(i).getParam(DefaultParamInitializer.BIAS_KEY).length
          val subMatrix: INDArray = param.get(NDArrayIndex.interval(curr, curr + layerLength))
          val weightPortion: INDArray = subMatrix.get(NDArrayIndex.interval(0, layers(i).getParam(DefaultParamInitializer.WEIGHT_KEY).length))
          val beginHBias: Int = layers(i).getParam(DefaultParamInitializer.WEIGHT_KEY).length
          val endHbias: Int = subMatrix.length
          val hBiasPortion: INDArray = subMatrix.get(NDArrayIndex.interval(beginHBias, endHbias))
          val layerLengthSum: Int = weightPortion.length + hBiasPortion.length
          if (layerLengthSum != layerLength) {
            if (hBiasPortion.length != layers(i).getParam(DefaultParamInitializer.BIAS_KEY).length) throw new IllegalStateException("Hidden bias on layer " + i + " was off")
            if (weightPortion.length != layers(i).getParam(DefaultParamInitializer.WEIGHT_KEY).length) throw new IllegalStateException("Weight portion on layer " + i + " was off")
          }
          ret.add(new Nothing(weightPortion.reshape(layers(i).getParam(DefaultParamInitializer.WEIGHT_KEY).size(0), layers(i).getParam(DefaultParamInitializer.WEIGHT_KEY).columns), hBiasPortion.reshape(layers(i).getParam(DefaultParamInitializer.BIAS_KEY).size(0), layers(i).getParam(DefaultParamInitializer.BIAS_KEY).columns)))
          curr += layerLength
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  def fit(iterator: DataSetIterator) {
    var iter: DataSetIterator = null
    if (iterator.asyncSupported) {
      iter = new Nothing(iterator, 2)
    }
    else {
      iter = iterator
    }
    if (layerWiseConfigurations.isPretrain) {
      pretrain(iter)
      iter.reset
      while (iter.hasNext) {
        val next: DataSet = iter.next
        if (next.getFeatureMatrix == null || next.getLabels == null) break //todo: break is not supported
        setInput(next.getFeatureMatrix)
        setLabels(next.getLabels)
        finetune
      }
    }
    if (layerWiseConfigurations.isBackprop) {
      if (layerWiseConfigurations.isPretrain) iter.reset
      update(TaskUtils.buildTask(iter))
      iter.reset
      while (iter.hasNext) {
        val next: DataSet = iter.next
        if (next.getFeatureMatrix == null || next.getLabels == null) break //todo: break is not supported
        val hasMaskArrays: Boolean = next.hasMaskArrays
        if (layerWiseConfigurations.getBackpropType eq BackpropType.TruncatedBPTT) {
          doTruncatedBPTT(next.getFeatureMatrix, next.getLabels, next.getFeaturesMaskArray, next.getLabelsMaskArray)
        }
        else {
          if (hasMaskArrays) setLayerMaskArrays(next.getFeaturesMaskArray, next.getLabelsMaskArray)
          setInput(next.getFeatureMatrix)
          setLabels(next.getLabels)
          if (solver == null) {
            solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
          }
          solver.optimize
        }
        if (hasMaskArrays) clearLayerMaskArrays
      }
    }
  }

  /** Calculate and set gradients for MultiLayerNetwork, based on OutputLayer and labels */
  protected def backprop {
    val pair: Nothing = calcBackpropGradients(null, true)
    this.gradient = (if (pair == null) null else pair.getFirst)
    this.epsilon = (if (pair == null) null else pair.getSecond)
  }

  /** Calculate gradients and errors. Used in two places:
    * (a) backprop (for standard multi layer network learning)
    * (b) backpropGradient (layer method, for when MultiLayerNetwork is used as a layer)
    * @param epsilon Errors (technically errors .* activations). Not used if withOutputLayer = true
    * @param withOutputLayer if true: assume last layer is output layer, and calculate errors based on labels. In this
    *                        case, the epsilon input is not used (may/should be null).
    *                        If false: calculate backprop gradients
    * @return Gradients and the error (epsilon) at the input
    */
  protected def calcBackpropGradients(epsilon: INDArray, withOutputLayer: Boolean): Nothing = {
    if (flattenedGradients == null) initGradientsView
    var multiGradientKey: String = null
    val gradient: Nothing = new Nothing(flattenedGradients)
    var currLayer: Nothing = null
    val numLayers: Int = getnLayers
    val gradientList: LinkedList[Nothing] = new LinkedList[E]
    var layerFrom: Int = 0
    var currPair: Nothing = null
    if (withOutputLayer) {
      if (!(getOutputLayer.isInstanceOf[Nothing])) {
        MultiLayerNetwork.log.warn("Warning: final layer isn't output layer. You cannot use backprop without an output layer.")
        return null
      }
      val outputLayer: Nothing = getOutputLayer.asInstanceOf[Nothing]
      if (labels == null) throw new IllegalStateException("No labels found")
      outputLayer.setLabels(labels)
      currPair = outputLayer.backpropGradient(null)
      import scala.collection.JavaConversions._
      for (entry <- currPair.getFirst.gradientForVariable.entrySet) {
        val origName: String = entry.getKey
        multiGradientKey = String.valueOf(numLayers - 1) + "_" + origName
        gradientList.addLast(new Nothing(multiGradientKey, entry.getValue, currPair.getFirst.flatteningOrderForVariable(origName)))
      }
      if (getLayerWiseConfigurations.getInputPreProcess(numLayers - 1) != null) currPair = new Nothing(currPair.getFirst, this.layerWiseConfigurations.getInputPreProcess(numLayers - 1).backprop(currPair.getSecond, getInputMiniBatchSize))
      layerFrom = numLayers - 2
    }
    else {
      currPair = new Nothing(null, epsilon)
      layerFrom = numLayers - 1
    }
    {
      var j: Int = layerFrom
      while (j >= 0) {
        {
          currLayer = getLayer(j)
          currPair = currLayer.backpropGradient(currPair.getSecond)
          val tempList: LinkedList[Nothing] = new LinkedList[E]
          import scala.collection.JavaConversions._
          for (entry <- currPair.getFirst.gradientForVariable.entrySet) {
            val origName: String = entry.getKey
            multiGradientKey = String.valueOf(j) + "_" + origName
            tempList.addFirst(new Nothing(multiGradientKey, entry.getValue, currPair.getFirst.flatteningOrderForVariable(origName)))
          }
          import scala.collection.JavaConversions._
          for (triple <- tempList) gradientList.addFirst(triple)
          if (getLayerWiseConfigurations.getInputPreProcess(j) != null) currPair = new Nothing(currPair.getFirst, getLayerWiseConfigurations.getInputPreProcess(j).backprop(currPair.getSecond, getInputMiniBatchSize))
        }
        ({
          j -= 1; j + 1
        })
      }
    }
    import scala.collection.JavaConversions._
    for (triple <- gradientList) {
      gradient.setGradientFor(triple.getFirst, triple.getSecond, triple.getThird)
    }
    return new Nothing(gradient, currPair.getSecond)
  }

  protected def doTruncatedBPTT(input: INDArray, labels: INDArray, featuresMaskArray: INDArray, labelsMaskArray: INDArray) {
    if (input.rank != 3 || labels.rank != 3) {
      MultiLayerNetwork.log.warn("Cannot do truncated BPTT with non-3d inputs or labels. Expect input with shape [miniBatchSize,nIn,timeSeriesLength], got " + Arrays.toString(input.shape) + "\t" + Arrays.toString(labels.shape))
      return
    }
    if (input.size(2) != labels.size(2)) {
      MultiLayerNetwork.log.warn("Input and label time series have different lengths: {} input length, {} label length", input.size(2), labels.size(2))
      return
    }
    val fwdLen: Int = layerWiseConfigurations.getTbpttFwdLength
    update(TaskUtils.buildTask(input, labels))
    val timeSeriesLength: Int = input.size(2)
    val nSubsets: Int = timeSeriesLength / fwdLen
    if (fwdLen > timeSeriesLength) {
      MultiLayerNetwork.log.warn("Cannot do TBPTT: Truncated BPTT forward length (" + fwdLen + ") > input time series length (" + timeSeriesLength + ")")
      return
    }
    rnnClearPreviousState
    {
      var i: Int = 0
      while (i < nSubsets) {
        {
          val startTimeIdx: Int = i * fwdLen
          val endTimeIdx: Int = startTimeIdx + fwdLen
          val inputSubset: INDArray = input.get(NDArrayIndex.all, NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
          val labelSubset: INDArray = labels.get(NDArrayIndex.all, NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
          setInput(inputSubset)
          setLabels(labelSubset)
          var featuresMaskSubset: INDArray = null
          var labelsMaskSubset: INDArray = null
          if (featuresMaskArray != null) {
            featuresMaskSubset = featuresMaskArray.get(NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
          }
          if (labelsMaskArray != null) {
            labelsMaskSubset = labelsMaskArray.get(NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
          }
          if (featuresMaskSubset != null || labelsMaskSubset != null) setLayerMaskArrays(featuresMaskSubset, labelsMaskSubset)
          if (solver == null) {
            solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
          }
          solver.optimize
          updateRnnStateWithTBPTTState
        }
        ({
          i += 1; i - 1
        })
      }
    }
    rnnClearPreviousState
    if (featuresMaskArray != null || labelsMaskArray != null) clearLayerMaskArrays
  }

  def updateRnnStateWithTBPTTState {
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          if (layers(i).isInstanceOf[Nothing]) {
            val l: Nothing = (layers(i).asInstanceOf[Nothing])
            l.rnnSetPreviousState(l.rnnGetTBPTTState)
          }
          else if (layers(i).isInstanceOf[MultiLayerNetwork]) {
            (layers(i).asInstanceOf[MultiLayerNetwork]).updateRnnStateWithTBPTTState
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /** Equivalent to backprop(), but calculates gradient for truncated BPTT instead. */
  protected def truncatedBPTTGradient {
    if (flattenedGradients == null) initGradientsView
    var multiGradientKey: String = null
    gradient = new Nothing
    var currLayer: Nothing = null
    if (!(getOutputLayer.isInstanceOf[Nothing])) {
      MultiLayerNetwork.log.warn("Warning: final layer isn't output layer. You cannot use backprop (truncated BPTT) without an output layer.")
      return
    }
    val outputLayer: Nothing = getOutputLayer.asInstanceOf[Nothing]
    if (labels == null) throw new IllegalStateException("No labels found")
    if (outputLayer.conf.getLayer.getWeightInit eq WeightInit.ZERO) {
      throw new IllegalStateException("Output layer weights cannot be initialized to zero when using backprop.")
    }
    outputLayer.setLabels(labels)
    val numLayers: Int = getnLayers
    val gradientList: LinkedList[Nothing] = new LinkedList[E]
    var currPair: Nothing = outputLayer.backpropGradient(null)
    import scala.collection.JavaConversions._
    for (entry <- currPair.getFirst.gradientForVariable.entrySet) {
      multiGradientKey = String.valueOf(numLayers - 1) + "_" + entry.getKey
      gradientList.addLast(new Nothing(multiGradientKey, entry.getValue))
    }
    if (getLayerWiseConfigurations.getInputPreProcess(numLayers - 1) != null) currPair = new Nothing(currPair.getFirst, this.layerWiseConfigurations.getInputPreProcess(numLayers - 1).backprop(currPair.getSecond, getInputMiniBatchSize))
    {
      var j: Int = numLayers - 2
      while (j >= 0) {
        {
          currLayer = getLayer(j)
          if (currLayer.isInstanceOf[Nothing]) {
            currPair = (currLayer.asInstanceOf[Nothing]).tbpttBackpropGradient(currPair.getSecond, layerWiseConfigurations.getTbpttBackLength)
          }
          else {
            currPair = currLayer.backpropGradient(currPair.getSecond)
          }
          val tempList: LinkedList[Nothing] = new LinkedList[E]
          import scala.collection.JavaConversions._
          for (entry <- currPair.getFirst.gradientForVariable.entrySet) {
            multiGradientKey = String.valueOf(j) + "_" + entry.getKey
            tempList.addFirst(new Nothing(multiGradientKey, entry.getValue))
          }
          import scala.collection.JavaConversions._
          for (pair <- tempList) gradientList.addFirst(pair)
          if (getLayerWiseConfigurations.getInputPreProcess(j) != null) currPair = new Nothing(currPair.getFirst, getLayerWiseConfigurations.getInputPreProcess(j).backprop(currPair.getSecond, getInputMiniBatchSize))
        }
        ({
          j -= 1; j + 1
        })
      }
    }
    import scala.collection.JavaConversions._
    for (pair <- gradientList) gradient.setGradientFor(pair.getFirst, pair.getSecond)
  }

  /**
   *
   * @return
   */
  def getListeners: Collection[Nothing] = {
    return listeners
  }

  def setListeners(listeners: Collection[Nothing]) {
    this.listeners = listeners
    if (layers == null) {
      init
    }
    for (layer <- layers) {
      layer.setListeners(listeners)
    }
    if (solver != null) {
      solver.setListeners(listeners)
    }
  }

  def setListeners(listeners: Nothing*) {
    val cListeners: Collection[Nothing] = new ArrayList[E]
    if (listeners != null && listeners.length > 0) {
      for (i <- listeners) {
        if (i != null) cListeners.add(i)
      }
    }
    setListeners(cListeners)
  }

  /**
   * Run SGD based on the given labels
   *
   */
  def finetune {
    if (flattenedGradients == null) initGradientsView
    if (!(getOutputLayer.isInstanceOf[Nothing])) {
      MultiLayerNetwork.log.warn("Output layer not instance of output layer returning.")
      return
    }
    if (labels == null) throw new IllegalStateException("No labels found")
    MultiLayerNetwork.log.info("Finetune phase")
    val output: Nothing = getOutputLayer.asInstanceOf[Nothing]
    if (output.conf.getOptimizationAlgo ne OptimizationAlgorithm.HESSIAN_FREE) {
      feedForward
      output.fit(output.input, labels)
    }
    else {
      throw new UnsupportedOperationException
    }
  }

  /**
   * Returns the predictions for each example in the dataset
   *
   * @param d the matrix to predict
   * @return the prediction for the dataset
   */
  def predict(d: INDArray): Array[Int] = {
    val output: INDArray = output(d, Layer.TrainingMode.TEST)
    val ret: Array[Int] = new Array[Int](d.size(0))
    if (d.isRowVector) ret(0) = Nd4j.getBlasWrapper.iamax(output)
    else {
      {
        var i: Int = 0
        while (i < ret.length) {
          ret(i) = Nd4j.getBlasWrapper.iamax(output.getRow(i))
          ({
            i += 1; i - 1
          })
        }
      }
    }
    return ret
  }

  /**
   * Return predicted label names
   *
   * @param dataSet to predict
   * @return the predicted labels for the dataSet
   */
  def predict(dataSet: DataSet): List[String] = {
    val intRet: Array[Int] = predict(dataSet.getFeatureMatrix)
    val ret: List[String] = new ArrayList[String]
    {
      var i: Int = 0
      while (i < intRet.length) {
        {
          ret.add(i, dataSet.getLabelName(intRet(i)))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * Returns the probabilities for each label
   * for each example row wise
   *
   * @param examples the examples to classify (one example in each row)
   * @return the likelihoods of each example and each label
   */
  def labelProbabilities(examples: INDArray): INDArray = {
    val feed: List[INDArray] = feedForward(examples)
    val o: Nothing = getOutputLayer.asInstanceOf[Nothing]
    return o.labelProbabilities(feed.get(feed.size - 1))
  }

  /**
   * Fit the model
   *
   * @param data   the examples to classify (one example in each row)
   * @param labels the example labels(a binary outcome matrix)
   */
  def fit(data: INDArray, labels: INDArray) {
    setInput(data)
    setLabels(labels)
    update(TaskUtils.buildTask(data, labels))
    if (layerWiseConfigurations.isPretrain) {
      pretrain(data)
      finetune
    }
    if (layerWiseConfigurations.isBackprop) {
      if (layerWiseConfigurations.getBackpropType eq BackpropType.TruncatedBPTT) {
        doTruncatedBPTT(data, labels, null, null)
      }
      else {
        if (solver == null) {
          solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
        }
        solver.optimize
      }
    }
  }

  /**
   * Fit the unsupervised model
   *
   * @param data the examples to classify (one example in each row)
   */
  def fit(data: INDArray) {
    setInput(data)
    update(TaskUtils.buildTask(data))
    pretrain(data)
  }

  def iterate(input: INDArray) {
    pretrain(input)
  }

  /**
   * Fit the model
   *
   * @param data the data to train on
   */
  def fit(data: DataSet) {
    if (layerWiseConfigurations.getBackpropType eq BackpropType.TruncatedBPTT) {
      doTruncatedBPTT(data.getFeatureMatrix, data.getLabels, data.getFeaturesMaskArray, data.getLabelsMaskArray)
    }
    else {
      val hasMaskArrays: Boolean = data.hasMaskArrays
      if (hasMaskArrays) setLayerMaskArrays(data.getFeaturesMaskArray, data.getLabelsMaskArray)
      fit(data.getFeatureMatrix, data.getLabels)
      if (hasMaskArrays) clearLayerMaskArrays
    }
  }

  /**
   * Fit the model
   *
   * @param examples the examples to classify (one example in each row)
   * @param labels   the labels for each example (the number of labels must match
   */
  def fit(examples: INDArray, labels: Array[Int]) {
    val layerConf: Nothing = getOutputLayer.conf.getLayer.asInstanceOf[Nothing]
    fit(examples, FeatureUtil.toOutcomeMatrix(labels, layerConf.getNOut))
  }

  /**
   * Label the probabilities of the input
   *
   * @param input    the input to label
   * @param train whether the output
   *              is test or train. This mainly
   *              affect hyper parameters such as
   *              drop out where certain things should
   *              be applied with activations
   * @return a vector of probabilities
   *         given each label.
   *         <p>
   *         This is typically of the form:
   *         [0.5, 0.5] or some other probability distribution summing to one
   */
  def output(input: INDArray, train: Nothing): INDArray = {
    return output(input, train eq TrainingMode.TRAIN)
  }

  /**
   * Label the probabilities of the input
   *
   * @param input    the input to label
   * @param train whether the output
   *              is test or train. This mainly
   *              affect hyper parameters such as
   *              drop out where certain things should
   *              be applied with activations
   * @return a vector of probabilities
   *         given each label.
   *         <p>
   *         This is typically of the form:
   *         [0.5, 0.5] or some other probability distribution summing to one
   */
  def output(input: INDArray, train: Boolean): INDArray = {
    val activations: List[INDArray] = feedForward(input, train)
    return activations.get(activations.size - 1)
  }

  /** Calculate the output of the network, with masking arrays. The masking arrays are used in situations such
    * as one-to-many and many-to-one recurrent neural network (RNN) designs, as well as for supporting time series
    * of varying lengths within the same minibatch.
    */
  def output(input: INDArray, train: Boolean, featuresMask: INDArray, labelsMask: INDArray): INDArray = {
    setLayerMaskArrays(featuresMask, labelsMask)
    val out: INDArray = output(input, train)
    clearLayerMaskArrays
    return out
  }

  /**
   * Label the probabilities of the input
   *
   * @param input the input to label
   * @return a vector of probabilities
   *         given each label.
   *         <p>
   *         This is typically of the form:
   *         [0.5, 0.5] or some other probability distribution summing to one
   */
  def output(input: INDArray): INDArray = {
    return output(input, TrainingMode.TRAIN)
  }

  /**
   * Label the probabilities of the input
   *
   * @param iterator test data to evaluate
   * @return a vector of probabilities
   *         given each label.
   *         <p>
   *         This is typically of the form:
   *         [0.5, 0.5] or some other probability distribution summing to one
   */
  def output(iterator: DataSetIterator, train: Boolean): INDArray = {
    val outList: List[INDArray] = new ArrayList[INDArray]
    while (iterator.hasNext) {
      val next: DataSet = iterator.next
      if (next.getFeatureMatrix == null || next.getLabels == null) break //todo: break is not supported
      val features: INDArray = next.getFeatures
      if (next.hasMaskArrays) {
        val fMask: INDArray = next.getFeaturesMaskArray
        val lMask: INDArray = next.getLabelsMaskArray
        outList.add(this.output(features, train, fMask, lMask))
      }
      else {
        outList.add(output(features, train))
      }
    }
    return Nd4j.vstack(outList.toArray(new Array[INDArray](0)))
  }

  def output(iterator: DataSetIterator): INDArray = {
    return output(iterator, false)
  }

  /**
   * Reconstructs the input.
   * This is equivalent functionality to a
   * deep autoencoder.
   *
   * @param x        the input to transform
   * @param layerNum the layer to output for encoding
   * @return a reconstructed matrix
   *         relative to the size of the last hidden layer.
   *         This is great for data compression and visualizing
   *         high dimensional data (or just doing dimensionality reduction).
   *         <p>
   *         This is typically of the form:
   *         [0.5, 0.5] or some other probability distribution summing to one
   */
  def reconstruct(x: INDArray, layerNum: Int): INDArray = {
    val forward: List[INDArray] = feedForward(x)
    return forward.get(layerNum - 1)
  }

  /**
   * Prints the configuration
   */
  def printConfiguration {
    val sb: StringBuilder = new StringBuilder
    var count: Int = 0
    import scala.collection.JavaConversions._
    for (conf <- getLayerWiseConfigurations.getConfs) {
      sb.append(" Layer " + ({
        count += 1; count - 1
      }) + " conf " + conf)
    }
    MultiLayerNetwork.log.info(sb.toString)
  }

  /**
   * Assigns the parameters of this model to the ones specified by this
   * network. This is used in loading from input streams, factory methods, etc
   *
   * @param network the network to getFromOrigin parameters from
   */
  def update(network: MultiLayerNetwork) {
    this.defaultConfiguration = (if (network.defaultConfiguration != null) network.defaultConfiguration.clone else null)
    if (network.input != null) setInput(network.input.dup)
    this.labels = network.labels
    if (network.layers != null) {
      layers = new Array[Nothing](network.layers.length)
      {
        var i: Int = 0
        while (i < layers.length) {
          {
            layers(i) = network.layers(i).clone
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    else {
      this.layers = null
    }
    if (network.solver != null) {
      val updaterView: INDArray = network.getUpdater.getStateViewArray
      if (updaterView != null) {
        val newUpdater: Nothing = new Nothing(this, updaterView.dup)
        this.setUpdater(newUpdater)
      }
    }
    else {
      this.solver = null
    }
  }

  /**
   * Sets the input and labels and returns a score for the prediction
   * wrt true labels
   *
   * @param input  the input to score
   * @param labels the true labels
   * @return the score for the given input,label pairs
   */
  def f1Score(input: INDArray, labels: INDArray): Double = {
    feedForward(input)
    setLabels(labels)
    val eval: Nothing = new Nothing
    eval.eval(labels, labelProbabilities(input))
    return eval.f1
  }

  /**
   * Returns the number of possible labels
   *
   * @return the number of possible labels for this classifier
   */
  def numLabels: Int = {
    return labels.columns
  }

  /** Sets the input and labels and returns a score for the prediction with respect to the true labels<br>
    * This is equivalent to {@link #score(DataSet, boolean)} with training==true.
    * @param data the data to score
    * @return the score for the given input,label pairs
    * @see #score(DataSet, boolean)
    */
  def score(data: DataSet): Double = {
    return score(data, false)
  }

  /** Calculate the score (loss function) of the prediction with respect to the true labels<br>
    * @param data data to calculate score for
    * @param training If true: score during training. If false: score at test time. This can affect the application of
    *                 certain features, such as dropout and dropconnect (which are applied at training time only)
    * @return the score (value of the loss function)
    */
  def score(data: DataSet, training: Boolean): Double = {
    val hasMaskArray: Boolean = data.hasMaskArrays
    if (hasMaskArray) setLayerMaskArrays(data.getFeaturesMaskArray, data.getLabelsMaskArray)
    val activations: List[INDArray] = feedForwardToLayer(layers.length - 2, data.getFeatureMatrix, training)
    val n: Int = activations.size
    setLabels(data.getLabels)
    if (getOutputLayer.isInstanceOf[Nothing]) {
      val ol: Nothing = getOutputLayer.asInstanceOf[Nothing]
      var olInput: INDArray = activations.get(n - 1)
      if (getLayerWiseConfigurations.getInputPreProcess(n - 1) != null) {
        olInput = getLayerWiseConfigurations.getInputPreProcess(n - 1).preProcess(olInput, input.size(0))
      }
      ol.setInput(olInput)
      ol.setLabels(data.getLabels)
      ol.computeScore(calcL1, calcL2, training)
      this.score = ol.score
    }
    else {
      MultiLayerNetwork.log.warn("Cannot calculate score wrt labels without an OutputLayer")
      return 0.0
    }
    if (hasMaskArray) clearLayerMaskArrays
    return score
  }

  def scoreExamples(iter: DataSetIterator, addRegularizationTerms: Boolean): INDArray = {
    val out: List[INDArray] = new ArrayList[INDArray]
    while (iter.hasNext) {
      out.add(scoreExamples(iter.next, addRegularizationTerms))
    }
    return Nd4j.toFlattened('f', out)
  }

  /** Calculate the score for each example in a DataSet individually. Unlike {@link #score(DataSet)} and {@link #score(DataSet, boolean)}
    * this method does not average/sum over examples. This method allows for examples to be scored individually (at test time only), which
    * may be useful for example for autoencoder architectures and the like.<br>
    * Each row of the output (assuming addRegularizationTerms == true) is equivalent to calling score(DataSet) with a single example.
    * @param data The data to score
    * @param addRegularizationTerms If true: add l1/l2 regularization terms (if any) to the score. If false: don't add regularization terms
    * @return An INDArray (column vector) of size input.numRows(); the ith entry is the score (loss value) of the ith example
    */
  def scoreExamples(data: DataSet, addRegularizationTerms: Boolean): INDArray = {
    val hasMaskArray: Boolean = data.hasMaskArrays
    if (hasMaskArray) setLayerMaskArrays(data.getFeaturesMaskArray, data.getLabelsMaskArray)
    feedForward(data.getFeatureMatrix, false)
    setLabels(data.getLabels)
    var out: INDArray = null
    if (getOutputLayer.isInstanceOf[Nothing]) {
      val ol: Nothing = getOutputLayer.asInstanceOf[Nothing]
      ol.setLabels(data.getLabels)
      val l1: Double = (if (addRegularizationTerms) calcL1 else 0.0)
      val l2: Double = (if (addRegularizationTerms) calcL2 else 0.0)
      out = ol.computeScoreForExamples(l1, l2)
    }
    else {
      throw new UnsupportedOperationException("Cannot calculate score with respect to labels without an OutputLayer")
    }
    if (hasMaskArray) clearLayerMaskArrays
    return out
  }

  def fit {
    fit(input, labels)
  }

  def update(gradient: INDArray, paramType: String) {
    throw new UnsupportedOperationException("Not implemented")
  }

  /**
   * Score of the model (relative to the objective function)
   *
   * @return the score of the model (relative to the objective function)
   */
  def score: Double = {
    return score
  }

  def setScore(score: Double) {
    this.score = score
  }

  def computeGradientAndScore {
    if (layerWiseConfigurations.getBackpropType eq BackpropType.TruncatedBPTT) {
      rnnActivateUsingStoredState(getInput, true, true)
      truncatedBPTTGradient
    }
    else {
      val activations: List[INDArray] = feedForwardToLayer(layers.length - 2, true)
      var actSecondLastLayer: INDArray = activations.get(activations.size - 1)
      if (layerWiseConfigurations.getInputPreProcess(layers.length - 1) != null) actSecondLastLayer = layerWiseConfigurations.getInputPreProcess(layers.length - 1).preProcess(actSecondLastLayer, getInputMiniBatchSize)
      getOutputLayer.setInput(actSecondLastLayer)
      backprop
    }
    if (!(getOutputLayer.isInstanceOf[Nothing])) {
      throw new IllegalStateException("Cannot calculate gradient and score with respect to labels: final layer is not an IOutputLayer")
    }
    score = (getOutputLayer.asInstanceOf[Nothing]).computeScore(calcL1, calcL2, true)
  }

  def accumulateScore(accum: Double) {
  }

  /**
   * Clear the inputs. Clears optimizer state.
   */
  def clear {
    for (layer <- layers) layer.clear
    input = null
    labels = null
    solver = null
  }

  /**
   * Averages the given logistic regression
   * from a mini batch in to this one
   *
   * @param layer     the logistic regression to average in to this one
   * @param batchSize the batch size
   */
  def merge(layer: Nothing, batchSize: Int) {
    throw new UnsupportedOperationException
  }

  /**
   * Merges this network with the other one.
   * This is a weight averaging with the update of:
   * a += b - a / n
   * where a is a matrix on the network
   * b is the incoming matrix and n
   * is the batch size.
   * This update is performed across the network neuralNets
   * as well as hidden neuralNets and logistic neuralNets
   *
   * @param network   the network to merge with
   * @param batchSize the batch size (number of training examples)
   *                  to average by
   */
  def merge(network: MultiLayerNetwork, batchSize: Int) {
    if (network.layers.length != layers.length) throw new IllegalArgumentException("Unable to merge networks that are not of equal length")
    {
      var i: Int = 0
      while (i < getnLayers) {
        {
          val n: Nothing = layers(i)
          val otherNetwork: Nothing = network.layers(i)
          n.merge(otherNetwork, batchSize)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    getOutputLayer.merge(network.getOutputLayer, batchSize)
  }

  /**
   * Note that if input isn't null
   * and the neuralNets are null, this is a way
   * of initializing the neural network
   *
   * @param input
   */
  def setInput(input: INDArray) {
    this.input = input
    if (this.layers == null) this.initializeLayers(getInput)
    if (input != null) {
      if (input.length == 0) throw new IllegalArgumentException("Invalid input: length 0 (shape: " + Arrays.toString(input.shape) + ")")
      setInputMiniBatchSize(input.size(0))
    }
  }

  private def initMask {
    setMask(Nd4j.ones(1, pack.length))
  }

  /**
   * Get the output layer
   *
   * @return
   */
  def getOutputLayer: Nothing = {
    return getLayers(getLayers.length - 1)
  }

  /**
   * Sets parameters for the model.
   * This is used to manipulate the weights and biases across
   * all neuralNets (including the output layer)
   *
   * @param params a parameter vector equal 1,numParameters
   */
  def setParameters(params: INDArray) {
    setParams(params)
  }

  def applyLearningRateScoreDecay {
    for (layer <- layers) {
      if (!layer.conf.getLearningRateByParam.isEmpty) {
        import scala.collection.JavaConversions._
        for (lrPair <- layer.conf.getLearningRateByParam.entrySet) {
          layer.conf.setLearningRateByParam(lrPair.getKey, lrPair.getValue * (layer.conf.getLrPolicyDecayRate + Nd4j.EPS_THRESHOLD))
        }
      }
    }
  }

  /**
   * Feed forward with the r operator
   *
   * @param v the v for the r operator
   * @return the activations based on the r operator
   */
  def feedForwardR(acts: List[INDArray], v: INDArray): List[INDArray] = {
    val R: List[INDArray] = new ArrayList[INDArray]
    R.add(Nd4j.zeros(input.size(0), input.columns))
    val vWvB: List[Nothing] = unPack(v)
    val W: List[INDArray] = MultiLayerUtil.weightMatrices(this)
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          val derivative: String = getLayers(i).conf.getLayer.getActivationFunction
          R.add(R.get(i).mmul(W.get(i)).addi(acts.get(i).mmul(vWvB.get(i).getFirst.addiRowVector(vWvB.get(i).getSecond))).muli((Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(derivative, acts.get(i + 1)).derivative))))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return R
  }

  def getDefaultConfiguration: Nothing = {
    return defaultConfiguration
  }

  def getLabels: INDArray = {
    return labels
  }

  def getInput: INDArray = {
    return input
  }

  /**
   *
   * @param labels
   */
  def setLabels(labels: INDArray) {
    this.labels = labels
  }

  /**
   * Get the number of layers in the network
   *
   * @return the number of layers in the network
   */
  def getnLayers: Int = {
    return layerWiseConfigurations.getConfs.size
  }

  /**
   *
   * @return
   */
  def getLayers: Array[Nothing] = {
    return layers
  }

  def getLayer(i: Int): Nothing = {
    return layers(i)
  }

  def getLayer(name: String): Nothing = {
    return layerMap.get(name)
  }

  def getLayerNames: List[String] = {
    return new ArrayList[String](layerMap.keySet)
  }

  def setLayers(layers: Array[Nothing]) {
    this.layers = layers
  }

  def getMask: INDArray = {
    return mask
  }

  def setMask(mask: INDArray) {
    this.mask = mask
  }

  def error(errorSignal: INDArray): Nothing = {
    throw new UnsupportedOperationException
  }

  def `type`: Nothing = {
    return Type.MULTILAYER
  }

  def derivativeActivation(input: INDArray): INDArray = {
    throw new UnsupportedOperationException
  }

  def calcGradient(layerError: Nothing, activation: INDArray): Nothing = {
    throw new UnsupportedOperationException
  }

  def preOutput(x: INDArray): INDArray = {
    var lastLayerActivation: INDArray = x
    {
      var i: Int = 0
      while (i < layers.length - 1) {
        {
          if (getLayerWiseConfigurations.getInputPreProcess(i) != null) lastLayerActivation = getLayerWiseConfigurations.getInputPreProcess(i).preProcess(lastLayerActivation, getInputMiniBatchSize)
          lastLayerActivation = layers(i).activate(lastLayerActivation)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (getLayerWiseConfigurations.getInputPreProcess(layers.length - 1) != null) lastLayerActivation = getLayerWiseConfigurations.getInputPreProcess(layers.length - 1).preProcess(lastLayerActivation, getInputMiniBatchSize)
    return layers(layers.length - 1).preOutput(lastLayerActivation)
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

  def transpose: Nothing = {
    throw new UnsupportedOperationException
  }

  def backpropGradient(epsilon: INDArray): Nothing = {
    if (getOutputLayer.isInstanceOf[Nothing]) throw new UnsupportedOperationException("Cannot calculate gradients based on epsilon with OutputLayer")
    return calcBackpropGradients(epsilon, false)
  }

  def setIndex(index: Int) {
    layerIndex = index
  }

  def getIndex: Int = {
    return layerIndex
  }

  def calcL2: Double = {
    var l2: Double = 0.0
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          l2 += layers(i).calcL2
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return l2
  }

  def calcL1: Double = {
    var l1: Double = 0.0
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          l1 += layers(i).calcL1
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return l1
  }

  def update(gradient: Nothing) {
    if (gradient.gradient.length ne numParams(true)) throw new IllegalArgumentException("Invalid input: expect gradients array of length " + numParams(true))
    import scala.collection.JavaConversions._
    for (entry <- gradient.gradientForVariable.entrySet) {
      val key: String = entry.getKey
      val `val`: INDArray = entry.getValue
      val idx: Int = key.indexOf('_')
      if (idx == -1) throw new IllegalStateException("Invalid param key: not have layer separator: \"" + key + "\"")
      val layerId: Integer = key.substring(0, idx).toInt
      val paramType: String = key.substring(idx + 1)
      this.gradient.gradientForVariable.put(key, `val`)
      layers(layerId).update(`val`, paramType)
    }
    setBackpropGradientsViewArray(gradient.gradient)
  }

  def preOutput(x: INDArray, training: Boolean): INDArray = {
    throw new UnsupportedOperationException
  }

  def activate(training: Boolean): INDArray = {
    throw new UnsupportedOperationException
  }

  def activate(input: INDArray, training: Boolean): INDArray = {
    throw new UnsupportedOperationException
  }

  def setInputMiniBatchSize(size: Int) {
    if (layers != null) for (l <- layers) l.setInputMiniBatchSize(size)
  }

  def getInputMiniBatchSize: Int = {
    return input.size(0)
  }

  def setMaskArray(maskArray: INDArray) {
    throw new UnsupportedOperationException
  }

  /**
   *
   * If this MultiLayerNetwork contains one or more RNN layers: conduct forward pass (prediction)
   * but using previous stored state for any RNN layers. The activations for the final step are
   * also stored in the RNN layers for use next time rnnTimeStep() is called.<br>
   * This method can be used to generate output one or more steps at a time instead of always having to do
   * forward pass from t=0. Example uses are for streaming data, and for generating samples from network output
   * one step at a time (where samples are then fed back into the network as input)<br>
   * If no previous state is present in RNN layers (i.e., initially or after calling rnnClearPreviousState()),
   * the default initialization (usually 0) is used.<br>
   * Supports mini-batch (i.e., multiple predictions/forward pass in parallel) as well as for single examples.<br>
   * @param input Input to network. May be for one or multiple time steps. For single time step:
   *              input has shape [miniBatchSize,inputSize] or [miniBatchSize,inputSize,1]. miniBatchSize=1 for single example.<br>
   *              For multiple time steps: [miniBatchSize,inputSize,inputTimeSeriesLength]
   * @return Output activations. If output is RNN layer (such as RnnOutputLayer): if input has shape [miniBatchSize,inputSize]
   *         i.e., is 2d, output has shape [miniBatchSize,outputSize] (i.e., also 2d).<br>
   *         Otherwise output is 3d [miniBatchSize,outputSize,inputTimeSeriesLength] when using RnnOutputLayer.
   */
  def rnnTimeStep(input: INDArray): INDArray = {
    this.setInputMiniBatchSize(input.size(0))
    this.input = input
    val inputIs2d: Boolean = input.rank == 2
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          if (getLayerWiseConfigurations.getInputPreProcess(i) != null) input = getLayerWiseConfigurations.getInputPreProcess(i).preProcess(input, getInputMiniBatchSize)
          if (layers(i).isInstanceOf[Nothing]) {
            input = (layers(i).asInstanceOf[Nothing]).rnnTimeStep(input)
          }
          else if (layers(i).isInstanceOf[MultiLayerNetwork]) {
            input = (layers(i).asInstanceOf[MultiLayerNetwork]).rnnTimeStep(input)
          }
          else {
            input = layers(i).activate(input, false)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (inputIs2d && input.rank == 3 && layers(layers.length - 1).`type` eq Type.RECURRENT) {
      return input.tensorAlongDimension(0, 1, 0)
    }
    this.input = null
    return input
  }

  /** Get the state of the RNN layer, as used in rnnTimeStep().
    * @param layer Number/index of the layer.
    * @return Hidden state, or null if layer is not an RNN layer
    */
  def rnnGetPreviousState(layer: Int): Map[String, INDArray] = {
    if (layer < 0 || layer >= layers.length) throw new IllegalArgumentException("Invalid layer number")
    if (!(layers(layer).isInstanceOf[Nothing])) throw new IllegalArgumentException("Layer is not an RNN layer")
    return (layers(layer).asInstanceOf[Nothing]).rnnGetPreviousState
  }

  /** Set the state of the RNN layer.
    * @param layer The number/index of the layer.
    * @param state The state to set the specified layer to
    */
  def rnnSetPreviousState(layer: Int, state: Map[String, INDArray]) {
    if (layer < 0 || layer >= layers.length) throw new IllegalArgumentException("Invalid layer number")
    if (!(layers(layer).isInstanceOf[Nothing])) throw new IllegalArgumentException("Layer is not an RNN layer")
    val r: Nothing = layers(layer).asInstanceOf[Nothing]
    r.rnnSetPreviousState(state)
  }

  /** Clear the previous state of the RNN layers (if any).
    */
  def rnnClearPreviousState {
    if (layers == null) return
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          if (layers(i).isInstanceOf[Nothing]) (layers(i).asInstanceOf[Nothing]).rnnClearPreviousState
          else if (layers(i).isInstanceOf[MultiLayerNetwork]) {
            (layers(i).asInstanceOf[MultiLayerNetwork]).rnnClearPreviousState
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /** Similar to rnnTimeStep and feedForward() methods. Difference here is that this method:<br>
    * (a) like rnnTimeStep does forward pass using stored state for RNN layers, and<br>
    * (b) unlike rnnTimeStep does not modify the RNN layer state<br>
    * Therefore multiple calls to this method with the same input should have the same output.<br>
    * Typically used during training only. Use rnnTimeStep for prediction/forward pass at test time.
    * @param input Input to network
    * @param training Whether training or not
    * @param storeLastForTBPTT set to true if used as part of truncated BPTT training
    * @return Activations for each layer (including input, as per feedforward() etc)
    */
  def rnnActivateUsingStoredState(input: INDArray, training: Boolean, storeLastForTBPTT: Boolean): List[INDArray] = {
    var currInput: INDArray = input
    val activations: List[INDArray] = new ArrayList[INDArray]
    activations.add(currInput)
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          if (getLayerWiseConfigurations.getInputPreProcess(i) != null) currInput = getLayerWiseConfigurations.getInputPreProcess(i).preProcess(currInput, input.size(0))
          if (layers(i).isInstanceOf[Nothing]) {
            currInput = (layers(i).asInstanceOf[Nothing]).rnnActivateUsingStoredState(currInput, training, storeLastForTBPTT)
          }
          else if (layers(i).isInstanceOf[MultiLayerNetwork]) {
            val temp: List[INDArray] = (layers(i).asInstanceOf[MultiLayerNetwork]).rnnActivateUsingStoredState(currInput, training, storeLastForTBPTT)
            currInput = temp.get(temp.size - 1)
          }
          else {
            currInput = layers(i).activate(currInput, training)
          }
          activations.add(currInput)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return activations
  }

  /** Get the updater for this MultiLayerNetwork
    * @return Updater for MultiLayerNetwork
    */
  def getUpdater: Nothing = {
    if (solver == null) {
      solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
      solver.getOptimizer.setUpdater(UpdaterCreator.getUpdater(this))
    }
    return solver.getOptimizer.getUpdater
  }

  /** Set the updater for the MultiLayerNetwork */
  def setUpdater(updater: Nothing) {
    if (solver == null) {
      solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
    }
    solver.getOptimizer.setUpdater(updater)
  }

  /** Set the mask arrays for features and labels. Mask arrays are typically used in situations such as one-to-many
    * and many-to-one learning with recurrent neural networks, as well as for supporting time series of varying lengths
    * within the same minibatch.<br>
    * For example, with RNN data sets with input of shape [miniBatchSize,nIn,timeSeriesLength] and outputs of shape
    * [miniBatchSize,nOut,timeSeriesLength], the features and mask arrays will have shape [miniBatchSize,timeSeriesLength]
    * and contain values 0 or 1 at each element (to specify whether a given input/example is present - or merely padding -
    * at a given time step).<br>
    * <b>NOTE</b>: This method is not usually used directly. Instead, methods such as {@link #feedForward(INDArray, INDArray, INDArray)}
    * and {@link #output(INDArray, boolean, INDArray, INDArray)} handle setting of masking internally.
    * @param featuresMaskArray Mask array for features (input)
    * @param labelsMaskArray Mask array for labels (output)
    * @see #clearLayerMaskArrays()
    */
  def setLayerMaskArrays(featuresMaskArray: INDArray, labelsMaskArray: INDArray) {
    if (featuresMaskArray != null) {
      val reshapedFeaturesMask: INDArray = TimeSeriesUtils.reshapeTimeSeriesMaskToVector(featuresMaskArray)
      {
        var i: Int = 0
        while (i < layers.length - 1) {
          {
            val t: Nothing = layers(i).`type`
            if (t eq Type.CONVOLUTIONAL || t eq Type.FEED_FORWARD) {
              layers(i).setMaskArray(reshapedFeaturesMask)
            }
            else if (t eq Type.RECURRENT) break //todo: break is not supported
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    if (labelsMaskArray != null) {
      if (!(getOutputLayer.isInstanceOf[Nothing])) return
      layers(layers.length - 1).setMaskArray(labelsMaskArray)
    }
  }

  /** Remove the mask arrays from all layers.<br>
    * See {@link #setLayerMaskArrays(INDArray, INDArray)} for details on mask arrays.
    */
  def clearLayerMaskArrays {
    for (layer <- layers) {
      layer.setMaskArray(null)
    }
  }

  /** Evaluate the network (classification performance)
    * @param iterator Iterator to evaluate on
    * @return Evaluation object; results of evaluation on all examples in the data set
    */
  def evaluate(iterator: DataSetIterator): Nothing = {
    return evaluate(iterator, null)
  }

  /** Evaluate the network on the provided data set. Used for evaluating the performance of classifiers
    * @param iterator Data to undertake evaluation on
    * @return Evaluation object, summarizing returs of the evaluation
    */
  def evaluate(iterator: DataSetIterator, labelsList: List[String]): Nothing = {
    if (layers == null || !(getOutputLayer.isInstanceOf[Nothing])) {
      throw new IllegalStateException("Cannot evaluate network with no output layer")
    }
    if (labelsList == null) labelsList = iterator.getLabels
    val e: Nothing = if ((labelsList == null)) new Nothing else new Nothing(labelsList)
    while (iterator.hasNext) {
      val next: DataSet = iterator.next
      if (next.getFeatureMatrix == null || next.getLabels == null) break //todo: break is not supported
      val features: INDArray = next.getFeatures
      val labels: INDArray = next.getLabels
      var out: INDArray = null
      if (next.hasMaskArrays) {
        val fMask: INDArray = next.getFeaturesMaskArray
        val lMask: INDArray = next.getLabelsMaskArray
        out = this.output(features, false, fMask, lMask)
        if (lMask != null) {
          e.evalTimeSeries(labels, out, lMask)
        }
        else {
          e.evalTimeSeries(labels, out)
        }
      }
      else {
        out = this.output(features, false)
        if (labels.rank == 3) e.evalTimeSeries(labels, out)
        else e.eval(labels, out)
      }
    }
    return e
  }

  private def update(task: Task) {
    if (!initDone) {
      initDone = true
      val heartbeat: Heartbeat = Heartbeat.getInstance
      task = ModelSerializer.taskByModel(this)
      val env: Environment = EnvironmentUtils.buildEnvironment
      heartbeat.reportEvent(Event.STANDALONE, env, task)
    }
  }
}