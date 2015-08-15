package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Returns the RDD on which this RDD depends.

 */
object Dependencies {

  def useCases(sc: SparkContext) = {


    val a = sc . parallelize ( List (1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,2 ,4 ,2 ,1 ,1 ,1 ,1 ,1) )
    val b = sc . parallelize ( List (1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,2 ,4 ,2 ,1 ,1 ,1 ,1 ,1) )
    b . dependencies.length
    b . map (a =>a ) . dependencies . length
    b . cartesian (a) . dependencies . length
    b . cartesian (a) . dependencies


  }
}
