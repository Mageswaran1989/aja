package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Returns the RDD on which this RDD depends.

 */
object Dependencies extends App{

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)
    val a = sc.parallelize(List(1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,2 ,4 ,2 ,1 ,1 ,1 ,1 ,1))
    val b = sc.parallelize(List(1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,2 ,4 ,2 ,1 ,1 ,1 ,1 ,1))
    val c = a.map(_ + 1) // take each element and increment by 1
    val d = c.filter(_ > 2) //create a RDD of elements that are greater than 2


    println("a.dependencies.length : " + a.dependencies.length) //a.dependencies.length : 0
    println("a.dependencies : " + a.dependencies) //a.dependencies : List()

    println("b.dependencies.length : " + b.dependencies.length)
    println("b.dependencies : " + b.dependencies)

    println("b.cartesian(a).dependencies.length : " + b.cartesian(a).dependencies.length)
    // b.cartesian(a).dependencies.length : 2
    println("b.cartesian(a).dependencies : " + b.cartesian(a).dependencies)
    //b.cartesian(a).dependencies : List(org.apache.spark.rdd.CartesianRDD$$anon$1@7086eef6, org.apache.spark.rdd.CartesianRDD$$anon$2@2ab29231)

    println("c.dependencies.length : " + c.dependencies.length) //c.dependencies.length : 1
    println("c.dependencies : " + c.dependencies) //c.dependencies : List(org.apache.spark.OneToOneDependency@319be4dd)

    println("d.dependencies.length : " + d.dependencies.length)
    println("d.dependencies : " + d.dependencies)
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
