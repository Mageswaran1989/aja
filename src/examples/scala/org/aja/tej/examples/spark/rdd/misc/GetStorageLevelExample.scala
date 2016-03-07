package org.aja.tej.examples.spark.rdd.misc

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Retrieves the currently set storage level of the RDD. This can only be used to assign a
new storage level if the RDD does not have a storage level set yet. The example below
shows the error you will get, when you try to reassign the storage level.

 */
object GetStorageLevelExample  extends App {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize (1 to 100000 , 2)
    a . persist (org.apache.spark.storage . StorageLevel.DISK_ONLY)
    a . getStorageLevel . description
    a . cache
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
