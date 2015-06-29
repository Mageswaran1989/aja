package org.aja.ml.stats

import scala.Array.canBuildFrom
import org.scalaml.core.Types.ScalaMl._
import Stats._

class Statistics[T](values: Vector[T])(implicit convertion: T => Double) {
  require(!values.isEmpty, "Statistics can't be done on empty series")

  private class __Statistics (
    var minValue: Double,
    var maxValue: Double,
    var sum: Double,
    var sumSqr: Double)

  private[this] val __statisticsData = {
  val __statLocal = new __Statistics(Double.MaxValue, Double.MinValue, 0, 0)

  values.foreach(value => {
      if(value < __statLocal.minValue) __statLocal.minValue = value
      if(value > __statLocal.maxValue) __statLocal.maxValue = value
      __statLocal.sum += value
      __statLocal.sumSqr = sum * sum
    }
    __statLocal
  } 

  lazy val mean: Double = __statisticsData.sum / values.size

  //http://www.theseashore.org.uk/theseashore/Stats%20for%20twits/T%20Test.html
  lazy val variance: Double = (__statisticsData.sumSqr - (__statisticsData.mean * __statisticsData.mean)/values.size)/ values.size - 1 
  
  //  lazy val variance: Double = (__statisticsData.sumSqr - (mean * mean))

  lazy stdDev: Double = if(variance < ZERO_EPSILON) ZERO_EPSILON else Math.sqrt(variance)

  lazy val min: Double = __statisticsData.minValue

  lazy val max: Double = __statisticsData.maxValue  
} 

object Statistics {
  final val ZERO_EPSILON = 1e-12
  final val INVERTED_SQRT_2PI = 1.0/Math.sqrt(2.0 * Math.PI)
 
} 
