

import breeze.linalg._

/**
 * Created by mdhandapani on 18/8/15.
 * The difference in using Breeze vector is that we get Linear Algebra
 * functions/operations as part of the library
 */


//Creating vectors:
//----------------

//Creating a vector from values
//As of creating any Vector we need to know the size,
//if we use constructor to create then the size id interrupted,
//in other cases we are obliged to pass the size as first argument

val denseVector = DenseVector(1,2,3,4,5)
val sparseVector = SparseVector(6,0,7,0,8) //stored with index

//Creating a zero vector
val denseZeros = DenseVector.zeros[Double](5) //Allocates memory for zero
val sparseZeros = SparseVector.zeros[Double](5) //Doesn't allocate memory for zero

//Creating a vector out of a function
val tabulateDense = DenseVector.tabulate[Double](5)(i => i * i)
val tabulateSparse = SparseVector.tabulate[Double](5)(i => i % 2)

//Creating a vector of linearly spaced values
val spaceVector = breeze.linalg.linspace(2,10,7)

//Creating a vector with values in a specific range
val allNosTill10=DenseVector.range(0, 10)
val evenNosTill20=DenseVector.range(0, 20, 2)
val rangeD=DenseVector.rangeD(0.5, 20, 2.5) //Double Values

//Creating an entire vector with a single value
val fillDense = DenseVector.fill[Double](5, 1.1)
val fillSparse = SparseVector.fill[Double](5)(2.2)

//Slicing a sub-vector from a bigger vector
val fourThroughSevenIndexVector= allNosTill10.slice(4, 7)
val twoThroughNineSkip2IndexVector= allNosTill10.slice(2, 9, 2)

//Creating a Breeze vector from a Scala vector
val vectFromArray=DenseVector(collection.immutable.Vector(1,2,3,4))


// Vector arithmetic:
//-------------------
//Scalar operations
val inPlaceValueAddition=evenNosTill20 + 2
val inPlaceValueMultiplication=evenNosTill20 * 2

//Calculating the dot product of a vector
val justFive2s = DenseVector.fill(5, 2)
val zeroThrough4 = DenseVector.range(0, 5, 1)
val dotVector = zeroThrough4.dot(justFive2s)

//Creating a new vector by adding two vectors together
val additionVector = justFive2s + zeroThrough4

val fiveLength=DenseVector(1,2,3,4,5)
val tenLength=DenseVector.fill(10, 20)
fiveLength+tenLength //takes first vecor length $ truncates second one
//tenLength+fiveLength
// java.lang.ArrayIndexOutOfBoundsException: 5


//Appending vectors and converting a vector of one type to another:
//-----------------------------------------------------------------
//Concatenating two vectors
val concatVector=DenseVector.vertcat(zeroThrough4, justFive2s) //Can take diff lengths
val concatVector1=DenseVector.horzcat(zeroThrough4, justFive2s) //breeze.linalg.DenseMatrix[Int]

//Converting a vector of int to a vector of double
val evenNosTill20Double = breeze.linalg.convert(evenNosTill20,
  Double)


//Computing basic statistics:
//---------------------------
import breeze.numerics._
import  breeze.stats._
//Mean and variance
meanAndVariance(evenNosTill20Double)

//Standard deviation
stddev(evenNosTill20Double)

//Find the largest value
val intMaxOfVectorVals=max(evenNosTill20)


//Finding the sum, square root and log of all the values in the vector
val intSumOfVectorVals=sum (evenNosTill20)
val sqrtOfVectorVals= sqrt (evenNosTill20)
val log2VectorVals=log(evenNosTill20)



//Column Vector
val x = DenseVector.zeros[Double](5) //Allocates memory for zero
val y = SparseVector.zeros[Double](5) //Doesn't allocate memory for zero
val z = DenseVector(1,2,3,4,5)
z.length

//Row vector is transpose of column vector
val tabulatedVals_t = Transpose(x)
println(tabulatedVals_t)
//Length of vector is 0 to x.length-1
println("Length of tabulatedVals is: " + z.length )
println("z(0): " + z(0) + "tabulatedVals(-1) : " + z(-1))
//Slicing
println("x(2 to 4) is: " + z(2 to 4))

