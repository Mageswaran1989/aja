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
package org.deeplearning4j.util

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import java.util.ArrayList
import java.util.List

/**
 *
 * Moving window on a matrix (usually used for images)
 *
 * Given a:          This is a list of flattened arrays:
 * 1 1 1 1          1 1 2 2
 * 2 2 2 2 ---->    1 1 2 2
 * 3 3 3 3          3 3 4 4
 * 4 4 4 4          3 3 4 4
 *
 * @author Adam Gibson
 */
class MovingWindowMatrix {
  private var windowRowSize: Int = 28
  private var windowColumnSize: Int = 28
  private var toSlice: INDArray = null
  private var addRotate: Boolean = false

  /**
   *
   * @param toSlice matrix to slice
   * @param windowRowSize the number of rows in each window
   * @param windowColumnSize the number of columns in each window
   * @param addRotate whether to add the possible rotations of each moving window
   */
  def this(toSlice: INDArray, windowRowSize: Int, windowColumnSize: Int, addRotate: Boolean) {
    this()
    this.toSlice = toSlice
    this.windowRowSize = windowRowSize
    this.windowColumnSize = windowColumnSize
    this.addRotate = addRotate
  }

  /**
   * Same as calling new MovingWindowMatrix(toSlice,windowRowSize,windowColumnSize,false)
   * @param toSlice
   * @param windowRowSize
   * @param windowColumnSize
   */
  def this(toSlice: INDArray, windowRowSize: Int, windowColumnSize: Int) {
    this()
    `this`(toSlice, windowRowSize, windowColumnSize, false)
  }

  /**
   * Returns a list of non flattened moving window matrices
   * @return the list of matrices
   */
  def windows: List[INDArray] = {
    return windows(false)
  }

  /**
   * Moving window, capture a row x column moving window of
   * a given matrix
   * @param flattened whether the arrays should be flattened or not
   * @return the list of moving windows
   */
  def windows(flattened: Boolean): List[INDArray] = {
    val ret: List[INDArray] = new ArrayList[INDArray]
    var window: Int = 0
    {
      var i: Int = 0
      while (i < toSlice.length) {
        {
          if (window >= toSlice.length) break //todo: break is not supported
        val w: Array[Double] = new Array[Double](this.windowRowSize * this.windowColumnSize)
          {
            var count: Int = 0
            while (count < this.windowRowSize * this.windowColumnSize) {
              {
                w(count) = toSlice.getDouble(count + window)
              }
              ({
                count += 1; count - 1
              })
            }
          }
          var add: INDArray = Nd4j.create(w)
          if (flattened) add = add.ravel
          else add = add.reshape(windowRowSize, windowColumnSize)
          if (addRotate) {
            val currRotation: INDArray = add.dup
            {
              var rotation: Int = 0
              while (rotation < 3) {
                {
                  Nd4j.rot90(currRotation)
                  ret.add(currRotation.dup)
                }
                ({
                  rotation += 1; rotation - 1
                })
              }
            }
          }
          window += this.windowRowSize * this.windowColumnSize
          ret.add(add)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }
}