package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 12/8/15.
 */

/*
Computes the cartesian product between two RDDs (i.e. Each item of the first RDD is
joined with each item of the second RDD) and returns them as a new RDD. (Warning:
Be careful when using this function.! Memory consumption can quickly become an issue!)
 */
class CartesianExample(val sc: SparkContext) {

  val x = sc.parallelize( List (1 ,2 ,3 ,4 ,5))
  val y = sc.parallelize( List (6 ,7 ,8 ,9 ,10))
  val res = x.cartesian(y).collect //Array[(Int,Int)]
  println(res.foreach(println))

//  Array [( Int , Int ) ] = Array ((1 ,6) , (1 ,7) , (1 ,8) , (1 ,9) , (1 ,10) ,
//  (2 ,6) , (2 ,7) , (2 ,8) , (2 ,9) , (2 ,10) , (3 ,6) , (3 ,7) , (3 ,8) , (3 ,9) ,
//  (3 ,10) , (4 ,6) , (5 ,6) , (4 ,7) , (5 ,7) , (4 ,8) , (5 ,8) , (4 ,9) , (4 ,10) ,
//  (5 ,9) , (5 ,10) )
}
