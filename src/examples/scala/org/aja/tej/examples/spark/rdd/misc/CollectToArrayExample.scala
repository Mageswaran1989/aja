package org.aja.tej.examples.spark.rdd.misc

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Converts the RDD into a Scala array and returns it. If you provide a standard map-
function (i.e. f = T ⇒ U ) it will be applied before inserting the values into the result
array.

 */
object CollectToArrayExample extends App{

  def useCases(sc: SparkContext) = {
    println(this.getClass.getSimpleName)
    val c = sc.parallelize (List ("Gnu","Cat","Rat","Dog","Gnu","Rat") ,2)
    println("c.collect return type : " + c.collect().getClass)
    //c.collect return type : class [Ljava.lang.String;
    c.collect.foreach(println)
    //    Gnu
    //    Cat
    //    Rat
    //    Dog
    //    Gnu
    //    Rat
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
