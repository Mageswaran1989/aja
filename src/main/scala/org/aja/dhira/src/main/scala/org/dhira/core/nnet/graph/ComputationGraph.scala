/*
 *
 *  * Copyright 2016 Skymind,Inc.
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
package org.dhira.core.nnet.graph

import lombok.Setter
import org.deeplearning4j.berkeley.Pair
import org.deeplearning4j.berkeley.Triple
import org.deeplearning4j.datasets.iterator.AsyncDataSetIterator
import org.deeplearning4j.datasets.iterator.AsyncMultiDataSetIterator
import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.api.Model
import org.deeplearning4j.nn.api.layers.IOutputLayer
import org.deeplearning4j.nn.api.layers.RecurrentLayer
import org.deeplearning4j.nn.conf.BackpropType
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.gradient.DefaultGradient
import org.deeplearning4j.nn.gradient.Gradient
import org.deeplearning4j.nn.graph.util.ComputationGraphUtil
import org.deeplearning4j.nn.graph.vertex.GraphVertex
import org.deeplearning4j.nn.graph.vertex.VertexIndices
import org.deeplearning4j.nn.graph.vertex.impl.InputVertex
import org.deeplearning4j.nn.layers.BasePretrainNetwork
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.updater.graph.ComputationGraphUpdater
import org.deeplearning4j.optimize.Solver
import org.deeplearning4j.optimize.api.ConvexOptimizer
import org.deeplearning4j.optimize.api.IterationListener
import org.deeplearning4j.util.ModelSerializer
import org.deeplearning4j.util.TimeSeriesUtils
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.api.DataSet
import org.nd4j.linalg.dataset.api.MultiDataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.heartbeat.Heartbeat
import org.nd4j.linalg.heartbeat.reports.Environment
import org.nd4j.linalg.heartbeat.reports.Event
import org.nd4j.linalg.heartbeat.reports.Task
import org.nd4j.linalg.heartbeat.utils.EnvironmentUtils
import org.nd4j.linalg.heartbeat.utils.TaskUtils
import org.nd4j.linalg.indexing.NDArrayIndex
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util._

/**
 * A ComputationGraph network is a neural network with arbitrary (directed acyclic graph) connection structure.
 * A ComputationGraph may also have an arbitrary number of inputs and outputs.
 *
 * @author Alex Black
 */
object ComputationGraph {
  private val log: Logger = LoggerFactory.getLogger(classOf[ComputationGraph])
}

class ComputationGraph extends Serializable with Model {
  protected var configuration: ComputationGraphConfiguration = null
  protected var initCalled: Boolean = false
  @transient
  protected var solver: Nothing = null
  protected var flattenedParams: INDArray = null
  @transient
  protected var flattenedGradients: INDArray = null
  protected var gradient: Gradient = null
  protected var score: Double = .0
  @Setter private var initDone: Boolean = false
  /**
   * All GraphVertex objects in the network.
   */
  protected var vertices: Array[GraphVertex] = null
  /**
   * Map of vertices by name
   */
  protected var verticesMap: Map[String, GraphVertex] = null
  /**
   * Indexes of graph vertices, in topological order. The topological order defines the order in which forward pass
   * (and hence also backward pass, which is the opposite to this) is conducted in the network.
   */
  protected var topologicalOrder: Array[Int] = null
  /**
   * A list of layers. Each of these layers is present in a GraphVertex, but are here for easy reference.
   * This array also defines the order in which the getLayer(int) method returns layers.
   */
  protected var layers: Array[Nothing] = null
  /**
   * The number of input arrays to the network. Many networks only have 1 input; however, a ComputationGraph may
   * have an arbitrary number (>=1) separate input arrays
   */
  private var numInputArrays: Int = 0
  /**
   * The number of output arrays to the network. Many networks only have 1 output; however, a ComputationGraph may
   * have an arbitrary number (>=1) separate output arrays
   */
  private var numOutputArrays: Int = 0
  @transient
  private var inputs: Array[INDArray] = null
  @transient
  private var labels: Array[INDArray] = null
  @transient
  private var inputMaskArrays: Array[INDArray] = null
  @transient
  private var labelMaskArrays: Array[INDArray] = null
  private var defaultConfiguration: Nothing = null
  private var listeners: Collection[Nothing] = new ArrayList[E]

  def this(configuration: ComputationGraphConfiguration) {
    this()
    this.configuration = configuration
    this.numInputArrays = configuration.getNetworkInputs.size
    this.numOutputArrays = configuration.getNetworkOutputs.size
    this.inputs = new Array[INDArray](numInputArrays)
    this.labels = new Array[INDArray](numOutputArrays)
    this.defaultConfiguration = configuration.getDefaultConfiguration
  }

  def getConfiguration: ComputationGraphConfiguration = {
    return configuration
  }

  /**
   * Returns the number of layers in the ComputationGraph
   */
  def getNumLayers: Int = {
    return (if (layers != null) layers.length else 0)
  }

  /**
   * Get the layer by the number of that layer, in range 0 to getNumLayers()-1
   * NOTE: This is different from the internal GraphVertex index for the layer
   */
  def getLayer(idx: Int): Nothing = {
    return layers(idx)
  }

  /**
   * Get all layers in the ComputationGraph
   */
  def getLayers: Array[Nothing] = {
    return layers
  }

  /**
   * Get a given layer by name.
   */
  def getLayer(name: String): Nothing = {
    return verticesMap.get(name).getLayer
  }

  /**
   * Returns an array of all GraphVertex objects.
   */
  def getVertices: Array[GraphVertex] = {
    return vertices
  }

  /**
   * Return a given GraphVertex by name, or null if no vertex with that name exists
   */
  def getVertex(name: String): GraphVertex = {
    return verticesMap.get(name)
  }

  /**
   * The number of inputs to this network
   */
  def getNumInputArrays: Int = {
    return numInputArrays
  }

  /**
   * The number of output (arrays) for this network
   */
  def getNumOutputArrays: Int = {
    return numOutputArrays
  }

  /**
   * Set the specified input for the ComputationGraph
   */
  def setInput(inputNum: Int, input: INDArray) {
    inputs(inputNum) = input
  }

  /**
   * Set all inputs for the ComputationGraph network
   */
  def setInputs(inputs: INDArray*) {
    if (inputs != null && inputs.length != this.numInputArrays) {
      throw new IllegalArgumentException("Invalid input array: network has " + numInputArrays + " inputs, but array is of length " + inputs.length)
    }
    this.inputs = inputs
  }

  /**
   * Get the previously set input for the ComputationGraph
   */
  def getInput(inputNum: Int): INDArray = {
    if (inputs == null) return null
    return inputs(inputNum)
  }

  /**
   * Get the previously set inputs for the ComputationGraph
   */
  def getInputs: Array[INDArray] = {
    return inputs
  }

  /**
   * Get the previously set feature/input mask arrays for the ComputationGraph
   */
  def getInputMaskArrays: Array[INDArray] = {
    return inputMaskArrays
  }

  /**
   * Get the previously set label/output mask arrays for the ComputationGraph
   */
  def getLabelMaskArrays: Array[INDArray] = {
    return labelMaskArrays
  }

  /**
   * Set the specified label for the ComputationGraph
   */
  def setLabel(labelNum: Int, label: INDArray) {
    labels(labelNum) = label
  }

  /**
   * Set all labels for the ComputationGraph network
   */
  def setLabels(labels: INDArray*) {
    if (labels != null && labels.length != this.numOutputArrays) {
      throw new IllegalArgumentException("Invalid output array: network has " + numOutputArrays + " outputs, but array is of length " + labels.length)
    }
    this.labels = labels
  }

  /**
   * Initialize the ComputationGraph network
   */
  def init {
    init(null, false)
  }

