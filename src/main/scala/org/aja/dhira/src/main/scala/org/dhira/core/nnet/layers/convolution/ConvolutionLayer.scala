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
package org.dhira.core.nnet.layers.convolution

import org.dhira.core.containers.Pair
import org.dhira.core.nnet.api.Layer
import org.dhira.core.nnet.conf.NeuralNetConfiguration
import org.dhira.core.nnet.gradient.DefaultGradient
import org.dhira.core.nnet.gradient.Gradient
import org.dhira.core.nnet.layers.BaseLayer
import org.deeplearning4j.nn.params.ConvolutionParamInitializer
import org.deeplearning4j.util.Dropout
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.shape.Shape
import org.nd4j.linalg.convolution.Convolution
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Convolution layer
 *
 * @author Adam Gibson (original impl), Alex Black (current version)
 */
object ConvolutionLayer {
  protected val log: Logger = LoggerFactory.getLogger(classOf[ConvolutionLayer])
}

class ConvolutionLayer extends BaseLayer[ConvolutionLayer] {
  private[convolution] var helper: ConvolutionHelper = null

  def this(conf: NeuralNetConfiguration) {
    this()
    `super`(conf)
    initializeHelper
  }

  def this(conf: Nothing, input: INDArray) {
    this()
    `super`(conf, input)
    initializeHelper
  }

  private[convolution] def initializeHelper {
    try {
      helper = Class.forName("org.deeplearning4j.nn.layers.convolution.CudnnConvolutionHelper").asSubclass(classOf[Nothing]).newInstance
      ConvolutionLayer.log.debug("CudnnConvolutionHelper successfully loaded")
    }
    catch {
      case t: Throwable => {
        if (!(t.isInstanceOf[ClassNotFoundException])) {
          ConvolutionLayer.log.warn("Could not load CudnnConvolutionHelper", t)
        }
      }
    }
  }

  override def calcL2: Double = {
    if (!conf.isUseRegularization || conf.getLayer.getL2 <= 0.0) return 0.0
    val l2Norm: Double = getParam(ConvolutionParamInitializer.WEIGHT_KEY).norm2Number.doubleValue
    return 0.5 * conf.getLayer.getL2 * l2Norm * l2Norm
  }

  override def calcL1: Double = {
    if (!conf.isUseRegularization || conf.getLayer.getL1 <= 0.0) return 0.0
    return conf.getLayer.getL1 * getParam(ConvolutionParamInitializer.WEIGHT_KEY).norm1Number.doubleValue
  }

  override def `type`: Nothing = {
    return Type.CONVOLUTIONAL
  }

