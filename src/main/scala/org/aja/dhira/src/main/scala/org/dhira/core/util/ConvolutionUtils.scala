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
package org.dhira.core.util

import org.dhira.core.nnet.conf.NeuralNetConfiguration
import org.dhira.core.nnet.layers.ConvolutionLayer

/**
 * Convolutional shape utilities
 *
 * @author Mageswaran Dhandapani
 * @since 10-Sep-2016
 */
class ConvolutionUtils {
  private def this() {
    this()
  }

  /**
   * Get the height and width
   * from the configuration
   * @param conf the configuration to get height and width from
   * @return the configuration to get height and width from
   */
  def getHeightAndWidth(conf: NeuralNetConfiguration): Array[Int] = {
    getHeightAndWidth((conf.getLayer.asInstanceOf[ConvolutionLayer]).getKernelSize)
  }

  /**
   * @param conf the configuration to get
   *             the number of kernels from
   * @return the number of kernels/filters to apply
   */
  def numFeatureMap(conf: NeuralNetConfiguration): Int = {
    (conf.getLayer.asInstanceOf[ConvolutionLayer]).getNOut
  }

  /**
   * Get the height and width
   * for an image
   * @param shape the shape of the image
   * @return the height and width for the image
   */
  def getHeightAndWidth(shape: Array[Int]): Array[Int] = {
    if (shape.length < 2) throw new IllegalArgumentException("No width and height able to be found: array must be at least length 2")
    return Array[Int](shape(shape.length - 1), shape(shape.length - 2))
  }

  /**
   * Returns the number of
   * feature maps for a given shape (must be at least 3 dimensions
   * @param shape the shape to get the
   *              number of feature maps for
   * @return the number of feature maps
   *         for a particular shape
   */
  def numChannels(shape: Array[Int]): Int = {
    if (shape.length < 4) return 1
    return shape(1)
  }
}
