package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.aja.tej.utils.TejUtils._
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 14/8/15.
 */
/*
Very efficient implementation that combines the values of a RDD consisting of two-
component tuples by applying multiple aggregators one after another.

[Pair]

Turns an RDD[(K, V)] into a result of type RDD[(K, C)]
RDD[(K, V)] -> RDD[(K, C)]
 */
object CombineByKeyExample extends App{

  def useCases(sc:SparkContext) = {
    val a = sc . parallelize ( List (" dog " ," cat " ," gnu " ," salmon " ," rabbit " ," turkey " ," wolf " ," bear " ," bee ") , 3)
    println("a:" )
    a.foreach(println)
    val b = sc . parallelize ( List (1 ,1 ,2 ,2 ,2 ,1 ,2 ,2 ,2) , 3)
    val c = b . zip ( a )
    c.foreach(println) //RDD[(Int, String)]
    /**
     * Place the cursor on top of the API and Ctrl + B ;)
     */
    // Rdd[(Int, List[String])]
    val d = c.combineByKey(List ( _ ) ,
                          (x: List [ String ], y : String ) => y :: x ,
                          (x : List [ String ] , y : List [ String ]) => x ::: y )

    d . collect
    println("d :")
    d.foreach(println)

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}

