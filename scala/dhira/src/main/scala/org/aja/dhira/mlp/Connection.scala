package org.aja.dhira.mlp

import org.aja.dhira.utils.FormatUtils

import scala.util.Random

/**
 * <p>Class that defines the connection between two consecutive (or sequential layers)
 * in a Multi-layer Perceptron. The connections is composed of all the synapses between
 * any neuron or variable of each layer.The Synapse is defined as a nested tuple(Double, Double)
 * tuple (weights, deltaWeights)</p>
 * @constructor Create a MLP connection between two consecutive neural layer.
 * @param config  Configuration for the Multi-layer Perceptron.
 * @param src  Source (or input or upstream) neural layer to this connection
 * @param dst  Destination (or output or downstream) neural layer for this connection.
 * @param  mlpObjective Objective of the Neural Network (binary classification, regression...)
 * @author Mageswaran
 * @since July 20, 2015
 * @note Scala for Machine Learning Chapter 9 Artificial Neural Network / Multilayer perceptron
 * / Model definition
 */
final protected class Connection(
                                     config: Config,
                                     src: Layer,
                                     dst: Layer)
                                   (implicit mlpObjective: MLPMain.Objective)  {
  import Connection._

  /**
   * <p>Type of Synapse defined as a tuple of [weight, gradient(weights)]
   */
  type Synapse = (Double, Double)

  /*
   * Initialize the matrix (Array of Array) of Synapse by generating
   * a random value between 0 and BETA
   */
  private[this] val synapses: Array[Array[Synapse]] = Array.tabulate(dst.length)( n =>
    if(n > 0) Array.fill(src.length)((Random.nextDouble*BETA, 0.0))
    else Array.fill(src.length)((1.0, 0.0)))

  /**
   * <p>Implement the forward propagation of input value. The output
   * value depends on the conversion selected for the output. If the output or destination
   * layer is a hidden layer, then the activation function is applied to the dot product of
   * weights and values. If the destination is the output layer, the output value is just
   * the dot product weights and values.</p>
   */
  def connectionForwardPropagation: Unit = {
    // Iterates over all the synapsed except the first or bian selement
    val _output = synapses.drop(1).map(x => {
      // Compute the dot product
      val sum = x.zip(src.data).foldLeft(0.0)((s, xy) => s + xy._1._1 * xy._2)

      // Applies the activation function if this is a hidden layer (not output)
      if(!isOutLayer) config.activation(sum) else sum
    })

    // Apply the objective function (SoftMax,...) to the output layer
    val out = if(isOutLayer) mlpObjective(_output) else _output
    out.copyToArray(dst.data, 1)
  }

  /**
   * Access the identifier for the source and destination layers
   * @return tuple (source layer id, destination layer id)
   */
  @inline
  final def getLayerIds: (Int, Int) = (src.id, dst.id)

  @inline
  final def getSynapses: Array[Array[Synapse]] = synapses

  /**
   * <p>Implement the back propagation of output error (target - output). The method uses
   * the derivative of the logistic function to compute the delta value for the output of
   * the source layer.</p>
   */
  def connectionBackpropagation: Unit =
    Range(1, src.length).foreach(i => {
      val err = Range(1, dst.length).foldLeft(0.0)((s, j) =>
        s + synapses(j)(i)._1*dst.delta(j) )

      // The delta value is computed as the derivative of the
      // output value adjusted for the back-propagated error, err
      src.delta(i) =  src.data(i)* (1.0- src.data(i))*err
    })


  /**
   * <p>Implement the update of the synapse (weight, grad weight) following the
   * back propagation of output error. This method is called during training.</p>
   */
  def connectionUpdate: Unit =
  // Iterates through all element of the destination layer except the bias element
    Range(1, dst.length).foreach(i => {
      val delta = dst.delta(i)

      // Compute all the synapses (weight, gradient weight) between
      // the destination elements (index i) and the source elements (index j)
      Range(0, src.length).foreach(j => {
        val _output = src.data(j)
        val oldSynapse = synapses(i)(j)
        // Compute the gradient with the delta
        val grad = config.eta*delta*_output
        // Apply the gradient adjustment formula
        val deltaWeight = grad + config.alpha*oldSynapse._2
        // Update the synapse
        synapses(i)(j) = (oldSynapse._1 + deltaWeight, grad)
      })
    })

  /**
   * Textual representation of this connection. The description list the
   * values of each synapse as a pair (weight, delta weight)
   */
  override def toString: String = {
    val buf = new StringBuilder
    buf.append(s"\nConnections weights from layer ${src.id} to layer ${dst.id}\n")

    Range(0, dst.length).foreach( i => {
      Range(0, src.length).foreach(j => {
        val wij: Synapse = synapses(i)(j)
        val weights_str = FormatUtils.format(wij._1, "", FormatUtils.MediumFormat)
        val dWeights_str = FormatUtils.format(wij._2, "", FormatUtils.MediumFormat)
        buf.append(s"$i,$j: ($weights_str, $dWeights_str)  ")
      })
      buf.append("\n")
    })
    buf.toString
  }

  /**
   * Convenient method to update the values of a synapse while
   * maintaining immutability
   */
  private def update(i: Int, j: Int, x: Double, dx: Double): Unit = {
    val old = synapses(i)(j)
    synapses(i)(j) = (old._1 + x, dx)
  }

  private def isOutLayer: Boolean = dst.id == config.outLayerId
}



/**
 * Companion object for the connection of Multi-layer perceptron.
 * @author Patrick Nicolas
 * @since July 20, 2015
 * @note Scala for Machine Learning Chapter 9 Artificial Neural Network / Multilayer perceptron
 * / Model definition
 */
object Connection {
  private val BETA = 0.1
}