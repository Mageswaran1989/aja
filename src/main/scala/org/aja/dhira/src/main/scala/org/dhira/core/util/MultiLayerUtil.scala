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

import org.dhira.core.nnet.MultiLayerNetwork
import org.dhira.core.nnet.params.DefaultParamInitializer
import org.nd4j.linalg.api.ndarray.INDArray
//import java.util.ArrayList
//import java.util.List

/**
 * Various cooccurrences for manipulating a multi layer network
 * @author
 */
object MultiLayerUtil {
  /**
   * Return the weight matrices for a multi layer network
   * @param network the network to get the weights for
   * @return the weight matrices for a given multi layer network
   */
  def weightMatrices(network: MultiLayerNetwork): List[INDArray] = {
    var ret: List[INDArray] = List() //TODO var
    for(i <- 0 until network.getLayers.length) {
      ret = (network.getLayers(i).getParam(DefaultParamInitializer.WEIGHT_KEY)) :: ret
    }

    ret
  }
}

class MultiLayerUtil {
  private def this() {
    this()
  }
}