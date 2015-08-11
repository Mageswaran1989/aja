package org.aja.tantra.test.fp

/**
 * Created by mdhandapani on 10/8/15.
 */
object Factorial {

  def factorial(i: Int) = {
    // Return value must be given for recursive functions, since it will be difficult for compiler to interpret
    def fact(x: Int, accumulator: Int): Int = {
      if (x <= 1)
        accumulator
      else
        fact(x - 1, x * accumulator)
    }
    fact(i, 1)
  }

  def main(args: Array[String]) {
    var f = factorial(5)
    println(s"Factorial of 5: $f")
    f = (1 to 5).foldLeft(1)(_ * _)
    println(s"Factorial of 5: $f")
  }
}
