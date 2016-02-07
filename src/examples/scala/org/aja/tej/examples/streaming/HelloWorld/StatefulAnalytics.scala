package org.aja.tej.examples.streaming.HelloWorld

import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mdhandapani on 16/12/15.
 */
//!!!!!!!!!!!!!!Start StreamProducer!!!!!!!!!!!!!!!!!!!!!!!!

object StatefulAnalytics {

  def updateState(prices: Seq[(String, Double)], currentTotal: Option[(Int, Double)]) = {
    val currentRevenue = prices.map(_._2).sum
    val currentNumberPurchases = prices.size
    val state = currentTotal.getOrElse((0, 0.0))
    Some((currentNumberPurchases + state._1, currentRevenue +  state._2))
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
      //userid   prodId      price
      (event(0), event(1), event(2).toDouble)
    }

    val users = events.map{ case (user, product, price) =>
      (user, (product, price)) }

    val revenuePerUser = users.updateStateByKey(updateState)

    revenuePerUser.print()
    // start the context
    ssc.start()
    ssc.awaitTermination()

  }
}

//(Janet,(1,895.0))
//(Juan,(2,1498.0))
//(Frank,(3,2339.0))
//(James,(3,2497.0))
//(Michael,(3,1847.0))
//(Miguel,(1,999.0))
//(Shawn,(2,1644.0))
//(Eric,(2,1444.0))
//(Doug,(4,3434.0))
//
//
//(Janet,(4,2992.0))
//(Juan,(2,1498.0))
//(Frank,(4,3088.0))
//(James,(9,7929.0))
//(Gary,(4,2796.0))
//(Michael,(7,4589.0))
//(Miguel,(3,2643.0))
//(Shawn,(4,3288.0))
//(Eric,(5,3991.0))
//(Doug,(6,4732.0))
//
//(Janet,(6,4436.0))
//(Juan,(4,2796.0))
//(Frank,(8,6334.0))
//(James,(13,10975.0))
//(Gary,(5,3795.0))
//(Michael,(10,6436.0))
//(Miguel,(3,2643.0))
//(Shawn,(7,5827.0))
//(Eric,(10,8182.0))
//(Doug,(8,6480.0))