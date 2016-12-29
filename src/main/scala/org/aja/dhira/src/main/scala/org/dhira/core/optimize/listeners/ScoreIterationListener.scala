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
package org.dhira.core.optimize.listeners

import org.dhira.core.nnet.api.Model
import org.dhira.core.optimize.api.IterationListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Score iteration listener
 *
 * @author Adam Gibson
 */
object ScoreIterationListener {
  private val log: Logger = LoggerFactory.getLogger(classOf[ScoreIterationListener])
}

class ScoreIterationListener extends IterationListener {
  private var printIterations: Int = 10
  private var isInvoked: Boolean = false
  private var iterCount: Long = 0

  /**
   * @param printIterations    frequency with which to print scores (i.e., every printIterations parameter updates)
   */
  def this(printIterations: Int) {
    this()
    this.printIterations = printIterations
  }

  /** Default constructor printing every 10 iterations */
//  def this() {
//    this()
//  }

  def invoked: Boolean = {
    return isInvoked
  }

  def invoke {
    this.isInvoked = true
  }

  def iterationDone(model: Model, iteration: Int) {
    if (printIterations <= 0) printIterations = 1
    if (iterCount % printIterations == 0) {
      invoke
      val result: Double = model.score
      ScoreIterationListener.log.info("Score at iteration " + iterCount + " is " + result)
    }
    iterCount += 1
  }
}