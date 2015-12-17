package org.aja.tej.examples.streaming.HelloWorld

import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mdhandapani on 16/12/15.
 */
//!!!!!!!!!!!!!!Start StreamProducer!!!!!!!!!!!!!!!!!!!!!!!!

object StatefulAnalytics {

  def updateState(prices: Seq[(String, Double)], currentTotal:
  Option[(Int, Double)]) = {
    val currentRevenue = prices.map(_._2).sum
    val currentNumberPurchases = prices.size
    val state = currentTotal.getOrElse((0, 0.0))
    Some((currentNumberPurchases + state._1, currentRevenue +
      state._2))
  }

  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[2]", "First Streaming App", Seconds(10))
    // for stateful operations, we need to set a checkpoint
    // location
    ssc.checkpoint("/tmp/sparkstreaming/")
    val stream = ssc.socketTextStream("localhost", 9999)
    // create stream of events from raw text elements
    val events = stream.map { record =>
      val event = record.split(",")
      (event(0), event(1), event(2).toDouble)
    }
    val users = events.map{ case (user, product, price) => (user,
      (product, price)) }
    val revenuePerUser = users.updateStateByKey(updateState)
    revenuePerUser.print()
    // start the context
    ssc.start()
    ssc.awaitTermination()

  }
}