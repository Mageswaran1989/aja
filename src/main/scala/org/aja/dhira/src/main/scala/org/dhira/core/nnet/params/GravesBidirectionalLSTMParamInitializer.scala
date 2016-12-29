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
object GravesBidirectionalLSTMParamInitializer {
  private val INSTANCE: GravesBidirectionalLSTMParamInitializer = new GravesBidirectionalLSTMParamInitializer

  def getInstance: GravesBidirectionalLSTMParamInitializer = {
    return INSTANCE
  }

  /** Weights for previous time step -> current time step connections */
  val RECURRENT_WEIGHT_KEY_FORWARDS: String = "RWF"
  val BIAS_KEY_FORWARDS: String = DefaultParamInitializer.BIAS_KEY + "F"
  val INPUT_WEIGHT_KEY_FORWARDS: String = DefaultParamInitializer.WEIGHT_KEY + "F"
  val RECURRENT_WEIGHT_KEY_BACKWARDS: String = "RWB"
  val BIAS_KEY_BACKWARDS: String = DefaultParamInitializer.BIAS_KEY + "B"
  val INPUT_WEIGHT_KEY_BACKWARDS: String = DefaultParamInitializer.WEIGHT_KEY + "B"
}

class GravesBidirectionalLSTMParamInitializer extends ParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nL: Int = layerConf.getNOut
    val nLast: Int = layerConf.getNIn
    val nParamsForward: Int = nLast * (4 * nL) + nL * (4 * nL + 3) + 4 * nL
    return 2 * nParamsForward
  }

  def init(conf: Nothing, paramsView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    val params: Map[String, INDArray] = Collections.synchronizedMap(new LinkedHashMap[String, INDArray])
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val forgetGateInit: Double = layerConf.getForgetGateBiasInit
    val dist: Distribution = Distributions.createDistribution(layerConf.getDist)
    val nL: Int = layerConf.getNOut
    val nLast: Int = layerConf.getNIn
    conf.addVariable(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_FORWARDS)
    conf.addVariable(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_FORWARDS)
    conf.addVariable(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_FORWARDS)
    conf.addVariable(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_BACKWARDS)
    conf.addVariable(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_BACKWARDS)
    conf.addVariable(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_BACKWARDS)
    val nParamsInput: Int = nLast * (4 * nL)
    val nParamsRecurrent: Int = nL * (4 * nL + 3)
    val nBias: Int = 4 * nL
    val rwFOffset: Int = nParamsInput
    val bFOffset: Int = rwFOffset + nParamsRecurrent
    val iwROffset: Int = bFOffset + nBias
    val rwROffset: Int = iwROffset + nParamsInput
    val bROffset: Int = rwROffset + nParamsRecurrent
    val iwF: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, rwFOffset))
    val rwF: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(rwFOffset, bFOffset))
    val bF: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(bFOffset, iwROffset))
    val iwR: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(iwROffset, rwROffset))
    val rwR: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(rwROffset, bROffset))
    val bR: INDArray = paramsView.get(NDArrayIndex.point(0), NDArrayIndex.interval(bROffset, bROffset + nBias))
    if (initializeParams) {
      bF.put(Array[INDArrayIndex](NDArrayIndex.point(0), NDArrayIndex.interval(nL, 2 * nL)), Nd4j.ones(1, nL).muli(forgetGateInit))
      bR.put(Array[INDArrayIndex](NDArrayIndex.point(0), NDArrayIndex.interval(nL, 2 * nL)), Nd4j.ones(1, nL).muli(forgetGateInit))
    }
    if (initializeParams) {
      params.put(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_FORWARDS, WeightInitUtil.initWeights(nLast, 4 * nL, layerConf.getWeightInit, dist, iwF))
      params.put(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_FORWARDS, WeightInitUtil.initWeights(nL, 4 * nL + 3, layerConf.getWeightInit, dist, rwF))
      params.put(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_FORWARDS, bF)
      params.put(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_BACKWARDS, WeightInitUtil.initWeights(nLast, 4 * nL, layerConf.getWeightInit, dist, iwR))
      params.put(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_BACKWARDS, WeightInitUtil.initWeights(nL, 4 * nL + 3, layerConf.getWeightInit, dist, rwR))
      params.put(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_BACKWARDS, bR)
    }
    else {
      params.put(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_FORWARDS, WeightInitUtil.reshapeWeights(Array[Int](nLast, 4 * nL), iwF))
      params.put(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_FORWARDS, WeightInitUtil.reshapeWeights(Array[Int](nL, 4 * nL + 3), rwF))
      params.put(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_FORWARDS, bF)
      params.put(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_BACKWARDS, WeightInitUtil.reshapeWeights(Array[Int](nLast, 4 * nL), iwR))
      params.put(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_BACKWARDS, WeightInitUtil.reshapeWeights(Array[Int](nL, 4 * nL + 3), rwR))
      params.put(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_BACKWARDS, bR)
    }
    return params
  }

  def getGradientsFromFlattened(conf: Nothing, gradientView: INDArray): Map[String, INDArray] = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    val nL: Int = layerConf.getNOut
    val nLast: Int = layerConf.getNIn
    val nParamsInput: Int = nLast * (4 * nL)
    val nParamsRecurrent: Int = nL * (4 * nL + 3)
    val nBias: Int = 4 * nL
    val rwFOffset: Int = nParamsInput
    val bFOffset: Int = rwFOffset + nParamsRecurrent
    val iwROffset: Int = bFOffset + nBias
    val rwROffset: Int = iwROffset + nParamsInput
    val bROffset: Int = rwROffset + nParamsRecurrent
    val iwFG: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, rwFOffset)).reshape('f', nLast, 4 * nL)
    val rwFG: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(rwFOffset, bFOffset)).reshape('f', nL, 4 * nL + 3)
    val bFG: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(bFOffset, iwROffset))
    val iwRG: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(iwROffset, rwROffset)).reshape('f', nLast, 4 * nL)
    val rwRG: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(rwROffset, bROffset)).reshape('f', nL, 4 * nL + 3)
    val bRG: INDArray = gradientView.get(NDArrayIndex.point(0), NDArrayIndex.interval(bROffset, bROffset + nBias))
    val out: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
    out.put(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_FORWARDS, iwFG)
    out.put(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_FORWARDS, rwFG)
    out.put(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_FORWARDS, bFG)
    out.put(GravesBidirectionalLSTMParamInitializer.INPUT_WEIGHT_KEY_BACKWARDS, iwRG)
    out.put(GravesBidirectionalLSTMParamInitializer.RECURRENT_WEIGHT_KEY_BACKWARDS, rwRG)
    out.put(GravesBidirectionalLSTMParamInitializer.BIAS_KEY_BACKWARDS, bRG)
    return out
  }
}