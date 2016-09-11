package org.aja.dhira.incubate.core

/**
 * Created by mageswaran on 10/10/15.
 */

/**
 * Modeling the Neuron/Perceptron
 * @param index To store the index of the neuron with respect to its @Layer
 */
class Neuron(val index: Int) {
  var weight: Long = 0
  var gradient: Long = 0
  def apply(activationFunction: Long => Long, v:Long) = activationFunction(v)
  override def toString(): String = "N_" + index
}

object Neuron {

}