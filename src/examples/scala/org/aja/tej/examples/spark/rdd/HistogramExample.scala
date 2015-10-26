package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
 histogram[Double]
These functions take an RDD of doubles and create a histogram with either even spacing
(the number of buckets equals to bucketCount) or arbitrary spacing based on custom
bucket boundaries supplied by the user via an array of double values. The result type of
both variants is slightly different, the first function will return a tuple consisting of two
arrays. The first array contains the computed bucket boundary values and the second
array contains the corresponding count of values (i.e. the histogram). The second variant
of the function will just return the histogram as an array of integers.

 */
object HistogramExample  extends App {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize ( List (1.1 , 1.2 , 1.3 , 2.0 , 2.1 , 7.4 , 7.5 , 7.6 ,
      8.8 , 9.0) , 3)
    a . histogram (5)

    val a1 = sc . parallelize ( List (9.1 , 1.0 , 1.2 , 2.1 , 1.3 , 5.0 , 2.0 , 2.1 ,
      7.4 , 7.5 , 7.6 , 8.8 , 10.0 , 8.9 , 5.5) , 3)
    a1 . histogram (6)

    // Example with custom spacing

    val a3 = sc . parallelize ( List (1.1 , 1.2 ,1.3 , 2.0 , 2.1 , 7.4 , 7.5 , 7.6 ,8.8 , 9.0) , 3)
    a3 . histogram ( Array (0.0 , 3.0 , 8.0) )

    val a4 = sc . parallelize ( List (9.1 , 1.0 , 1.2 , 2.1 , 1.3 ,5.0 , 2.0 , 2.1,7.4 , 7.5 , 7.6 , 8.8 , 10.0 , 8.9 , 5.5) , 3)
    a4 . histogram ( Array (0.0 , 5.0 , 10.0) )
    a4 . histogram ( Array (0.0 , 5.0 , 10.0 , 15.0) )


  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
