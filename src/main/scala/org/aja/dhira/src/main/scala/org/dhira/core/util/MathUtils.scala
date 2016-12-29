/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */
package org.dhira.core.util

import org.apache.commons.math3.linear.CholeskyDecomposition
import org.apache.commons.math3.linear.NonSquareMatrixException
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.util.FastMath
import org.deeplearning4j.berkeley.Counter
import java.util.ArrayList
import java.util.List
import java.util.Random
import java.util.Set

/**
 * This is a math utils class.
 *
 * @author Adam Gibson
 *
 */
object MathUtils {
  /** The natural logarithm of 2. */
  var log2: Double = Math.log(2)

  /**
   * Normalize a value
   * (val - min) / (max - min)
   * @param val value to normalize
   * @param max max value
   * @param min min value
   * @return the normalized value
   */
  def normalize(`val`: Double, min: Double, max: Double): Double = {
    if (max < min) throw new IllegalArgumentException("Max must be greater than min")
    return (`val` - min) / (max - min)
  }

  /**
   * Clamps the value to a discrete value
   * @param value the value to clamp
   * @param min min for the probability distribution
   * @param max max for the probability distribution
   * @return the discrete value
   */
  def clamp(value: Int, min: Int, max: Int): Int = {
    if (value < min) value = min
    if (value > max) value = max
    return value
  }

  /**
   * Discretize the given value
   * @param value the value to discretize
   * @param min the min of the distribution
   * @param max the max of the distribution
   * @param binCount the number of bins
   * @return the discretized value
   */
  def discretize(value: Double, min: Double, max: Double, binCount: Int): Int = {
    val discreteValue: Int = (binCount * normalize(value, min, max)).toInt
    return clamp(discreteValue, 0, binCount - 1)
  }

  /**
   * See: http://stackoverflow.com/questions/466204/rounding-off-to-nearest-power-of-2
   * @param v the number to getFromOrigin the next power of 2 for
   * @return the next power of 2 for the passed in value
   */
  def nextPowOf2(v: Long): Long = {
    v -= 1
    v |= v >> 1
    v |= v >> 2
    v |= v >> 4
    v |= v >> 8
    v |= v >> 16
    v += 1
    return v
  }

