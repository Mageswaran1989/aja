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

object StepFunctions {
  private val DEFAULT_STEP_FUNCTION_INSTANCE: Nothing = new Nothing
  private val GRADIENT_STEP_FUNCTION_INSTANCE: Nothing = new Nothing
  private val NEGATIVE_DEFAULT_STEP_FUNCTION_INSTANCE: Nothing = new Nothing
  private val NEGATIVE_GRADIENT_STEP_FUNCTION_INSTANCE: Nothing = new Nothing

  def createStepFunction(stepFunction: Nothing): Nothing = {
    if (stepFunction == null) return null
    if (stepFunction.isInstanceOf[Nothing]) return DEFAULT_STEP_FUNCTION_INSTANCE
    if (stepFunction.isInstanceOf[Nothing]) return GRADIENT_STEP_FUNCTION_INSTANCE
    if (stepFunction.isInstanceOf[Nothing]) return NEGATIVE_DEFAULT_STEP_FUNCTION_INSTANCE
    if (stepFunction.isInstanceOf[Nothing]) return NEGATIVE_GRADIENT_STEP_FUNCTION_INSTANCE
    throw new RuntimeException("unknown step function: " + stepFunction)
  }
}

class StepFunctions {
  private def this() {
    this()
  }
}