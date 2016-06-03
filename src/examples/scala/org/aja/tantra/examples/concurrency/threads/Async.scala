//package org.aja.tantra.examples.concurrency.threads
//
//import java.util.Timer
//import java.util.TimerTask
//import scala.concurrent._
//import scala.concurrent._
//import scala.concurrent.duration._
//import scala.async.Async._
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.async.Async.async
//import scala.async.Async.await
//import scala.concurrent.Await
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import scala.concurrent.duration.Duration
//import scala.concurrent.duration.DurationInt
//import scala.concurrent.future
//import scala.concurrent.promise
//import scala.language.postfixOps
//import scala.util.Failure
//import scala.util.Success
//import scala.concurrent.ExecutionContext
//import scala.concurrent.Promise
//
///**
// * Created by mageswaran on 29/3/16.
// */
//
///**
// * Create Future[T] instances which will be completed after a delay.
// */
//object TimedEvent {
//  val timer = new Timer
//
//  /** Return a Future which completes successfully with the supplied value after secs seconds. */
//  def delayedSuccess[T](secs: Int, value: T): Future[T] = {
//    val result = Promise[T]
//    timer.schedule(new TimerTask() {
//      def run() = {
//        result.success(value)
//      }
//    }, secs * 1000)
//    result.future
//  }
//
//  /** Return a Future which completes failing with an IllegalArgumentException after secs
//    * seconds. */
//  def delayedFailure(secs: Int, msg: String): Future[Int] = {
//    val result = Promise[Int]
//    timer.schedule(new TimerTask() {
//      def run() = {
//        result.failure(new IllegalArgumentException(msg))
//      }
//    }, secs * 1000)
//    result.future
//  }
//}
//
//object AsyncHappy extends App {
//  import ExecutionContext.Implicits.global
//
//  // task definitions
//  def task1(input: Int) = TimedEvent.delayedSuccess(1, input + 1)
//  def task2(input: Int) = TimedEvent.delayedSuccess(2, input + 2)
//  def task3(input: Int) = TimedEvent.delayedSuccess(3, input + 3)
//  def task4(input: Int) = TimedEvent.delayedSuccess(1, input + 4)
//
//  /** Run tasks with block waits. */
//  def runBlocking() = {
//    val v1 = Await.result(task1(1), Duration.Inf)
//    val future2 = task2(v1)
//    val future3 = task3(v1)
//    val v2 = Await.result(future2, Duration.Inf)
//    val v3 = Await.result(future3, Duration.Inf)
//    val v4 = Await.result(task4(v2 + v3), Duration.Inf)
//    val result = Promise[Int]
//    result.success(v4)
//    result.future
//  }
//
//  /** Run tasks with callbacks. */
////  def runOnSuccess() = {
////    val result = Promise[Int]
////    task1(1).onSuccess(v => v match {
////      case v1 => {
////        val a = task2(v1)
////        val b = task3(v1)
////        a.onSuccess(v => v match {
////          case v2 =>
////            b.onSuccess(v => v match {
////              case v3 => task4(v2 + v3).onSuccess(v4 => v4 match {
////                case x => result.success(x)
////              })
////            })
////        })
////      }
////    })
////    result.future
////  }
//
//  /** Run tasks with flatMap. */
//  def runFlatMap() = {
//    task1(1) flatMap {v1 =>
//      val a = task2(v1)
//      val b = task3(v1)
//      a flatMap { v2 =>
//        b flatMap { v3 => task4(v2 + v3) }}
//    }
//  }
//
//  def timeComplete(f: () => Future[Int], name: String) {
//    println("Starting " + name)
//    val start = System.currentTimeMillis
//    val result = Await.result(f(), Duration.Inf)
//    val time = System.currentTimeMillis - start
//    println(name + " returned " + result + " in " + time + " ms.")
//  }
//
//}
//
//object AsyncUnhappy {
//  import ExecutionContext.Implicits.global
//
//  // task definitions
//  def task1(input: Int) = TimedEvent.delayedSuccess(1, input + 1)
//  def task2(input: Int) = TimedEvent.delayedSuccess(2, input + 2)
//  def task3(input: Int) = TimedEvent.delayedSuccess(3, input + 3)
//  def task4(input: Int) = TimedEvent.delayedFailure(1, "This won't work!")
//
//  /** Run tasks with block waits. */
//  def runBlocking() = {
//    val result = Promise[Int]
//    try {
//      val v1 = Await.result(task1(1), Duration.Inf)
//      val future2 = task2(v1)
//      val future3 = task3(v1)
//      val v2 = Await.result(future2, Duration.Inf)
//      val v3 = Await.result(future3, Duration.Inf)
//      val v4 = Await.result(task4(v2 + v3), Duration.Inf)
//      result.success(v4)
//    } catch {
//      case t: Throwable => result.failure(t)
//    }
//    result.future
//  }
//
//  /** Run tasks with callbacks. */
//  def runOnComplete() = {
//    val result = Promise[Int]
//    task1(1).onComplete(v => v match {
//      case Success(v1) => {
//        val a = task2(v1)
//        val b = task3(v1)
//        a.onComplete(v => v match {
//          case Success(v2) =>
//            b.onComplete(v => v match {
//              case Success(v3) => task4(v2 + v3).onComplete(v4 => v4 match {
//                case Success(x) => result.success(x)
//                case Failure(t) => result.failure(t)
//              })
//              case Failure(t) => result.failure(t)
//            })
//          case Failure(t) => result.failure(t)
//        })
//      }
//      case Failure(t) => result.failure(t)
//    })
//    result.future
//  }
//
//  /** Run tasks with flatMap. */
//  def runFlatMap() = {
//    task1(1) flatMap { v1 =>
//      val a = task2(v1)
//      val b = task3(v1)
//      a flatMap { v2 =>
//        b flatMap { v3 => task4(v2 + v3) }
//      }
//    }
//  }
//
//  /** Run tasks with async macro. */
//  def runAsync(): Future[Int] = {
//    async {
//      val v1 = await(task1(1))
//      val a = task2(v1)
//      val b = task3(v1)
//      await(task4(await(a) + await(b)))
//    }
//  }
//
//  def timeComplete(f: () => Future[Int], name: String) {
//    println("Starting " + name)
//    val start = System.currentTimeMillis
//    val future = f()
//    try {
//      val result = Await.result(future, Duration.Inf)
//      val time = System.currentTimeMillis - start
//      println(name + " returned " + result + " in " + time + " ms.")
//    } catch {
//      case t: Throwable => {
//        val time = System.currentTimeMillis - start
//        println(name + " threw " + t.getClass.getName + ": " + t.getMessage + " after " + time + " ms.")
//      }
//    }
//  }
//
//
//
//}
//
//object Async extends App {
//
//  AsyncHappy.timeComplete(AsyncHappy.runBlocking, "runBlocking")
//  //  timeComplete(runOnSuccess, "runOnSuccess")
//  AsyncHappy.timeComplete(AsyncHappy.runFlatMap, "runFlatMap")
//
//
//  AsyncUnhappy.timeComplete(AsyncUnhappy.runBlocking, "runBlocking")
//  AsyncUnhappy.timeComplete(AsyncUnhappy.runOnComplete, "runOnComplete")
//  AsyncUnhappy.timeComplete(AsyncUnhappy.runFlatMap, "runFlatMap")
//  AsyncUnhappy.timeComplete(AsyncUnhappy.runAsync, "runAsync")
//
//  // force everything to terminate
//  System.exit(0)
//}
