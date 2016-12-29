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
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.INDArrayIndex
import org.nd4j.linalg.indexing.NDArrayIndex
import java.util.Collections
import java.util.LinkedHashMap
import java.util.Map

/** LSTM Parameter initializer, for LSTM based on
  * Graves: Supervised Sequence Labelling with Recurrent Neural Networks
  * http://www.cs.toronto.edu/~graves/phd.pdf
  */
object GravesLSTMParamInitializer {
  private val INSTANCE: GravesLSTMParamInitializer = new GravesLSTMParamInitializer

  def getInstance: GravesLSTMParamInitializer = {
    return INSTANCE
  }

  /** Weights for previous time step -> current time step connections */
  val RECURRENT_WEIGHT_KEY: String = "RW"
  val BIAS_KEY: String = DefaultParamInitializer.BIAS_KEY
  val INPUT_WEIGHT_KEY: String = DefaultParamInitializer.WEIGHT_KEY
}

class GravesLSTMParamInitializer extends ParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nL: Int = layerConf.getNOut
    val nLast: Int = layerConf.getNIn
    val nParams: Int = nLast * (4 * nL) + nL * (4 * nL + 3) + 4 * nL
    return nParams
  }

  def init(conf: Nothing, paramsView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    val params: Map[String, INDArray] = Collections.synchronizedMap(new LinkedHashMap[String, INDArray])
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val forgetGateInit: Double = layerConf.getForgetGateBiasInit
    val dist: Distribution = Distributions.createDistribution(layerConf.getDist)
    val nL: Int = layerConf.getNOut
    val nLast: Int = layerConf.getNIn
    conf.addVariable(GravesLSTMParamInitializer.INPUT_WEIGHT_KEY)
    conf.addVariable(GravesLSTMParamInitializer.RECURRENT_WEIGHT_KEY)
    conf.addVariable(GravesLSTMParamInitializer.BIAS_KEY)
    val length: Int = numParams(conf, true)
    if (paramsView.length != length) throw new IllegalStateException("Expected params view of length " + length + ", got length " + paramsView.length)
    val nParamsIn: Int = nLast * (4 * nL)
    val nParamsRecurrent: Int = nL * (4 * nL + 3)
    val nBias: Int = 4 * nL
    val inputWeightView: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nParamsIn))
    val recurrentWeightView: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nParamsIn, nParamsIn + nParamsRecurrent))
    val biasView: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nParamsIn + nParamsRecurrent, nParamsIn + nParamsRecurrent + nBias))
    if (initializeParams) {
      params.put(GravesLSTMParamInitializer.INPUT_WEIGHT_KEY, WeightInitUtil.initWeights(nLast, 4 * nL, layerConf.getWeightInit, dist, inputWeightView))
      params.put(GravesLSTMParamInitializer.RECURRENT_WEIGHT_KEY, WeightInitUtil.initWeights(nL, 4 * nL + 3, layerConf.getWeightInit, dist, recurrentWeightView))
      biasView.put(Array[INDArrayIndex](NDArrayIndex.point(0), NDArrayIndex.interval(nL, 2 * nL)), Nd4j.ones(1, nL).muli(forgetGateInit))
      params.put(GravesLSTMParamInitializer.BIAS_KEY, biasView)
    }
    else {
      params.put(GravesLSTMParamInitializer.INPUT_WEIGHT_KEY, WeightInitUtil.reshapeWeights(Array[Int](nLast, 4 * nL), inputWeightView))
      params.put(GravesLSTMParamInitializer.RECURRENT_WEIGHT_KEY, WeightInitUtil.reshapeWeights(Array[Int](nL, 4 * nL + 3), recurrentWeightView))
      params.put(GravesLSTMParamInitializer.BIAS_KEY, biasView)
    }
    return params
  }

  def getGradientsFromFlattened(conf: Nothing, gradientView: INDArray): Map[String, INDArray] = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nL: Int = layerConf.getNOut
    val nLast: Int = layerConf.getNIn
    val length: Int = numParams(conf, true)
    if (gradientView.length != length) throw new IllegalStateException("Expected gradient view of length " + length + ", got length " + gradientView.length)
    val nParamsIn: Int = nLast * (4 * nL)
    val nParamsRecurrent: Int = nL * (4 * nL + 3)
    val nBias: Int = 4 * nL
    val inputWeightGradView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, nParamsIn)).reshape('f', nLast, 4 * nL)
    val recurrentWeightGradView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nParamsIn, nParamsIn + nParamsRecurrent)).reshape('f', nL, 4 * nL + 3)
    val biasGradView: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(nParamsIn + nParamsRecurrent, nParamsIn + nParamsRecurrent + nBias))
    val out: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
    out.put(GravesLSTMParamInitializer.INPUT_WEIGHT_KEY, inputWeightGradView)
    out.put(GravesLSTMParamInitializer.RECURRENT_WEIGHT_KEY, recurrentWeightGradView)
    out.put(GravesLSTMParamInitializer.BIAS_KEY, biasGradView)
    return out
  }
}