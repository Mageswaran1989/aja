package org.aja.tej.examples.streaming.HelloWorld

import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by mdhandapani on 16/12/15.
 */
//!!!!!!!!!!!!!!Start StreamProducer!!!!!!!!!!!!!!!!!!!!!!!!

object StreamingApp {

  def main(args: Array[String]) {
    val ssc = new StreamingContext("local[2]", "First Streaming App", Seconds(1))
    val stream = ssc.socketTextStream("localhost", 9999)
    // here we simply print out the first few elements of each
    // batch
    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
