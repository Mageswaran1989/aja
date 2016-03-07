package org.aja.tej.examples.streaming.HelloWorld

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mdhandapani on 16/12/15.
 */

//!!!!!!!!!!!!!!Start StreamProducer!!!!!!!!!!!!!!!!!!!!!!!!

/*
which computes statistics and
prints the results for each batch in a DStream
 */
object SimpleAnalytics {

  def main(args: Array[String]): Unit = {
    val ssc = new StreamingContext("local[2]", "First Streaming App", Seconds(5))
    val stream = ssc.socketTextStream("localhost", 9999)
    // create stream of events from raw text elements
    val events = stream.map { record =>
      val event = record.split(",")
      (event(0), event(1), event(2))
      //(user, product, price)
    }


    //We compute and print out stats for each batch.
    //Since each batch is an RDD, we call forEeachRDD on the
    //DStream, and apply the usual RDD functions
    //we normally use

    events.foreachRDD { (rdd, time) =>
      val numPurchases = rdd.count()
      val uniqueUsers = rdd.map {
        case (user, _, _) => user
      }.distinct().count()

      val totalRevenue = rdd.map { case (_, _, price) =>
        price.toDouble }.sum()

      val productsByPopularity = rdd
        .map { case (user, product, price) => (product, 1) }
        .reduceByKey(_ + _)
        .collect()
        .sortBy(-_._2)

      val mostPopular = productsByPopularity(0)
      val formatter = new SimpleDateFormat
      val dateStr = formatter.format(new Date(time.milliseconds))
      println(s"== Batch start time: $dateStr ==")

      println("Total purchases: " + numPurchases)
      println("Unique users: "    + uniqueUsers)
      println("Total revenue: "   + totalRevenue)
      println("Most popular product: %s with %d purchases".format(mostPopular._1, mostPopular._2))
    }
    // start the context
    ssc.start()
    ssc.awaitTermination()
  }
}
