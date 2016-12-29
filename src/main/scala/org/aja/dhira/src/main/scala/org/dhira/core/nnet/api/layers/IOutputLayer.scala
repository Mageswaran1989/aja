/*
 *  * Copyright 2016 Skymind,Inc.
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
 */
package org.dhira.core.nnet.api.layers

import org.dhira.core.nnet.api.Classifier
import org.dhira.core.nnet.api.Layer
import org.nd4j.linalg.api.ndarray.INDArray


/**
 * Interface for output layers (those that calculate gradients with respect to a labels array)
 */
trait IOutputLayer extends Layer with Classifier {
  /**
   * Set the labels array for this output layer
   *
   * @param labels Labels array to set
   */
  def setLabels(labels: INDArray)

  /**
   * Get the labels array previously set with {@link #setLabels(INDArray)}
   *
   * @return Labels array, or null if it has not been set
   */
  def getLabels: INDArray

  /**
   * Compute score after labels and input have been set.
   *
   * @param fullNetworkL1 L1 regularization term for the entire network
   * @param fullNetworkL2 L2 regularization term for the entire network
   * @param training      whether score should be calculated at train or test time (this affects things like application of
   *                      dropout, etc)
   * @return score (loss function)
   */
  def computeScore(fullNetworkL1: Double, fullNetworkL2: Double, training: Boolean): Double

  /**
   * Compute the score for each example individually, after labels and input have been set.
   *
   * @param fullNetworkL1 L1 regularization term for the entire network (or, 0.0 to not include regularization)
   * @param fullNetworkL2 L2 regularization term for the entire network (or, 0.0 to not include regularization)
   * @return A column INDArray of shape [numExamples,1], where entry i is the score of the ith example
   */
  def computeScoreForExamples(fullNetworkL1: Double, fullNetworkL2: Double): INDArray
}