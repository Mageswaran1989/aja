package org.aja.dhira.core

/**
 * Created by mageswaran on 18/7/15.
 *
 * <p> Performace consideration:
 * http://docs.scala-lang.org/overviews/collections/performance-characteristics.html
 */
object Types {

  //A Scala Set type to store weight and delta of weight
  type Synapses = (Double, Double)

  //List of data of type double for each neuron
  type DoubleList = Array[Double]

}
