//package org.aja.tej.examples.usecases.crawler
//
//import org.apache.spark.rdd.RDD
//import org.apache.spark.{Partition, TaskContext, SparkContext, SparkConf}
//
//import scala.collection.mutable.ArrayBuffer
//
///**
// * Created by mdhandapani on 7/10/15.
// */
//
////When the application become a spark one, same procedure happens but encapsulate in Spark notion:
//// we can customize a CrawlRDD do the same staff:
////
////Split sites: def getPartitions: Array[Partition] is a good place to do the split task.
////Threads to crawl each split: def compute(part: Partition, context: TaskContext): Iterator[X] will be spread to
//// all the executors of your application, run in parallel.
////save the rdd into HDFS.
//
//class CrawlPartition(rddId: Int, idx: Int, val baseURL: String) extends Partition {
//  override def index: Int = 0
//}
//
//class CrawlRDD(baseURL: String, sc: SparkContext) extends RDD(sc, Nil) {
//
//  override protected def getPartitions: Array[CrawlPartition] = {
//    val partitions = new ArrayBuffer[CrawlPartition]
//    //split baseURL to subsets and populate the partitions
//    partitions.toArray
//  }
//
//  override def compute(part: Partition, context: TaskContext): Iterator = {
//    val p = part.asInstanceOf[CrawlPartition]
//    val baseUrl = p.baseURL
//
//    new Iterator {
//      var nextURL = _
//      override def hasNext: Boolean = {
//        //logic to find next url if has one, fill in nextURL and return true
//        // else false
//      }
//
//      override def next() = {
//        //logic to crawl the web page nextURL and return the content in X
//      }
//    }
//  }
//}
//
//object Crawl {
//  def main(args: Array[String]) {
//    val sparkConf = new SparkConf().setAppName("Crawler")
//    val sc = new SparkContext(sparkConf)
//    val crdd = new CrawlRDD("http://stackoverflow.com/questions/29950299/distributed-web-crawling-using-apache-spark", sc)
//    crdd.saveAsTextFile("output/crawler")
//    sc.stop()
//  }
//}
