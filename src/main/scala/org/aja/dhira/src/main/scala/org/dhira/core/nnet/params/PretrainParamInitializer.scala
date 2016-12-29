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

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import java.util.Map

/**
 * Pretrain weight initializer.
 * Has the visible bias as well as hidden and weight matrix.
 *
 * @author Adam Gibson
 */
object PretrainParamInitializer {
  private val INSTANCE: PretrainParamInitializer = new PretrainParamInitializer

  def getInstance: PretrainParamInitializer = {
    return INSTANCE
  }

  val VISIBLE_BIAS_KEY: String = DefaultParamInitializer.BIAS_KEY + "B"
}

class PretrainParamInitializer extends DefaultParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    if (backprop) return super.numParams(conf, backprop)
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    return super.numParams(conf, backprop) + layerConf.getNIn
  }

  def init(conf: Nothing, paramsView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    val params: Map[String, INDArray] = super.init(conf, paramsView, initializeParams)
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    params.put(PretrainParamInitializer.VISIBLE_BIAS_KEY, Nd4j.valueArrayOf(layerConf.getNIn, 0.0))
    conf.addVariable(PretrainParamInitializer.VISIBLE_BIAS_KEY)
    return params
  }
}