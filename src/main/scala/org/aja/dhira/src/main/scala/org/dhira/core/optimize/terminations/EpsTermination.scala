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

import org.dhira.core.optimize.api.TerminationCondition
import org.nd4j.linalg.factory.Nd4j

/**
 * Epsilon termination (absolute change based on tolerance)
 *
 * @author Adam Gibson
 */
class EpsTermination extends TerminationCondition {
  private var eps: Double = 1e-4
  private var tolerance: Double = Nd4j.EPS_THRESHOLD

  def this(eps: Double, tolerance: Double) {
    this()
    this.eps = eps
    this.tolerance = tolerance
  }

  def this() {
    this()
  }

  def terminate(cost: Double, old: Double, otherParams: Array[AnyRef]): Boolean = {
    if (cost == 0 && old == 0) return false
    if (otherParams.length >= 2) {
      val eps: Double = otherParams(0).asInstanceOf[Double]
      val tolerance: Double = otherParams(1).asInstanceOf[Double]
      return 2.0 * Math.abs(old - cost) <= tolerance * (Math.abs(old) + Math.abs(cost) + eps)
    }
    else return 2.0 * Math.abs(old - cost) <= tolerance * (Math.abs(old) + Math.abs(cost) + eps)
  }
}