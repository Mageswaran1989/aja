package org.aja.tantra.examples.breeze

/**
 * Created by mdhandapani on 8/10/15.
 */

//Aja Docs : ml/dimensionality_reduction/svd.tex

import breeze.linalg._

object SVDExample extends App{

  val a = new DenseMatrix(4,2, Array(2.0,1.0,0.0,0.0,4.0,3.0,0.0,0.0))
  // 4 X 2 => 4 x 4 . 1 X 2 . 2 X 2
  //          4 X 2 . (2 X 2). 2 X 2
  val svd.SVD(u,s,v) = svd(a)

  println("u => \n" + u)
  println("s => \n" + diag(s))
  println("v => \n" + v)
}
