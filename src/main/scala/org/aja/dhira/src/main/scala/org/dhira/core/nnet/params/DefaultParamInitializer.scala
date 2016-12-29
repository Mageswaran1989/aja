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
package org.dhira.core.nnet.params

import org.deeplearning4j.nn.api.ParamInitializer
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.distribution.Distributions
import org.deeplearning4j.nn.weights.WeightInitUtil
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.rng.distribution.Distribution
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Map

/**
 * Static weight initializer with just a weight matrix and a bias
 * @author Adam Gibson
 */
object DefaultParamInitializer {
  private val INSTANCE: DefaultParamInitializer = new DefaultParamInitializer

  def getInstance: DefaultParamInitializer = {
    return INSTANCE
  }

  val WEIGHT_KEY: String = "W"
  val BIAS_KEY: String = "b"
}

class DefaultParamInitializer extends ParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nIn: Int = layerConf.getNIn
    val nOut: Int = layerConf.getNOut
    return nIn * nOut + nOut
  }

  def init(conf: Nothing, paramsView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    if (!(conf.getLayer.isInstanceOf[Nothing])) throw new IllegalArgumentException("unsupported layer type: " + conf.getLayer.getClass.getName)
    val params: Map[String, INDArray] = Collections.synchronizedMap(new LinkedHashMap[String, INDArray])
    val length: Int = numParams(conf, true)
    if (paramsView.length != length) throw new IllegalStateException("Expected params view of length " + length + ", got length " + paramsView.length)
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nIn: Int = layerConf.getNIn
    val nOut: Int = layerConf.getNOut
    val nWeightParams: Int = nIn * nOut
    val weightView: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nWeightParams))
    val biasView: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nWeightParams, nWeightParams + nOut))
    params.put(DefaultParamInitializer.WEIGHT_KEY, createWeightMatrix(conf, weightView, initializeParams))
    params.put(DefaultParamInitializer.BIAS_KEY, createBias(conf, biasView, initializeParams))
    conf.addVariable(DefaultParamInitializer.WEIGHT_KEY)
    conf.addVariable(DefaultParamInitializer.BIAS_KEY)
    return params
  }

  def getGradientsFromFlattened(conf: Nothing, gradientView: INDArray): Map[String, INDArray] = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nIn: Int = layerConf.getNIn
    val nOut: Int = layerConf.getNOut
    val nWeightParams: Int = nIn * nOut
    val weightGradientView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nWeightParams)).reshape('f', nIn, nOut)
    val biasView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nWeightParams, nWeightParams + nOut))
    val out: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
    out.put(DefaultParamInitializer.WEIGHT_KEY, weightGradientView)
    out.put(DefaultParamInitializer.BIAS_KEY, biasView)
    return out
  }

  protected def createBias(conf: Nothing, biasParamView: INDArray, initializeParameters: Boolean): INDArray = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    if (initializeParameters) {
      val ret: INDArray = Nd4j.valueArrayOf(layerConf.getNOut, layerConf.getBiasInit)
      biasParamView.assign(ret)
    }
    return biasParamView
  }

  protected def createWeightMatrix(conf: Nothing, weightParamView: INDArray, initializeParameters: Boolean): INDArray = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    if (initializeParameters) {
      val dist: Distribution = Distributions.createDistribution(layerConf.getDist)
      val ret: INDArray = WeightInitUtil.initWeights(layerConf.getNIn, layerConf.getNOut, layerConf.getWeightInit, dist, weightParamView)
      return ret
    }
    else {
      return WeightInitUtil.reshapeWeights(Array[Int](layerConf.getNIn, layerConf.getNOut), weightParamView)
    }
  }
}