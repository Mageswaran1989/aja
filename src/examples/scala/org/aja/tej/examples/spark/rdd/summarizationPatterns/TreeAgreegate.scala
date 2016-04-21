package org.aja.tej.examples.spark.rdd.summarizationPatterns

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 24/9/15.
 */

/*
Aggregates the elements of a RDD in a multi-level tree pattern.
Git history: https://github.com/apache/spark/pull/1110
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
object TreeAgreegateExample  extends App {

  def usecases(sc: SparkContext) = {
    try {
      println(this.getClass.getSimpleName)

      val l = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8, 9), 3)
      //1+2+3+4+5+6+7+8+9 = 45

      println("TreeAgreegateExample result: " + l.treeAggregate(0)(_ + _, _ + _)) //45
      println("AgreegateExample result: " + l.aggregate(0)(_ + _  , _ + _ )) //45
      println("------------------------------------------------------------")

      println("TreeAgreegateExample result: " +  l.treeAggregate(10)(_ + _, _ + _)) //45 + 3(partition) * 10
      println("AgreegateExample result: " + l.aggregate(10)(_ + _  , _ + _ ))
      //45 + 3(partition) * 10 + 10 (added by the driver or in main program)
      println("------------------------------------------------------------")


      val strList = sc.parallelize(List('a','b','c','d','e','f','g','h','i'), 3)
      println("TreeAgreegateExample result: " +  strList.treeAggregate("x")(_ + _, _ + _)) //xghixdefxabc
      println("AgreegateExample result: " + strList.aggregate("x")(_ + _  , _ + _ )) //xxghixdefxabc



    }
    finally {
      TejUtils.waitForSparkUI(sc)
    }
  }

  usecases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
