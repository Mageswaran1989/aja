package org.aja.tantra.examples.traits


/**
 * Created by mageswaran on 10/11/15.
 */

//Avoid concrete fields in traits that can’t be initialized to suitable default
//values. Use abstract fields instead, or convert the trait to a class with
//a constructor. Of course, stateless traits don’t have any issues with
//initialization.

trait T1 {
  println(" in T1: x = " + x)
  val x = 1
  println(" in T1: x = " + x)
}

trait T2 {
  println(" in T2: y = " + y)
  val y = "T2"
  println(" in T2: y = " + y)
}

class Base12 {
  println(" in Base12 b = " + b)
  val b = "Base12"
  println(" in Base12 b = " + b)
}

//the order of construction for this simple class hierarchy is left to right,
//and ending with the C12 constructor body
class C12 extends Base12 with T1 with T2 {
  println(" in C12 c = " + c)
  val c = "C12"
  println(" in C12 c = + c")
}
object TraitSample extends App{
  println(" Creating C12:")
  val c12 = new C12
  println(" After creating C12")
}

