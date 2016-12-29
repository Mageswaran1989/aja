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
package org.deeplearning4j.nn.params

import org.deeplearning4j.nn.api.ParamInitializer
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.distribution.Distributions
import org.deeplearning4j.nn.weights.WeightInitUtil
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.rng.distribution.Distribution
import org.nd4j.linalg.indexing.NDArrayIndex
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Map

/**
 * Initialize convolution params.
 *
 * @author Adam Gibson
 */
object ConvolutionParamInitializer {
  private val INSTANCE: ConvolutionParamInitializer = new ConvolutionParamInitializer

  def getInstance: ConvolutionParamInitializer = {
    return INSTANCE
  }

  val WEIGHT_KEY: String = DefaultParamInitializer.WEIGHT_KEY
  val BIAS_KEY: String = DefaultParamInitializer.BIAS_KEY
}

class ConvolutionParamInitializer extends ParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val kernel: Array[Int] = layerConf.getKernelSize
    val nIn: Int = layerConf.getNIn
    val nOut: Int = layerConf.getNOut
    return nIn * nOut * kernel(0) * kernel(1) + nOut
  }

  def init(conf: Nothing, paramsView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    if ((conf.getLayer.asInstanceOf[Nothing]).getKernelSize.length ne 2) throw new IllegalArgumentException("Filter size must be == 2")
    val params: Map[String, INDArray] = Collections.synchronizedMap(new LinkedHashMap[String, INDArray])
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val kernel: Array[Int] = layerConf.getKernelSize
    val nIn: Int = layerConf.getNIn
    val nOut: Int = layerConf.getNOut
    val biasView: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nOut))
    val weightView: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nOut, numParams(conf, true)))
    params.put(ConvolutionParamInitializer.BIAS_KEY, createBias(conf, biasView, initializeParams))
    params.put(ConvolutionParamInitializer.WEIGHT_KEY, createWeightMatrix(conf, weightView, initializeParams))
    conf.addVariable(ConvolutionParamInitializer.WEIGHT_KEY)
    conf.addVariable(ConvolutionParamInitializer.BIAS_KEY)
    return params
  }

  def getGradientsFromFlattened(conf: Nothing, gradientView: INDArray): Map[String, INDArray] = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val kernel: Array[Int] = layerConf.getKernelSize
    val nIn: Int = layerConf.getNIn
    val nOut: Int = layerConf.getNOut
    val biasGradientView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nOut))
    val weightGradientView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nOut, numParams(conf, true))).reshape('c', nOut, nIn, kernel(0), kernel(1))
    val out: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
    out.put(ConvolutionParamInitializer.BIAS_KEY, biasGradientView)
    out.put(ConvolutionParamInitializer.WEIGHT_KEY, weightGradientView)
    return out
  }

  protected def createBias(conf: Nothing, biasView: INDArray, initializeParams: Boolean): INDArray = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    if (initializeParams) biasView.assign(layerConf.getBiasInit)
    return biasView
  }

  protected def createWeightMatrix(conf: Nothing, weightView: INDArray, initializeParams: Boolean): INDArray = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    if (initializeParams) {
      val dist: Distribution = Distributions.createDistribution(conf.getLayer.getDist)
      val kernel: Array[Int] = layerConf.getKernelSize
      return WeightInitUtil.initWeights(Array[Int](layerConf.getNOut, layerConf.getNIn, kernel(0), kernel(1)), layerConf.getWeightInit, dist, 'c', weightView)
    }
    else {
      val kernel: Array[Int] = layerConf.getKernelSize
      return WeightInitUtil.reshapeWeights(Array[Int](layerConf.getNOut, layerConf.getNIn, kernel(0), kernel(1)), weightView, 'c')
    }
  }
}