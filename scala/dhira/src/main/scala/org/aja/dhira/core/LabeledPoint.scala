package org.aja.dhira.core

import org.aja.dhira.core.Types.{LabeledPoint, DoubleList}

import scala.annotation.implicitNotFound
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.{Try, Success, Failure}

/**
 * <p> Inspired by Apache Spark LabeledPoint with some extra cheese</p>
 */
class LabeledPoint[T](val label: String, array:Array[T]) {
  require(!array.isEmpty, "LabeledPoint cannot be created with undefined values")

  final def toArray: Array[T] = array
  final def toList: List[T] = array.toList

  final def head: T = array.head
  final def last: T = array.last

  final def size = array.size

//  @inline
//  final def isEmpty: Boolean = array.isEmpty

  def == (that: LabeledPoint[T]): Boolean = {
    require(!that.isEmpty, "LabeledPoint.== cannot comapare with unidentified labelpoint series")
    size == that.size && array.equals(that.toArray)
  }

  @implicitNotFound("Conversion from type T to DoubleList is undefined")
  def toDoubleList(implicit f: T => Double): DoubleList = array.map(f(_))

  def + (n: Int, t: T)(implicit f: (T, T)=> T) = f(array(n), t)

  @inline
  def tail: LabeledPoint[T] = new LabeledPoint[T](label, array.tail)

  def take(n: Int): LabeledPoint[T] = new LabeledPoint[T](label, array.take(n))
  def takeRight(n: Int): LabeledPoint[T] = new  LabeledPoint[T](label, array.takeRight(n))

  def drop(n: Int): LabeledPoint[T] = new LabeledPoint[T](label, array.drop(n))
  def dropRight(n: Int): LabeledPoint[T] = new LabeledPoint[T](label, array.dropRight(n))

  def map[U: ClassTag](f: T => U): LabeledPoint[U] = new LabeledPoint[U](label, array.map(f(_))

  def apply(n: Int): T = array.apply(n)


  def zip[U](that: LabeledPoint[U]): LabeledPoint[(T, U)] = LabeledPoint[(T,U)](array.zip(that.toArray))

  def slice(start: Int, end: Int): LabeledPoint[T] = {
    require(start < array.size && end <= array.size && start < end,
      s"LabeledPoint.slice starting $start or ending $end index incorrect")
    new LabeledPoint[T](label, array.slice(start, end))
  }

  @inline
  final def isEmpty: Boolean = size == 0

  final def max(implicit cmp: Ordering[T]): T = array.max


  final def min(implicit cmp: Ordering[T]): T = array.min


  override def toString: String =  array.mkString("\n")

  final val size: Int = array.size

  def foreach( f: T => Unit) = array.foreach(f)

  def sortWith(lt: (T,T)=>Boolean): LabeledPoint[T] = LabeledPoint[T](label, array.sortWith(lt))

  def zipWithIndex: LabeledPoint[(T, Int)] = LabeledPoint[(T, Int)](label, array.zipWithIndex)

  def foldLeft[U](z: U)(op: (U, T)=> U): U = array.foldLeft(z)(op)

}


/**
 * <p>Companion object for time series, that define constructors and most
 * common implicit conversions.</p>
 * @author Patrick Nicolas
 * @since January, 22, 2014
 * @note Scala for Machine Learning Chapter 3 Data pre-processing / Time series
 */
object LabeledPoint {
//  private val logger = Logger.getLogger("LabeledPoint")

  type DblSeries = LabeledPoint[Double]
  type DblVecSeries = LabeledPoint[DoubleList]

  final val EPS = 1-20

  /**
   * Constructor for LabeledPoint with a predefined label and array of elements (or data points)
   * @param label Name for the time series (optional)
   * @param arr Array of values of the parameterized T
   */
  def apply[T](label: String, arr: Array[T]): LabeledPoint[T] = new LabeledPoint[T](label, arr)