  /**
   * Initialize the ComputationGraph, optionally with an existing parameters array.
   * If an existing parameters array is specified, it will be used (and the values will not be modified) in the network;
   * if no parameters array is specified, parameters will be initialized randomly according to the network configuration.
   *
   * @param parameters           Network parameter. May be null. If null: randomly initialize.
   * @param cloneParametersArray Whether the parameter array (if any) should be cloned, or used directly
   */
  def init(parameters: INDArray, cloneParametersArray: Boolean) {
    if (initCalled) return
    topologicalOrder = topologicalSortOrder
    val configVertexMap: Map[String, Nothing] = configuration.getVertices
    val networkInputNames: List[String] = configuration.getNetworkInputs
    val vertexInputs: Map[String, List[String]] = configuration.getVertexInputs
    this.vertices = new Array[GraphVertex](networkInputNames.size + configuration.getVertices.size)
    val allNamesReverse: Map[String, Integer] = new HashMap[String, Integer]
    val vertexNumber: Int = 0
    import scala.collection.JavaConversions._
    for (name <- networkInputNames) {
      val gv: GraphVertex = new InputVertex(this, name, vertexNumber, null)
      allNamesReverse.put(name, vertexNumber)
      vertices(({
        vertexNumber += 1; vertexNumber - 1
      })) = gv
    }
    var numParams: Int = 0
    val numParamsForVertex: Array[Int] = new Array[Int](topologicalOrder.length)
    var i: Int = 0
    while (i < configuration.getNetworkInputs.size) {
      {
        numParamsForVertex(i) = 0
      }
      ({
        i += 1; i - 1
      })
    }
    import scala.collection.JavaConversions._
    for (nodeEntry <- configVertexMap.entrySet) {
      val n: Nothing = nodeEntry.getValue
      numParamsForVertex(i) = n.numParams(true)
      numParams += numParamsForVertex(i)
      i += 1
    }
    var initializeParams: Boolean = false
    if (parameters != null) {
      if (!parameters.isRowVector) throw new IllegalArgumentException("Invalid parameters: should be a row vector")
      if (parameters.length != numParams) throw new IllegalArgumentException("Invalid parameters: expected length " + numParams + ", got length " + parameters.length)
      if (cloneParametersArray) flattenedParams = parameters.dup
      else flattenedParams = parameters
      initializeParams = false
    }
    else {
      flattenedParams = Nd4j.create(1, numParams)
      initializeParams = true
    }
    val paramsViewForVertex: Array[INDArray] = new Array[INDArray](topologicalOrder.length)
    var paramOffsetSoFar: Int = 0
    i = 0
    for (vertexIdx <- topologicalOrder) {
      val nParamsThisVertex: Int = numParamsForVertex(vertexIdx)
      if (nParamsThisVertex != 0) {
        paramsViewForVertex(vertexIdx) = flattenedParams.get(NDArrayIndex.point(0), NDArrayIndex.interval(paramOffsetSoFar, paramOffsetSoFar + nParamsThisVertex))
      }
      i += 1
      paramOffsetSoFar += nParamsThisVertex
    }
    var numLayers: Int = 0
    val tempLayerList: List[Nothing] = new ArrayList[E]
    import scala.collection.JavaConversions._
    for (nodeEntry <- configVertexMap.entrySet) {
      val n: Nothing = nodeEntry.getValue
      val name: String = nodeEntry.getKey
      val gv: GraphVertex = n.instantiate(this, name, vertexNumber, paramsViewForVertex(vertexNumber), initializeParams)
      if (gv.hasLayer) {
        numLayers += 1
        tempLayerList.add(gv.getLayer)
      }
      allNamesReverse.put(name, vertexNumber)
      vertices(({
        vertexNumber += 1; vertexNumber - 1
      })) = gv
    }
    layers = tempLayerList.toArray(new Array[Nothing](numLayers))
    verticesMap = new HashMap[String, GraphVertex]
    for (gv <- vertices) {
      verticesMap.put(gv.getVertexName, gv)
    }
    val verticesOutputTo: Map[String, List[String]] = new HashMap[String, List[String]]
    for (gv <- vertices) {
      val vertexName: String = gv.getVertexName
      var vertexInputNames: List[String] = null
      vertexInputNames = vertexInputs.get(vertexName)
      if (vertexInputNames == null) continue //todo: continue is not supported
      import scala.collection.JavaConversions._
      for (s <- vertexInputNames) {
        var list: List[String] = verticesOutputTo.get(s)
        if (list == null) {
          list = new ArrayList[String]
          verticesOutputTo.put(s, list)
        }
        list.add(vertexName)
      }
    }
    for (gv <- vertices) {
      val vertexName: String = gv.getVertexName
      val vertexIndex: Int = gv.getVertexIndex
      var vertexInputNames: List[String] = null
      vertexInputNames = vertexInputs.get(vertexName)
      if (vertexInputNames == null) continue //todo: continue is not supported
      val inputIndices: Array[VertexIndices] = new Array[VertexIndices](vertexInputNames.size)
      {
        var j: Int = 0
        while (j < vertexInputNames.size) {
          {
            val inName: String = vertexInputNames.get(j)
            val inputVertexIndex: Int = allNamesReverse.get(inName)
            val inputVertex: GraphVertex = vertices(inputVertexIndex)
            val inputVertexOutputsTo: List[String] = verticesOutputTo.get(inName)
            val outputNumberOfInput: Int = inputVertexOutputsTo.indexOf(vertexName)
            if (outputNumberOfInput == -1) throw new IllegalStateException("Could not find vertex " + vertexIndex + " in the list of outputs " + "for vertex " + inputVertex + "; error in graph structure?")
            inputIndices(j) = new VertexIndices(inputVertexIndex, outputNumberOfInput)
          }
          ({
            j += 1; j - 1
          })
        }
      }
      gv.setInputVertices(inputIndices)
    }
    for (gv <- vertices) {
      val vertexName: String = gv.getVertexName
      val thisVertexOutputsTo: List[String] = verticesOutputTo.get(vertexName)
      if (thisVertexOutputsTo == null || thisVertexOutputsTo.isEmpty) continue //todo: continue is not supported
      val outputIndices: Array[VertexIndices] = new Array[VertexIndices](thisVertexOutputsTo.size)
      val j: Int = 0
      import scala.collection.JavaConversions._
      for (s <- thisVertexOutputsTo) {
        val nextVertexInputNames: List[String] = vertexInputs.get(s)
        val outputVertexInputNumber: Int = nextVertexInputNames.indexOf(vertexName)
        val outputVertexIndex: Int = allNamesReverse.get(s)
        outputIndices(({
          j += 1; j - 1
        })) = new VertexIndices(outputVertexIndex, outputVertexInputNumber)
      }
      gv.setOutputVertices(outputIndices)
    }
    initCalled = true
  }

  /**
   * This method: initializes the flattened gradients array (used in backprop) and sets the appropriate subset in all layers.
   * As a general rule, this shouldn't ever need to be called manually when doing training via fit(DataSet), fit(DataSetIterator)
   * or fit(MultiDataSet) methods
   */
  def initGradientsView {
    if (!initCalled) init
    var numParams: Int = 0
    val numParamsForVertex: Array[Int] = new Array[Int](topologicalOrder.length)
    var i: Int = 0
    while (i < configuration.getNetworkInputs.size) {
      {
        numParamsForVertex(i) = 0
      }
      ({
        i += 1; i - 1
      })
    }
    val configVertexMap: Map[String, Nothing] = configuration.getVertices
    import scala.collection.JavaConversions._
    for (nodeEntry <- configVertexMap.entrySet) {
      val n: Nothing = nodeEntry.getValue
      numParamsForVertex(i) = n.numParams(true)
      numParams += numParamsForVertex(i)
      i += 1
    }
    flattenedGradients = Nd4j.create(1, numParams)
    var paramOffsetSoFar: Int = 0
    i = 0
    for (vertexIdx <- topologicalOrder) {
      val nParamsThisVertex: Int = numParamsForVertex(vertexIdx)
      if (nParamsThisVertex != 0) {
        val gradientView: INDArray = flattenedGradients.get(NDArrayIndex.point(0), NDArrayIndex.interval(paramOffsetSoFar, paramOffsetSoFar + nParamsThisVertex))
        vertices(vertexIdx).setBackpropGradientsViewArray(gradientView)
      }
      i += 1
      paramOffsetSoFar += nParamsThisVertex
    }
  }

  /**
   * Pretrain network with a single input and single output. DataSetIterators can only be used if the number of input
   * and output arrays for the ComputationGraph are both 1.
   * For networks with more than one input or output, use {@link #pretrain(MultiDataSetIterator)}
   */
  def pretrain(iter: DataSetIterator) {
    if (numInputArrays != 1 || numOutputArrays != 1) throw new UnsupportedOperationException("Cannot train ComputationGraph network with " + " multiple inputs or outputs using a DataSetIterator")
    pretrain(ComputationGraphUtil.toMultiDataSetIterator(iter))
  }

