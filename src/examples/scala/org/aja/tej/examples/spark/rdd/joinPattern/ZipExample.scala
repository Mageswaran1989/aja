package org.aja.tej.examples.spark.rdd.joinPattern

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
zip
Joins two RDDs by combining the i-th of either partition with each other. The resulting
RDD will consist of two-component tuples which are interpreted as key-value pairs by
the methods provided by the PairRDDFunctions extension.

zipParititions
Similar to zip. But provides more control over the zipping process.

 */
object ZipExample  extends App {

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)

    val a = sc.parallelize(1 to 100,3)
    val b = sc.parallelize(101 to 200,3)
    a.zip(b).collect foreach println
    println("---------------------------------------------------------------")
//    (1,101)
//    (2,102)
//    (3,103)
//    (4,104)
//    (5,105)

    val a1 = sc.parallelize(1 to 100,3)
    val b1 = sc.parallelize(101 to 200,3)
    val c1 = sc.parallelize(201 to 300,3)
    a1.zip(b1).zip(c1).map((x) => (x._1._1,x._1._2,x._2)).collect foreach println

    println("---------------------------------------------------------------")
//    (1,101,201)
//    (2,102,202)
//    (3,103,203)
//    (4,104,204)
//    (5,105,205)
//    (6,106,206)
//    (7,107,207)
//    (8,108,208)

    def myfunc(aiter: Iterator[Int], biter: Iterator[Int], citer: Iterator[Int]): Iterator[String] =
    {
      var res = List[String]()
      while (aiter.hasNext && biter.hasNext && citer.hasNext)
      {
        val x = aiter.next + " " + biter.next + " " + citer.next
        res ::= x
      }
      res.iterator
    }
    a1.zipPartitions(b1,c1)(myfunc).collect foreach println

//    33 133 233
//    32 132 232
//    31 131 231
//    30 130 230
//    29 129 229
//    28 128 228
//    27 127 227
//    ...
//    1 101 201

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
