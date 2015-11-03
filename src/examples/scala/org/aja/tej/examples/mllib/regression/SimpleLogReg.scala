package org.aja.tej.examples.mllib.regression

import breeze.linalg.DenseVector

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

case class LabelPt(label: Double, features: DenseVector[Double])

object SimpleLogReg {

  def sigmoid(n: Double) = (1 / Math.exp(n))

  def loadDataset(filePath: String) = {
    val fileBuffer = Source.fromFile(filePath)

    val featureLabelTuple = fileBuffer.getLines()
      .toList.map(_.split("\t"))
      .map(x => (DenseVector(x(0).toDouble, x(1).toDouble), DenseVector(x(2).toDouble)))

    featureLabelTuple
  }

}
