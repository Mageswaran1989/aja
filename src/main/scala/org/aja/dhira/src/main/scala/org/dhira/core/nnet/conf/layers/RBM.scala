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
package org.dhira.core.nnet.conf.layers

import lombok._
import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.api.ParamInitializer
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.params.PretrainParamInitializer
import org.deeplearning4j.optimize.api.IterationListener
import org.nd4j.linalg.api.ndarray.INDArray
import java.util.Collection
import java.util.Map

/**
 * Restricted Boltzmann Machine.
 *
 * Markov chain with gibbs sampling.
 *
 * Supports the following visible units:
 * BINARY
 * GAUSSIAN
 * SOFTMAX
 * LINEAR
 *
 * Supports the following hidden units:
 * RECTIFIED
 * BINARY
 * GAUSSIAN
 * SOFTMAX
 *
 * Based on Hinton et al.'s work
 *
 * Great reference:
 * http://www.iro.umontreal.ca/~lisa/publications2/index.php/publications/show/239
 *
 */
//@Data
//@NoArgsConstructor
//@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
object RBM {

  object VisibleUnit extends Enumeration {
    type VisibleUnit = Value
    val BINARY, GAUSSIAN, SOFTMAX, LINEAR = Value
  }

  object HiddenUnit extends Enumeration {
    type HiddenUnit = Value
    val RECTIFIED, BINARY, GAUSSIAN, SOFTMAX = Value
  }

  @AllArgsConstructor class Builder extends BasePretrainNetwork.Builder[RBM.Builder] {
    private var hiddenUnit = HiddenUnit.BINARY
    private var visibleUnit = VisibleUnit.BINARY
    private var k: Int = 1
    private var sparsity: Double = 0f

    def this(hiddenUnit: RBM.HiddenUnit, visibleUnit: RBM.VisibleUnit) {
      this()
      this.hiddenUnit = hiddenUnit
      this.visibleUnit = visibleUnit
    }

    def this() {
      this()
    }

    @SuppressWarnings(Array("unchecked")) def build: RBM = {
      return new RBM(this)
    }

    def k(k: Int): RBM.Builder = {
      this.k = k
      return this
    }

    def hiddenUnit(hiddenUnit: RBM.HiddenUnit): RBM.Builder = {
      this.hiddenUnit = hiddenUnit
      return this
    }

    def visibleUnit(visibleUnit: RBM.VisibleUnit): RBM.Builder = {
      this.visibleUnit = visibleUnit
      return this
    }

    def sparsity(sparsity: Double): RBM.Builder = {
      this.sparsity = sparsity
      return this
    }
  }

}

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true) class RBM extends BasePretrainNetwork {
  protected var hiddenUnit: RBM.HiddenUnit = null
  protected var visibleUnit: RBM.VisibleUnit = null
  protected var k: Int = 0
  protected var sparsity: Double = .0

  def instantiate(conf: Nothing, iterationListeners: Collection[Nothing], layerIndex: Int, layerParamsView: INDArray, initializeParams: Boolean): Nothing = {
    val ret: Nothing = new Nothing(conf)
    ret.setListeners(iterationListeners)
    ret.setIndex(layerIndex)
    ret.setParamsViewArray(layerParamsView)
    val paramTable: Map[String, INDArray] = initializer.init(conf, layerParamsView, initializeParams)
    ret.setParamTable(paramTable)
    ret.setConf(conf)
    return ret
  }

  def initializer: Nothing = {
    return PretrainParamInitializer.getInstance
  }

  private def this(builder: RBM.Builder) {
    this()
    `super`(builder)
    this.hiddenUnit = builder.hiddenUnit
    this.visibleUnit = builder.visibleUnit
    this.k = builder.k
    this.sparsity = builder.sparsity
  }
}