package org.aja.tantra.examples.fp

/**
 * Created by mdhandapani on 10/8/15.
 *
 * Simply put passing function around the program. Giving the "first class citizen" recognition
 * to the functions just like objects.
 */
object HigherOrderFunction {
  def main(args: Array[String]) {
    def apply(f: Int => String, v: Int) = f(v)
    def layout[A](x: A) = "[" + x.toString() + "]"
    println(apply(layout, 10))
  }
}
