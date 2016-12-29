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

/**
 * @author Adam Gibson
 */
object SummaryStatistics {
  def summaryStatsString(d: INDArray): String = {
    return new SummaryStatistics(d.mean(Integer.MAX_VALUE), d.sum(Integer.MAX_VALUE), d.min(Integer.MAX_VALUE), d.max(Integer.MAX_VALUE)).toString
  }

  def summaryStats(d: INDArray): SummaryStatistics = {
    return new SummaryStatistics(d.mean(Integer.MAX_VALUE), d.sum(Integer.MAX_VALUE), d.min(Integer.MAX_VALUE), d.max(Integer.MAX_VALUE))
  }
}

class SummaryStatistics {
  private var mean: Double = .0
  private var sum: Double = .0
  private var min: Double = .0
  private var max: Double = .0

//  private def this() {
//    this()
//  }

  private def this(mean: INDArray, sum: INDArray, min: INDArray, max: INDArray) {
    this()
    this.mean = mean.element.asInstanceOf[Double]
    this.sum = sum.element.asInstanceOf[Double]
    this.min = min.element.asInstanceOf[Double]
    this.max = max.element.asInstanceOf[Double]
  }

  private def this(mean: Double, sum: Double, min: Double, max: Double) {
    this()
    this.mean = mean
    this.sum = sum
    this.min = min
    this.max = max
  }

  def getMean: Double = {
    return mean
  }

  def setMean(mean: Double) {
    this.mean = mean
  }

  def getSum: Double = {
    return sum
  }

  def setSum(sum: Double) {
    this.sum = sum
  }

  def getMin: Double = {
    return min
  }

  def setMin(min: Double) {
    this.min = min
  }

  def getMax: Double = {
    return max
  }

  def setMax(max: Double) {
    this.max = max
  }

  override def toString: String = {
    return "SummaryStatistics{" + "mean=" + mean + ", sum=" + sum + ", min=" + min + ", max=" + max + '}'
  }
}