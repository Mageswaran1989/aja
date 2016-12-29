/*
 *
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
 *
 */
package org.dhira.core.nnet.layers.convolution

import org.dhira.core.containers.Pair
import org.dhira.core.nnet.gradient.Gradient
import org.nd4j.linalg.api.ndarray.INDArray

/**
 * Helper for the convolution layer.
 *
 * @author saudet
 */
trait ConvolutionHelper {
  def backpropGradient(input: INDArray, weights: INDArray,
                       delta: INDArray, kernel: Array[Int],
                       strides: Array[Int], pad: Array[Int],
                       biasGradView: INDArray, weightGradView: INDArray,
                       afn: String): Pair[Gradient, Nothing]

  def preOutput(input: INDArray, weights: INDArray, bias: INDArray,
                kernel: Array[Int], strides: Array[Int], pad: Array[Int]): INDArray

  def activate(z: INDArray, afn: String): INDArray
}