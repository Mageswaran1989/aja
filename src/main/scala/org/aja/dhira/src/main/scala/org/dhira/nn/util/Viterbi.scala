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

import org.apache.commons.math3.util.FastMath
import org.deeplearning4j.berkeley.Pair
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import java.io.Serializable

/**
 * Based on the impl from:
 * https://gist.github.com/rmcgibbo/3915977
 *
 */
class Viterbi extends Serializable {
  private var metaStability: Double = 0.9
  private var pCorrect: Double = 0.99
  private var possibleLabels: INDArray = null
  private var states: Int = 0
  private var logPCorrect: Double = .0
  private var logPIncorrect: Double = .0
  private var logMetaInstability: Double = Math.log(metaStability)
  private var logOfDiangnalTProb: Double = .0
  private var logStates: Double = .0

  /**
   * The possible outcomes for the chain.
   * This should be the labels in the form of the possible outcomes (1,2,3)
   * not the binarized label matrix
   * @param possibleLabels the possible labels of the markov chain
   */
  def this(possibleLabels: INDArray) {
    this()
    this.possibleLabels = possibleLabels
    this.states = possibleLabels.length
    this.logPCorrect = FastMath.log(pCorrect)
    this.logPIncorrect = FastMath.log(1 - pCorrect / states - 1)
    logOfDiangnalTProb = FastMath.log(1 - metaStability / states - 1)
    this.logStates = FastMath.log(states)
  }

  /**
   * Decodes the given labels, assuming its a binary label matrix
   * @param labels the labels as a binary label matrix
   * @return the decoded labels and the most likely outcome of the sequence
   */
  def decode(labels: INDArray): Nothing = {
    return decode(labels, true)
  }

  /**
   * Decodes a series of labels
   * @param labels the labels to decode
   * @param binaryLabelMatrix whether the label  is a binary label matrix
   * @return the most likely sequence and the sequence labels
   */
  def decode(labels: INDArray, binaryLabelMatrix: Boolean): Nothing = {
    val outcomeSequence: INDArray = if (labels.isColumnVector || labels.isRowVector || binaryLabelMatrix) toOutcomesFromBinaryLabelMatrix(labels) else labels
    val frames: Int = outcomeSequence.length
    val V: INDArray = Nd4j.ones(frames, states)
    val pointers: INDArray = Nd4j.zeros(frames, states)
    val assigned: INDArray = V.getRow(0)
    assigned.assign(logPCorrect - logStates)
    V.putRow(0, assigned)
    V.put(0, outcomeSequence.getDouble(0).toInt, logPCorrect - logStates)
    {
      var t: Int = 1
      while (t < frames) {
        {
          {
            var k: Int = 0
            while (k < states) {
              {
                val rowLogProduct: INDArray = rowOfLogTransitionMatrix(k).add(V.getRow(t - 1))
                val maxVal: Int = Nd4j.getBlasWrapper.iamax(rowLogProduct)
                val argMax: Double = rowLogProduct.max(Integer.MAX_VALUE).getDouble(0)
                V.put(t, k, argMax)
                val element: Int = outcomeSequence.getDouble(t).toInt
                if (k == element) V.put(t, k, logPCorrect + maxVal)
                else V.put(t, k, logPIncorrect + maxVal)
              }
              ({
                k += 1; k - 1
              })
            }
          }
        }
        ({
          t += 1; t - 1
        })
      }
    }
    val rectified: INDArray = Nd4j.zeros(frames)
    rectified.put(rectified.length - 1, V.getRow(frames - 1).max(Integer.MAX_VALUE))
    {
      var t: Int = rectified.length - 2
      while (t > 0) {
        {
          rectified.putScalar(t, pointers.getDouble(t + 1, rectified.getDouble(t + 1).toInt))
        }
        ({
          t -= 1; t + 1
        })
      }
    }
    return new Nothing(V.getRow(frames - 1).max(Integer.MAX_VALUE).getDouble(0), rectified)
  }

  private def rowOfLogTransitionMatrix(k: Int): INDArray = {
    val row: INDArray = Nd4j.ones(1, states).muli(logOfDiangnalTProb)
    row.putScalar(k, logMetaInstability)
    return row
  }

  private def toOutcomesFromBinaryLabelMatrix(outcomes: INDArray): INDArray = {
    val ret: INDArray = Nd4j.create(outcomes.rows, 1)
    {
      var i: Int = 0
      while (i < outcomes.rows) {
        ret.put(i, 0, Nd4j.getBlasWrapper.iamax(outcomes.getRow(i)))
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  def getMetaStability: Double = {
    return metaStability
  }

  def setMetaStability(metaStability: Double) {
    this.metaStability = metaStability
  }

  def getpCorrect: Double = {
    return pCorrect
  }

  def setpCorrect(pCorrect: Double) {
    this.pCorrect = pCorrect
  }

  def getPossibleLabels: INDArray = {
    return possibleLabels
  }

  def setPossibleLabels(possibleLabels: INDArray) {
    this.possibleLabels = possibleLabels
  }

  def getStates: Int = {
    return states
  }

  def setStates(states: Int) {
    this.states = states
  }

  def getLogPCorrect: Double = {
    return logPCorrect
  }

  def setLogPCorrect(logPCorrect: Double) {
    this.logPCorrect = logPCorrect
  }

  def getLogPIncorrect: Double = {
    return logPIncorrect
  }

  def setLogPIncorrect(logPIncorrect: Double) {
    this.logPIncorrect = logPIncorrect
  }

  def getLogMetaInstability: Double = {
    return logMetaInstability
  }

  def setLogMetaInstability(logMetaInstability: Double) {
    this.logMetaInstability = logMetaInstability
  }

  def getLogOfDiangnalTProb: Double = {
    return logOfDiangnalTProb
  }

  def setLogOfDiangnalTProb(logOfDiangnalTProb: Double) {
    this.logOfDiangnalTProb = logOfDiangnalTProb
  }

  def getLogStates: Double = {
    return logStates
  }

  def setLogStates(logStates: Double) {
    this.logStates = logStates
  }
}