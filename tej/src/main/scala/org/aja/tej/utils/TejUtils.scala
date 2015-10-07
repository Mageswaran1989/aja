package org.aja.tej.utils

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by mageswaran on 26/7/15.
 */
object TejUtils {

  def getSparkContext(applicationName: String, master: String = "local[4]") = {
    val conf = new SparkConf().setAppName(applicationName).setMaster(master /*"spark://myhost:7077"*/)
    new SparkContext(conf)
  }

  def getRawData(sc: SparkContext, filePath: String, numPartions: Int = 2) = {
    sc.textFile(filePath, numPartions)
  }
}
