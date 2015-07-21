package org.aja.dhira.statistics

import org.aja.dhira.core.Types.{DVector, DoubleList}
import Statistics._

/**
 *  <p>Parameterized class (view bound T <% Double) that compute and update the statistics 
 *  (mean, standard deviation) for any set of observations for which the
 *  type can be converted to a Double.<br>
 *  This class is immutable as no elements can be added to the original set of values.</p>
 *  @constructor Create an immutable statistics instance for a vector of type T 
 *  @param values vector or array of elements of type T
 *  @throws IllegalArgumentException if values is either undefined or have no elements
 *  @author Patrick Nicolas
 *  @since Jan 24, 2014
 *  @note Scala for Machine Learning Chapter 2 Hello World!
 */
class Statistics[T <% Double](values: DVector[T]) {
  require( !values.isEmpty, "Statistics: Cannot initialize Statistics with undefined values")

  private class _Statistics(
                        var minValue: Double,
                        var maxValue: Double,
                        var sum: Double,
                        var sumSqr: Double)

  // Create the statistics used in the computation of the mean and variance
  private[this] val _Statistics = {
    val _Statistics = new _Statistics(Double.MaxValue, Double.MinValue, 0.0, 0.0)

    values.foreach(x => {
      if(x < _Statistics.minValue) _Statistics.minValue = x
      if(x > _Statistics.maxValue) _Statistics.maxValue = x
      _Statistics.sum += x
      _Statistics.sumSqr += x*x
    })
    _Statistics
  }

  /**
   * Arithmetic mean of the vector of values
   */
  @inline
  lazy val mean = _Statistics.sum/values.size

  /**
   * Computation of variance for the array values
   */
  lazy val variance = (_Statistics.sumSqr - mean*mean*values.size)/(values.size-1)

  /**
   * Computation of standard deviation for the array values
   */
  lazy val stdDev = if(variance < ZERO_EPS) ZERO_EPS else Math.sqrt(variance)

  /**
   * Computation of minimum values of a vector. This values is
   * computed during instantiation
   */
  lazy val min = _Statistics.minValue

  /**
   * Computation of minimum values of a vector. This values is
   * computed during instantiation
   */
  lazy val max = _Statistics.maxValue

  /**
   * Compute the Lidstone smoothing factor for a set of values
   * @param smoothing smoothing values ]0, 1] for Lidstone smoothing function
   * @param dim Dimension of the model
   * @return smoothed mean
   * @throws IllegalArgumentException if either the smoothing or dimension of the model is 
   * out of range
   */
  final def lidstoneMean(smoothing: Double, dim: Int): Double = {
    require( smoothing >0.0 && smoothing <= 1.0,
      s"Statistics.lidstoneMean Lidstone smoothing factor $smoothing is out of range")
    require(dim > 0, s"Statistics.lidstoneMean Dimension for Lidstone factor $dim is out of range")

    (_Statistics.sum + smoothing)/(values.size + smoothing*dim)
  }


  /**
   * Compute the Laplace smoothing factor for a set of values
   * @param smoothing smoothing values ]0, 1] for Laplace smoothing function
   * @return smoothed mean
   * @throws IllegalArgumentException if the smoothing factor is out of range
   */
  final def laplaceMean(dim: Int): Double = {
    require(dim > 0, s"Statistics.laplaceMean Dimension for Lidstone factor $dim is out of range")
    (_Statistics.sum + 1.0)/(values.size + dim)
  }

  /**
   * Fast normalization of values within a range of [0, 1]
   * @return vector with normaliSed values
   * @throws throw a Arithmetic exception if the minimum and maximum have identical values
   */
  def normalize: DoubleList = {
    val range = max - min
    assert( range > ZERO_EPS, s"Statistics.normalize: cannot normalize $min and $max")
    values.map(x => (x - min)/range)
  }

  /**
   * Normalization of values within a range [-0.5. 0.5]
   */
  def normalizeMean: DoubleList = normalize(-0.5, 0.5)

  /**
   * Normalize the data within a range [l, h]
   * @param l lower bound for the normalization
   * @param h higher bound for the normalization
   * @return vector of values normalized over the interval [0, 1]
   * @throws IllegalArgumentException of h <= l
   */
  def normalize(l: Double, h: Double): DoubleList = {
    require(h > l + ZERO_EPS, s"Statistics.normalize: cannot normalize between $l and $h")
    val range = h-l
    values.map( x =>(x - l)/range)
  }

