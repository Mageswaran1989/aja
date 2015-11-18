package org.aja.tantra.examples.concurrency.akka.threads

/**
 * Created by mageswaran on 17/11/15.
 */
object RunnableDemo extends Runnable{
  override def run(): Unit = {
    println(" I am in thread: " + Thread.currentThread().getName)

    while(true) {
      print("-")
    }
  }
}

object RunnableClass extends App {

}