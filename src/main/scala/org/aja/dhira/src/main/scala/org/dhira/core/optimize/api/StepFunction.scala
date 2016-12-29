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
package org.dhira.core.optimize.api

import org.nd4j.linalg.api.ndarray.INDArray
import java.io.Serializable

/**
 * Custom step function for line search
 *
 * @author Adam Gibson
 */
trait StepFunction extends Serializable {
  /**
   * Step with the given parameters
   * @param x the current parameters
   * @param line the line to step
   * @param step
   */
  def step(x: INDArray, line: INDArray, step: Double)

  /**
   * Step with no parameters
   */
  def step(x: INDArray, line: INDArray)

  def step
}