  /**
   * Generates a binomial distributed number using
   * the given rng
   * @param rng
   * @param n
   * @param p
   * @return
   */
  def binomial(rng: RandomGenerator, n: Int, p: Double): Int = {
    if ((p < 0) || (p > 1)) {
      return 0
    }
    var c: Int = 0

    {
      var i: Int = 0
      while (i < n) {
        {
          if (rng.nextDouble < p) {
            c += 1
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return c
  }

  /**
   * Generate a uniform random number from the given rng
   * @param rng the rng to use
   * @param min the min num
   * @param max the max num
   * @return a number uniformly distributed between min and max
   */
  def uniform(rng: Random, min: Double, max: Double): Double = {
    return rng.nextDouble * (max - min) + min
  }

  /**
   * Returns the correlation coefficient of two double vectors.
   *
   * @param residuals residuals
   * @param targetAttribute target attribute vector
   *
   * @return the correlation coefficient or r
   */
  def correlation(residuals: Array[Double], targetAttribute: Double): Double = {
    val predictedValues: Array[Double] = new Array[Double](residuals.length)

    {
      var i: Int = 0
      while (i < predictedValues.length) {
        {
          predictedValues(i) = targetAttribute(i) - residuals(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val ssErr: Double = ssError(predictedValues, targetAttribute)
    val total: Double = ssTotal(residuals, targetAttribute)
    return 1 - (ssErr / total)
  }

  /**
   * 1 / 1 + exp(-x)
   * @param x
   * @return
   */
  def sigmoid(x: Double): Double = {
    return 1.0 / (1.0 + FastMath.exp(-x))
  }

  /**
   * How much of the variance is explained by the regression
   * @param residuals error
   * @param targetAttribute data for target attribute
   * @return the sum squares of regression
   */
  def ssReg(residuals: Array[Double], targetAttribute: Array[Double]): Double = {
    val mean: Double = sum(targetAttribute) / targetAttribute.length
    var ret: Double = 0

    {
      var i: Int = 0
      while (i < residuals.length) {
        {
          ret += Math.pow(residuals(i) - mean, 2)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * How much of the variance is NOT explained by the regression
   * @param predictedValues predicted values
   * @param targetAttribute data for target attribute
   * @return the sum squares of regression
   */
  def ssError(predictedValues: Array[Double], targetAttribute: Array[Double]): Double = {
    var ret: Double = 0

    {
      var i: Int = 0
      while (i < predictedValues.length) {
        {
          ret += Math.pow(targetAttribute(i) - predictedValues(i), 2)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * Calculate string similarity with tfidf weights relative to each character
   * frequency and how many times a character appears in a given string
   * @param strings the strings to calculate similarity for
   * @return the cosine similarity between the strings
   */
  def stringSimilarity(strings: String*): Double = {
    if (strings == null) return 0
    val counter: Nothing = new Nothing
    val counter2: Nothing = new Nothing
    {
      var i: Int = 0
      while (i < strings(0).length) {
        counter.incrementCount(String.valueOf(strings(0).charAt(i)), 1.0)
        ({
          i += 1; i - 1
        })
      }
    }
    {
      var i: Int = 0
      while (i < strings(1).length) {
        counter2.incrementCount(String.valueOf(strings(1).charAt(i)), 1.0)
        ({
          i += 1; i - 1
        })
      }
    }
    val v1: Set[String] = counter.keySet
    val v2: Set[String] = counter2.keySet
    val both: Set[String] = SetUtils.intersection(v1, v2)
    var sclar: Double = 0
    var norm1: Double = 0
    var norm2: Double = 0
    import scala.collection.JavaConversions._
    for (k <- both) sclar += counter.getCount(k) * counter2.getCount(k)
    import scala.collection.JavaConversions._
    for (k <- v1) norm1 += counter.getCount(k) * counter.getCount(k)
    import scala.collection.JavaConversions._
    for (k <- v2) norm2 += counter2.getCount(k) * counter2.getCount(k)
    return sclar / Math.sqrt(norm1 * norm2)
  }

  /**
   * Returns the vector length (sqrt(sum(x_i))
   * @param vector the vector to return the vector length for
   * @return the vector length of the passed in array
   */
  def vectorLength(vector: Array[Double]): Double = {
    var ret: Double = 0
    if (vector == null) return ret
    else {
      {
        var i: Int = 0
        while (i < vector.length) {
          {
            ret += Math.pow(vector(i), 2)
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    return ret
  }

  /**
   * Inverse document frequency: the total docs divided by the number of times the word
   * appeared in a document
   * @param totalDocs the total documents for the data applyTransformToDestination
   * @param numTimesWordAppearedInADocument the number of times the word occurred in a document
   * @return log(10) (totalDocs/numTImesWordAppearedInADocument)
   */
  def idf(totalDocs: Double, numTimesWordAppearedInADocument: Double): Double = {
    if (totalDocs == 0) return 0
    val idf: Double = Math.log10(totalDocs / numTimesWordAppearedInADocument)
    return idf
  }

  /**
   * Term frequency: 1+ log10(count)
   * @param count the count of a word or character in a given string or document
   * @return 1+ log(10) count
   */
  def tf(count: Int, documentLength: Int): Double = {
    val tf: Double = (count.toDouble / documentLength)
    return tf
  }

  /**
   * Return td * idf
   * @param tf the term frequency (assumed calculated)
   * @param idf inverse document frequency (assumed calculated)
   * @return td * idf
   */
  def tfidf(tf: Double, idf: Double): Double = {
    return tf * idf
  }

  private def charForLetter(c: Char): Int = {
    val chars: Array[Char] = Array('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
    {
      var i: Int = 0
      while (i < chars.length) {
        if (chars(i) == c) return i
        ({
          i += 1; i - 1
        })
      }
    }
    return -1
  }

  /**
   * Total variance in target attribute
   * @param residuals error
   * @param targetAttribute data for target attribute
   * @return Total variance in target attribute
   */
  def ssTotal(residuals: Array[Double], targetAttribute: Array[Double]): Double = {
    return ssReg(residuals, targetAttribute) + ssError(residuals, targetAttribute)
  }

  /**
   * This returns the sum of the given array.
   * @param nums the array of numbers to sum
   * @return the sum of the given array
   */
  def sum(nums: Array[Double]): Double = {
    var ret: Double = 0
    for (d <- nums) ret += d
    return ret
  }

  /**
   * This will merge the coordinates of the given coordinate system.
   * @param x the x coordinates
   * @param y the y coordinates
   * @return a vector such that each (x,y) pair is at ret[i],ret[i+1]
   */
  def mergeCoords(x: Array[Double], y: Array[Double]): Array[Double] = {
    if (x.length != y.length) throw new IllegalArgumentException("Sample sizes must be the same for each data applyTransformToDestination.")
    val ret: Array[Double] = new Array[Double](x.length + y.length)
    {
      var i: Int = 0
      while (i < x.length) {
        {
          ret(i) = x(i)
          ret(i + 1) = y(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * This will merge the coordinates of the given coordinate system.
   * @param x the x coordinates
   * @param y the y coordinates
   * @return a vector such that each (x,y) pair is at ret[i],ret[i+1]
   */
  def mergeCoords(x: List[Double], y: List[Double]): List[Double] = {
    if (x.size != y.size) throw new IllegalArgumentException("Sample sizes must be the same for each data applyTransformToDestination.")
    val ret: List[Double] = new ArrayList[Double]
    {
      var i: Int = 0
      while (i < x.size) {
        {
          ret.add(x.get(i))
          ret.add(y.get(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * This returns the minimized loss values for a given vector.
   * It is assumed that  the x, y pairs are at
   * vector[i], vector[i+1]
   * @param vector the vector of numbers to getFromOrigin the weights for
   * @return a double array with w_0 and w_1 are the associated indices.
   */
  def weightsFor(vector: List[Double]): Array[Double] = {
    val coords: List[Array[Double]] = coordSplit(vector)
    val x: Array[Double] = coords.get(0)
    val y: Array[Double] = coords.get(1)
    val meanX: Double = sum(x) / x.length
    val meanY: Double = sum(y) / y.length
    val sumOfMeanDifferences: Double = sumOfMeanDifferences(x, y)
    val xDifferenceOfMean: Double = sumOfMeanDifferencesOnePoint(x)
    val w_1: Double = sumOfMeanDifferences / xDifferenceOfMean
    val w_0: Double = meanY - (w_1) * meanX
    val ret: Array[Double] = new Array[Double](vector.size)
    ret(0) = w_0
    ret(1) = w_1
    return ret
  }

  /**
   * This will return the squared loss of the given
   * points
   * @param x the x coordinates to use
   * @param y the y coordinates to use
   * @param w_0 the first weight
   *
   * @param w_1 the second weight
   * @return the squared loss of the given points
   */
  def squaredLoss(x: Array[Double], y: Array[Double], w_0: Double, w_1: Double): Double = {
    var sum: Double = 0

    {
      var j: Int = 0
      while (j < x.length) {
        {
          sum += Math.pow((y(j) - (w_1 * x(j) + w_0)), 2)
        }
        ({
          j += 1; j - 1
        })
      }
    }
    return sum
  }

  def w_1(x: Array[Double], y: Array[Double], n: Int): Double = {
    return (n * sumOfProducts(x, y) - sum(x) * sum(y)) / (n * sumOfSquares(x) - Math.pow(sum(x), 2))
  }

  def w_0(x: Array[Double], y: Array[Double], n: Int): Double = {
    val weight1: Double = w_1(x, y, n)
    return (sum(y) - (weight1 * sum(x))) / n
  }

  /**
   * This returns the minimized loss values for a given vector.
   * It is assumed that  the x, y pairs are at
   * vector[i], vector[i+1]
   * @param vector the vector of numbers to getFromOrigin the weights for
   * @return a double array with w_0 and w_1 are the associated indices.
   */
  def weightsFor(vector: Array[Double]): Array[Double] = {
    val coords: List[Array[Double]] = coordSplit(vector)
    val x: Array[Double] = coords.get(0)
    val y: Array[Double] = coords.get(1)
    val meanX: Double = sum(x) / x.length
    val meanY: Double = sum(y) / y.length
    val sumOfMeanDifferences: Double = sumOfMeanDifferences(x, y)
    val xDifferenceOfMean: Double = sumOfMeanDifferencesOnePoint(x)
    val w_1: Double = sumOfMeanDifferences / xDifferenceOfMean
    val w_0: Double = meanY - (w_1) * meanX
    val ret: Array[Double] = new Array[Double](vector.length)
    ret(0) = w_0
    ret(1) = w_1
    return ret
  }

  def errorFor(actual: Double, prediction: Double): Double = {
    return actual - prediction
  }

  /**
   * Used for calculating top part of simple regression for
   * beta 1
   * @param vector the x coordinates
   * @param vector2 the y coordinates
   * @return the sum of mean differences for the input vectors
   */
  def sumOfMeanDifferences(vector: Array[Double], vector2: Array[Double]): Double = {
    val mean: Double = sum(vector) / vector.length
    val mean2: Double = sum(vector2) / vector2.length
    var ret: Double = 0

    {
      var i: Int = 0
      while (i < vector.length) {
        {
          val vec1Diff: Double = vector(i) - mean
          val vec2Diff: Double = vector2(i) - mean2
          ret += vec1Diff * vec2Diff
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * Used for calculating top part of simple regression for
   * beta 1
   * @param vector the x coordinates
   * @return the sum of mean differences for the input vectors
   */
  def sumOfMeanDifferencesOnePoint(vector: Array[Double]): Double = {
    val mean: Double = sum(vector) / vector.length
    var ret: Double = 0

    {
      var i: Int = 0
      while (i < vector.length) {
        {
          val vec1Diff: Double = Math.pow(vector(i) - mean, 2)
          ret += vec1Diff
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  def variance(vector: Array[Double]): Double = {
    return sumOfMeanDifferencesOnePoint(vector) / vector.length
  }

  /**
   * This returns the product of all numbers in the given array.
   * @param nums the numbers to multiply over
   * @return the product of all numbers in the array, or 0
   *         if the length is or nums i null
   */
  def times(nums: Array[Double]): Double = {
    if (nums == null || nums.length == 0) return 0
    var ret: Double = 1

    {
      var i: Int = 0
      while (i < nums.length) {
        ret *= nums(i)
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * This returns the sum of products for the given
   * numbers.
   * @param nums the sum of products for the give numbers
   * @return the sum of products for the given numbers
   */
  def sumOfProducts(nums: Array[Double]*): Double = {
    if (nums == null || nums.length < 1) return 0
    var sum: Double = 0
    {
      var i: Int = 0
      while (i < nums.length) {
        {
          val column: Array[Double] = column(i, nums)
          sum += times(column)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sum
  }

  /**
   * This returns the given column over an n arrays
   * @param column the column to getFromOrigin values for
   * @param nums the arrays to extract values from
   * @return a double array containing all of the numbers in that column
   *         for all of the arrays.
   * @throws IllegalArgumentException if the index is < 0
   */
  @throws(classOf[IllegalArgumentException])
  private def column(column: Int, nums: Array[Double]*): Array[Double] = {
    val ret: Array[Double] = new Array[Double](nums.length)

    {
      var i: Int = 0
      while (i < nums.length) {
        {
          val curr: Array[Double] = nums(i)
          ret(i) = curr(column)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * This returns the coordinate split in a list of coordinates
   * such that the values for ret[0] are the x values
   * and ret[1] are the y values
   * @param vector the vector to split with x and y values/
   * @return a coordinate split for the given vector of values.
   *         if null, is passed in null is returned
   */
  def coordSplit(vector: Array[Double]): List[Array[Double]] = {
    if (vector == null) return null
    val ret: List[Array[Double]] = new ArrayList[Array[Double]]
    val xVals: Array[Double] = new Array[Double](vector.length / 2)
    val yVals: Array[Double] = new Array[Double](vector.length / 2)
    val xTracker: Int = 0
    val yTracker: Int = 0
    {
      var i: Int = 0
      while (i < vector.length) {
        {
          if (i % 2 == 0) xVals(({
            xTracker += 1; xTracker - 1
          })) = vector(i)
          else yVals(({
            yTracker += 1; yTracker - 1
          })) = vector(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    ret.add(xVals)
    ret.add(yVals)
    return ret
  }

  /**
   * This will partition the given whole variable data applyTransformToDestination in to the specified chunk number.
   * @param arr the data applyTransformToDestination to pass in
   * @param chunk the number to separate by
   * @return a partition data applyTransformToDestination relative to the passed in chunk number
   */
  def partitionVariable(arr: List[Double], chunk: Int): List[List[Double]] = {
    var count: Int = 0
    val ret: List[List[Double]] = new ArrayList[List[Double]]
    while (count < arr.size) {
      val sublist: List[Double] = arr.subList(count, count + chunk)
      count += chunk
      ret.add(sublist)
    }
    import scala.collection.JavaConversions._
    for (lists <- ret) {
      if (lists.size < chunk) ret.remove(lists)
    }
    return ret
  }

  /**
   * This returns the coordinate split in a list of coordinates
   * such that the values for ret[0] are the x values
   * and ret[1] are the y values
   * @param vector the vector to split with x and y values
   *               Note that the list will be more stable due to the size operator.
   *               The array version will have extraneous values if not monitored
   *               properly.
   * @return a coordinate split for the given vector of values.
   *         if null, is passed in null is returned
   */
  def coordSplit(vector: List[Double]): List[Array[Double]] = {
    if (vector == null) return null
    val ret: List[Array[Double]] = new ArrayList[Array[Double]]
    val xVals: Array[Double] = new Array[Double](vector.size / 2)
    val yVals: Array[Double] = new Array[Double](vector.size / 2)
    val xTracker: Int = 0
    val yTracker: Int = 0
    {
      var i: Int = 0
      while (i < vector.size) {
        {
          if (i % 2 == 0) xVals(({
            xTracker += 1; xTracker - 1
          })) = vector.get(i)
          else yVals(({
            yTracker += 1; yTracker - 1
          })) = vector.get(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    ret.add(xVals)
    ret.add(yVals)
    return ret
  }

  /**
   * This returns the x values of the given vector.
   * These are assumed to be the even values of the vector.
   * @param vector the vector to getFromOrigin the values for
   * @return the x values of the given vector
   */
  def xVals(vector: Array[Double]): Array[Double] = {
    if (vector == null) return null
    val x: Array[Double] = new Array[Double](vector.length / 2)
    val count: Int = 0
    {
      var i: Int = 0
      while (i < vector.length) {
        {
          if (i % 2 != 0) x(({
            count += 1; count - 1
          })) = vector(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return x
  }

  /**
   * This returns the odd indexed values for the given vector
   * @param vector the odd indexed values of rht egiven vector
   * @return the y values of the given vector
   */
  def yVals(vector: Array[Double]): Array[Double] = {
    val y: Array[Double] = new Array[Double](vector.length / 2)
    var count: Int = 0

    {
      var i: Int = 0
      while (i < vector.length) {
        {
          if (i % 2 == 0) y(({
            count += 1; count - 1
          })) = vector(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return y
  }

  /**
   * This returns the sum of squares for the given vector.
   *
   * @param vector the vector to obtain the sum of squares for
   * @return the sum of squares for this vector
   */
  def sumOfSquares(vector: Array[Double]): Double = {
    var ret: Double = 0
    for (d <- vector) ret += Math.pow(d, 2)
    return ret
  }

  /**
   * This returns the determination coefficient of two vectors given a length
   * @param y1 the first vector
   * @param y2 the second vector
   * @param n the length of both vectors
   * @return the determination coefficient or r^2
   */
  def determinationCoefficient(y1: Array[Double], y2: Array[Double], n: Int): Double = {
    return Math.pow(correlation(y1, y2), 2)
  }

  /**
   * Returns the logarithm of a for base 2.
   *
   * @param a a double
   * @return	the logarithm for base 2
   */
  def log2(a: Double): Double = {
    if (a == 0) return 0.0
    return Math.log(a) / log2
  }

  /**
   * This returns the root mean squared error of two data sets
   * @param real the real values
   * @param predicted the predicted values
   * @return the root means squared error for two data sets
   */
  def rootMeansSquaredError(real: Array[Double], predicted: Array[Double]): Double = {
    var ret: Double = 0.0

    {
      var i: Int = 0
      while (i < real.length) {
        {
          ret += Math.pow((real(i) - predicted(i)), 2)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return Math.sqrt(ret / real.length)
  }

  /**
   * This returns the entropy (information gain, or uncertainty of a random variable).
   * @param vector the vector of values to getFromOrigin the entropy for
   * @return the entropy of the given vector
   */
  def entropy(vector: Array[Double]): Double = {
    if (vector == null || vector.length < 1) return 0
    else {
      var ret: Double = 0
      for (d <- vector) ret += d * Math.log(d)
      return ret
    }
  }

  /**
   * This returns the kronecker delta of two doubles.
   * @param i the first number to compare
   * @param j the second number to compare
   * @return 1 if they are equal, 0 otherwise
   */
  def kroneckerDelta(i: Double, j: Double): Int = {
    return if ((i == j)) 1 else 0
  }

  /**
   * This calculates the adjusted r^2 including degrees of freedom.
   * Also known as calculating "strength" of a regression
   * @param rSquared the r squared value to calculate
   * @param numRegressors number of variables
   * @param numDataPoints size of the data applyTransformToDestination
   * @return an adjusted r^2 for degrees of freedom
   */
  def adjustedrSquared(rSquared: Double, numRegressors: Int, numDataPoints: Int): Double = {
    val divide: Double = (numDataPoints - 1.0) / (numDataPoints - numRegressors - 1.0)
    val rSquaredDiff: Double = 1 - rSquared
    return 1 - (rSquaredDiff * divide)
  }

  def normalizeToOne(doubles: Array[Double]): Array[Double] = {
    normalize(doubles, sum(doubles))
    return doubles
  }

  def min(doubles: Array[Double]): Double = {
    var ret: Double = doubles(0)
    for (d <- doubles) if (d < ret) ret = d
    return ret
  }

  def max(doubles: Array[Double]): Double = {
    var ret: Double = doubles(0)
    for (d <- doubles) if (d > ret) ret = d
    return ret
  }

  /**
   * Normalizes the doubles in the array using the given value.
   *
   * @param doubles the array of double
   * @param sum the value by which the doubles are to be normalized
   * @exception IllegalArgumentException if sum is zero or NaN
   */
  def normalize(doubles: Array[Double], sum: Double) {
    if (Double.NaN == sum) {
      throw new IllegalArgumentException("Can't normalize array. Sum is NaN.")
    }
    if (sum == 0) {
      throw new IllegalArgumentException("Can't normalize array. Sum is zero.")
    }
    {
      var i: Int = 0
      while (i < doubles.length) {
        {
          doubles(i) /= sum
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /**
   * Converts an array containing the natural logarithms of
   * probabilities stored in a vector back into probabilities.
   * The probabilities are assumed to sum to one.
   *
   * @param a an array holding the natural logarithms of the probabilities
   * @return the converted array
   */
  def logs2probs(a: Array[Double]): Array[Double] = {
    val max: Double = a(maxIndex(a))
    var sum: Double = 0.0
    val result: Array[Double] = new Array[Double](a.length)

    {
      var i: Int = 0
      while (i < a.length) {
        {
          result(i) = Math.exp(a(i) - max)
          sum += result(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    normalize(result, sum)
    return result
  }

  /**
   * This returns the entropy for a given vector of probabilities.
   * @param probabilities the probabilities to getFromOrigin the entropy for
   * @return the entropy of the given probabilities.
   */
  def information(probabilities: Array[Double]): Double = {
    var total: Double = 0.0
    for (d <- probabilities) {
      total += (-1.0 * log2(d) * d)
    }
    return total
  }

  /**
   *
   *
   * Returns index of maximum element in a given
   * array of doubles. First maximum is returned.
   *
   * @param doubles the array of doubles
   * @return the index of the maximum element
   */
  def maxIndex(doubles: Array[Double]): Int = {
    var maximum: Double = 0
    var maxIndex: Int = 0

    {
      var i: Int = 0
      while (i < doubles.length) {
        {
          if ((i == 0) || (doubles(i) > maximum)) {
            maxIndex = i
            maximum = doubles(i)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return maxIndex
  }

  /**
   * This will return the factorial of the given number n.
   * @param n the number to getFromOrigin the factorial for
   * @return the factorial for this number
   */
  def factorial(n: Double): Double = {
    if (n == 1 || n == 0) return 1
    {
      var i: Double = n
      while (i > 0) {
        {
        }
        ({
          i -= 1; i + 1
        })
        n *= (if (i > 0) i else 1)
      }
    }
    return n
  }

  /** The small deviation allowed in double comparisons. */
  var SMALL: Double = 1e-6

  /**
   * Returns the log-odds for a given probability.
   *
   * @param prob the probability
   *
   * @return the log-odds after the probability has been mapped to
   *         [Utils.SMALL, 1-Utils.SMALL]
   */
  def probToLogOdds(prob: Double): Double = {
    if (gr(prob, 1) || (sm(prob, 0))) {
      throw new IllegalArgumentException("probToLogOdds: probability must " + "be in [0,1] " + prob)
    }
    val p: Double = SMALL + (1.0 - 2 * SMALL) * prob
    return Math.log(p / (1 - p))
  }

  /**
   * Rounds a double to the next nearest integer value. The JDK version
   * of it doesn't work properly.
   *
   * @param value the double value
   * @return the resulting integer value
   */
  def round(value: Double): Int = {
    return if (value > 0) (value + 0.5).toInt else -(Math.abs(value) + 0.5).toInt
  }

  /**
   * This returns the permutation of n choose r.
   * @param n the n to choose
   * @param r the number of elements to choose
   * @return the permutation of these numbers
   */
  def permutation(n: Double, r: Double): Double = {
    val nFac: Double = MathUtils.factorial(n)
    val nMinusRFac: Double = MathUtils.factorial((n - r))
    return nFac / nMinusRFac
  }

  /**
   * This returns the combination of n choose r
   * @param n the number of elements overall
   * @param r the number of elements to choose
   * @return the amount of possible combinations for this applyTransformToDestination of elements
   */
  def combination(n: Double, r: Double): Double = {
    val nFac: Double = MathUtils.factorial(n)
    val rFac: Double = MathUtils.factorial(r)
    val nMinusRFac: Double = MathUtils.factorial((n - r))
    return nFac / (rFac * nMinusRFac)
  }

  /**
   * sqrt(a^2 + b^2) without under/overflow.
   */
  def hypotenuse(a: Double, b: Double): Double = {
    var r: Double = .0
    if (Math.abs(a) > Math.abs(b)) {
      r = b / a
      r = Math.abs(a) * Math.sqrt(1 + r * r)
    }
    else if (b != 0) {
      r = a / b
      r = Math.abs(b) * Math.sqrt(1 + r * r)
    }
    else {
      r = 0.0
    }
    return r
  }

  /**
   * Rounds a double to the next nearest integer value in a probabilistic
   * fashion (e.g. 0.8 has a 20% chance of being rounded down to 0 and a
   * 80% chance of being rounded up to 1). In the limit, the average of
   * the rounded numbers generated by this procedure should converge to
   * the original double.
   *
   * @param value the double value
   * @param rand the random number generator
   * @return the resulting integer value
   */
  def probRound(value: Double, rand: Random): Int = {
    if (value >= 0) {
      val lower: Double = Math.floor(value)
      val prob: Double = value - lower
      if (rand.nextDouble < prob) {
        return lower.toInt + 1
      }
      else {
        return lower.toInt
      }
    }
    else {
      val lower: Double = Math.floor(Math.abs(value))
      val prob: Double = Math.abs(value) - lower
      if (rand.nextDouble < prob) {
        return -(lower.toInt + 1)
      }
      else {
        return -lower.toInt
      }
    }
  }

  /**
   * Rounds a double to the given number of decimal places.
   *
   * @param value the double value
   * @param afterDecimalPoint the number of digits after the decimal point
   * @return the double rounded to the given precision
   */
  def roundDouble(value: Double, afterDecimalPoint: Int): Double = {
    val mask: Double = Math.pow(10.0, afterDecimalPoint.toDouble)
    return (value * mask.round).toDouble / mask
  }

  /**
   * Rounds a double to the given number of decimal places.
   *
   * @param value the double value
   * @param afterDecimalPoint the number of digits after the decimal point
   * @return the double rounded to the given precision
   */
  def roundFloat(value: Float, afterDecimalPoint: Int): Float = {
    val mask: Float = Math.pow(10, afterDecimalPoint.toFloat).toFloat
    return (value * mask.round).toFloat / mask
  }

  /**
   * This will return the bernoulli trial for the given event.
   * A bernoulli trial is a mechanism for detecting the probability
   * of a given event occurring k times in n independent trials
   * @param n the number of trials
   * @param k the number of times the target event occurs
   * @param successProb the probability of the event happening
   * @return the probability of the given event occurring k times.
   */
  def bernoullis(n: Double, k: Double, successProb: Double): Double = {
    val combo: Double = MathUtils.combination(n, k)
    val q: Double = 1 - successProb
    return combo * Math.pow(successProb, k) * Math.pow(q, n - k)
  }

  /**
   * Tests if a is smaller than b.
   *
   * @param a a double
   * @param b a double
   */
  def sm(a: Double, b: Double): Boolean = {
    return (b - a > SMALL)
  }

  /**
   * Tests if a is greater than b.
   *
   * @param a a double
   * @param b a double
   */
  def gr(a: Double, b: Double): Boolean = {
    return (a - b > SMALL)
  }

  /**
   * This will take a given string and separator and convert it to an equivalent
   * double array.
   * @param data the data to separate
   * @param separator the separator to use
   * @return the new double array based on the given data
   */
  def fromString(data: String, separator: String): Array[Double] = {
    val split: Array[String] = data.split(separator)
    val ret: Array[Double] = new Array[Double](split.length)

    {
      var i: Int = 0
      while (i < split.length) {
        {
          ret(i) = split(i).toDouble
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * Computes the mean for an array of doubles.
   *
   * @param vector the array
   * @return the mean
   */
  def mean(vector: Array[Double]): Double = {
    var sum: Double = 0
    if (vector.length == 0) {
      return 0
    }
    {
      var i: Int = 0
      while (i < vector.length) {
        {
          sum += vector(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sum / vector.length.toDouble
  }

  /**
   * This will convert the given binary string to a decimal based
   * integer
   * @param binary the binary string to convert
   * @return an equivalent base 10 number
   */
  def toDecimal(binary: String): Int = {
    var num: Long = Long.parseLong(binary)
    var rem: Long = 0L
    while (num > 0) {
      rem = num % 10
      num = num / 10
      if (rem != 0 && rem != 1) {
        System.out.println("This is not a binary number.")
        System.out.println("Please try once again.")
        return -1
      }
    }
    return Integer.parseInt(binary, 2)
  }

  /**
   * This will translate a vector in to an equivalent integer
   * @param vector the vector to translate
   * @return a z value such that the value is the interleaved lsd to msd for each
   *         double in the vector
   */
  def distanceFinderZValue(vector: Array[Double]): Int = {
    val binaryBuffer: StringBuilder = new StringBuilder
    val binaryReps: List[String] = new ArrayList[String](vector.length)
    {
      var i: Int = 0
      while (i < vector.length) {
        {
          val d: Double = vector(i)
          val j: Int = d.toInt
          val binary: String = Integer.toBinaryString(j)
          binaryReps.add(binary)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    while (!binaryReps.isEmpty) {
      {
        var j: Int = 0
        while (j < binaryReps.size) {
          {
            var curr: String = binaryReps.get(j)
            if (!curr.isEmpty) {
              val first: Char = curr.charAt(0)
              binaryBuffer.append(first)
              curr = curr.substring(1)
              binaryReps.set(j, curr)
            }
            else binaryReps.remove(j)
          }
          ({
            j += 1; j - 1
          })
        }
      }
    }
    return Integer.parseInt(binaryBuffer.toString, 2)
  }

  /**
   * This returns the distance of two vectors
   * sum(i=1,n)   (q_i - p_i)^2
   * @param p the first vector
   * @param q the second vector
   * @return the distance between two vectors
   */
  def euclideanDistance(p: Array[Double], q: Array[Double]): Double = {
    var ret: Double = 0

    {
      var i: Int = 0
      while (i < p.length) {
        {
          val diff: Double = (q(i) - p(i))
          val sq: Double = Math.pow(diff, 2)
          ret += sq
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * This returns the distance of two vectors
   * sum(i=1,n)   (q_i - p_i)^2
   * @param p the first vector
   * @param q the second vector
   * @return the distance between two vectors
   */
  def euclideanDistance(p: Array[Float], q: Array[Float]): Double = {
    var ret: Double = 0

    {
      var i: Int = 0
      while (i < p.length) {
        {
          val diff: Double = (q(i) - p(i))
          val sq: Double = Math.pow(diff, 2)
          ret += sq
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * This will generate a series of uniformally distributed
   * numbers between l times
   * @param l the number of numbers to generate
   * @return l uniformally generated numbers
   */
  def generateUniform(l: Int): Array[Double] = {
    val ret: Array[Double] = new Array[Double](l)
    val rgen: Random = new Random

    {
      var i: Int = 0
      while (i < l) {
        {
          ret(i) = rgen.nextDouble
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  /**
   * This will calculate the Manhattan distance between two sets of points.
   * The Manhattan distance is equivalent to:
   * 1_sum_n |p_i - q_i|
   * @param p the first point vector
   * @param q the second point vector
   * @return the Manhattan distance between two object
   */
  def manhattanDistance(p: Array[Double], q: Array[Double]): Double = {
    var ret: Double = 0

    {
      var i: Int = 0
      while (i < p.length) {
        {
          val difference: Double = p(i) - q(i)
          ret += Math.abs(difference)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret
  }

  def sampleDoublesInInterval(doubles: Array[Array[Double]], l: Int): Array[Double] = {
    val sample: Array[Double] = new Array[Double](l)

    {
      var i: Int = 0
      while (i < l) {
        {
          val rand1: Int = randomNumberBetween(0, doubles.length - 1)
          val rand2: Int = randomNumberBetween(0, doubles(i).length)
          sample(i) = doubles(rand1)(rand2)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sample
  }

  /**
   * Generates a random integer between the specified numbers
   * @param begin the begin of the interval
   * @param end the end of the interval
   * @return an int between begin and end
   */
  def randomNumberBetween(begin: Double, end: Double): Int = {
    if (begin > end) throw new IllegalArgumentException("Begin must not be less than end")
    return begin.toInt + (Math.random * ((end - begin) + 1)).toInt
  }

  /**
   * Generates a random integer between the specified numbers
   * @param begin the begin of the interval
   * @param end the end of the interval
   * @return an int between begin and end
   */
  def randomNumberBetween(begin: Double, end: Double, rng: RandomGenerator): Int = {
    if (begin > end) throw new IllegalArgumentException("Begin must not be less than end")
    return begin.toInt + (rng.nextDouble * ((end - begin) + 1)).toInt
  }

  def randomFloatBetween(begin: Float, end: Float): Float = {
    val rand: Float = Math.random.toFloat
    return begin + (rand * ((end - begin)))
  }

  def randomDoubleBetween(begin: Double, end: Double): Double = {
    return begin + (Math.random * ((end - begin)))
  }

  def shuffleArray(array: Array[Int], rngSeed: Long) {
    shuffleArray(array, new Random(rngSeed))
  }

  def shuffleArray(array: Array[Int], rng: Random) {
    {
      var i: Int = array.length - 1
      while (i > 0) {
        {
          val j: Int = rng.nextInt(i + 1)
          val temp: Int = array(j)
          array(j) = array(i)
          array(i) = temp
        }
        ({
          i -= 1; i + 1
        })
      }
    }
  }
}

class MathUtils {
  /**
   * This returns the slope of the given points.
   * @param x1 the first x to use
   * @param x2 the end x to use
   * @param y1 the begin y to use
   * @param y2 the end y to use
   * @return the slope of the given points
   */
  def slope(x1: Double, x2: Double, y1: Double, y2: Double): Double = {
    return (y2 - y1) / (x2 - x1)
  }

  /**
   * This will return the cholesky decomposition of
   * the given matrix
   * @param m the matrix to convert
   * @return the cholesky decomposition of the given
   *         matrix.
   *         See:
   *         http://en.wikipedia.org/wiki/Cholesky_decomposition
   * @throws NonSquareMatrixException
   */
  @throws(classOf[Exception])
  def choleskyFromMatrix(m: RealMatrix): CholeskyDecomposition = {
    return new CholeskyDecomposition(m)
  }
}