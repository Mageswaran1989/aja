package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Calls stats and extracts the mean component. The approximate version of the function
can finish somewhat faster in some scenarios. However, it trades accuracy for speed.

stdev[Double] , sampleStdev[Double]
Calls stats and extracts either stdev -component or corrected sampleStdev -component.
stdDev = sqrt((1/N) * sum(xi -mean)(i=1 to N)
sampleStdDev = sqrt((1/N-1) * sum(xi -mean)(i=1 to N)

 variance[Double] , sampleVariance[Double]
Calls stats and extracts either variance-component or corrected sampleVariance-component.

 */
object MeanExample {
def useCases(sc:SparkContext) = {
  val a = sc . parallelize ( List (9.1 , 1.0 , 1.2 , 2.1 , 1.3 , 5.0 , 2.0 , 2.1 ,
    7.4 , 7.5 , 7.6 , 8.8 , 10.0 , 8.9 , 5.5) , 3)
  a . mean

  val d = sc . parallelize ( List (0.0 , 0.0 , 1.0) , 3)
  d.stdev()
  d.sampleStdev()

  val a1 = sc . parallelize ( List (9.1 , 1.0 , 1.2 , 2.1 , 1.3 , 5.0 , 2.0 , 2.1 ,
    7.4 , 7.5 , 7.6 , 8.8 , 10.0 , 8.9 , 5.5) , 3)
  a1 . variance

  val x = sc . parallelize ( List (1.0 , 2.0 , 3.0 , 5.0 , 20.0 , 19.02 , 19.29 ,
    11.09 , 21.0) , 2)
  x . variance
  x . sampleVariance


}
}
