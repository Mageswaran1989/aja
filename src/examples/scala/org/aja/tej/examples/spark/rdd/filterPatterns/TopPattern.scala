package org.aja.tej.examples.spark.rdd.filterPatterns

import org.aja.tej.utils.TejUtils

/**
 * Created by mageswaran on 19/3/16.
 */
object TopPattern  extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val nums = List(1 to 20: _*)
  val rdd = sc.parallelize(nums,5)

  rdd.top(5)

  //SELECT * FROM table ORDER BY col4 DESC LIMIT 10;


}
