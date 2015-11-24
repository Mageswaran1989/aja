package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
Count:
Returns the number of items stored within a RDD.

countApprox:

countByKey[Pair]:
Very similar to count, but counts the values of a RDD consisting of two-component
tuples for each distinct key separately.

countByKeyApprox[Pair]:

countByValue:
  Returns a map that contains all unique values of the RDD and their respective occurrence
counts. (Warning: This operation will finally aggregate the information in a single
reducer!)

countByValueApprox:

countApproxDistinct:
Computes the approximate number of distinct values. For large RDDs which are spread
across many nodes, this function may execute faster than other counting methods. The
parameter relativeSD controls the accuracy of the computation.

countApproxDistinctByKey[Pair]
Similar to countApproxDistinct, but computes the approximate number of distinct values
for each distinct key. Hence, the RDD must consist of two-component tuples. For large
RDDs which are spread across many nodes, this function may execute faster than other
counting methods. The parameter relativeSD controls the accuracy of the computation.
 */

object Count extends App {
  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)

    val c = sc.parallelize(List("Gnu","Cat","Rat","Dog"), 2)
    println(c.count) //4
    println("---------------------------------------------------------")

    //c.countApprox

    val d = sc.parallelize(List((3,"Gnu"),(3,"Yak"),(5,"Mouse"),(3,"Dog")),2)
    println(d.countByKey) //Map(3 -> 3, 5 -> 1)
    println("---------------------------------------------------------")

    val b = sc.parallelize(List(1,2,3,4,5,6,7,8,2,4,2,1,1,1,1,1))
    println(b.countByValue) //Map(5 -> 1, 1 -> 6, 6 -> 1, 2 -> 3, 7 -> 1, 3 -> 1, 8 -> 1, 4 -> 2)
    println("---------------------------------------------------------")

    val a = sc.parallelize(1 to 10000,20)
    val e = a ++ a ++ a ++ a ++ a
    println(e.countApproxDistinct(0.1)) //8224
    println(e.countApproxDistinct(0.05)) //9760
    println(e.countApproxDistinct(0.01)) //9947
    println(e.countApproxDistinct(0.001)) //10000
    println("---------------------------------------------------------")

    val a1 = sc.parallelize(List("Gnu","Cat","Rat","Dog"),2)
    val b1 = sc.parallelize(a1.takeSample(true,10000,0),20)
    val c1 = sc.parallelize(1 to b1.count().toInt,20)
    val d1 = b1.zip(c1)
    d1.countApproxDistinctByKey(0.1) foreach println
    println("---")
    d1.countApproxDistinctByKey(0.01).collect foreach println
    println("---")
    d1.countApproxDistinctByKey(0.001).collect foreach println
    println("---------------------------------------------------------")

//    (Rat,2249)
//    (Cat,2651)
//    (Dog,2762)
//    (Gnu,2030)
//    ---
//    (Rat,2499)
//    (Cat,2509)
//    (Dog,2524)
//    (Gnu,2435)
//    ---
//    (Rat,2515)
//    (Cat,2505)
//    (Dog,2539)
//    (Gnu,2442)
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}
