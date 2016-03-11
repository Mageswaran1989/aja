package org.aja.tej.examples.spark.rdd.summarizationPatterns

import org.aja.tej.examples.dataset.StackOverFlowUtility
import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 stats[Double]
Simultaneously computes the mean, variance and the standard deviation of all values in
the RDD.

 */
object StatExample  extends App {

  def useCases(sc: SparkContext) = {
    val x = sc.parallelize ( List (1.0 , 2.0 , 3.0 , 5.0 , 20.0 ,19.02 , 19.29 ,11.09 , 21.0) , 2)
    x.stats

    x.min()
    x.max()
    x.count()

    TejUtils.waitForSparkUI(sc)

  }


  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))


}