  /**
   * Constructor for LabeledPoint with a predefined list of elements (or data points)
   * @param label Name for the time series (optional)
   * @param arr Array of values of the parameterized T
   */
  def apply[T](arr: Array[T]): LabeledPoint[T] = new LabeledPoint[T]("", arr)

  /**
   * Constructor for LabeledPoint with a predefined array of elements (or data points)
   * @param arr Array of values of the parameterized T
   */
  def apply[T: ClassTag](xs: List[T]): LabeledPoint[T] = new LabeledPoint[T]("", xs.toArray)

  /**
   * Implicit conversion of a vector and label to a LabeledPoint
   * @param label label for the time series
   * @param v vector to convert
   */
  implicit def LabeledPoint[T: ClassTag](label: String, v: Vector[T]) =
    new LabeledPoint[T](label, v.toArray)

  /**
   * Implicit conversion of a list to LabeledPoint
   * @param xs List to convert
   */
  implicit def LabeledPoint[T: ClassTag](xs: List[T]): LabeledPoint[T] =
    new LabeledPoint[T]("", xs.toArray)

  /**
   * Implicit conversion (deep copy) of this time series
   * @param xt Time series to duplicate
   */
  implicit def LabeledPoint[T](xt: LabeledPoint[T]) = new LabeledPoint[T]("", xt.toArray)

  /**
   * Implicit conversion of a time series to a vector
   * @param xt time series to convert
   */
  implicit def series2DblVector[T](xt: LabeledPoint[T])(implicit f: T => Double): DoubleList =
    xt.toDoubleList(f)

  /**
   * Implicit conversion of a time series to Matrix of type Double
   *  @param xt time series to convert
   */
  implicit def series2DblMatrix[T](xt: LabeledPoint[T])(implicit fv: T => DoubleList): DblMatrix =
    xt.toDblMatrix(fv)

  /**
   * Retrieve the dimension of the time series that is the number of variable in
   * each feature or observations or data points
   * @param xt time series of arrays
   */
  def dimension[T](xt: LabeledPoint[Array[T]]): Int = xt.toArray(0).size

  /**
   * Define an empty time series of type LabeledPoint
   */
  def empty[T: ClassTag]: LabeledPoint[T] = new LabeledPoint[T]("", Array.empty[T])

  /**
   * Convert a list of observations (vector) to a list of time series of these observations
   * @param xt List of observations to convert
   */
  def |>[T] (xs: List[Array[T]]): List[LabeledPoint[T]] = xs map{ LabeledPoint[T](_) }

  /**
   * Implements the normalization of a parameterized time series
   * @param xt single dimension parameterized time series
   * @throws IllegalArgumentException if the time series is undefined
   * @throws implicitNotFound if the implicit ordering is undefined
   * @return normalized time series as double elements if max > min, None otherwise
   */
  @implicitNotFound("Ordering for normalization is undefined")
  def normalize[T <% Double](xt: LabeledPoint[T])(implicit ordering: Ordering[T]): Option[DblSeries] = {
    require( !xt.isEmpty, "LabeledPoint.normalize Cannot normalize an undefined time series")

    val mn = xt.min
    val range = xt.max - mn
    if(range < EPS) None  else Some(xt.map(x => (x -mn)/range))
  }

  /**
   * Implements the normalization of a parameterized single dimension time series within [0, 1]
   * @param x a parameterized array
   * @throws IllegalArgumentException if the time series is undefined
   * @throws implicitNotFound if the implicit ordering is undefined
   * @return normalized time series as double elements if max > min, None otherwise
   */
  @implicitNotFound("Ordering for normalization is undefined")
  def normalize[T <% Double](x: Array[T])(implicit ordering: Ordering[T]): Option[DblVector] = {
    require( !x.isEmpty, "LabeledPoint.normalize  Cannot normalize an undefined time vector")

    val mn = x.min
    val range = x.max - mn
    if(range < EPS) None else Some(x.map(x => (x -mn)/range))
  }

