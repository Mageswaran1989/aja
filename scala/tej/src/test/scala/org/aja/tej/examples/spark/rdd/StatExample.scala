package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 stats[Double]
Simultaneously computes the mean, variance and the standard deviation of all values in
the RDD.

 */
object StatExample {

  def useCases(sc: SparkContext) = {
  val x = sc.parallelize ( List (1.0 , 2.0 , 3.0 , 5.0 , 20.0 ,19.02 , 19.29 ,11.09 , 21.0) , 2)
  x.stats
  }
}
