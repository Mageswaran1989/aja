import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._


//Uniform distribution with low being 0 and high being 10
val uniformDist = Uniform(0,10)
//Gaussian distribution with mean being 5 and Standard deviation being 1
val gaussianDist = Gaussian(5,1)
//Poission distribution with mean being 5
val poissonDist = Poisson(5)

uniformDist.sample()
gaussianDist.sample(2)
poissonDist.sample(3)

//Creating vectors with random values

val uniformWithoutSize = DenseVector.rand(10)

val uniformVectInRange = DenseVector.rand(10, uniformDist)
val gaussianVector = DenseVector.rand(10, gaussianDist)
val poissonVector = DenseVector.rand(10, poissonDist)

//Creating a matrix with uniformly random values

val uniformMat = DenseMatrix.rand(3, 3) //values range 0 to 1
val uniformMatrixInRange = DenseMatrix.rand(3,3, uniformDist)
val gaussianMatrix = DenseMatrix.rand(3, 3,gaussianDist)
val poissonMatrix = DenseMatrix.rand(3, 3,poissonDist)