  override def backpropGradient(epsilon: INDArray): Nothing = {
    val weights: INDArray = getParam(ConvolutionParamInitializer.WEIGHT_KEY)
    val miniBatch: Int = input.size(0)
    val inH: Int = input.size(2)
    val inW: Int = input.size(3)
    val outDepth: Int = weights.size(0)
    val inDepth: Int = weights.size(1)
    val kH: Int = weights.size(2)
    val kW: Int = weights.size(3)
    val kernel: Array[Int] = layerConf.getKernelSize
    val strides: Array[Int] = layerConf.getStride
    val pad: Array[Int] = layerConf.getPadding
    val outH: Int = Convolution.outSize(inH, kernel(0), strides(0), pad(0), false)
    val outW: Int = Convolution.outSize(inW, kernel(1), strides(1), pad(1), false)
    val biasGradView: INDArray = gradientViews.get(ConvolutionParamInitializer.BIAS_KEY)
    val weightGradView: INDArray = gradientViews.get(ConvolutionParamInitializer.WEIGHT_KEY)
    val weightGradView2df: INDArray = Shape.newShapeNoCopy(weightGradView, Array[Int](outDepth, inDepth * kH * kW), false).transpose
    var delta: INDArray = null
    val afn: String = conf.getLayer.getActivationFunction
    if ("identity" == afn) {
      delta = epsilon
    }
    else {
      val sigmaPrimeZ: INDArray = preOutput(true)
      Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(afn, sigmaPrimeZ, conf.getExtraArgs).derivative)
      delta = sigmaPrimeZ.muli(epsilon)
    }
    if (helper != null) {
      val ret: Nothing = helper.backpropGradient(input, weights, delta, kernel, strides, pad, biasGradView, weightGradView, afn)
      if (ret != null) {
        return ret
      }
    }
    delta = delta.permute(1, 0, 2, 3)
    val delta2d: INDArray = delta.reshape('c', Array[Int](outDepth, miniBatch * outH * outW))
    val col: INDArray = Nd4j.createUninitialized(Array[Int](miniBatch, outH, outW, inDepth, kH, kW), 'c')
    val col2: INDArray = col.permute(0, 3, 4, 5, 1, 2)
    Convolution.im2col(input, kH, kW, strides(0), strides(1), pad(0), pad(1), false, col2)
    val im2col2d: INDArray = col.reshape('c', miniBatch * outH * outW, inDepth * kH * kW)
    Nd4j.gemm(im2col2d, delta2d, weightGradView2df, true, true, 1.0, 0.0)
    val wPermuted: INDArray = weights.permute(3, 2, 1, 0)
    val w2d: INDArray = wPermuted.reshape('f', inDepth * kH * kW, outDepth)
    val epsNext2d: INDArray = w2d.mmul(delta2d)
    var eps6d: INDArray = Shape.newShapeNoCopy(epsNext2d, Array[Int](kW, kH, inDepth, outW, outH, miniBatch), true)
    eps6d = eps6d.permute(5, 2, 1, 0, 4, 3)
    val epsNextOrig: INDArray = Nd4j.create(Array[Int](inDepth, miniBatch, inH, inW), 'c')
    val epsNext: INDArray = epsNextOrig.permute(1, 0, 2, 3)
    Convolution.col2im(eps6d, epsNext, strides(0), strides(1), pad(0), pad(1), inH, inW)
    val retGradient: Gradient = new DefaultGradient
    val biasGradTemp: INDArray = delta2d.sum(1)
    biasGradView.assign(biasGradTemp)
    retGradient.setGradientFor(ConvolutionParamInitializer.BIAS_KEY, biasGradView)
    retGradient.setGradientFor(ConvolutionParamInitializer.WEIGHT_KEY, weightGradView, 'c')
    return new Nothing(retGradient, epsNext)
  }

  override def preOutput(training: Boolean): INDArray = {
    var weights: INDArray = getParam(ConvolutionParamInitializer.WEIGHT_KEY)
    val bias: INDArray = getParam(ConvolutionParamInitializer.BIAS_KEY)
    if (conf.isUseDropConnect && training && conf.getLayer.getDropOut > 0) {
      weights = Dropout.applyDropConnect(this, ConvolutionParamInitializer.WEIGHT_KEY)
    }
    val miniBatch: Int = input.size(0)
    val inH: Int = input.size(2)
    val inW: Int = input.size(3)
    val outDepth: Int = weights.size(0)
    val inDepth: Int = weights.size(1)
    val kH: Int = weights.size(2)
    val kW: Int = weights.size(3)
    val kernel: Array[Int] = layerConf.getKernelSize
    val strides: Array[Int] = layerConf.getStride
    val pad: Array[Int] = layerConf.getPadding
    val outH: Int = Convolution.outSize(inH, kernel(0), strides(0), pad(0), false)
    val outW: Int = Convolution.outSize(inW, kernel(1), strides(1), pad(1), false)
    if (helper != null) {
      val ret: INDArray = helper.preOutput(input, weights, bias, kernel, strides, pad)
      if (ret != null) {
        return ret
      }
    }
    val col: INDArray = Nd4j.createUninitialized(Array[Int](miniBatch, outH, outW, inDepth, kH, kW), 'c')
    val col2: INDArray = col.permute(0, 3, 4, 5, 1, 2)
    Convolution.im2col(input, kH, kW, strides(0), strides(1), pad(0), pad(1), false, col2)
    val reshapedCol: INDArray = Shape.newShapeNoCopy(col, Array[Int](miniBatch * outH * outW, inDepth * kH * kW), false)
    val permutedW: INDArray = weights.permute(3, 2, 1, 0)
    val reshapedW: INDArray = permutedW.reshape('f', kW * kH * inDepth, outDepth)
    var z: INDArray = reshapedCol.mmul(reshapedW)
    z.addiRowVector(bias)
    z = Shape.newShapeNoCopy(z, Array[Int](outW, outH, miniBatch, outDepth), true)
    return z.permute(2, 3, 1, 0)
  }

  override def activate(training: Boolean): INDArray = {
    if (input == null) throw new IllegalArgumentException("No null input allowed")
    applyDropOutIfNecessary(training)
    val z: INDArray = preOutput(training)
    val afn: String = conf.getLayer.getActivationFunction
    if ("identity" == afn) {
      return z
    }
    if (helper != null) {
      val ret: INDArray = helper.activate(z, conf.getLayer.getActivationFunction)
      if (ret != null) {
        return ret
      }
    }
    val activation: INDArray = Nd4j.getExecutioner.execAndReturn(Nd4j.getOpFactory.createTransform(afn, z))
    return activation
  }

  override def transpose: Nothing = {
    throw new UnsupportedOperationException("Not yet implemented")
  }

  override def calcGradient(layerError: Gradient, indArray: INDArray): Gradient = {
    throw new UnsupportedOperationException("Not yet implemented")
  }

  override def fit(input: INDArray) {
  }

  override def merge(layer: Nothing, batchSize: Int) {
    throw new UnsupportedOperationException
  }

  override def params: INDArray = {
    return Nd4j.toFlattened('c', params.values)
  }

  override def setParams(params: INDArray) {
    setParams(params, 'c')
  }
}