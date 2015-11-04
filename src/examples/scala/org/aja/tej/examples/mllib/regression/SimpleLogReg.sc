import breeze.linalg.DenseMatrix

import scala.io.Source

/**
 * Created by mageswaran on 3/11/15.
 */

//Output prediction is using Sigmoid function
/*
  Predicted class (0 or 1) = $/sigma(z) = frac{1}{1+e^-z}
  Where z = w_0x_0 + w_1x_1 + w_2x_2 + ... + w_nx_n
   i.e z = W^TX
   W = Weight vector
   X = Input feature vector
 */

/*
  Story  Line:
    You have asked to reach the tip/valley of Mount everest (Say in our case that is the best possible
    avaialbe weights that seprates two classes(spam or not spam)).You are dropped somewhere on once side
    of the mountain. You are been monitored using GPS, and have a option to ask your team to guide in
    possible right direction(gradient ascend/descend). After knowing the direction, how fast you wanted
    to go in that direction inches/feets/meters/kilo-meters is up to you (which is basically $\alpha).
    It would save your resources/life if would have load how long you wanted to wander like this before
    your team comes for rescue. (number of iterations)
 */

/*
  Optimization algorithm:
  w += w + \alpha \delta_w f(w)  : Gradient ascent
  w += w - \alpha \delta_w f(w)  : Gradient descent
 */

/*
  Training Steps:
  Start with the weights all set to 1
  Repeat R number of times:
    Calculate the gradient of the entire dataset
    Update the weights vector by alpha*gradient
  Return the weights vector

 */

def sigmoid(n: Double) = (1 / Math.exp(n))

val filePath : String = "/opt/aja/data/testSet.txt"
val fileBuffer = Source.fromFile(filePath)

//val labelsAndFeatures = fileBuffer.getLines()
//  .toList.map(_.split("\t"))
//  .map(x => (DenseVector(x(2).toDouble), Array(1.0, x(0).toDouble, x(1).toDouble)))
//
//labelsAndFeatures.toArray
//
//
////extract label & data
//val label = labelsAndFeatures.map(_._1).toArray //1 X 100
//val data = labelsAndFeatures.flatMap(_._2).toArray //3 X 100
//data.getClass
//data

val labelsAndFeatures = fileBuffer.getLines()
  .toList.map(_.split("\t"))
  .map(x => (List(x(2).toDouble), List(1.0, x(0).toDouble, x(1).toDouble)))

labelsAndFeatures.toArray


//extract label & data
val label = labelsAndFeatures.map(_._1) //100 X 1
val data = labelsAndFeatures.map(_._2) //100 X 3
data.getClass
//data
val dataMatrix = DenseMatrix(data: _*) //100 X 3
val rows = dataMatrix.rows
val cols = dataMatrix.cols
//dataMatrix
val labelMatrix = DenseMatrix(label: _*) //100 x 1
val rows1 = labelMatrix.rows
val cols1 = labelMatrix.cols
//labelMatrix
val numIterations = 100
val alpha = 0.001
var weigths = DenseMatrix.zeros[Double](3,1) // 3 X 1
val h = dataMatrix * weigths

for ( r <- 0 to numIterations) {
  val h = (dataMatrix * weigths) // 100 x 3 * 3 x 1 = 100 x 1
  h.map(sigmoid(_))
  val error = (labelMatrix - h) //100 x 1
  weigths = weigths + alpha * dataMatrix.t * error
  // 3 x 1 = 3 x 1 + 3 x 100 * 100 * 3

  println(error)
}
weigths

import breeze.linalg._
import breeze.plot._

val f = Figure()
val p = f.subplot(0)
val x = linspace(0.0,1.0)
p += plot(x, x :^ 2.0)
p += plot(x, x :^ 3.0, '.')
p.xlabel = "x axis"
p.ylabel = "y axis"
f.saveas("lines.png")

