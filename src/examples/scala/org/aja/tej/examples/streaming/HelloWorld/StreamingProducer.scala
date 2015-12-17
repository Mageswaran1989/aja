package org.aja.tej.examples.streaming.HelloWorld

import java.io.PrintWriter
import java.net.ServerSocket

import scala.util.Random

/**
 * Created by mdhandapani on 16/12/15.
 *
 * Reference: Machine Learning with Spark
 */



/*A producer application that generates random "product events",
up to 5 per second, and sends them over a
network connection*/
object StreamingProducer {
  def main(args: Array[String]) {
    val random = new Random()

    // Maximum number of events per second
    val MaxEvents = 6

    // Read the list of possible names
    val names = scala.io.Source.fromFile("data/names.csv")
      .getLines()
      .toList
      .head
      .split(",")
      .toSeq

    // Generate a sequence of possible products
    val products = Seq("iPhone Cover" -> 999,
      "Headphones" -> 549,
      "Samsung Galaxy Cover" -> 895,
      "iPad Cover" -> 749
    )

    /** Generate a number of random product events */
    def generateProductEvents(n: Int) = {
      (1 to n).map { i =>
        val (product, price) =
          products(random.nextInt(products.size))
        val user = random.shuffle(names).head
        (user, product, price)
      }
    }

    // create a network producer
    val listener = new ServerSocket(9999)
    println("Listening on port: 9999")

    while (true) {
      val socket = listener.accept()
      new Thread() {
        override def run = {
          println("Got client connected from: " + socket.getInetAddress)
          val out = new PrintWriter(socket.getOutputStream(), true)
          while (true) {
            Thread.sleep(1000)
            val num = random.nextInt(MaxEvents)
            val productEvents = generateProductEvents(num)
            productEvents.foreach{ event =>
              out.write(event.productIterator.mkString(","))
              out.write("\n")
            }
            out.flush()
            println(s"Created $num events...")
          }
          socket.close()
        }
      }.start()
    }
  }
}

