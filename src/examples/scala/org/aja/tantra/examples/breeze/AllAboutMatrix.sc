

import breeze.linalg._
import breeze.numerics._
//Computing basic statistics
import  breeze.stats._

//Working with matrices
//Creating matrices
//Creating a matrix from values
val simpleMatrix = DenseMatrix((1,2,3),(11,12,13),(21,22,23))
val sparseMatrix = CSCMatrix((1,0,0),(11,0,0),(0,0,23))
//Compressed Sparse Column Matrix(CSCMatrix)
//Breeze's Sparse matrix is a Dictionary of Keys (DOK) representation with
//(row, column) mapped against the value.

//Creating a zero matrix
val denseZeros = DenseMatrix.zeros[Double](5,4)
val compressedSparseMatrix = CSCMatrix.zeros[Double](5,4)

//Creating a matrix out of a function
val denseTabulate = DenseMatrix.tabulate[Double](5,4)((firstIdx,secondIdx) =>
      firstIdx + secondIdx)

//Creating an identity matrix
val identityMatrix = DenseMatrix.eye[Int](3)

//Creating a matrix from random numbers
val randomMatrix = DenseMatrix.rand(4, 4)

//Creating from a Scala collection
val vectFromArray = new DenseMatrix(2,2,Array(2,3,4,5))
val vectFromArrayWithExtraValues=new DenseMatrix(2,2,Array(2,3,4,5,6,7))
//val vectFromArrayIobe=new DenseMatrix(2,2,Array(2,3,4))
//throws java.lang.ArrayIndexOutOfBoundsException: 3

//Matrix arithmetic
val additionMatrix = identityMatrix + simpleMatrix
val simpleTimesIdentity = simpleMatrix * identityMatrix

val elementWiseMulti = identityMatrix :* simpleMatrix
//element-by-element operation that has the format of prefixing
//  the operator with a colon, for example, :+,:-, :*, and so on.

//Appending and conversion
//Concatenating matrices – vertically
val vertConcatMatrix = DenseMatrix.vertcat(identityMatrix, simpleMatrix)
//Attempting to concatenate a matrix of different columns would, as expected, throw an
//IllegalArgumentException:

//Concatenating matrices – horizontally
val horzConcatMatrix = DenseMatrix.horzcat(identityMatrix, simpleMatrix)
//Similar to the vertical concatenation, attempting to concatenate a matrix of a different row
//size would throw an IllegalArgumentException:


//Converting a matrix of Int to a matrix of Double
import breeze.linalg.convert
val simpleMatrixAsDouble = convert(simpleMatrix, Double)

//Data manipulation operations
val simpleMatrix1 = DenseMatrix((4.0, 7.0),
                                (3.0,-5.0))

//Getting column vectors out of the matrix
val firstVector=simpleMatrix(::,0) //DenseVector(4.0,
//                                               3.0)
val secondVector=simpleMatrix(::,1) //DenseVector(7.0,
//                                               -5.0)
val firstVectorByCols=simpleMatrix(0 to 1,0) //DenseVector(4.0, 3.0)
//!!!Carefull with the range, as it shouln't be greater than the length

//Getting row vectors out of the matrix
val firstRowStatingCols=simpleMatrix(0,0 to 1) //Transpose(DenseVector(4.0, 7.0))
val firstRowAllCols=simpleMatrix(0,::) //Transpose(DenseVector(4.0, 7.0))
val secondRow=simpleMatrix(1,::) //Transpose(DenseVector(3.0, -5.0))

//Getting values inside the matrix
val firstRowFirstCol=simpleMatrix(0,0)  //Double = 4.0

//Getting the inverse and transpose of a matrix
val transpose=simpleMatrix.t
val inverse=inv(simpleMatrix)

simpleMatrix * inverse



meanAndVariance(simpleMatrixAsDouble)
// MeanAndVariance(12.0,75.75,9)

meanAndVariance(convert(simpleMatrix, Double))

//Standard deviation
stddev(simpleMatrixAsDouble)

//Finding the largest value in a matrix
val intMaxOfMatrixVals = max(simpleMatrix)

//Finding the sum, square root and log of all the values in the matrix
val intSumOfMatrixVals = sum(simpleMatrix)

val sqrtOfMatrixVals = sqrt(simpleMatrix)

val log2MatrixVals = log(simpleMatrix)

//Calculating the eigenvectors and eigenvalues of a matrix
val simpleMatrix2=DenseMatrix((4.0,7.0),(3.0,-5.0))

val denseEig = eig(simpleMatrix)
//val eigenVectors = denseEig.eigenvectors
//val eigenValues = denseEig.eigenvalues
//
//val matrixToEigVector=simpleMatrix*denseEig.eigenvectors (::,0)
//val vectorToEigValue=denseEig.eigenvectors(::,0) * denseEig.eigenvalues (0)









val data = ((1.0,2.0,3.0),(4.0,5.0,6.0))
val dataArray = Array[Double](1.0,2.0,3.0,4.0,5.0,6.0)
data.getClass
val m0 = new DenseMatrix[Double](2,3,dataArray)
val size =m0.activeSize
val rows = m0.rows
val cols = m0.cols
m0.valueAt(0)
m0.valueAt(1)
m0.valueAt(0,0)
m0

println("/////////////////////////////////////////////////////////////////")

val list=Seq(2.3,3.4,2.0,1.0)
val listOfList=Seq(list,list,list)

val bbb=DenseVector(list:_*)

val bbm= DenseMatrix(listOfList:_*)
bbm.rows
bbm.cols

bbm.map(_+1)

println("/////////////////////////////////////////////////////////////////")
val m = DenseMatrix.zeros[Double](5,5)
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
//val array = Array[(Double,Double,Double)]((1,2,3),(1,2,3),(1,2,3))
//val arrayToMat = new DenseMatrix(3,3, array)
//arrayToMat.toString()

m.map(_+1)

