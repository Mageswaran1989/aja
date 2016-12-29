package org.deeplearning4j.nn.params

import org.deeplearning4j.nn.api.ParamInitializer
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.nd4j.linalg.api.ndarray.INDArray
import java.util.Collections
import java.util.Map

/**
 * @author Adam Gibson
 */
object EmptyParamInitializer {
  private val INSTANCE: EmptyParamInitializer = new EmptyParamInitializer

  def getInstance: EmptyParamInitializer = {
    return INSTANCE
  }
}

class EmptyParamInitializer extends ParamInitializer {
  def numParams(conf: Nothing, backprop: Boolean): Int = {
    return 0
  }

  def init(conf: Nothing, paramsView: INDArray, initializeParams: Boolean): Map[String, INDArray] = {
    return Collections.EMPTY_MAP
  }

  def getGradientsFromFlattened(conf: Nothing, gradientView: INDArray): Map[String, INDArray] = {
    return Collections.emptyMap
  }
}