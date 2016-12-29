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

/**
 * Normal gradient step function
 * @author Adam Gibson
 */
class GradientStepFunction extends StepFunction {
  def step(x: INDArray, line: INDArray, step: Double) {
    x.addi(line)
  }

  def step(x: INDArray, line: INDArray) {
    x.addi(line)
  }

  def step {
  }
}