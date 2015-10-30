

import breeze.linalg.{Transpose, SparseVector, DenseVector}

/**
 * Created by mdhandapani on 18/8/15.
 */


  //Column Vector
  val x = DenseVector.zeros[Double](5) //Allocates memory for zero
  val y = SparseVector.zeros[Double](5) //Doesn't allocate memory for zero
  val z = DenseVector(1,2,3,4,5)
  val tabulatedVals = DenseVector.tabulate(5)(x => x * 2)
  val filledValues = DenseVector.fill(5)(6)

  println(x)
  println(y)
  println(z)
  println(tabulatedVals)
  println(filledValues)

  //Row vector is transpose of column vector
  val tabulatedVals_t = Transpose(x)
  println(tabulatedVals_t)

  //Length of vector is 0 to x.length-1
  println("Length of tabulatedVals is: " + tabulatedVals.length )
  println("tabulatedVals(0): " + tabulatedVals(0) + "tabulatedVals(-1) : " + tabulatedVals(-1))

  //Slicing
  println("tabulatedVals(2 to 4) is: " + tabulatedVals(2 to 4))


