package org.dhira.core.nnet.updater.graph

import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.api.Updater
import org.deeplearning4j.nn.gradient.DefaultGradient
import org.deeplearning4j.nn.gradient.Gradient
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.updater.UpdaterCreator
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import java.io.Serializable
import java.util.HashMap
import java.util.Map

/**
 * Gradient updater for ComputationGraph.<br>
 * Note: ComputationGraph does not implement the Layer interface (due to multiple in/out etc), hence ComputationGraphUpdater
 * can't be defined as an {@link Updater}.
 *
 * @author Alex Black
 */
class ComputationGraphUpdater extends Serializable with Cloneable {
  private final val layerUpdaters: Array[Nothing] = null
  private final val layerUpdatersMap: Map[String, Integer] = null
  private var viewArray: INDArray = null

  def this(graph: ComputationGraph) {
    this()
    layerUpdaters = new Array[Nothing](graph.getNumLayers)
    layerUpdatersMap = new HashMap[String, Integer]
    var i: Int = 0
    var updaterStateSize: Int = 0
    for (layer <- graph.getLayers) {
      val u: Nothing = UpdaterCreator.getUpdater(layer)
      layerUpdaters(i) = u
      layerUpdatersMap.put(layer.conf.getLayer.getLayerName, i)
      updaterStateSize += layerUpdaters(i).stateSizeForLayer(layer)
      i += 1
    }
    if (updaterStateSize > 0) {
      viewArray = Nd4j.createUninitialized(Array[Int](1, updaterStateSize), Nd4j.order)
    }
    var soFar: Int = 0
    i = 0
    for (layer <- graph.getLayers) {
      val thisSize: Int = layerUpdaters(i).stateSizeForLayer(layer)
      if (thisSize == 0) {
        i += 1
        continue //todo: continue is not supported
      }
      val view: INDArray = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, soFar + thisSize))
      layerUpdaters(({
        i += 1; i - 1
      })).setStateViewArray(layer, view, true)
      soFar += thisSize
    }
  }

  def this(graph: ComputationGraph, updaterState: INDArray) {
    this()
    layerUpdatersMap = new HashMap[String, Integer]
    val layers: Array[Nothing] = graph.getLayers
    layerUpdaters = new Array[Nothing](layers.length)
    var updaterStateSize: Int = 0
    {
      var i: Int = 0
      while (i < layers.length) {
        {
          layerUpdaters(i) = UpdaterCreator.getUpdater(layers(i))
          updaterStateSize += layerUpdaters(i).stateSizeForLayer(layers(i))
          layerUpdatersMap.put(layers(i).conf.getLayer.getLayerName, i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (updaterState != null) {
      if (updaterState.length != updaterStateSize) {
        throw new IllegalStateException("Expected updater state with size " + updaterStateSize + ", got size " + updaterState.length)
      }
      this.viewArray = updaterState
      var soFar: Int = 0
      {
        var i: Int = 0
        while (i < layers.length) {
          {
            val thisSize: Int = layerUpdaters(i).stateSizeForLayer(layers(i))
            if (thisSize == 0) continue //todo: continue is not supported
          val view: INDArray = viewArray.get(NDArrayIndex.point(0), NDArrayIndex.interval(soFar, soFar + thisSize))
            layerUpdaters(i).setStateViewArray(layers(i), view, false)
            soFar += thisSize
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    else if (updaterStateSize != 0) {
      throw new IllegalStateException("Expected updater state with size " + updaterStateSize + ", got null input")
    }
  }

  private def this(size: Int, layerUpdatersMap: Map[String, Integer]) {
    this()
    layerUpdaters = new Array[Nothing](size)
    this.layerUpdatersMap = layerUpdatersMap
  }

  private def this(updater: ComputationGraphUpdater) {
    this()
    layerUpdaters = new Array[Nothing](updater.layerUpdaters.length)
    {
      var i: Int = 0
      while (i < layerUpdaters.length) {
        layerUpdaters(i) = updater.layerUpdaters(i).clone
        ({
          i += 1; i - 1
        })
      }
    }
    layerUpdatersMap = new HashMap[String, Integer](updater.layerUpdatersMap)
  }

  override def clone: ComputationGraphUpdater = {
    return new ComputationGraphUpdater(this)
  }

  /**
   * Update the gradients for the given ComputationGraph
   */
  def update(graph: ComputationGraph, gradient: Gradient, iteration: Int, batchSize: Int) {
    val layerGradients: Map[String, Gradient] = new HashMap[String, Gradient]
    import scala.collection.JavaConversions._
    for (gradientPair <- gradient.gradientForVariable.entrySet) {
      val key: String = gradientPair.getKey
      val idx: Int = key.lastIndexOf('_')
      if (idx == -1) throw new IllegalStateException("Invalid key: ComputationGraph Gradient key does not have layer separator: \"" + key + "\"")
      val layerName: String = key.substring(0, idx)
      var g: Gradient = layerGradients.get(layerName)
      if (g == null) {
        g = new DefaultGradient
        layerGradients.put(layerName, g)
      }
      val newKey: String = key.substring(idx + 1)
      g.setGradientFor(newKey, gradientPair.getValue)
    }
    import scala.collection.JavaConversions._
    for (entry <- layerGradients.entrySet) {
      val layerName: String = entry.getKey
      val updaterIdx: Int = layerUpdatersMap.get(layerName)
      layerUpdaters(updaterIdx).update(graph.getLayer(layerName), entry.getValue, iteration, batchSize)
      import scala.collection.JavaConversions._
      for (entry2 <- layerGradients.get(layerName).gradientForVariable.entrySet) {
        gradient.setGradientFor(entry.getKey + "_" + entry2.getKey, entry2.getValue)
      }
    }
  }

  def setStateViewArray(viewArray: INDArray) {
    if (this.viewArray.length != viewArray.length) throw new IllegalStateException("Invalid input: view arrays differ in length. " + "Expected length " + this.viewArray.length + ", got length " + viewArray.length)
    this.viewArray.assign(viewArray)
  }

  def getStateViewArray: INDArray = {
    return viewArray
  }

  override def equals(other: AnyRef): Boolean = {
    if (!(other.isInstanceOf[ComputationGraphUpdater])) return false
    return layerUpdatersMap == (other.asInstanceOf[ComputationGraphUpdater]).layerUpdatersMap
  }

  override def hashCode: Int = {
    return layerUpdatersMap.hashCode
  }
}