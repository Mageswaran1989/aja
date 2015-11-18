package org.aja.tantra.examples.threads

/**
 * Created by mageswaran on 17/11/15.
 */
object MainThread extends App {

  val thread = Thread.currentThread()

  println(thread.getName)

  thread.setName("Aja")

  println(thread.getName)

  println(thread.getPriority)

  try {
    for (i <- 0 to 10) {
      println(i)
      Thread.sleep(1000)
    }
  } catch {
    case e: InterruptedException => println("Aja main thread interrupted")
  }
}
