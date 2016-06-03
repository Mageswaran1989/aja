package org.aja.tantra.examples.concurrency.threads

import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * Created by mageswaran on 28/3/16.
 */


object FutureExample extends App {

  val lastInteger = new AtomicInteger
  def futureInt() = Future {
    Thread sleep 2000
    lastInteger incrementAndGet
  }

  // use callbacks for completion of futures
  val a1 = futureInt //1
  val a2 = futureInt //2
  a1.onSuccess {
    case i1 => {
      a2.onSuccess {
        case i2 => println("1.Sum of values is " + (i1 + i2))
      }
    }
  }
 // Thread sleep 3000

  // use for construct to extract values when futures complete
  val b1 = futureInt //3
  val b2 = futureInt //4
  for (i1 <- b1; i2 <- b2) yield println("2.Sum of values is " + (i1 + i2))
  //Thread sleep 3000

  // wait directly for completion of futures
  val c1 = futureInt //5
  val c2 = futureInt //6
  println("3.Sum of values is " +
    (Await.result(c1, Duration.Inf) +
    Await.result(c2, Duration.Inf)))
}
