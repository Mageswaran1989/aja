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
package org.dhira.core.optimize.stepfunctions

import org.deeplearning4j.optimize.api.StepFunction
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

/**
 * Default step function
 * @author Adam Gibson
 */
@SerialVersionUID(-4707790524365648985L)
class DefaultStepFunction extends StepFunction {
  /** Does x = x + stepSize * line
    * @param step step size.
    */
  def step(parameters: INDArray, searchDirection: INDArray, step: Double) {
    Nd4j.getBlasWrapper.level1.axpy(searchDirection.length, step, searchDirection, parameters)
  }

  def step(x: INDArray, line: INDArray) {
    throw new UnsupportedOperationException
  }

  def step {
    throw new UnsupportedOperationException
  }
}