  /**
   * Pretrain network with multiple inputs and/or outputs
   */
  def pretrain(iter: MultiDataSetIterator) {
    {
      var i: Int = 0
      while (i < topologicalOrder.length) {
        {
          if (!vertices(i).hasLayer) continue //todo: continue is not supported
          if (vertices(i).getLayer.isInstanceOf[Nothing]) continue //todo: continue is not supported
        val partialTopoSort: LinkedList[Integer] = new LinkedList[Integer]
          val seenSoFar: Set[Integer] = new HashSet[Integer]
          partialTopoSort.add(topologicalOrder(i))
          seenSoFar.add(topologicalOrder(i))
          {
            var j: Int = i - 1
            while (j >= 0) {
              {
                val outputsTo: Array[VertexIndices] = vertices(topologicalOrder(j)).getOutputVertices
                var needed: Boolean = false
                for (vi <- outputsTo) {
                  if (seenSoFar.contains(vi.getVertexIndex)) {
                    needed = true
                    break //todo: break is not supported
                  }
                }
                if (needed) {
                  partialTopoSort.addFirst(topologicalOrder(j))
                  seenSoFar.add(topologicalOrder(j))
                }
              }
              ({
                j -= 1; j + 1
              })
            }
          }
          val fwdPassOrder: Array[Int] = new Array[Int](partialTopoSort.size)
          val k: Int = 0
          import scala.collection.JavaConversions._
          for (g <- partialTopoSort) fwdPassOrder(({
            k += 1; k - 1
          })) = g
          val gv: GraphVertex = vertices(fwdPassOrder(fwdPassOrder.length - 1))
          val layer: Nothing = gv.getLayer
          if (!(layer.isInstanceOf[BasePretrainNetwork[_ <: Nothing]])) throw new IllegalStateException("Cannot pretrain network with layer that is not pretrainable")
          ComputationGraph.log.info("Pretraining on layer \"{}\"", vertices(i).getVertexName)
          val toPretrain: BasePretrainNetwork[_] = layer.asInstanceOf[BasePretrainNetwork[_]]
          if (listeners != null) toPretrain.setListeners(listeners)
          while (iter.hasNext) {
            val multiDataSet: MultiDataSet = iter.next
            setInputs(multiDataSet.getFeatures)
            {
              var j: Int = 0
              while (j < fwdPassOrder.length - 1) {
                {
                  val current: GraphVertex = vertices(fwdPassOrder(j))
                  if (current.isInputVertex) {
                    val inputsTo: Array[VertexIndices] = current.getOutputVertices
                    val input: INDArray = inputs(current.getVertexIndex)
                    for (v <- inputsTo) {
                      val vIdx: Int = v.getVertexIndex
                      val vIdxInputNum: Int = v.getVertexEdgeNumber
                      vertices(vIdx).setInput(vIdxInputNum, input.dup)
                    }
                  }
                  else {
                    val out: INDArray = current.doForward(true)
                    val outputsTo: Array[VertexIndices] = current.getOutputVertices
                    if (outputsTo != null) {
                      for (v <- outputsTo) {
                        val vIdx: Int = v.getVertexIndex
                        val inputNum: Int = v.getVertexEdgeNumber
                        vertices(vIdx).setInput(inputNum, out)
                      }
                    }
                  }
                }
                ({
                  j += 1; j - 1
                })
              }
            }
            toPretrain.fit(gv.getInputs(0))
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
   * Fit the ComputationGraph using a DataSet.
   * Note that this method can only be used with ComputationGraphs with 1 input and 1 output.
   * For networks with more than one input or output, use {@link #fit(MultiDataSetIterator)}
   */
  def fit(dataSet: DataSet) {
    if (numInputArrays != 1 || numOutputArrays != 1) throw new UnsupportedOperationException("Cannot train ComputationGraph network with " + " multiple inputs or outputs using a DataSet")
    val hasMaskArrays: Boolean = dataSet.hasMaskArrays
    if (hasMaskArrays) {
      val fMask: Array[INDArray] = (if (dataSet.getFeaturesMaskArray != null) Array[INDArray](dataSet.getFeaturesMaskArray) else null)
      val lMask: Array[INDArray] = (if (dataSet.getLabelsMaskArray != null) Array[INDArray](dataSet.getLabelsMaskArray) else null)
      setLayerMaskArrays(fMask, lMask)
    }
    fit(Array[INDArray](dataSet.getFeatureMatrix), Array[INDArray](dataSet.getLabels))
    if (hasMaskArrays) clearLayerMaskArrays
  }

  /**
   * Fit the ComputationGraph using a DataSetIterator.
   * Note that this method can only be used with ComputationGraphs with 1 input and 1 output
   */
  def fit(iterator: DataSetIterator) {
    if (numInputArrays != 1 || numOutputArrays != 1) throw new UnsupportedOperationException("Cannot train ComputationGraph network with " + " multiple inputs or outputs using a DataSetIterator")
    var dataSetIterator: DataSetIterator = null
    if (iterator.asyncSupported) {
      dataSetIterator = new Nothing(iterator, 2)
    }
    else dataSetIterator = iterator
    if (configuration.isPretrain) {
      pretrain(dataSetIterator)
    }
    if (configuration.isBackprop) {
      update(TaskUtils.buildTask(dataSetIterator))
      while (dataSetIterator.hasNext) {
        val next: DataSet = dataSetIterator.next
        if (next.getFeatureMatrix == null || next.getLabels == null) break //todo: break is not supported
        val hasMaskArrays: Boolean = next.hasMaskArrays
        if (hasMaskArrays) {
          val fMask: Array[INDArray] = (if (next.getFeaturesMaskArray != null) Array[INDArray](next.getFeaturesMaskArray) else null)
          val lMask: Array[INDArray] = (if (next.getLabelsMaskArray != null) Array[INDArray](next.getLabelsMaskArray) else null)
          setLayerMaskArrays(fMask, lMask)
        }
        if (configuration.getBackpropType eq BackpropType.TruncatedBPTT) {
          doTruncatedBPTT(Array[INDArray](next.getFeatures), Array[INDArray](next.getLabels), (if (hasMaskArrays) Array[INDArray](next.getFeaturesMaskArray) else null), (if (hasMaskArrays) Array[INDArray](next.getLabelsMaskArray) else null))
        }
        else {
          setInput(0, next.getFeatureMatrix)
          setLabel(0, next.getLabels)
          if (solver == null) {
            solver = new Nothing().configure(defaultConfiguration).listeners(listeners).model(this).build
          }
          solver.optimize
        }
        if (hasMaskArrays) {
          clearLayerMaskArrays
        }
      }
    }
  }

  /**
   * Fit the ComputationGraph using a MultiDataSet
   */
  def fit(multiDataSet: MultiDataSet) {
    if (multiDataSet.hasMaskArrays) {
      setLayerMaskArrays(multiDataSet.getFeaturesMaskArrays, multiDataSet.getLabelsMaskArrays)
    }
    fit(multiDataSet.getFeatures, multiDataSet.getLabels)
    if (multiDataSet.hasMaskArrays) clearLayerMaskArrays
  }

  /**
   * Fit the ComputationGraph using a MultiDataSetIterator
   */
  def fit(multi: MultiDataSetIterator) {
    var multiDataSetIterator: MultiDataSetIterator = null
    if (multi.asyncSupported) {
      multiDataSetIterator = new AsyncMultiDataSetIterator(multi, 2)
    }
    else multiDataSetIterator = multi
    if (configuration.isPretrain) {
      pretrain(multiDataSetIterator)
    }
    if (configuration.isBackprop) {
      while (multiDataSetIterator.hasNext) {
        val next: MultiDataSet = multiDataSetIterator.next
        if (next.getFeatures == null || next.getLabels == null) break //todo: break is not supported
        if (configuration.getBackpropType eq BackpropType.TruncatedBPTT) {
          doTruncatedBPTT(next.getFeatures, next.getLabels, next.getFeaturesMaskArrays, next.getLabelsMaskArrays)
        }
        else {
          val hasMaskArrays: Boolean = next.hasMaskArrays
          if (hasMaskArrays) {
            setLayerMaskArrays(next.getFeaturesMaskArrays, next.getLabelsMaskArrays)
          }
          setInputs(next.getFeatures)
          setLabels(next.getLabels)
          if (solver == null) {
            solver = new Nothing().configure(defaultConfiguration).listeners(listeners).model(this).build
          }
          solver.optimize
          if (hasMaskArrays) {
            clearLayerMaskArrays
          }
        }
      }
    }
  }

  /**
   * Fit the ComputationGraph given arrays of inputs and labels.
   *
   * @param inputs The network inptus
   * @param labels The labels
   */
  def fit(inputs: Array[INDArray], labels: Array[INDArray]) {
    fit(inputs, labels, null, null)
  }

  /**
   * Fit the ComputationGraph using the specified inputs and labels (and mask arrays)
   *
   * @param inputs            The network inputs (features)
   * @param labels            The network labels
   * @param featureMaskArrays Mask arrays for inputs/features. Typically used for RNN training. May be null.
   * @param labelMaskArrays   Mas arrays for the labels/outputs. Typically used for RNN training. May be null.
   */
  def fit(inputs: Array[INDArray], labels: Array[INDArray], featureMaskArrays: Array[INDArray], labelMaskArrays: Array[INDArray]) {
    setInputs(inputs)
    setLabels(labels)
    setLayerMaskArrays(featureMaskArrays, labelMaskArrays)
    update(TaskUtils.buildTask(inputs, labels))
    if (configuration.isPretrain) {
      throw new UnsupportedOperationException("Pretraining: Not yet implemented")
    }
    if (configuration.isBackprop) {
      if (configuration.getBackpropType eq BackpropType.TruncatedBPTT) {
        doTruncatedBPTT(inputs, labels, null, null)
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
   * Calculate a topological sort order for the vertices in the graph.
   * Note that this is used for
   * (a) working out what order to do forward pass,
   * (b) what order to do backprop (i.e., reverse of this)
   * (c) order to flatten parameters (and gradients)
   */
  def topologicalSortOrder: Array[Int] = {
    if (topologicalOrder != null) return topologicalOrder
    val nodeMap: Map[String, Nothing] = configuration.getVertices
    val networkInputNames: List[String] = configuration.getNetworkInputs
    val numVertices: Int = networkInputNames.size + configuration.getVertices.size
    val out: Array[Int] = new Array[Int](numVertices)
    val outCounter: Int = 0
    val vertexNamesMap: Map[Integer, String] = new HashMap[Integer, String]
    val vertexNamesMap2: Map[String, Integer] = new HashMap[String, Integer]
    var i: Int = 0
    import scala.collection.JavaConversions._
    for (inputName <- configuration.getNetworkInputs) {
      vertexNamesMap.put(i, inputName)
      vertexNamesMap2.put(inputName, i)
      i += 1
    }
    import scala.collection.JavaConversions._
    for (entry <- nodeMap.entrySet) {
      val name: String = entry.getKey
      vertexNamesMap.put(i, name)
      vertexNamesMap2.put(name, i)
      i += 1
    }
    val inputEdges: Map[Integer, Set[Integer]] = new HashMap[Integer, Set[Integer]]
    val outputEdges: Map[Integer, Set[Integer]] = new HashMap[Integer, Set[Integer]]
    import scala.collection.JavaConversions._
    for (s <- configuration.getNetworkInputs) {
      val idx: Int = vertexNamesMap2.get(s)
      inputEdges.put(idx, null)
    }
    import scala.collection.JavaConversions._
    for (entry <- nodeMap.entrySet) {
      val thisVertexName: String = entry.getKey
      val idx: Int = vertexNamesMap2.get(thisVertexName)
      val inputsToThisVertex: List[String] = configuration.getVertexInputs.get(thisVertexName)
      if (inputsToThisVertex == null || inputsToThisVertex.isEmpty) {
        inputEdges.put(idx, null)
        continue //todo: continue is not supported
      }
      val inputSet: Set[Integer] = new HashSet[Integer]
      import scala.collection.JavaConversions._
      for (s <- inputsToThisVertex) {
        val inputIdx: Integer = vertexNamesMap2.get(s)
        if (inputIdx == null) {
          System.out.println
        }
        inputSet.add(inputIdx)
        var outputSetForInputIdx: Set[Integer] = outputEdges.get(inputIdx)
        if (outputSetForInputIdx == null) {
          outputSetForInputIdx = new HashSet[Integer]
          outputEdges.put(inputIdx, outputSetForInputIdx)
        }
        outputSetForInputIdx.add(idx)
      }
      inputEdges.put(idx, inputSet)
    }
    val noIncomingEdges: LinkedList[Integer] = new LinkedList[Integer]
    import scala.collection.JavaConversions._
    for (entry <- inputEdges.entrySet) {
      val inputsFrom: Set[Integer] = entry.getValue
      if (inputsFrom == null || inputsFrom.isEmpty) {
        noIncomingEdges.add(entry.getKey)
      }
    }
    while (!noIncomingEdges.isEmpty) {
      val next: Int = noIncomingEdges.removeFirst
      out(({
        outCounter += 1; outCounter - 1
      })) = next
      val vertexOutputsTo: Set[Integer] = outputEdges.get(next)
      if (vertexOutputsTo != null) {
        import scala.collection.JavaConversions._
        for (v <- vertexOutputsTo) {
          val set: Set[Integer] = inputEdges.get(v)
          set.remove(next)
          if (set.isEmpty) {
            noIncomingEdges.add(v)
          }
        }
      }
    }
    import scala.collection.JavaConversions._
    for (entry <- inputEdges.entrySet) {
      val set: Set[Integer] = entry.getValue
      if (set == null) continue //todo: continue is not supported
      if (!set.isEmpty) throw new IllegalStateException("Invalid configuration: cycle detected in graph. Cannot calculate topological ordering with graph cycle (" + "cycle includes vertex \"" + vertexNamesMap.get(entry.getKey) + "\")")
    }
    return out
  }

  def computeGradientAndScore {
    if (configuration.getBackpropType eq BackpropType.TruncatedBPTT) {
      rnnActivateUsingStoredState(inputs, true, true)
      calcBackpropGradients(true)
    }
    else {
      feedForward(true, true)
      calcBackpropGradients(false)
    }
    var l1: Double = calcL1
    var l2: Double = calcL2
    score = 0.0
    import scala.collection.JavaConversions._
    for (s <- configuration.getNetworkOutputs) {
      val gv: GraphVertex = verticesMap.get(s)
      score += (gv.getLayer.asInstanceOf[Nothing]).computeScore(l1, l2, true)
      l1 = 0.0
      l2 = 0.0
    }
  }

  /**
   * Conduct forward pass using a single input array. Note that this method can only be used with ComputationGraphs
   * with a single input array.
   *
   * @param input The input array
   * @param train If true: do forward pass at training time
   * @return A map of activations for each layer (not each GraphVertex). Keys = layer name, values = layer activations
   */
  def feedForward(input: INDArray, train: Boolean): Map[String, INDArray] = {
    if (numInputArrays != 1) throw new UnsupportedOperationException("Cannot feedForward with single input for graph network with " + numInputArrays + " expected inputs")
    setInput(0, input)
    return feedForward(train)
  }

  /**
   * Conduct forward pass using an array of inputs
   *
   * @param input An array of ComputationGraph inputs
   * @param train If true: do forward pass at training time; false: do forward pass at test time
   * @return A map of activations for each layer (not each GraphVertex). Keys = layer name, values = layer activations
   */
  def feedForward(input: Array[INDArray], train: Boolean): Map[String, INDArray] = {
    if (numInputArrays != input.length) throw new UnsupportedOperationException("Cannot feedForward with " + input.length + " inputs for graph network with " + numInputArrays + " expected inputs")
    {
      var i: Int = 0
      while (i < input.length) {
        setInput(i, input(i))
        ({
          i += 1; i - 1
        })
      }
    }
    return feedForward(train)
  }

  /**
   * Conduct forward pass using the stored inputs, at test time
   *
   * @return A map of activations for each layer (not each GraphVertex). Keys = layer name, values = layer activations
   */
  def feedForward: Map[String, INDArray] = {
    return feedForward(false)
  }

  /**
   * Conduct forward pass using the stored inputs
   *
   * @param train If true: do forward pass at training time; false: do forward pass at test time
   * @return A map of activations for each layer (not each GraphVertex). Keys = layer name, values = layer activations
   */
  def feedForward(train: Boolean): Map[String, INDArray] = {
    return feedForward(train, false)
  }

  private def feedForward(train: Boolean, excludeOutputLayers: Boolean): Map[String, INDArray] = {
    val layerActivations: Map[String, INDArray] = new HashMap[String, INDArray]
    {
      var i: Int = 0
      while (i < topologicalOrder.length) {
        {
          val current: GraphVertex = vertices(topologicalOrder(i))
          if (current.isInputVertex) {
            val inputsTo: Array[VertexIndices] = current.getOutputVertices
            val input: INDArray = inputs(current.getVertexIndex)
            layerActivations.put(current.getVertexName, input)
            for (v <- inputsTo) {
              val vIdx: Int = v.getVertexIndex
              val vIdxInputNum: Int = v.getVertexEdgeNumber
              vertices(vIdx).setInput(vIdxInputNum, input.dup)
            }
          }
          else {
            if (excludeOutputLayers && current.isOutputVertex && current.hasLayer && current.getLayer.isInstanceOf[Nothing]) {
              continue //todo: continue is not supported
            }
            val out: INDArray = current.doForward(train)
            if (current.hasLayer) {
              layerActivations.put(current.getVertexName, out)
            }
            val outputsTo: Array[VertexIndices] = current.getOutputVertices
            if (outputsTo != null) {
              for (v <- outputsTo) {
                val vIdx: Int = v.getVertexIndex
                val inputNum: Int = v.getVertexEdgeNumber
                vertices(vIdx).setInput(inputNum, out)
              }
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return layerActivations
  }

  /**
   * Return an array of network outputs (predictions) at test time, given the specified network inputs
   * Network outputs are for output layers only.
   *
   * @param input Inputs to the network
   * @return Output activations (order: same as defined in network configuration)
   */
  def output(input: INDArray*): Array[INDArray] = {
    return output(false, input)
  }

  /**
   * A convenience method that returns a single INDArray, instead of an INDArray[].
   * Useful for ComputationGraphs that have only a single output.
   * Otherwise identical to {@link #output(INDArray...)}
   *
   * @param input Inputs to the network
   * @return Output activations array
   */
  def outputSingle(input: INDArray*): INDArray = {
    return outputSingle(false, input)
  }

  /**
   * Return an array of network outputs (predictions), given the specified network inputs
   * Network outputs are for output layers only.
   *
   * @param train If true: do forward pass at training time; false: do forward pass at test time
   * @param input Inputs to the network
   * @return Output activations (order: same as defined in network configuration)
   */
  def output(train: Boolean, input: INDArray*): Array[INDArray] = {
    setInputs(input)
    val activations: Map[String, INDArray] = feedForward(train)
    val outputs: Array[INDArray] = new Array[INDArray](numOutputArrays)
    val i: Int = 0
    import scala.collection.JavaConversions._
    for (s <- configuration.getNetworkOutputs) {
      outputs(({
        i += 1; i - 1
      })) = activations.get(s)
    }
    return outputs
  }

  /**
   * A convenience method that returns a single INDArray, instead of an INDArray[].
   * Useful for ComputationGraphs that have only a single output.
   * Otherwise identical to {@link #output(boolean, INDArray...)}
   *
   * @param train If true: do forward pass at training time; false: do forward pass at test time
   * @param input Inputs to the network
   * @return Output activations array
   */
  def outputSingle(train: Boolean, input: INDArray*): INDArray = {
    if (numOutputArrays != 1) {
      throw new IllegalStateException("Cannot use outputSingle with ComputationGraph that does not have exactly 1 output. nOutputs: " + numOutputArrays)
    }
    return output(train, input)(0)
  }

  /**
   * Calculate the gradient of the network with respect to some external errors.
   * Note that this is typically used for things like reinforcement learning, not typical networks that include
   * an OutputLayer or RnnOutputLayer
   *
   * @param epsilons Epsilons (errors) at the output. Same order with which the output layers are defined in configuration setOutputs(String...)
   * @return Gradient for the network
   */
  def backpropGradient(epsilons: INDArray*): Gradient = {
    if (epsilons == null || epsilons.length != numOutputArrays) throw new IllegalArgumentException("Invalid input: must have epsilons length equal to number of output arrays")
    calcBackpropGradients(configuration.getBackpropType eq BackpropType.TruncatedBPTT, epsilons)
    return gradient
  }

  /**
   * Do backprop (gradient calculation)
   *
   * @param truncatedBPTT    false: normal backprop. true: calculate gradients using truncated BPTT for RNN layers
   * @param externalEpsilons null usually (for typical supervised learning). If not null (and length > 0) then assume that
   *                         the user has provided some errors externally, as they would do for example in reinforcement
   *                         learning situations.
   */
  protected def calcBackpropGradients(truncatedBPTT: Boolean, externalEpsilons: INDArray*) {
    if (flattenedGradients == null) initGradientsView
    val gradients: LinkedList[Nothing] = new LinkedList[E]
    {
      var i: Int = topologicalOrder.length - 1
      while (i >= 0) {
        {
          val current: GraphVertex = vertices(topologicalOrder(i))
          if (current.isInputVertex) continue //todo: continue is not supported
          if (current.isOutputVertex) {
            val thisOutputNumber: Int = configuration.getNetworkOutputs.indexOf(current.getVertexName)
            if (current.getLayer.isInstanceOf[Nothing]) {
              val outputLayer: Nothing = current.getLayer.asInstanceOf[Nothing]
              val currLabels: INDArray = labels(thisOutputNumber)
              outputLayer.setLabels(currLabels)
            }
            else {
              current.setErrors(externalEpsilons(thisOutputNumber))
            }
          }
          val pair: Nothing = current.doBackward(truncatedBPTT)
          val epsilons: Array[INDArray] = pair.getSecond
          val inputVertices: Array[VertexIndices] = current.getInputVertices
          if (inputVertices != null) {
            var j: Int = 0
            for (v <- inputVertices) {
              val gv: GraphVertex = vertices(v.getVertexIndex)
              val outputNumberOfInputVertex: Int = v.getVertexEdgeNumber
              gv.setError(outputNumberOfInputVertex, epsilons(({
                j += 1; j - 1
              })))
            }
          }
          if (pair.getFirst != null) {
            val g: Gradient = pair.getFirst
            val map: Map[String, INDArray] = g.gradientForVariable
            val tempList: LinkedList[Nothing] = new LinkedList[E]
            import scala.collection.JavaConversions._
            for (entry <- map.entrySet) {
              val origName: String = entry.getKey
              val newName: String = current.getVertexName + "_" + origName
              tempList.addFirst(new Nothing(newName, entry.getValue, g.flatteningOrderForVariable(origName)))
            }
            import scala.collection.JavaConversions._
            for (t <- tempList) gradients.addFirst(t)
          }
        }
        ({
          i -= 1; i + 1
        })
      }
    }
    val gradient: Gradient = new DefaultGradient(flattenedGradients)
    import scala.collection.JavaConversions._
    for (t <- gradients) {
      gradient.setGradientFor(t.getFirst, t.getSecond, t.getThird)
    }
    this.gradient = gradient
  }

  override def clone: ComputationGraph = {
    val cg: ComputationGraph = new ComputationGraph(configuration.clone)
    cg.init(params.dup, false)
    if (solver != null) {
      val u: ComputationGraphUpdater = this.getUpdater
      val updaterState: INDArray = u.getStateViewArray
      if (updaterState != null) {
        cg.getUpdater.setStateViewArray(updaterState.dup)
      }
    }
    cg.listeners = this.listeners
    return cg
  }

  /**
   * Calculate the L2 regularization term for all layers in the entire network. This is the sum of the L2 terms
   * for each layer individually
   */
  def calcL2: Double = {
    var l2: Double = 0.0
    for (l <- layers) {
      l2 += l.calcL2
    }
    return l2
  }

  /**
   * Calculate the L1 regularization term for all layers in the entire network. This is the sum of the L1 terms
   * for each layer individually
   */
  def calcL1: Double = {
    var l1: Double = 0.0
    for (l <- layers) {
      l1 += l.calcL1
    }
    return l1
  }

  /**
   * Set the IterationListeners for the ComputationGraph (and all layers in the network)
   */
  def setListeners(listeners: Collection[Nothing]) {
    this.listeners = listeners
    if (layers == null) init
    for (l <- layers) {
      l.setListeners(listeners)
    }
    if (solver != null) {
      solver.setListeners(listeners)
    }
  }

  /**
   * Set the IterationListeners for the ComputationGraph (and all layers in the network)
   */
  def setListeners(listeners: Nothing*) {
    val list: List[Nothing] = new ArrayList[E]
    if (listeners != null && listeners.length > 0) {
      for (i <- listeners) {
        if (i != null) list.add(i)
      }
    }
    setListeners(list)
  }

  /**
   * Get the IterationListeners for the ComputationGraph
   */
  def getListeners: Collection[Nothing] = {
    return listeners
  }

  /**
   * Get the ComputationGraphUpdater for the network
   */
  def getUpdater: ComputationGraphUpdater = {
    if (solver == null) {
      solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
      solver.getOptimizer.setUpdaterComputationGraph(new ComputationGraphUpdater(this))
    }
    return solver.getOptimizer.getComputationGraphUpdater
  }

  /**
   * Set the computationGraphUpdater for the network
   */
  def setUpdater(updater: ComputationGraphUpdater) {
    if (solver == null) {
      solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
    }
    solver.getOptimizer.setUpdaterComputationGraph(updater)
  }

  /**
   * Get the specified output layer, by index. The index of the output layer may be 0 to {@link #getNumOutputArrays()}-1
   */
  def getOutputLayer(outputLayerIdx: Int): Nothing = {
    if (outputLayerIdx >= numOutputArrays) throw new IllegalArgumentException("Invalid index: cannot get output layer " + outputLayerIdx + ", total number of network outputs = " + numOutputArrays)
    return getLayer(configuration.getNetworkOutputs.get(outputLayerIdx))
  }

  /**
   * Get the parameters for the ComputationGraph
   *
   * @param backwardOnly If true: backprop parameters only (i.e., no visible layer biases used in layerwise pretraining layers)
   */
  def params(backwardOnly: Boolean): INDArray = {
    if (backwardOnly) return flattenedParams
    val list: List[INDArray] = new ArrayList[INDArray](layers.length)
    {
      var i: Int = 0
      while (i < topologicalOrder.length) {
        {
          if (!vertices(topologicalOrder(i)).hasLayer) continue //todo: continue is not supported
        val l: Nothing = vertices(topologicalOrder(i)).getLayer
          val layerParams: INDArray = l.params
          if (layerParams != null) list.add(layerParams)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return Nd4j.toFlattened('f', list)
  }

  /**
   * Sets the input and labels and returns a score for the prediction with respect to the true labels<br>
   * This is equivalent to {@link #score(DataSet, boolean)} with training==true.<br>
   * <b>NOTE:</b> this version of the score function can only be used with ComputationGraph networks that have
   * a single input and a single output.
   *
   * @param dataSet the data to score
   * @return the score for the given input,label pairs
   * @see #score(DataSet, boolean)
   */
  def score(dataSet: DataSet): Double = {
    return score(dataSet, false)
  }

  /**
   * Sets the input and labels and returns a score for the prediction with respect to the true labels<br>
   * <b>NOTE:</b> this version of the score function can only be used with ComputationGraph networks that have
   * a single input and a single output. Use {@link #score(MultiDataSet, boolean)} for multiple input/output networks
   *
   * @param dataSet  the data to score
   * @param training whether score is being calculated at training time (true) or test time (false)
   * @return the score for the given input,label pairs
   * @see #score(DataSet, boolean)
   */
  def score(dataSet: DataSet, training: Boolean): Double = {
    if (numInputArrays != 1 || numOutputArrays != 1) throw new UnsupportedOperationException("Cannot score ComputationGraph network with " + " DataSet: network does not have 1 input and 1 output arrays")
    return score(ComputationGraphUtil.toMultiDataSet(dataSet), training)
  }

  /**
   * Score the network given the MultiDataSet, at test time
   */
  def score(dataSet: MultiDataSet): Double = {
    return score(dataSet, false)
  }

  /**
   * Sets the input and labels and returns a score for the prediction with respect to the true labels<br>
   *
   * @param dataSet  the data to score
   * @param training whether score is being calculated at training time (true) or test time (false)
   * @return the score for the given input,label pairs
   */
  def score(dataSet: MultiDataSet, training: Boolean): Double = {
    val hasMaskArrays: Boolean = dataSet.hasMaskArrays
    if (hasMaskArrays) {
      setLayerMaskArrays(dataSet.getFeaturesMaskArrays, dataSet.getLabelsMaskArrays)
    }
    feedForward(dataSet.getFeatures, training)
    val labels: Array[INDArray] = dataSet.getLabels
    setLabels(labels)
    var l1: Double = calcL1
    var l2: Double = calcL2
    var score: Double = 0.0
    var i: Int = 0
    import scala.collection.JavaConversions._
    for (s <- configuration.getNetworkOutputs) {
      val outLayer: Nothing = verticesMap.get(s).getLayer
      if (outLayer == null || !(outLayer.isInstanceOf[Nothing])) {
        ComputationGraph.log.warn("Cannot calculate score: vertex \"" + s + "\" is not an output layer")
        return 0.0
      }
      val ol: Nothing = outLayer.asInstanceOf[Nothing]
      ol.setLabels(labels(({
        i += 1; i - 1
      })))
      score += ol.computeScore(l1, l2, true)
      l1 = 0.0
      l2 = 0.0
    }
    if (hasMaskArrays) clearLayerMaskArrays
    return score
  }

  /**
   * Calculate the score for each example in a DataSet individually. Unlike {@link #score(DataSet)} and {@link #score(DataSet, boolean)}
   * this method does not average/sum over examples. This method allows for examples to be scored individually (at test time only), which
   * may be useful for example for autoencoder architectures and the like.<br>
   * Each row of the output (assuming addRegularizationTerms == true) is equivalent to calling score(DataSet) with a single example.
   *
   * @param data                   The data to score
   * @param addRegularizationTerms If true: add l1/l2 regularization terms (if any) to the score. If false: don't add regularization terms
   * @return An INDArray (column vector) of size input.numRows(); the ith entry is the score (loss value) of the ith example
   */
  def scoreExamples(data: DataSet, addRegularizationTerms: Boolean): INDArray = {
    if (numInputArrays != 1 || numOutputArrays != 1) throw new UnsupportedOperationException("Cannot score ComputationGraph network with " + " DataSet: network does not have 1 input and 1 output arrays")
    return scoreExamples(ComputationGraphUtil.toMultiDataSet(data), addRegularizationTerms)
  }

  /**
   * Calculate the score for each example in a DataSet individually. Unlike {@link #score(MultiDataSet)} and {@link #score(MultiDataSet, boolean)}
   * this method does not average/sum over examples. This method allows for examples to be scored individually (at test time only), which
   * may be useful for example for autoencoder architectures and the like.<br>
   * Each row of the output (assuming addRegularizationTerms == true) is equivalent to calling score(MultiDataSet) with a single example.
   *
   * @param data                   The data to score
   * @param addRegularizationTerms If true: add l1/l2 regularization terms (if any) to the score. If false: don't add regularization terms
   * @return An INDArray (column vector) of size input.numRows(); the ith entry is the score (loss value) of the ith example
   */
  def scoreExamples(data: MultiDataSet, addRegularizationTerms: Boolean): INDArray = {
    val hasMaskArray: Boolean = data.hasMaskArrays
    if (hasMaskArray) setLayerMaskArrays(data.getFeaturesMaskArrays, data.getLabelsMaskArrays)
    feedForward(data.getFeatures, false)
    setLabels(data.getLabels)
    var out: INDArray = null
    var l1: Double = (if (addRegularizationTerms) calcL1 else 0.0)
    var l2: Double = (if (addRegularizationTerms) calcL2 else 0.0)
    var i: Int = 0
    import scala.collection.JavaConversions._
    for (s <- configuration.getNetworkOutputs) {
      val outLayer: Nothing = verticesMap.get(s).getLayer
      if (outLayer == null || !(outLayer.isInstanceOf[Nothing])) {
        throw new UnsupportedOperationException("Cannot calculate score: vertex \"" + s + "\" is not an output layer")
      }
      val ol: Nothing = outLayer.asInstanceOf[Nothing]
      ol.setLabels(labels(({
        i += 1; i - 1
      })))
      val scoreCurrLayer: INDArray = ol.computeScoreForExamples(l1, l2)
      if (out == null) out = scoreCurrLayer
      else out.addi(scoreCurrLayer)
      l1 = 0.0
      l2 = 0.0
    }
    if (hasMaskArray) clearLayerMaskArrays
    return out
  }

  def fit {
    fit(inputs, labels, inputMaskArrays, labelMaskArrays)
  }

  def update(gradient: INDArray, paramType: String) {
    throw new UnsupportedOperationException("Not implemented")
  }

  def update(gradient: Gradient) {
    if (gradient.gradient.length != numParams(true)) throw new IllegalArgumentException("Invalid input: expect gradients array of length " + numParams(true))
    import scala.collection.JavaConversions._
    for (entry <- gradient.gradientForVariable.entrySet) {
      val key: String = entry.getKey
      val `val`: INDArray = entry.getValue
      val idx: Int = key.indexOf('_')
      if (idx == -1) throw new IllegalStateException("Invalid param key: not have layer separator: \"" + key + "\"")
      val layerName: String = key.substring(0, idx)
      val paramType: String = key.split("_")(1)
      this.gradient.gradientForVariable.put(key, `val`)
      getLayer(layerName).update(`val`, paramType)
    }
    setBackpropGradientsViewArray(gradient.gradient)
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

  def score: Double = {
    return score
  }

  def setScore(score: Double) {
    this.score = score
  }

  def accumulateScore(accum: Double) {
    throw new UnsupportedOperationException("Not implemented")
  }

  def params: INDArray = {
    return params(true)
  }

  def numParams: Int = {
    return numParams(true)
  }

  def numParams(backwards: Boolean): Int = {
    var nParams: Int = 0
    for (layer <- layers) {
      nParams += layer.numParams(backwards)
    }
    return nParams
  }

  def setParams(params: INDArray) {
    if (params eq flattenedParams) return
    if (this.flattenedParams != null && this.flattenedParams.length == params.length) {
      this.flattenedParams.assign(params)
      return
    }
    var idx: Int = 0
    {
      var i: Int = 0
      while (i < topologicalOrder.length) {
        {
          if (!vertices(topologicalOrder(i)).hasLayer) continue //todo: continue is not supported
        val layer: Nothing = vertices(topologicalOrder(i)).getLayer
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

  def setParamsViewArray(gradient: INDArray) {
    throw new RuntimeException("Not yet implemented")
  }

  def setBackpropGradientsViewArray(gradient: INDArray) {
    var paramsSoFar: Int = 0
    {
      var i: Int = 0
      while (i < topologicalOrder.length) {
        {
          if (!vertices(topologicalOrder(i)).hasLayer) continue //todo: continue is not supported
        val layer: Nothing = vertices(topologicalOrder(i)).getLayer
          val range: Int = layer.numParams
          if (range <= 0) continue //todo: continue is not supported
          layer.setBackpropGradientsViewArray(gradient.get(NDArrayIndex.point(0), NDArrayIndex.interval(paramsSoFar, paramsSoFar + range)))
          paramsSoFar += range
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def applyLearningRateScoreDecay {
    throw new UnsupportedOperationException("Not implemented")
  }

  def fit(data: INDArray) {
    throw new UnsupportedOperationException("Cannot pretrain ComputationGraph with single INDArray")
  }

  def iterate(input: INDArray) {
    throw new UnsupportedOperationException("Not implemented")
  }

  def gradient: Gradient = {
    return gradient
  }

  def gradientAndScore: Nothing = {
    return new Nothing(gradient, score)
  }

  def batchSize: Int = {
    return inputs(0).size(0)
  }

  def conf: Nothing = {
    return defaultConfiguration
  }

  def setConf(conf: Nothing) {
    throw new UnsupportedOperationException
  }

  def input: INDArray = {
    if (numInputArrays == 1) return (if (inputs != null) inputs(0) else null)
    else throw new UnsupportedOperationException("Cannot return single input: ComputationGraph  has multiple inputs")
  }

  def validateInput {
  }

  def getOptimizer: Nothing = {
    return solver.getOptimizer
  }

  def getParam(param: String): INDArray = {
    throw new UnsupportedOperationException("Not implemented")
  }

  def initParams {
    throw new UnsupportedOperationException("Not implemented")
  }

  def paramTable: Map[String, INDArray] = {
    val allParams: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
    for (layer <- layers) {
      val paramMap: Map[String, INDArray] = layer.paramTable
      import scala.collection.JavaConversions._
      for (entry <- paramMap.entrySet) {
        val newKey: String = layer.conf.getLayer.getLayerName + "_" + entry.getKey
        allParams.put(newKey, entry.getValue)
      }
    }
    return allParams
  }

  def setParamTable(paramTable: Map[String, INDArray]) {
    throw new UnsupportedOperationException("Not implemented")
  }

  def setParam(key: String, `val`: INDArray) {
    val idx: Int = key.indexOf('_')
    if (idx == -1) throw new IllegalStateException("Invalid param key: not have layer separator: \"" + key + "\"")
    val layerName: String = key.substring(0, idx)
    val paramType: String = key.substring(idx + 1)
    getLayer(layerName).setParam(paramType, `val`)
  }

  def clear {
    inputs = null
    labels = null
    inputMaskArrays = null
    labelMaskArrays = null
  }

  /**
   * If this ComputationGraph contains one or more RNN layers: conduct forward pass (prediction)
   * but using previous stored state for any RNN layers. The activations for the final step are
   * also stored in the RNN layers for use next time rnnTimeStep() is called.<br>
   * This method can be used to generate output one or more steps at a time instead of always having to do
   * forward pass from t=0. Example uses are for streaming data, and for generating samples from network output
   * one step at a time (where samples are then fed back into the network as input)<br>
   * If no previous state is present in RNN layers (i.e., initially or after calling rnnClearPreviousState()),
   * the default initialization (usually 0) is used.<br>
   * Supports mini-batch (i.e., multiple predictions/forward pass in parallel) as well as for single examples.<br>
   *
   * @param inputs Input to network. May be for one or multiple time steps. For single time step:
   *               input has shape [miniBatchSize,inputSize] or [miniBatchSize,inputSize,1]. miniBatchSize=1 for single example.<br>
   *               For multiple time steps: [miniBatchSize,inputSize,inputTimeSeriesLength]
   * @return Output activations. If output is RNN layer (such as RnnOutputLayer): if all inputs have shape [miniBatchSize,inputSize]
   *         i.e., is 2d, then outputs have shape [miniBatchSize,outputSize] (i.e., also 2d) instead of [miniBatchSize,outputSize,1].<br>
   *         Otherwise output is 3d [miniBatchSize,outputSize,inputTimeSeriesLength] when using RnnOutputLayer (or unmodified otherwise).
   */
  def rnnTimeStep(inputs: INDArray*): Array[INDArray] = {
    this.inputs = inputs
    var inputIs2d: Boolean = true
    for (i <- inputs) {
      if (i.rank != 2) {
        inputIs2d = false
        break //todo: break is not supported
      }
    }
    val outputs: Array[INDArray] = new Array[INDArray](this.numOutputArrays)
    for (currVertexIdx <- topologicalOrder) {
      val current: GraphVertex = vertices(currVertexIdx)
      if (current.isInputVertex) {
        val inputsTo: Array[VertexIndices] = current.getOutputVertices
        val input: INDArray = inputs(current.getVertexIndex)
        for (v <- inputsTo) {
          val vIdx: Int = v.getVertexIndex
          val vIdxInputNum: Int = v.getVertexEdgeNumber
          vertices(vIdx).setInput(vIdxInputNum, input.dup)
        }
      }
      else {
        var out: INDArray = null
        if (current.hasLayer) {
          val l: Nothing = current.getLayer
          if (l.isInstanceOf[Nothing]) {
            out = (l.asInstanceOf[Nothing]).rnnTimeStep(current.getInputs(0))
          }
          else if (l.isInstanceOf[Nothing]) {
            out = (l.asInstanceOf[Nothing]).rnnTimeStep(current.getInputs(0))
          }
          else {
            out = current.doForward(false)
          }
        }
        else {
          out = current.doForward(false)
        }
        if (current.isOutputVertex) {
          val idx: Int = configuration.getNetworkOutputs.indexOf(current.getVertexName)
          outputs(idx) = out
        }
        val outputsTo: Array[VertexIndices] = current.getOutputVertices
        if (outputsTo != null) {
          for (v <- outputsTo) {
            val vIdx: Int = v.getVertexIndex
            val inputNum: Int = v.getVertexEdgeNumber
            vertices(vIdx).setInput(inputNum, out)
          }
        }
      }
    }
    if (inputIs2d) {
      {
        var i: Int = 0
        while (i < outputs.length) {
          {
            if (outputs(i).rank == 3 && outputs(i).size(2) == 1) {
              outputs(i) = outputs(i).tensorAlongDimension(0, 1, 0)
            }
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    this.inputs = null
    return outputs
  }

  /**
   * Get the state of the RNN layer, as used in {@link #rnnTimeStep(INDArray...)}.
   *
   * @param layer Number/index of the layer.
   * @return Hidden state, or null if layer is not an RNN layer
   */
  def rnnGetPreviousState(layer: Int): Map[String, INDArray] = {
    return rnnGetPreviousState(layers(layer).conf.getLayer.getLayerName)
  }

  /**
   * Get the state of the RNN layer, as used in {@link #rnnTimeStep(INDArray...)}.
   *
   * @param layerName name of the layer
   * @return Hidden state, or null if layer is not an RNN layer
   */
  def rnnGetPreviousState(layerName: String): Map[String, INDArray] = {
    val l: Nothing = verticesMap.get(layerName).getLayer
    if (l == null || !(l.isInstanceOf[Nothing])) return null
    return (l.asInstanceOf[Nothing]).rnnGetPreviousState
  }

  /**
   * Get a map of states for ALL RNN layers, as used in {@link #rnnTimeStep(INDArray...)}.
   * Layers that are not RNN layers will not have an entry in the returned map
   *
   * @return Map of states (keyed by layer name) or null if layer is not an RNN layer
   * @see #rnnSetPreviousStates(Map)
   */
  def rnnGetPreviousStates: Map[String, Map[String, INDArray]] = {
    val states: Map[String, Map[String, INDArray]] = new HashMap[String, Map[String, INDArray]]
    for (l <- layers) {
      if (l.isInstanceOf[Nothing]) {
        states.put(l.conf.getLayer.getLayerName, (l.asInstanceOf[Nothing]).rnnGetPreviousState)
      }
    }
    return states
  }

  /**
   * Set the state of the RNN layer, for use in {@link #rnnTimeStep(INDArray...)}
   *
   * @param layer The number/index of the layer.
   * @param state The state to set the specified layer to
   */
  def rnnSetPreviousState(layer: Int, state: Map[String, INDArray]) {
    rnnSetPreviousState(layers(layer).conf.getLayer.getLayerName, state)
  }

  /**
   * Set the state of the RNN layer, for use in {@link #rnnTimeStep(INDArray...)}
   *
   * @param layerName The name of the layer.
   * @param state     The state to set the specified layer to
   */
  def rnnSetPreviousState(layerName: String, state: Map[String, INDArray]) {
    val l: Nothing = verticesMap.get(layerName).getLayer
    if (l == null || !(l.isInstanceOf[Nothing])) {
      throw new UnsupportedOperationException("Layer \"" + layerName + "\" is not a recurrent layer. Cannot set state")
    }
    (l.asInstanceOf[Nothing]).rnnSetPreviousState(state)
  }

  /**
   * Set the states for all RNN layers, for use in {@link #rnnTimeStep(INDArray...)}
   *
   * @param previousStates The previous time step states for all layers (key: layer name. Value: layer states)
   * @see #rnnGetPreviousStates()
   */
  def rnnSetPreviousStates(previousStates: Map[String, Map[String, INDArray]]) {
    import scala.collection.JavaConversions._
    for (entry <- previousStates.entrySet) {
      rnnSetPreviousState(entry.getKey, entry.getValue)
    }
  }

  /**
   * Clear the previous state of the RNN layers (if any), used in {@link #rnnTimeStep(INDArray...)}
   */
  def rnnClearPreviousState {
    if (layers == null) return
    for (layer <- layers) {
      if (layer.isInstanceOf[Nothing]) (layer.asInstanceOf[Nothing]).rnnClearPreviousState
      else if (layer.isInstanceOf[Nothing]) {
        (layer.asInstanceOf[Nothing]).rnnClearPreviousState
      }
    }
  }

  /**
   * Fit the network using truncated BPTT
   */
  protected def doTruncatedBPTT(inputs: Array[INDArray], labels: Array[INDArray], featureMasks: Array[INDArray], labelMasks: Array[INDArray]) {
    if (flattenedGradients == null) initGradientsView
    var timeSeriesLength: Int = -1
    for (in <- inputs) {
      if (in.rank != 3) continue //todo: continue is not supported
      if (timeSeriesLength == -1) timeSeriesLength = in.size(2)
      else if (timeSeriesLength != in.size(2)) {
        ComputationGraph.log.warn("Cannot do TBPTT with time series of different lengths")
        return
      }
    }
    for (out <- labels) {
      if (out.rank != 3) continue //todo: continue is not supported
      if (timeSeriesLength == -1) timeSeriesLength = out.size(2)
      else if (timeSeriesLength != out.size(2)) {
        ComputationGraph.log.warn("Cannot do TBPTT with time series of different lengths")
        return
      }
    }
    val fwdLen: Int = configuration.getTbpttFwdLength
    if (fwdLen > timeSeriesLength) {
      ComputationGraph.log.warn("Cannot do TBPTT: Truncated BPTT forward length (" + fwdLen + ") > input time series length (" + timeSeriesLength + ")")
      return
    }
    val nSubsets: Int = timeSeriesLength / fwdLen
    rnnClearPreviousState
    val newInputs: Array[INDArray] = new Array[INDArray](inputs.length)
    val newLabels: Array[INDArray] = new Array[INDArray](labels.length)
    val newFeatureMasks: Array[INDArray] = (if (featureMasks != null) new Array[INDArray](featureMasks.length) else null)
    val newLabelMasks: Array[INDArray] = (if (labelMasks != null) new Array[INDArray](labelMasks.length) else null)
    {
      var i: Int = 0
      while (i < nSubsets) {
        {
          val startTimeIdx: Int = i * fwdLen
          val endTimeIdx: Int = startTimeIdx + fwdLen
          {
            var j: Int = 0
            while (j < inputs.length) {
              {
                if (inputs(j).rank != 3) newInputs(j) = inputs(j)
                else {
                  newInputs(j) = inputs(j).get(NDArrayIndex.all, NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
                }
              }
              ({
                j += 1; j - 1
              })
            }
          }
          {
            var j: Int = 0
            while (j < labels.length) {
              {
                if (labels(j).rank != 3) newLabels(j) = labels(j)
                else {
                  newLabels(j) = labels(j).get(NDArrayIndex.all, NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
                }
              }
              ({
                j += 1; j - 1
              })
            }
          }
          if (featureMasks != null) {
            {
              var j: Int = 0
              while (j < featureMasks.length) {
                {
                  if (featureMasks(j) == null) continue //todo: continue is not supported
                  newFeatureMasks(j) = featureMasks(j).get(NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
                }
                ({
                  j += 1; j - 1
                })
              }
            }
          }
          if (labelMasks != null) {
            {
              var j: Int = 0
              while (j < labelMasks.length) {
                {
                  if (labelMasks(j) == null) continue //todo: continue is not supported
                  newLabelMasks(j) = labelMasks(j).get(NDArrayIndex.all, NDArrayIndex.interval(startTimeIdx, endTimeIdx))
                }
                ({
                  j += 1; j - 1
                })
              }
            }
          }
          setInputs(newInputs)
          setLabels(newLabels)
          setLayerMaskArrays(newFeatureMasks, newLabelMasks)
          if (solver == null) {
            solver = new Nothing().configure(conf).listeners(getListeners).model(this).build
          }
          solver.optimize
          rnnUpdateStateWithTBPTTState
        }
        ({
          i += 1; i - 1
        })
      }
    }
    rnnClearPreviousState
  }

  /**
   * Similar to rnnTimeStep and feedForward() methods. Difference here is that this method:<br>
   * (a) like rnnTimeStep does forward pass using stored state for RNN layers, and<br>
   * (b) unlike rnnTimeStep does not modify the RNN layer state<br>
   * Therefore multiple calls to this method with the same input should have the same output.<br>
   * Typically used during training only. Use rnnTimeStep for prediction/forward pass at test time.
   *
   * @param inputs            Input to network
   * @param training          Whether training or not
   * @param storeLastForTBPTT set to true if used as part of truncated BPTT training
   * @return Activations for each layer (including input, as per feedforward() etc)
   */
  def rnnActivateUsingStoredState(inputs: Array[INDArray], training: Boolean, storeLastForTBPTT: Boolean): Map[String, INDArray] = {
    val layerActivations: Map[String, INDArray] = new HashMap[String, INDArray]
    for (currVertexIdx <- topologicalOrder) {
      val current: GraphVertex = vertices(currVertexIdx)
      if (current.isInputVertex) {
        val inputsTo: Array[VertexIndices] = current.getOutputVertices
        val input: INDArray = inputs(current.getVertexIndex)
        layerActivations.put(current.getVertexName, input)
        for (v <- inputsTo) {
          val vIdx: Int = v.getVertexIndex
          val vIdxInputNum: Int = v.getVertexEdgeNumber
          vertices(vIdx).setInput(vIdxInputNum, input.dup)
        }
      }
      else {
        var out: INDArray = null
        if (current.hasLayer) {
          val l: Nothing = current.getLayer
          if (l.isInstanceOf[Nothing]) {
            out = (l.asInstanceOf[Nothing]).rnnActivateUsingStoredState(current.getInputs(0), training, storeLastForTBPTT)
          }
          else if (l.isInstanceOf[Nothing]) {
            val temp: List[INDArray] = (l.asInstanceOf[Nothing]).rnnActivateUsingStoredState(current.getInputs(0), training, storeLastForTBPTT)
            out = temp.get(temp.size - 1)
          }
          else {
            out = current.doForward(training)
          }
          layerActivations.put(current.getVertexName, out)
        }
        else {
          out = current.doForward(training)
        }
        val outputsTo: Array[VertexIndices] = current.getOutputVertices
        if (outputsTo != null) {
          for (v <- outputsTo) {
            val vIdx: Int = v.getVertexIndex
            val inputNum: Int = v.getVertexEdgeNumber
            vertices(vIdx).setInput(inputNum, out)
          }
        }
      }
    }
    return layerActivations
  }

  /**
   * Set the mask arrays for features and labels. Mask arrays are typically used in situations such as one-to-many
   * and many-to-one learning with recurrent neural networks, as well as for supporting time series of varying lengths
   * within the same minibatch.<br>
   * For example, with RNN data sets with input of shape [miniBatchSize,nIn,timeSeriesLength] and outputs of shape
   * [miniBatchSize,nOut,timeSeriesLength], the features and mask arrays will have shape [miniBatchSize,timeSeriesLength]
   * and contain values 0 or 1 at each element (to specify whether a given input/example is present - or merely padding -
   * at a given time step).<br>
   * <b>NOTE</b>: This method is not usually used directly. Instead, the various feedForward and fit methods handle setting
   * of masking internally.
   *
   * @param featureMaskArrays Mask array for features (input)
   * @param labelMaskArrays   Mask array for labels (output)
   * @see #clearLayerMaskArrays()
   */
  def setLayerMaskArrays(featureMaskArrays: Array[INDArray], labelMaskArrays: Array[INDArray]) {
    this.inputMaskArrays = featureMaskArrays
    this.labelMaskArrays = labelMaskArrays
    if (featureMaskArrays != null) {
      if (featureMaskArrays.length != numInputArrays) {
        throw new IllegalArgumentException("Invalid number of feature mask arrays")
      }
      {
        var i: Int = 0
        while (i < featureMaskArrays.length) {
          {
            val inputName: String = configuration.getNetworkInputs.get(i)
            val reshapedFeaturesMask: INDArray = TimeSeriesUtils.reshapeTimeSeriesMaskToVector(featureMaskArrays(i))
            val stack: LinkedList[String] = new LinkedList[String]
            val gv: GraphVertex = verticesMap.get(inputName)
            var outputsFromThisInput: Array[VertexIndices] = gv.getOutputVertices
            for (v <- outputsFromThisInput) {
              stack.addLast(vertices(v.getVertexIndex).getVertexName)
            }
            while (!stack.isEmpty) {
              val nextVertexName: String = stack.removeLast
              val nextVertex: GraphVertex = verticesMap.get(nextVertexName)
              if (nextVertex.hasLayer) {
                val l: Nothing = nextVertex.getLayer
                if (l.isInstanceOf[Nothing]) {
                  continue //todo: continue is not supported
                }
                else if (l.`type` eq Layer.Type.FEED_FORWARD || l.`type` eq Layer.Type.CONVOLUTIONAL) {
                  l.setMaskArray(reshapedFeaturesMask)
                }
              }
              outputsFromThisInput = nextVertex.getOutputVertices
              if (outputsFromThisInput != null) {
                for (v <- outputsFromThisInput) {
                  stack.addLast(vertices(v.getVertexIndex).getVertexName)
                }
              }
            }
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    if (labelMaskArrays != null) {
      if (labelMaskArrays.length != numOutputArrays) {
        throw new IllegalArgumentException("Invalid number of label mask arrays")
      }
      {
        var i: Int = 0
        while (i < labelMaskArrays.length) {
          {
            val outputName: String = configuration.getNetworkOutputs.get(i)
            val v: GraphVertex = verticesMap.get(outputName)
            val ol: Nothing = v.getLayer
            ol.setMaskArray(labelMaskArrays(i))
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
  }

  /**
   * Remove the mask arrays from all layers.<br>
   * See {@link #setLayerMaskArrays(INDArray[], INDArray[])} for details on mask arrays.
   */
  def clearLayerMaskArrays {
    for (layer <- layers) {
      layer.setMaskArray(null)
    }
    this.inputMaskArrays = null
    this.labelMaskArrays = null
  }

  /**
   * Update the internal state of RNN layers after a truncated BPTT fit call
   */
  protected def rnnUpdateStateWithTBPTTState {
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          if (layers(i).isInstanceOf[Nothing]) {
            val l: Nothing = (layers(i).asInstanceOf[Nothing])
            l.rnnSetPreviousState(l.rnnGetTBPTTState)
          }
          else if (layers(i).isInstanceOf[Nothing]) {
            (layers(i).asInstanceOf[Nothing]).updateRnnStateWithTBPTTState
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }
}