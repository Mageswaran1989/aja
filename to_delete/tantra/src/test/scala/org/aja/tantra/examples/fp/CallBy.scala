package org.aja.tantra.examples.fp

/**
 * Created by mdhandapani on 10/8/15.
 * Call-by-Value: the value of the parameter is determined before it is
 * passed to the function.
 */
object CallBy {

  def CallByValue() {
    def time() = {
      println("Getting time in nano seconds")
      System.nanoTime
    }
    def delayed(t: Long) {
      println("In delayed method")
      println("Param: " + t)
    }
    delayed(time())
  }

/* Call-by-Name: the value of the parameter is not determined until
 * it is called within the function.
 *
 */
  def CallByName() = {
  def time() = {
    println("Getting time in nano seconds")
    System.nanoTime
  }
  def delayed2(t: => Long) {
    println("In delayed method")
    println("Param: " + t)
  }
  delayed2(time())
 }

  def main(args: Array[String]) {

    CallByValue()
    println()
    CallByName()
  }
}