//package org.aja.dhira.exploration
//
///**
// * Created by mageswaran on 7/9/15.
// */
//
//import com.nativelibs4java.opencl._
//import com.nativelibs4java.opencl.CLEvent.CommandExecutionStatus
//import scala.collection.mutable.ArrayBuffer
//import breeze.numerics.sqrt
//import breeze.stats.MeanAndVariance
//
//class CLProfiler(actuallyProfile: Boolean) {
//  private var startingWallTime: Long = -1L
//  private var totalWallTime: Long = 0
//
//  def eventTimer(timerName: String) = new EventTimer(timerName)
//
//  def tick() {
//    startingWallTime = System.currentTimeMillis
//  }
//
//  def tock() = {
//    assert(startingWallTime != -1L)
//    val timeOut = System.currentTimeMillis
//    totalWallTime += (timeOut - startingWallTime)
//    startingWallTime = -1L
//  }
//
//  override def toString():String = report("")
//
//  def report(name: String) = {
//    val header = s"Profile for phase $name {"
//    val accounted = allTimers.map(_.processingTime).sum
//    val time = f"Wall Clock Time: ${totalWallTime/1E3}%.3fs of which ${accounted}%.6fs is accounted for in processing. (${accounted / totalWallTime * 1E5}%.3f%%)"
//    allTimers.filter(_.processingTime != 0.0).mkString(s"$header\n  $time\n  ", "\n  ","\n}")
//  }
//
//  private val allTimers = new ArrayBuffer[EventTimer]()
//
//  def clear() { allTimers foreach (_.clear()); totalWallTime = 0}
//
//  class EventTimer(portion: String) {
//    allTimers += this
//    private val events = collection.mutable.ArrayBuffer[CLEvent]()
//
//    def +=(event: CLEvent): this.type = {
//      if (actuallyProfile && event != null) events += event
//      this
//    }
//
//    def ++=(event: Traversable[CLEvent]): this.type = {
//      events foreach +=
//      this
//    }
//
//    def prof(events: Seq[CLEvent]): events.type = {
//      this ++= events
//      events
//    }
//
//    def prof(event: CLEvent): event.type = {
//      this += event
//      event
//    }
//
//    private[CLProfiler] def clear() {
//      events.clear()
//    }
//
//    def processingTime = {
//      val badEvents = events.filter(_ ne null).filter(_.getCommandExecutionStatus != CommandExecutionStatus.Complete)
//      if (badEvents.nonEmpty) {
//        println(s"Bunch of bad events! ${
//          badEvents.map {
//            x => x -> x.getCommandExecutionStatus
//          }
//        }")
//      }
//      val eventTimes = events.filter(_ ne null).filter(_.getCommandExecutionStatus == CommandExecutionStatus.Complete).map(e => (e.getProfilingCommandEnd - e.getProfilingCommandStart) / 1E9).sum
//      eventTimes
//    }
//
//    override def toString = {
//      val badEvents = events.filter(_ ne null).filter(_.getCommandExecutionStatus != CommandExecutionStatus.Complete)
//      if(badEvents.nonEmpty) {
//        println(s"Bunch of bad events! ${badEvents.map{x => x -> x.getCommandExecutionStatus}}")
//      }
//      val eventTimes = events.filter(_ ne null).filter(_.getCommandExecutionStatus == CommandExecutionStatus.Complete).map(e => (e.getProfilingCommandEnd - e.getProfilingCommandStart)/1E9)
//      val sum = eventTimes.sum
//      val MeanAndVariance(mean:Double, variance, _) = breeze.stats.meanAndVariance(eventTimes)
//      val std:Double = sqrt(variance)
//      //      val queueTimes = events.filter(_ ne null).filter(_.getCommandExecutionStatus == CommandExecutionStatus.Complete).map(e => (e.getProfilingCommandStart - e.getProfilingCommandQueued)/1E9).sum
//      //      val submitTimes = events.filter(_ ne null).filter(_.getCommandExecutionStatus == CommandExecutionStatus.Complete).map(e => (e.getProfilingCommandEnd - e.getProfilingCommandSubmit)/1E9).sum
//      f"Event Timer $portion: $sum%.6fs processing time. ${events.length} events, avg. $mean%.6fs per event, stddev $std%.6fs"
//    }
//  }
//
//
//
//}
//
////object HelloCL extends App{
////  import scalacl._
////
////  case class Matrix(data: CLArray[Float],
////                    rows: Int,
////                    columns: Int)
////                   (implicit context: Context)
////  {
////    def this(rows: Int, columns: Int)
////            (implicit context: Context) =
////      this(new CLArray[Float](rows * columns), rows, columns)
////
////    def this(n: Int)
////            (implicit context: Context) =
////      this(n, n)
////
////    def putProduct(a: Matrix, b: Matrix): Unit = {
////      assert(a.columns == b.rows)
////      assert(a.rows == rows)
////      assert(b.columns == columns)
////
////      kernel {
////        // This block will either be converted to an OpenCL kernel or cause compilation error
////        for (i <- 0 until rows;
////             j <- 0 until columns) {
////          // c(i, j) = sum(k, a(i, k) * b(k, j))
////          data(i * columns + j) = (
////            for (k <- 0 until a.columns) yield
////            a.data(i * a.columns + k) * b.data(k * b.columns + j)
////            ).sum
////        }
////      }
////    }
////
////    def putSum(a: Matrix, b: Matrix): Unit = {
////      assert(a.columns == b.columns && a.columns == columns)
////      assert(a.rows == b.rows && a.rows == rows)
////
////      kernel {
////        for (i <- 0 until rows; j <- 0 until columns) {
////          val offset = i * columns + j
////          data(offset) = a.data(offset) + b.data(offset)
////        }
////      }
////    }
////  }
////
////  implicit val context = Context.best
////
////  val n = 10
////  val a = new Matrix(n)
////  val b = new Matrix(n)
////  val out = new Matrix(n)
////
////  out.putProduct(a, b)
////
////  println(out.data)
////}
