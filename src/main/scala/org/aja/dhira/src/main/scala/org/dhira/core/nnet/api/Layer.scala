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
import org.dhira.core.nnet.gradient.Gradient
import org.dhira.core.optimize.api.IterationListener
import org.nd4j.linalg.api.ndarray.INDArray
import java.io.Serializable
import java.util.Collection

/**
 * Interface for a layer of a neural network.
 * This has an activation function, an input and output size,
 * weights, and a bias
 *
 * @author Adam Gibson
 */
object Layer {
  object LayerType extends Enumeration {
    type LayerType = Value
    val FEED_FORWARD, RECURRENT, CONVOLUTIONAL, SUBSAMPLING, RECURSIVE, MULTILAYER, NORMALIZATION = Value
  }

  object TrainingMode extends Enumeration {
    type TrainingMode = Value
    val TRAIN, TEST = Value
  }
}

trait Layer extends Serializable with Cloneable with Model {

  import  org.dhira.core.nnet.api.Layer.LayerType._
  import  org.dhira.core.nnet.api.Layer.TrainingMode._

  /** Calculate the l2 regularization term<br>
    * 0.0 if regularization is not used. Or 0.5 * l2Coeff * l2Magnitude otherwise.<br>
    * Note that this does not divide by mini-batch size
    * @return the l2 regularization term for this layer.
    */
  def calcL2(): Double

  /** Calculate the l1 regularization term<br>
    * 0.0 if regularization is not used. Or l1Coeff * l1Magnitude otherwise.<br>
    * Note that this does not divide by mini-batch size
    * @return the l1 regularization term for this layer.
    */
  def calcL1(): Double

  /**
   * Returns the layer type
   * @return
   */
  def layerType(): LayerType

  /**
   * Calculate error with respect to the
   * current layer.
   *
   * This gradient will contain the error signal
   * @param input the gradient for the forward layer
   *              If this is the final layer, it will start
   *              with the error from the output.
   *              This is on the user to initialize.
   * @return the gradient wrt the parameters
   *         on the current layer
   */
  def error(input: INDArray): Nothing

  /**
   * Take the derivative of the given input
   * based on the activation
   * @param input the input to take the derivative of
   * @return the derivative of the action
   */
  def derivativeActivation(input: INDArray): INDArray

  /**
   * Calculate the gradient
   * @param layerError the layer error
   * @param indArray
   * @return the gradient
   */
  def calcGradient(layerError: Nothing, indArray: INDArray): Nothing

  /** Calculate the gradient relative to the error in the next layer
    * @param epsilon w^(L+1)*delta^(L+1). Or, equiv: dC/da, i.e., (dC/dz)*(dz/da) = dC/da, where C
    *                is cost function a=sigma(z) is activation.
    * @return Pair<Gradient,INDArray> where Gradient is gradient for this layer, INDArray is epsilon needed by next
    *         layer, but before element-wise multiply by sigmaPrime(z). So for standard feed-forward layer, if this layer is
    *         L, then return.getSecond() == (w^(L)*(delta^(L))^T)^T
    */
  def backpropGradient(epsilon: INDArray): Nothing

  /**
   * Parameter averaging
   * @param layer the layer to merge
   * @param batchSize the batch size to merge on
   */
  def merge(layer: Layer, batchSize: Int)

  /**
   * Calculate the mean representation
   * for the activation for this layer
   * @return the activation mean for this layer
   */
  def activationMean: INDArray

  /**
   * Raw activations
   * @param x the input to transform
   * @return the raw activation
   *         for this layer
   */
  def preOutput(x: INDArray): INDArray

  /**
   * Raw activations
   * @param x the input to transform
   * @return the raw activation
   *         for this layer
   */
  def preOutput(x: INDArray, training: TrainingMode): INDArray

  /**
   * Trigger an activation with the last specified input
   * @param training  training or test mode
   * @return the activation of the last specified input
   */
  def activate(training: TrainingMode): INDArray

  /**
   * Initialize the layer with the given input
   * and return the activation for this layer
   * given this input
   * @param input the input to use
   * @param training  train or test mode
   * @return
   */
  def activate(input: INDArray, training: TrainingMode): INDArray

  /**
   * Raw activations
   * @param x the input to transform
   * @return the raw activation
   *         for this layer
   */
  def preOutput(x: INDArray, training: Boolean): INDArray

  /**
   * Trigger an activation with the last specified input
   * @param training  training or test mode
   * @return the activation of the last specified input
   */
  def activate(training: Boolean): INDArray

  /**
   * Initialize the layer with the given input
   * and return the activation for this layer
   * given this input
   * @param input the input to use
   * @param training  train or test mode
   * @return
   */
  def activate(input: INDArray, training: Boolean): INDArray

  /**
   * Trigger an activation with the last specified input
   * @return the activation of the last specified input
   */
  def activate: INDArray

  /**
   * Initialize the layer with the given input
   * and return the activation for this layer
   * given this input
   * @param input the input to use
   * @return
   */
  def activate(input: INDArray): INDArray

  /**
   * Return a transposed copy of the weights/bias
   * (this means reverse the number of inputs and outputs on the weights)
   *
   * @return the transposed layer
   */
  def transpose: Layer

  /**
   * Clone the layer
   * @return
   */
  override def clone: Layer

  /**
   * Get the iteration listeners for this layer.
   */
  def getListeners: Collection[IterationListener]

  /**
   * Set the iteration listeners for this layer.
   */
  def setListeners(listeners: IterationListener*)

  /**
   * Set the iteration listeners for this layer.
   */
  def setListeners(listeners: Collection[IterationListener])

  /**
   * Set the layer index.
   */
  def setIndex(index: Int)

  /**
   * Get the layer index.
   */
  def getIndex(): Int

  /**
   * Get the layer input.
   */
  def setInput(input: INDArray)

  /** Set current/last input mini-batch size.<br>
    * Used for score and gradient calculations. Mini batch size may be different from
    * getInput().size(0) due to reshaping operations - for example, when using RNNs with
    * DenseLayer and OutputLayer. Called automatically during forward pass.
    */
  def setInputMiniBatchSize(size: Int)

  /** Get current/last input mini-batch size, as set by setInputMiniBatchSize(int)
    * @see Layer#setInputMiniBatchSize(int)
    */
  def getInputMiniBatchSize(): Int

  def setMaskArray(maskArray: INDArray)
}