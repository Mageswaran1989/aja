package org.aja.tej.examples.spark.rdd

/**
 * Created by mageswaran on 24/9/15.
 */

/**
 * Used in GradientDescend.scala
 *
 * treeAggregate is a specialized implementation of aggregate that iteratively applies the combine function to a subset
 * of partitions. This is done in order to prevent returning all partial results to the driver where a single pass
 * reduce would take place as the classic aggregate does.
 *
 * For aggregate, we need a zero, a combiner function and a reduce function.  aggregate uses currying to specify the
 * zero value independently of the combine and reduce functions.

   val (gradientSum, lossSum, miniBatchSize) = data.sample(false, miniBatchFraction, 42 + i)
    .treeAggregate((BDV.zeros[Double](n), 0.0, 0L))(
      seqOp = (c, v) => {
        // c: (grad, loss, count), v: (label, features)
        val l = gradient.compute(v._2, v._1, bcWeights.value, Vectors.fromBreeze(c._1))
        (c._1, c._2 + l, c._3 + 1)
      },
      combOp = (c1, c2) => {
        // c: (grad, loss, count)
        (c1._1 += c2._1, c1._2 + c2._2, c1._3 + c2._3)
      })

 * We can then dissect the above function like this . Hopefully that helps understanding:

   val Zero: (BDV, Double, Long) = (BDV.zeros[Double](n), 0.0, 0L)
   val combinerFunction: ((BDV, Double, Long), (??, ??)) => (BDV, Double, Long)  =  (c, v) => {
        // c: (grad, loss, count), v: (label, features)
        val l = gradient.compute(v._2, v._1, bcWeights.value, Vectors.fromBreeze(c._1))
        (c._1, c._2 + l, c._3 + 1)
   val reducerFunction: ((BDV, Double, Long),(BDV, Double, Long)) => (BDV, Double, Long) = (c1, c2) => {
        // c: (grad, loss, count)
        (c1._1 += c2._1, c1._2 + c2._2, c1._3 + c2._3)
      }

 * Then we can rewrite the call to treeAggregate in a more digestable form:
   val (gradientSum, lossSum, miniBatchSize) = treeAggregate(Zero)(combinerFunction, reducerFunction)
 *
 * This form will 'extract' the resulting tuple into the named values gradientSum, lossSum, miniBatchSize for further usage.
 *
 * Note that treeAggregate takes an additional parameter depth which is declared with a default value depth = 2, thus, as
 * it's not provided in this particular call, it will take that default value.
 */
object TreeAgreegate  extends App {

}
