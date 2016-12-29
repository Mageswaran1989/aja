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
package org.dhira.core.nnet.weights

import org.apache.commons.math3.util.FastMath
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.rng.distribution.Distribution
import org.nd4j.linalg.factory.Nd4j

/**
 * Weight initialization utility
 *
 * @author Adam Gibson
 */
object WeightInitUtil {
  /**
   * Default order for the arrays created by WeightInitUtil.
   */
  val DEFAULT_WEIGHT_INIT_ORDER: Char = 'f'

  /**
   * Generate a random matrix with respect to the number of inputs and outputs.
   * This is a bound uniform distribution with the specified minimum and maximum
   *
   * @param shape the shape of the matrix
   * @param nIn   the number of inputs
   * @param nOut  the number of outputs
   * @return { @link INDArray}
   */
  def uniformBasedOnInAndOut(shape: Array[Int], nIn: Int, nOut: Int): INDArray = {
    val min: Double = -4.0 * Math.sqrt(6.0 / (nOut + nIn).toDouble)
    val max: Double = 4.0 * Math.sqrt(6.0 / (nOut + nIn).toDouble)
    return Nd4j.rand(shape, Nd4j.getDistributions.createUniform(min, max))
  }

  def initWeights(shape: Array[Int], min: Float, max: Float): INDArray = {
    return Nd4j.rand(shape, min, max, Nd4j.getRandom)
  }

  /**
   * Initializes a matrix with the given weight initialization scheme.
   * Note: Defaults to fortran ('f') order arrays for the weights. Use {@link #initWeights(int[], WeightInit, Distribution, char, INDArray)}
   * to control this
   *
   * @param shape      the shape of the matrix
   * @param initScheme the scheme to use
   * @return a matrix of the specified dimensions with the specified
   *         distribution based on the initialization scheme
   */
  def initWeights(shape: Array[Int], initScheme: Nothing, dist: Distribution, paramView: INDArray): INDArray = {
    return initWeights(shape, initScheme, dist, DEFAULT_WEIGHT_INIT_ORDER, paramView)
  }

  def initWeights(shape: Array[Int], initScheme: Nothing, dist: Distribution, order: Char, paramView: INDArray): INDArray = {
    var ret: INDArray = null
    initScheme match {
      case DISTRIBUTION =>
        ret = dist.sample(shape)
        break //todo: break is not supported
      case NORMALIZED =>
        ret = Nd4j.rand(order, shape)
        ret.subi(0.5).divi(shape(0))
        break //todo: break is not supported
      case RELU =>
        ret = Nd4j.randn(order, shape).muli(FastMath.sqrt(2.0 / shape(0)))
        break //todo: break is not supported
      case SIZE =>
        ret = uniformBasedOnInAndOut(shape, shape(0), shape(1))
        break //todo: break is not supported
      case UNIFORM =>
        val a: Double = 1 / shape(0).toDouble
        ret = Nd4j.rand(order, shape).muli(2 * a).subi(a)
        break //todo: break is not supported
      case VI =>
        ret = Nd4j.rand(order, shape)
        var len: Int = 0
        for (aShape <- shape) {
          len += aShape
        }
        val r: Double = Math.sqrt(6) / Math.sqrt(len + 1)
        ret.muli(2 * r).subi(r)
        break //todo: break is not supported
      case XAVIER =>
        ret = Nd4j.randn(order, shape).divi(FastMath.sqrt(shape(0) + shape(1)))
        break //todo: break is not supported
      case ZERO =>
        ret = Nd4j.create(shape, order)
        break //todo: break is not supported
      case _ =>
        throw new IllegalStateException("Illegal weight init value: " + initScheme)
    }
    val flat: INDArray = Nd4j.toFlattened(order, ret)
    if (flat.length != paramView.length) throw new RuntimeException("ParamView length does not match initialized weights length")
    paramView.assign(flat)
    return paramView.reshape(order, shape)
  }

  /**
   * Initializes a matrix with the given weight initialization scheme
   *
   * @param nIn        the number of rows in the matrix
   * @param nOut       the number of columns in the matrix
   * @param initScheme the scheme to use
   * @return a matrix of the specified dimensions with the specified
   *         distribution based on the initialization scheme
   */
  def initWeights(nIn: Int, nOut: Int, initScheme: Nothing, dist: Distribution, paramView: INDArray): INDArray = {
    return initWeights(Array[Int](nIn, nOut), initScheme, dist, paramView)
  }

  /**
   * Reshape the parameters view, without modifying the paramsView array values.
   * Same reshaping mechanism as {@link #initWeights(int[], WeightInit, Distribution, INDArray)}
   *
   * @param shape      Shape to reshape
   * @param paramsView Parameters array view
   */
  def reshapeWeights(shape: Array[Int], paramsView: INDArray): INDArray = {
    return reshapeWeights(shape, paramsView, DEFAULT_WEIGHT_INIT_ORDER)
  }

  /**
   * Reshape the parameters view, without modifying the paramsView array values.
   * Same reshaping mechanism as {@link #initWeights(int[], WeightInit, Distribution, char, INDArray)}
   *
   * @param shape           Shape to reshape
   * @param paramsView      Parameters array view
   * @param flatteningOrder Order in which parameters are flattened/reshaped
   */
  def reshapeWeights(shape: Array[Int], paramsView: INDArray, flatteningOrder: Char): INDArray = {
    return paramsView.reshape(flatteningOrder, shape)
  }
}

class WeightInitUtil {
  private def this() {
    this()
  }
}