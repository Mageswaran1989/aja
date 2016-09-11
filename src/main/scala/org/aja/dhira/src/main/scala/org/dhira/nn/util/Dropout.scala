package org.deeplearning4j.util

import org.deeplearning4j.nn.api.Layer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.transforms.DropOut
import org.nd4j.linalg.api.ops.impl.transforms.DropOutInverted
import org.nd4j.linalg.factory.Nd4j

/**
 * @author Adam Gibson
 */
object Dropout {
  /**
   * Apply drop connect to the given variable
   * @param layer the layer with the variables
   * @param variable the variable to apply
   * @return the post applied drop connect
   */
  def applyDropConnect(layer: Nothing, variable: String): INDArray = {
    val result: INDArray = layer.getParam(variable).dup
    Nd4j.getExecutioner.exec(new DropOut(result, result, layer.conf.getLayer.getDropOut))
    return result
  }

  /**
   * Apply dropout to the given input
   * and return the drop out mask used
   * @param input the input to do drop out on
   * @param dropout the drop out probability
   */
  def applyDropout(input: INDArray, dropout: Double) {
    Nd4j.getExecutioner.exec(new DropOutInverted(input, dropout))
  }
}

class Dropout {
  private def this() {
    this()
  }
}