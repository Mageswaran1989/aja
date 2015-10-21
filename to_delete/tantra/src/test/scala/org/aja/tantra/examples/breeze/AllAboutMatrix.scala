package org.aja.tantra.examples.breeze

import breeze.linalg.DenseMatrix

/**
 * Created by mdhandapani on 18/8/15.
 */
object AllAboutMatrix extends App{

  val m = DenseMatrix.zeros[Int](5,5)
  val n = DenseMatrix.tabulate(5,5)((x,y) => x * 1 + y * 2 )

  println(m)
  println()
  println(n)
  println()
  println("Size of n: ("+ n.rows + "," + n.cols +")")

  //Slicing
  println("Column 1 of n: ")
  println(n(::, 1))

  println("Row 4 of n: ")
  println(n(4, ::))

  //Boxing
  println("n(1 to 3, 1 to 3): ")
  println(n(1 to 3, 1 to 3))
}