  /**
   * Normalize the data set using the mean and standard deviation. It is assumed
   * that the data (values) follows a Gaussian distribution
   * @return vector of values transformed by the z-score
   * @throws  ArithmeticException in case of a divide by zero
   */
  def zScore: DoubleList = {
    assert(stdDev > ZERO_EPS, "Statistics.normalize Cannot compute zScore -  divide by zero")
    values.map(x => (x - mean)/stdDev )
  }
}

/**
 * Companion object to the Statistics class that define the main constructor
 * apply and the Gaussian distributions
 * @author Patrick Nicolas
 * @since January 24, 2014
 * @note Scala for Machine Learning Chapter 2 Hello World!
 */
object Statistics {
  final val ZERO_EPS = 1e-12
  final val INV_SQRT_2PI = 1.0/Math.sqrt(2.0*Math.PI)

  /**
   * Default constructor for statistics
   * @param values vector or array of elements of type T
   */
  def apply[T <% Double](values: Array[T]): Statistics[T] = new Statistics[T](values)

  /**
   * <p>Compute the Gauss density function for a vector given a mean and standard deviation</p>
   * @param mean mean values of the Gauss probability density function
   * @param stdDev standard deviation of the Gauss probability density function
   * @param values  array of variables for which the Gauss probability density function 
   * has to be computed
   * @return Gaussian probability 
   * @throws IllegalArgumentExeption if stdDev is close t zero or the values are not defined.
   */
  final def gauss(mean: Double, stdDev: Double, values: DoubleList) : DoubleList = {
    require(Math.abs(stdDev) >= ZERO_EPS,
      s"Statistics.gauss Standard deviation $stdDev is close to zero")
    require( !values.isEmpty, "Statistics.gauss Values are undefined")

    values.map( x => {
      val y = x - mean
      val variance = stdDev*stdDev
      INV_SQRT_2PI*Math.exp(-0.5*y*y /variance)/stdDev
    })
  }

  /**
   * <p>Compute the Gauss density function for a value given a mean and standard deviation</p>
   * @param mean mean values of the Gauss probability density function
   * @param stdDev standard deviation of the Gauss probability density function
   * @param value  value for which the Gauss probability density function has to be computed
   * @return Gaussian probability
   * @throws IllegalArgumentExeption if stdDev is close t zero
   */
  final def gauss(mean: Double, stdDev: Double, x: Double): Double = {
    require(Math.abs(stdDev) >= ZERO_EPS,
      s"Statistics.gauss, Gauss standard deviation $stdDev is close to zero")
    val y = x - mean
    val variance = stdDev*stdDev
    INV_SQRT_2PI*Math.exp(-0.5*y*y /variance)/stdDev
  }

  /**
   * <p>Compute the Gauss density value with a variable list of parameters</p>
   * @param x list of parameters
   * @return Gaussian probability
   * @throws IllegalArgumentExeption if stdDev is close to zero
   */
  final def gauss(x: Double*): Double = {
    require(x.size > 2, s"Statistics.gauss Number of parameters ${x.size} is out of range")
    gauss(x(0), x(1), x(2))
  }

  /**
   * <p>Compute the Normal (Normalized Gaussian) density (mean = 0, standard deviation = 1.0)</p>
   * @param x list of parameters
   * @return Gaussian probability
   * @throws IllegalArgumentExeption if stdDev is close to zero or the number of parameters 
   * is less than 3
   */
  final def normal(x: Double): Double = gauss(0.0, 1.0, x)

  /**
   * Compute the Bernoulli density given a mean and number of trials
   * @param mean mean value
   * @param p Number of trials
   */
  final def bernoulli(mean: Double, p: Int): Double = mean*p + (1-mean)*(1-p)

  /**
   * Compute the Bernoulli density given a mean and number of trials with a variable list 
   * of parameters
   * @param x list of parameters
   * @return Bernoulli probability 
   * @throws IllegalArgumentExeption if the number of parameters is less than 3
   */
  final def bernoulli(x: Double*): Double = {
    require(x.size > 2, s"Statistics.bernoulli Number of parameters ${x.size} is out of range")
    bernoulli(x(0), x(1).toInt)
  }
}