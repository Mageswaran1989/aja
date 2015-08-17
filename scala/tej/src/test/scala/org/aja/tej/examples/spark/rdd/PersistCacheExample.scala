package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
 persist, cache
These functions can be used to adjust the storage level of a RDD. When freeing up
memory, Spark will use the storage level identifier to decide which partitions should be
kept. The parameterless variants persist() and cache() are just abbreviations for per-
sist(StorageLevel.MEMORY ONLY). (Warning: Once the storage level has been changed,
it cannot be changed again!)

 unpersist
Dematerializes the RDD (i.e. Erases all data items from hard-disk and memory). How-
ever, the RDD object remains. If it is referenced in a computation, Spark will regenerate
it automatically using the stored dependency graph.

 */
object PersistCacheExample {
def useCases(sc: SparkContext) = {
  val c = sc . parallelize ( List (" Gnu " , " Cat " , " Rat " , " Dog " , " Gnu " , " Rat ") ,
    2)
  c.getStorageLevel
  c.cache()
  c.getStorageLevel

  c . unpersist ( true )
  c.getStorageLevel

}
}
