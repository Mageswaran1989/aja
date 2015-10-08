package org.aja.tantra.examples.fp

/**
 * Created by mdhandapani on 10/8/15.
 */
/*
Let's look at another example, currying, which converts a
 function of N arguments into a function of one argument that returns another
 function as its result. Here again, there is only one implementation that
 typechecks.
 */
//def curry[A,B,C](f: (A, B) => C): A => (B => C)
///* Implement uncurry, which reverses the
// transformation of curry. Note that since => associates to the right, A => (B
//   => C) can be written as A => B => C.
//*/
//
//def uncurry[A,B,C](f: A => B => C): (A, B) => C

object Currying {
  def main(args: Array[String]) {

  }
}
