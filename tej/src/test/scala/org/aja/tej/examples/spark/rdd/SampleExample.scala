package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 sample
Randomly selects a fraction of the items of a RDD and returns them in a new RDD.

 */
object SampleExample {

  def useCases(sc: SparkContext) = {

    val a = sc . parallelize (1 to 10000 , 3)
    a . sample ( false , 0.1 , 0) . count
    //res24 : Long = 960
    a . sample ( true , 0.3 , 0) . count
    //res25 : Long = 2888
    a . sample ( true , 0.3 , 13) . count
    //res26 : Long = 2985

  }

}
