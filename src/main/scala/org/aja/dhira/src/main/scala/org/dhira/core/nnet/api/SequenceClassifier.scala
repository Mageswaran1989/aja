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

import org.nd4j.linalg.api.ndarray.INDArray

/**
 * Created by agibsonccc on 8/27/14.
 */
@deprecated trait SequenceClassifier {
  def classifier: Nothing

  def mostLikelyInSequence(examples: INDArray): Int

  def predict(examples: INDArray): INDArray

  def fit(features: INDArray, labels: INDArray)
}