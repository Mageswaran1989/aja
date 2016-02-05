/**
 * Created by mageswaran on 31/1/16.
 */

import breeze.linalg._

//Vector -> DenseVector -> Matrix
val v = Vector(1,2,3,4,5)

v.toDenseVector

//val m = Matrix(v) //Error, can't convert Vector to Matrix

//////////////////////////////////////////////
//Array -> Matrix
val a = Array(1,2,3,4,5)

val m1 = Matrix(a)
m1.rows
m1.cols
//////////////////////////////////////////////
//Array of Array to Matrix : Not as expected!
val aa = Array(Array(1,2,3,4,5), Array(6,7,8,9,1))

val m2 = Matrix(aa)

m2.rows //1
m2.cols //2
m2(0,1)

//////////////////////////////////////////////
//Array -> DenseMatrix
val aa1 = Array(1,2,3,4,5,6,7,8,9,10)
val dm = new DenseMatrix(2,5, aa1)
/////////////////////////////////////////////
val v1 = DenseVector(1,2,3,4,5)

val dm1 = v1.toDenseMatrix

//val m3 = DenseMatrix(dm1)



