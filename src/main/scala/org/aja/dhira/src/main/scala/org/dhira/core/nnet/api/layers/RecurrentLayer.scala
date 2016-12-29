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

import org.dhira.core.containers.Pair
import org.dhira.core.nnet.api.Layer
import org.dhira.core.nnet.gradient.Gradient
import org.nd4j.linalg.api.ndarray.INDArray

/**
 * Created by Alex on 28/08/2016.
 */
trait RecurrentLayer extends Layer {
  /**
   * Do one or more time steps using the previous time step state stored in stateMap.<br>
   * Can be used to efficiently do forward pass one or n-steps at a time (instead of doing
   * forward pass always from t=0)<br>
   * If stateMap is empty, default initialization (usually zeros) is used<br>
   * Implementations also update stateMap at the end of this method
   *
   * @param input Input to this layer
   * @return activations
   */
  def rnnTimeStep(input: INDArray): INDArray

  /**
   * Returns a shallow copy of the RNN stateMap (that contains the stored history for use in methods such
   * as rnnTimeStep
   */
  def rnnGetPreviousState: Map[String, INDArray]

  /**
   * Set the stateMap (stored history). Values set using this method will be used in next call to rnnTimeStep()
   */
  def rnnSetPreviousState(stateMap: Map[String, INDArray])

  /**
   * Reset/clear the stateMap for rnnTimeStep() and tBpttStateMap for rnnActivateUsingStoredState()
   */
  def rnnClearPreviousState

  /**
   * Similar to rnnTimeStep, this method is used for activations using the state
   * stored in the stateMap as the initialization. However, unlike rnnTimeStep this
   * method does not alter the stateMap; therefore, unlike rnnTimeStep, multiple calls to
   * this method (with identical input) will:<br>
   * (a) result in the same output<br>
   * (b) leave the state maps (both stateMap and tBpttStateMap) in an identical state
   *
   * @param input             Layer input
   * @param training          if true: training. Otherwise: test
   * @param storeLastForTBPTT If true: store the final state in tBpttStateMap for use in truncated BPTT training
   * @return Layer activations
   */
  def rnnActivateUsingStoredState(input: INDArray, training: Boolean, storeLastForTBPTT: Boolean): INDArray

  /**
   * Get the RNN truncated backpropagations through time (TBPTT) state for the recurrent layer.
   * The TBPTT state is used to store intermediate activations/state between updating parameters when doing
   * TBPTT learning
   *
   * @return State for the RNN layer
   */
  def rnnGetTBPTTState: Map[String, INDArray]

  /**
   * Set the RNN truncated backpropagations through time (TBPTT) state for the recurrent layer.
   * The TBPTT state is used to store intermediate activations/state between updating parameters when doing
   * TBPTT learning
   *
   * @param state TBPTT state to set
   */
  def rnnSetTBPTTState(state: Map[String, INDArray])

  /**
   * Truncated BPTT equivalent of Layer.backpropGradient().
   * Primary difference here is that forward pass in the context of BPTT is that we do
   * forward pass using stored state for truncated BPTT vs. from zero initialization
   * for standard BPTT.
   */
  def tbpttBackpropGradient(epsilon: INDArray, tbpttBackLength: Int): Pair[Gradient, Nothing]
}