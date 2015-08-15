package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Retrieves the currently set storage level of the RDD. This can only be used to assign a
new storage level if the RDD does not have a storage level set yet. The example below
shows the error you will get, when you try to reassign the storage level.

 */
object GetStorageLevelExample {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize (1 to 100000 , 2)
    a . persist (org.apache.spark.storage . StorageLevel.DISK_ONLY)
    a . getStorageLevel . description
    a . cache


  }
}
