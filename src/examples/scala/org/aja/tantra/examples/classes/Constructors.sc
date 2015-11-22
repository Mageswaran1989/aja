package org.aja.tantra.examples.classes

/**
 * Created by mageswaran on 10/11/15.
 */
class Parent(val parentVal: Int, var parentVar: Int) {
  //Better Design with Design by contract
  require(parentVal != 0)
  require(parentVar != 0)
  println("Parent class Constructor")

  def this(parentVal: Int) = this(parentVal, 1)
}
class Child(val childVal:Int, var childVar: Int) extends Parent(childVal, childVar) {
  require(childVal != 0)
  require(childVar != 0)
  println("Child class Constructor")

  def this(childVal: Int) = this(childVal, 1)
}
object Constructors extends App{

  val c = new Child(2)

  val c1 = new Child(0)
}

println("/////////////////////////////////////////////////////////////")
trait Test {
  println("In Test trait Constructor")
}

class TestTrait extends Test {
  println("In TestTrait class")
}

val t = new TestTrait()
