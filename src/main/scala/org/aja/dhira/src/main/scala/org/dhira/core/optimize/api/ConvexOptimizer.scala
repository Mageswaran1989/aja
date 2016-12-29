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

import org.dhira.core.containers.Pair
import org.dhira.core.nnet.api.Model
import org.dhira.core.nnet.api.Updater
import org.dhira.core.nnet.conf.NeuralNetConfiguration
import org.dhira.core.nnet.gradient.Gradient
import org.dhira.core.nnet.updater.graph.ComputationGraphUpdater
import org.nd4j.linalg.api.ndarray.INDArray
import org.dhira.core.optimize.api.IterationListener
import java.io.Serializable


/**
 * Convex optimizer.
 * @author Adam Gibson
 */
trait ConvexOptimizer extends Serializable {
  /**
   * The score for the optimizer so far
   * @return the score for this optimizer so far
   */
  def score: Double

  def getUpdater: Updater

  def getComputationGraphUpdater: ComputationGraphUpdater

  def setUpdater(updater: Updater)

  def setUpdaterComputationGraph(updater: ComputationGraphUpdater)

  def setListeners(listeners: Iterable[IterationListener])

  def getConf: NeuralNetConfiguration

  /**
   * The gradient and score for this optimizer
   * @return the gradient and score for this optimizer
   */
  def gradientAndScore: Pair[Gradient, Double]

  /**
   * Calls optimize
   * @return whether the convex optimizer
   *         converted or not
   */
  def optimize: Boolean

  /**
   * The batch size for the optimizer
   * @return
   */
  def batchSize: Int

  /**
   * Set the batch size for the optimizer
   * @param batchSize
   */
  def setBatchSize(batchSize: Int)

  /**
   * Pre preProcess a line before an iteration
   */
  def preProcessLine

  /**
   * After the step has been made, do an action
   * @param line
   **/
  def postStep(line: INDArray)

  /**
   * Based on the gradient and score
   * setup a search state
   * @param pair the gradient and score
   */
  def setupSearchState(pair: Pair[Gradient, Double])

  /**
   * Update the gradient according to the configuration such as adagrad, momentum, and sparsity
   * @param gradient the gradient to modify
   * @param model the model with the parameters to update
   * @param batchSize batchSize for update
   * @paramType paramType to update
   */
  def updateGradientAccordingToParams(gradient: Gradient, model: Model, batchSize: Int)

  /**
   * Check termination conditions
   * setup a search state
   * @param gradient layer gradients
   * @param iteration what iteration the optimizer is on
   */
  def checkTerminalConditions(gradient: INDArray, oldScore: Double, score: Double, iteration: Int): Boolean
}