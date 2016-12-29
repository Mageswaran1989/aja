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
package org.dhira.core.optimize.solvers

import org.dhira.core.nnet.api.Model
import org.dhira.core.optimize.api.IterationListener
import org.dhira.core.optimize.api.StepFunction
import org.dhira.core.optimize.api.TerminationCondition
import org.dhira.core.nnet.conf.NeuralNetConfiguration
import org.dhira.core.optimize.api.StepFunction
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/** Originally based on cc.mallet.optimize.ConjugateGradient
  *
  * Rewritten based on Conjugate Gradient algorithm in Bengio et al.,
  * Deep Learning (in preparation) Ch8.
  * See also Nocedal & Wright, Numerical optimization, Ch5
  */
@SerialVersionUID(-1269296013474864091L)
object ConjugateGradient {
  private val logger: Logger = LoggerFactory.getLogger(classOf[ConjugateGradient])
}

@SerialVersionUID(-1269296013474864091L)
class ConjugateGradient extends BaseOptimizer {
  import BaseOptimizer._
  def this(conf: NeuralNetConfiguration, stepFunction: StepFunction, iterationListeners: Iterable[IterationListener], model: Model) {
    this()
    super(conf, stepFunction, iterationListeners, model)
  }

  def this(conf: NeuralNetConfiguration, stepFunction: StepFunction, iterationListeners: Iterable[IterationListener],
           terminationConditions: Iterable[TerminationCondition], model: Model) {
    this()
    super(conf, stepFunction, iterationListeners, terminationConditions, model)
  }

  def preProcessLine: {
    val gradient: INDArray = searchState.get(GRADIENT_KEY).asInstanceOf[INDArray]
    val searchDir: INDArray = searchState.get(SEARCH_DIR).asInstanceOf[INDArray]
    if (searchDir == null) searchState.put(SEARCH_DIR, gradient)
    else searchDir.assign(gradient)
  }

  def postStep(gradient: INDArray) {
    val gLast: INDArray = searchState.get(GRADIENT_KEY).asInstanceOf[INDArray]
    val searchDirLast: INDArray = searchState.get(SEARCH_DIR).asInstanceOf[INDArray]
    val dgg: Double = Nd4j.getBlasWrapper.dot(gradient.sub(gLast), gradient)
    val gg: Double = Nd4j.getBlasWrapper.dot(gLast, gLast)
    val gamma: Double = Math.max(dgg / gg, 0.0)
    if (dgg <= 0.0) ConjugateGradient.logger.debug("Polak-Ribiere gamma <= 0.0; using gamma=0.0 -> SGD line search. dgg={}, gg={}", dgg, gg)
    val searchDir: INDArray = searchDirLast.muli(gamma).addi(gradient)
    searchState.put(GRADIENT_KEY, gradient)
    searchState.put(SEARCH_DIR, searchDir)
  }
}