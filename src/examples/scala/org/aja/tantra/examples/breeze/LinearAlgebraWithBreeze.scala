package org.aja.tantra.examples.breeze

import breeze.linalg._
import breeze.numerics._ //Required LA Statistics

/**
 * Created by mageswaran on 14/2/16.
 *
 * Reference Document: cs229-linalg_LinearAlgebra.pdf
 *
 * Object that is used as an reference fo rall LinearAlgebra operations
 */
object LinearAlgebraWithBreeze {

  //Column Vector (n X 1) : x
  //Row Vector    (1 x n) : x^T

  //Index: 0 to n-1
  val vecX = DenseVector[Double](0.0,1.0,2.0)
  val vecY = DenseVector[Double](1.0,1.0,1.0)
  val ithElementInVecX = vecX(2) //2.0

  //Index: 0 to n-1
  //Breeze does column major filling, i.e it first fills the columns with values
  val matA = new DenseMatrix[Double](3,3,Array(1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0))
  //  1.0  4.0  7.0
  //  2.0  5.0  8.0
  //  3.0  6.0  9.0
  val matB = new DenseMatrix[Double](3,2,Array(1.0,1.0,1.0,1.0,1.0,1.0))
  //  1.0  1.0
  //  1.0  1.0
  //  1.0  1.0

  val matA_element22 = matA(2,2) //9.0
  val matA_2ndRow = matA(1,::).t  //2,5,8 : This circus is needed since Breeze is a Column major
  val matA_1stCol = matA(::, 0) //1,2,3

  val identityMat = DenseMatrix.eye[Double](3) //I_ij => {1 if i = j, 0 if i = j}
  //  1.0  0.0  0.0
  //  0.0  1.0  0.0
  //  0.0  0.0  1.0

  //Matrix Multiplication
  val newMatA = matA * identityMat //A * I = A
  //  1.0  4.0  7.0
  //  2.0  5.0  8.0
  //  3.0  6.0  9.0

  //Vector dot product
  val vecXY = vecX.dot(vecY) //3.0

  //Outer product
  val vecOuterProduct = vecX * vecY.t
  //  0.0  0.0  0.0
  //  1.0  1.0  1.0
  //  2.0  2.0  2.0

  //  Matrix-Vector Products:
  //    y is a linear combination of the columns of A, where the coefficients of
  //    the linear combination are given by the entries of x: y_i = a^T_i *x.

  val y = matA.t * vecX //aRemember Breeze is column major!
  //  |1.0  2.0  3.0|   |0.0|       1.0  4.0  7.0   0.0
  //  |4.0  5.0  6.0|   |1.0|   or  2.0  5.0  8.0   1.0
  //  |7.0  8.0  9.0|   |2.0|       3.0  6.0  9.0   2.0

  //  8.0
  //  17.0
  //  26.0

  //Matrix multiplication is associative.
  val associative = (matA * identityMat) * matB == matA * (identityMat * matB) //true
  val distributive = (matA + identityMat) * matB == matA * matB + identityMat * matB //true
  val commutative = matA * matB == matB * matA //error i.e false


  //Transpose properties
  val rule1 = matA.t.t == matA
  val rule2 = (matA * matB).t == matB.t * matA.t
  val rule3 = (matA + identityMat).t == matA.t + identityMat.t

  val isMatASymettric = matA ==matA.t

  //Trace
  val traceOfMatA = trace(matA)
  val property1 = trace(matA) == trace(matA.t)
  val property2 = trace(matA + identityMat) == trace(matA) + trace(identityMat)

  //Norm
  val distance = norm(vecX)

  //Rank
  //  A vector is linearly independent if no vector can
  //  be represented as a linear combination of the remaining vectors, if not then (linearly) dependent
  val rankOfMatA = rank(matA)
  val isFullRank = rank(matA) <= min(matA.rows,matA.cols)

  //Inverse
  val inveserseOfIMat = inv(identityMat)
  val inverseProperty = identityMat * inveserseOfIMat

  val isOrthogonal = vecX.t * vecY == 0

  val determinant = det(matA)

  val quadraticForm = vecX.t *  matA * vecX

  //Eigen
  //Ax = λx, x != 0. x is vector
  //  Intuitively, this definition means that multiplying A by the vector x results in a new vector
  //  that points in the same direction as x, but scaled by a factor λ.

  val eigenOfMatA = eig(matA)
  val eigenValues = eigenOfMatA.eigenvalues
  eigenOfMatA.eigenvaluesComplex
  val eigenVector = eigenOfMatA.eigenvectors

  val eigenProperty1 = trace(matA) == floor(sum(eigenValues))
  var productCache = 1.0
  eigenValues.map(x => productCache = x * productCache)
  val eigenProperty2 = det(matA) == floor(productCache)

  //TODO: page 20 onwards:


}
