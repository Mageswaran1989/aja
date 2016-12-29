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
import org.nd4j.linalg.api.ndarray.INDArray
import java.util.Map

object GRUParamInitializer {
  private val INSTANCE: GRUParamInitializer = new GRUParamInitializer

  def getInstance: GRUParamInitializer = {
    return INSTANCE
  }

  /** Weights for previous time step -> current time step connections */
  val RECURRENT_WEIGHT_KEY: String = "RW"
  val BIAS_KEY: String = DefaultParamInitializer.BIAS_KEY
  val INPUT_WEIGHT_KEY: String = DefaultParamInitializer.WEIGHT_KEY
}

class GRUParamInitializer extends ParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    throw new UnsupportedOperationException("Not yet implemented")
  }

  def init(conf: Nothing, paramsView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    val layerConf: Nothing = conf.getLayer.asInstanceOf[Nothing]
    throw new UnsupportedOperationException("Not yet implemented")
  }

  def getGradientsFromFlattened(conf: Nothing, gradientView: INDArray): Map[String, INDArray] = {
    throw new UnsupportedOperationException("Not yet implemented")
  }
}