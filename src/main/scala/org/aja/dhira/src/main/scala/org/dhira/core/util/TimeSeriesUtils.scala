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

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.INDArrayIndex
import org.nd4j.linalg.indexing.NDArrayIndex
import org.nd4s.Implicits._

/**
 * Basic time series utils
 * @author Adam Gibson
 */
object TimeSeriesUtils {
  /**
   * Calculate a moving average given the length
   * @param toAvg the array to average
   * @param n the length of the moving window
   * @return the moving averages for each row
   */
  def movingAverage(toAvg: INDArray, n: Int): INDArray = {
    val ret: INDArray = Nd4j.cumsum(toAvg)
    val ends: Array[INDArrayIndex] = Array[INDArrayIndex](NDArrayIndex.interval(n, toAvg.columns))
    val begins: Array[INDArrayIndex] = Array[INDArrayIndex](NDArrayIndex.interval(0, toAvg.columns - n, false))
    val nMinusOne: Array[INDArrayIndex] = Array[INDArrayIndex](NDArrayIndex.interval(n - 1, toAvg.columns))
    ret.put(ends, ret.get(ends).sub(ret.get(begins)))
    return ret.get(nMinusOne).divi(n)
  }

  /**
   * Reshape time series mask arrays. This should match the assumptions (f order, etc) in RnnOutputLayer
   * @param timeSeriesMask    Mask array to reshape to a column vector
   * @return                  Mask array as a column vector
   */
  def reshapeTimeSeriesMaskToVector(timeSeriesMask: INDArray): INDArray = {
    if (timeSeriesMask.rank != 2) throw new IllegalArgumentException("Cannot reshape mask: rank is not 2")
    if (timeSeriesMask.ordering != 'f') timeSeriesMask = timeSeriesMask.dup('f')
    return timeSeriesMask.reshape('f', Array[Int](timeSeriesMask.length, 1))
  }
}

class TimeSeriesUtils {
  private def this() {
    this()
  }
}