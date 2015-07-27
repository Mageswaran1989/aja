package org.aja.dhira.mlp

import org.aja.dhira.core.Types.DoubleList
import org.aja.dhira.utils.FormatUtils


/**
 * <p>:Class that defines a neuron/perceptron layer. A MLP layer is built using the
 * input vector and add an extra element (or neuron) to account for the intercept
 * weight w0. The MLP layer is fully defined by its rank in the Neuron Network with
 * input layer having id = 0 and the output layer having id = number of layers -1.</p>
 * @constructor Create a layer for a multi-layer perceptron.
 * @throws IllegalArgumentException if the class parameters are incorrect
 * @param id Identifier or rank of the MLP layer in the network.
 * @param length Number of elements or neuron in the MLP layer.
 *
 * @author Mageswaran.D
 * @since July 19, 2015
 * @note Aja dhira project
 */
class Layer(val id: Int, val length: Int) {

  val data = new DoubleList(length)
  val delta = new DoubleList(length)

  //Set bias element weight as 1
  data.update(0, 1.0)

  def setData(_x: DoubleList): Unit = {
    require(!_x.isEmpty, s"Layer.set cannot initialize this layer $id with undefined data")
    _x.copyToArray(data, 1)
  }

  final def sse(labels: DoubleList): Double = {
    require(!labels.isEmpty, s"Label/Target cannot be empty!")
    require(labels.length == data.length + 1, s"Label/Target size != Output size")

    var _sse = 0.0
    data.drop(1).zipWithIndex.foreach{case(predictedOutput, index) => {
      val error: Double = labels(index) - predictedOutput
      delta.update(index+1, predictedOutput * (1.0 - predictedOutput) * error)
      _sse += error * error
      println(predictedOutput * (1.0 - predictedOutput) * error)
    }}
    _sse * 0.5
    }
  
  @inline
  def isOutputLayer(lastLayerId: Int): Boolean =  id == lastLayerId

  override def toString: String = {
    val buf = new StringBuilder

    buf.append(s"\nLayer: $id \n output: \t delta\n")
    data.drop(1).zip(delta).foreach(x => buf.append(s"${FormatUtils.format(x,"", FormatUtils.ShortFormat)}\n"))
    buf.toString.substring(0, buf.length-1)
  }
}

object Layer {
  def apply(id: Int, length: Int) = new Layer(id, length)

  private def check(id: Int, length: Int): Unit = {
    require(id >= 0, s"Layer Create a MLP layer with incorrect id: $id")
    require(length > 0, s"Layer Create a MLP layer with incorrect length $length")
  }
}