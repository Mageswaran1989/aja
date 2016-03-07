package org.aja.tej.examples.spark.rdd.misc

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
Returns a new RDD that contains each unique value only once.

 */
object Distinct extends App{

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)
    val c = sc.parallelize(List("Gnu","Cat","Rat","Dog","Gnu","Rat") ,2)
    c.distinct.collect.foreach(println)

    val a = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10,1,2,3,4,5))
    println(a.distinct(2).partitions.length) //2
    println(a.distinct(3).partitions.length) //3
    println("-------------------------------------------------------")

    a.distinct(3).foreachPartition(p => {
      p.foreach(println)
      println("-------------------------------------------------------")
    }
    )
//    -------------------------------------------------------
//    8
//    5
//    2
//    -------------------------------------------------------
//    6
//    3
//    9
//    -------------------------------------------------------
//    4
//    1
//    7
//    10
//    -------------------------------------------------------

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}
