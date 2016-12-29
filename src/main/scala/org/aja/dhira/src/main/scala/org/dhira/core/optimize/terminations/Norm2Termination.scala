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
package org.dhira.core.optimize.terminations

import org.deeplearning4j.optimize.api.TerminationCondition
import org.dhira.core.optimize.api.TerminationCondition
import org.nd4j.linalg.api.ndarray.INDArray

/**
 * Terminate if the norm2 of the gradient is < a certain tolerance
 */
class Norm2Termination extends TerminationCondition {
  private var gradientTolerance: Double = 1e-3

  def this(gradientTolerance: Double) {
    this()
    this.gradientTolerance = gradientTolerance
  }

  def terminate(cost: Double, oldCost: Double, otherParams: Array[AnyRef]): Boolean = {
    val line: INDArray = otherParams(0).asInstanceOf[INDArray]
    val norm2: Double = line.norm2(Integer.MAX_VALUE).getDouble(0)
    return norm2 < gradientTolerance
  }
}