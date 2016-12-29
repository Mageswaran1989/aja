package org.deeplearning4j.nn.params

import org.deeplearning4j.nn.api.ParamInitializer
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.BatchNormalization
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.indexing.NDArrayIndex
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Map

/**
 * Batch normalization variable init
 */
object BatchNormalizationParamInitializer {
  private val INSTANCE: BatchNormalizationParamInitializer = new BatchNormalizationParamInitializer

  def getInstance: BatchNormalizationParamInitializer = {
    return INSTANCE
  }

  val GAMMA: String = "gamma"
  val BETA: String = "beta"
}

class BatchNormalizationParamInitializer extends ParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    val layer: Nothing = conf.getLayer.asInstanceOf[Nothing]
    return 2 * layer.getNOut
  }

  def init(conf: Nothing, paramView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    val params: Map[String, INDArray] = Collections.synchronizedMap(new LinkedHashMap[String, INDArray])
    val layer: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nOut: Int = layer.getNOut
    val gammaView: INDArray = paramView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nOut))
    val betaView: INDArray = paramView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nOut, 2 * nOut))
    params.put(BatchNormalizationParamInitializer.GAMMA, createGamma(conf, gammaView, initializeParams))
    conf.addVariable(BatchNormalizationParamInitializer.GAMMA)
    params.put(BatchNormalizationParamInitializer.BETA, createBeta(conf, betaView, initializeParams))
    conf.addVariable(BatchNormalizationParamInitializer.BETA)
    return params
  }

  def getGradientsFromFlattened(conf: Nothing, gradientView: INDArray): Map[String, INDArray] = {
    val layer: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nOut: Int = layer.getNOut
    val gammaView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nOut))
    val betaView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nOut, 2 * nOut))
    val out: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
    out.put(BatchNormalizationParamInitializer.GAMMA, gammaView)
    out.put(BatchNormalizationParamInitializer.BETA, betaView)
    return out
  }

  protected def createBeta(conf: Nothing, betaView: INDArray, initializeParams: Boolean): INDArray = {
    val layer: Nothing = conf.getLayer.asInstanceOf[Nothing]
    if (initializeParams) betaView.assign(layer.getBeta)
    return betaView
  }

  protected def createGamma(conf: Nothing, gammaView: INDArray, initializeParams: Boolean): INDArray = {
    val layer: Nothing = conf.getLayer.asInstanceOf[Nothing]
    if (initializeParams) gammaView.assign(layer.getGamma)
    return gammaView
  }
}