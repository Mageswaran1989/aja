package org.aja.tantra.examples.classes

/**
 * Created by mdhandapani on 21/8/15.
 */

//Here the constructor is made private
class Model private (x: Int, y: Int) {
//  def this() = this(1,1)
//  def this(y: Int) = this(1,y)
  def run = x + y
}

object Model {
  def train() = new Model(1,2).run
  def train(y: Int) = new Model(1,y).run
  def train(x: Int, y: Int) = new Model(x,y).run
}
object PrivateModifier extends App{
  val addResult = Model.train(1,2)
  println("addResult: " + addResult)
}
