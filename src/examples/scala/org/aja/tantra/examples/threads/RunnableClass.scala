package org.aja.tantra.examples.threads

/**
 * Created by mageswaran on 17/11/15.
 */
object RunnableDemo extends Runnable{
  override def run(): Unit = {
    println(" I am in thread: " + Thread.currentThread().getName)

    while(1) {
      print("-")
    }
  }
}

object RunnableClass extends App {

}