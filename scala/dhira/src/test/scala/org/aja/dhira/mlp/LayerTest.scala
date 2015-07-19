package org.aja.dhira.mlp
/**
 * Created by mageswaran on 19/7/15.
 */

object LayerTest {

  val layer = Layer(0,4)
  val dataOut = Array(1.0,2.0,3.0,4.0)
  val labels =  Array(1.0,2.0,1.0,4.0)


  def main(args: Array[String]) {
    layer.setInput(dataOut)
    println(layer.isOutputLayer(0))

    layer.sse(labels)
    println(layer)
    println(layer.delta)
  }


}