  /**
   * Implements the normalization of a parameterized multi-dimension time series within [0, 1]
   * @param xt multi-dimension parameterized time series
   * @throws IllegalArgumentException if the time series is undefined
   * @return normalized time series as double elements if max > min, None otherwise
   */
  @implicitNotFound("LabeledPoint.normalize Ordering for normalization is undefined")
  def normalize[T <% Double](
                              xt: LabeledPoint[Array[T]])
                            (implicit order: Ordering[T], m: Manifest[T]): Option[DblVecSeries] = {
    require( !xt.isEmpty,
      "LabeledPoint.normalize Cannot normalize an undefined time series of elements")
    require( LabeledPoint.dimension(xt) > 0,
      "LabeledPoint.normalize Incorrect function to normalize a single dimension time series")

    var k = 0;
    val res = new Array[Array[T]](xt.size)
    val dimension = xt(0).size

    val min = Array.fill(dimension)( Double.MaxValue)
    val max = Array.fill(dimension)(-Double.MaxValue)

    // computes min and max
    while( k < xt.size) {
      var j = 0
      while( j < dimension) {
        if( xt(k)(j) < min(j))
          min(j) = xt(k)(j)
        else if( xt(k)(j) > max(j))
          max(j) = xt(k)(j)
        j += 1
      }
      k += 1
    }
    val arr = new DblMatrix(xt.size)
    k = 0

    Try {
      while( k < xt.size) {
        var j = 0
        arr(k) = new Array[Double](dimension)
        while( j < dimension) {
          arr(k)(j) =(xt.toArray(k)(j) - min(j))/(max(j)-min(j))
          j += 1
        }
        k += 1
      }
      new LabeledPoint[DblVector](xt.label, arr)
    }
    match {
      case Success(xt) => Some(xt)
      case Failure(e) => DisplayUtils.none("LabeledPoint.normalize", logger, e)
    }
  }

  /**
   * <p>transform time series of parameterized array into a array of double vector
   * by applying the Z score transform to each element of the time series.</p>
   * @param xt multi-dimensional parameterized time series
   * @throws IllegalArgumentException if the time series is undefined
   * @return Time series of double array if the function succeeds, None otherwise
   */
  def zScoring[T <% Double](xt: LabeledPoint[Array[T]]): Option[LabeledPoint[DblVector]] = {
    require( !xt.isEmpty, "LabeledPoint.zScoring Cannot zScore an undefined time series")

    val stats = statistics(xt)
    var k = 0;
    val dimension = xt(0).size

    val arr = new Array[DblVector](xt.size)
    Try {
      while( k < xt.size) {
        var j = 0
        arr(k) = new Array[Double](dimension)
        while( j < dimension) {
          arr(k)(j) =(xt.toArray(k)(j) - stats(j).mean)/stats(j).stdDev
          j += 1
        }
        k += 1
      }
      new LabeledPoint[DoubleList](xt.label, arr)
    }
    match {
      case Success(xt) => Some(xt)
      case Failure(e) => DisplayUtils.none("LabeledPoint.zScoring", logger, e)
    }
  }

  /**
   * Transpose an array of array of data
   * @param from Array of Array to convert
   */
  def transpose[T](from: Array[Array[T]]): Array[Array[T]] = from.transpose

  /**
   * Transpose a list of array into an array of array
   * @param from List of observations to transpose
   */
  def transpose[T: ClassTag](from: List[Array[T]]):  Array[Array[T]] = from.toArray.transpose

  /**
   * Compute the basic aggregate statistics for a time series
   * @param xt time series for which the statistics are computed
   * @return Statistics instance
   */
  def statistics[T <% Double](xt: LabeledPoint[T]): Stats[T] = Stats[T](xt.toArray)

  /**
   * Compute the basic statistics for each dimension of a time series
   * @param xt time series for which the statistics are computed
   * @return Array of statistics for each dimension
   */
  def statistics[T <% Double](xt: LabeledPoint[Array[T]]): Array[Stats[T]] = {
    require( !xt.isEmpty, "LabeledPoint.statistics input time series undefined")

    import Stats._
    val transposed = xt.toArray.transpose
    val results = transposed.map(Stats[T]( _ ))
    results
  }
}
