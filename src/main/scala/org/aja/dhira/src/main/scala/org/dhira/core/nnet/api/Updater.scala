package org.dhira.core.nnet.api

import org.dhira.core.nnet.gradient.Gradient
import org.dhira.core.nnet.api.Layer
import org.nd4j.linalg.api.ndarray.INDArray
import java.io.Serializable

/**
 * Update the model
 *
 * @author Adam Gibson
 */
trait Updater extends Serializable with Cloneable {
  /**
   * Set the internal (historical) state view array for this updater
   *
   * @param layer      Layer that this updater belongs to
   * @param viewArray  View array
   * @param initialize Whether to initialize the array or not
   */
  def setStateViewArray(layer: Nothing, viewArray: INDArray, initialize: Boolean)

  /**
   * @return the view array for this updater
   */
  def getStateViewArray: INDArray

  /**
   * Calculate and return the state size for this updater (for the given layer).
   * How many parameters/values does this updater have?
   *
   * @param layer Layer that this updater belongs to
   * @return number of parameters/values in the updater state
   */
  def stateSizeForLayer(layer: Layer): Int

  /**
   * Updater: updates the model
   *
   * @param layer
   * @param gradient
   * @param iteration
   */
  def update(layer: Layer, gradient: Gradient, iteration: Int, miniBatchSize: Int)

  override def clone(): Updater
}