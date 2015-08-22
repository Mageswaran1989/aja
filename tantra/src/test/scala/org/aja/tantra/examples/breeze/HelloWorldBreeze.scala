package org.aja.tantra.examples.breeze

import breeze.linalg.DenseMatrix

/**
 * Created by mdhandapani on 18/8/15.
 */
object HelloWorldBreeze extends App{

  val m = DenseMatrix.zeros[Int](5,5)

  println(m)
}
