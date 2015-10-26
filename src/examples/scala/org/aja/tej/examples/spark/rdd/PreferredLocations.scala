package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Returns the hosts which are preferred by this RDD. The actual preference of a specific
host depends on various assumptions.

 */
object PreferredLocations  extends App {

  def useCases(sc: SparkContext) = {

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}
