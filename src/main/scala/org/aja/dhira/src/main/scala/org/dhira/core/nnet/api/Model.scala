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
package org.dhira.core.nnet.api

import org.dhira.core.containers.Pair
import org.dhira.core.nnet.conf.NeuralNetConfiguration
import org.dhira.core.nnet.gradient.Gradient
import  org.dhira.core.optimize.api.ConvexOptimizer
import org.nd4j.linalg.api.ndarray.INDArray
import java.util.Map

/**
 * A Model is meant for predicting something from data.
 * Note that this is not like supervised learning where
 * there are labels attached to the examples.
 *
 */
trait Model {
  /**
   * All models have a fit method
   */
  def fit()

  /**
   * Update layer weights and biases with gradient change
   */
  def update(gradient: Gradient)

  /**
   * Perform one update  applying the gradient
   * @param gradient the gradient to apply
   */
  def update(gradient: INDArray, paramType: String)

  /**
   * The score for the model
   * @return the score for the model
   */
  def score(): Double

  /**
   * Update the score
   */
  def computeGradientAndScore()

  /**
   * Sets a rolling tally for the score. This is useful for mini batch learning when
   * you are accumulating error across a dataset.
   * @param accum the amount to accum
   */
  def accumulateScore(accum: Double)

  /**
   * Parameters of the model (if any)
   * @return the parameters of the model
   */
  def params(): INDArray

  /**
   * the number of parameters for the model
   * @return the number of parameters for the model
   *
   */
  def numParams(): Int

  /**
   * the number of parameters for the model
   * @return the number of parameters for the model
   *
   */
  def numParams(backwards: Boolean): Int

  /**
   * Set the parameters for this model.
   * This expects a linear ndarray which then be unpacked internally
   * relative to the expected ordering of the model
   * @param params the parameters for the model
   */
  def setParams(params: INDArray)

  /**
   * Set the initial parameters array as a view of the full (backprop) network parameters
   * NOTE: this is intended to be used internally in MultiLayerNetwork and ComputationGraph, not by users.
   * @param params a 1 x nParams row vector that is a view of the larger (MLN/CG) parameters array
   */
  def setParamsViewArray(params: INDArray)

  /**
   * Set the gradients array as a view of the full (backprop) network parameters
   * NOTE: this is intended to be used internally in MultiLayerNetwork and ComputationGraph, not by users.
   * @param gradients a 1 x nParams row vector that is a view of the larger (MLN/CG) gradients array
   */
  def setBackpropGradientsViewArray(gradients: INDArray)

  /**
   * Update learningRate using for this model.
   * Use the learningRateScoreBasedDecay to adapt the score
   * if the Eps termination condition is met
   */
  def applyLearningRateScoreDecay()

  /**
   * Fit the model to the given data
   * @param data the data to fit the model to
   */
  def fit(data: INDArray)

  /**
   * Run one iteration
   * @param input the input to iterate on
   */
  def iterate(input: INDArray)

  /**
   * Calculate a gradient
   * @return the gradient for this model
   */
  def gradient(): Gradient

  /**
   * Get the gradient and score
   * @return the gradient and score
   */
  def gradientAndScore(): Pair[Gradient, Double]

  /**
   * The current inputs batch size
   * @return the current inputs batch size
   */
  def batchSize(): Int

  /**
   * The configuration for the neural network
   * @return the configuration for the neural network
   */
  def conf(): NeuralNetConfiguration

  /**
   * Setter for the configuration
   * @param conf
   */
  def setConf(conf: NeuralNetConfiguration)

  /**
   * The input/feature matrix for the model
   * @return the input/feature matrix for the model
   */
  def input(): INDArray

  /**
   * Validate the input
   */
  def validateInput()

  /**
   * Returns this models optimizer
   * @return this models optimizer
   */
  def getOptimizer(): ConvexOptimizer

  /**
   * Get the parameter
   * @param param the key of the parameter
   * @return the parameter vector/matrix with that particular key
   */
  def getParam(param: String): INDArray

  /**
   * Initialize the parameters
   */
  def initParams()

  /**
   * The param table
   * @return
   */
  def paramTable(): Map[String, INDArray]

  /**
   * Setter for the param table
   * @param paramTable
   */
  def setParamTable(paramTable: Map[String, INDArray])

  /**
   * Set the parameter with a new ndarray
   * @param key the key to se t
   * @param value the new ndarray
   */
  def setParam(key: String, value: INDArray)

  /**
   * Clear input
   */
  def clear()
}