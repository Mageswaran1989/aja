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

import org.deeplearning4j.berkeley.Pair
import org.nd4j.linalg.api.ndarray.INDArray
import java.util.ArrayList
import java.util.List
import java.util.Random

object InputSplit {
  def splitInputs(inputs: INDArray, outcomes: INDArray, train: List[Nothing], test: List[Nothing], split: Double) {
    val list: List[Nothing] = new ArrayList[E]
    {
      var i: Int = 0
      while (i < inputs.rows) {
        {
          list.add(new Nothing(inputs.getRow(i), outcomes.getRow(i)))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    splitInputs(list, train, test, split)
  }

  def splitInputs(pairs: List[Nothing], train: List[Nothing], test: List[Nothing], split: Double) {
    val rand: Random = new Random
    import scala.collection.JavaConversions._
    for (pair <- pairs) if (rand.nextDouble <= split) train.add(pair)
    else test.add(pair)
  }
}

class InputSplit {
  private def this() {
    this()
  }
}