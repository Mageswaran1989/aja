package org.aja.dhira.mlp

import org.aja.dhira.mlp.Config
/**
 * Created by mdhandapani on 22/7/15.
 */
object ConfigTest {

  def main(args: Array[String]) {
    var mlpConfig = Config(0.01, 0.1, 2, 10)
    var n = mlpConfig.outLayerId
    println(s"Out layer Id: $n")
    n = mlpConfig.nHiddenLayers
    println(s"Number of hidden neuron layers: $n")

    mlpConfig = Config(0.01, 0.1, Array(2,2,2), 10)
    n = mlpConfig.outLayerId
    println(s"Out layer Id: $n")
    n = mlpConfig.nHiddenLayers
    println(s"Number of hidden neuron layers: $n")

    println(s"Activation function h(1) is ${mlpConfig.activation(1)}")

  }
}
