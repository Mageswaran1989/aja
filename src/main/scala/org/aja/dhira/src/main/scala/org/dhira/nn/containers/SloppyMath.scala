package org.dhira.nn.containers

import org.aja.dhira.nn.containers.Counter

/**
 * The class <code>SloppyMath</code> contains methods for performing basic
 * numeric operations. In some cases, such as max and min, they cut a few
 * corners in the implementation for the sake of efficiency. In particular, they
 * may not handle special notions like NaN and -0.0 correctly. This was the
 * origin of the class name, but some other operations are just useful math
 * additions, such as logSum.
 *
 * @author Mageswaran Dhandapani
 * @version 10-Sep-2016
 */
object SloppyMath {

  def abs(x: Double): Double = {
    if (x > 0)  x
    else -1.0 * x
  }

  def lambert(v: Double, u: Double): Double = {
    val x: Double = -(Math.log(-v) + u)
    var w: Double = -x
    var diff: Double = 1
    while (Math.abs(diff) < 1.0e-5) {
      val z: Double = -x - Math.log(Math.abs(w))
      diff = z - w
      w = z
    }
     w
  }

  /**
   * Returns the minimum of three int values.
   */
  def max(a: Int, b: Int, c: Int): Int = {
    var ma: Int = 0
    ma = a
    if (b > ma) {
      ma = b
    }
    if (c > ma) {
      ma = c
    }
     ma
  }

  /**
   * Returns the minimum of three int values.
   */
  def min(a: Int, b: Int, c: Int): Int = {
    var mi: Int = 0
    mi = a
    if (b < mi) {
      mi = b
    }
    if (c < mi) {
      mi = c
    }
     mi
  }

  /**
   * Returns the greater of two <code>float</code> values. That is, the
   * result is the argument closer to positive infinity. If the arguments have
   * the same value, the result is that same value. Does none of the special
   * checks for NaN or -0.0f that <code>Math.max</code> does.
   *
   * @param a
	 * an argument.
   * @param b
	 * another argument.
   * @ the larger of <code>a</code> and <code>b</code>.
   */
  def max(a: Float, b: Float): Float = {
     if ((a >= b)) a else b
  }

  /**
   * Returns the greater of two <code>double</code> values. That is, the
   * result is the argument closer to positive infinity. If the arguments have
   * the same value, the result is that same value. Does none of the special
   * checks for NaN or -0.0f that <code>Math.max</code> does.
   *
   * @param a
	 * an argument.
   * @param b
	 * another argument.
   * @ the larger of <code>a</code> and <code>b</code>.
   */
  def max(a: Double, b: Double): Double = {
     if ((a >= b)) a else b
  }

  /**
   * Returns the smaller of two <code>float</code> values. That is, the
   * result is the value closer to negative infinity. If the arguments have
   * the same value, the result is that same value. Does none of the special
   * checks for NaN or -0.0f that <code>Math.max</code> does.
   *
   * @param a
	 * an argument.
   * @param b
	 * another argument.
   * @ the smaller of <code>a</code> and <code>b.</code>
   */
  def min(a: Float, b: Float): Float = {
     if ((a <= b)) a else b
  }

  /**
   * Returns the smaller of two <code>double</code> values. That is, the
   * result is the value closer to negative infinity. If the arguments have
   * the same value, the result is that same value. Does none of the special
   * checks for NaN or -0.0f that <code>Math.max</code> does.
   *
   * @param a
	 * an argument.
   * @param b
	 * another argument.
   * @ the smaller of <code>a</code> and <code>b</code>.
   */
  def min(a: Double, b: Double): Double = {
     if ((a <= b)) a else b
  }

  /**
   * Returns true if the argument is a "dangerous" double to have around,
   * namely one that is infinite, NaN or zero.
   */
  def isDangerous(d: Double): Boolean = {
    d == Double.PositiveInfinity || d == Double.NegativeInfinity || d == Double.NaN || d == 0.0
  }

  def isDangerous(d: Float): Boolean = {
    d == Float.PositiveInfinity || d == Float.NegativeInfinity || d == Float.NaN || d == 0.0
  }

  def isGreater(x: Double, y: Double): Boolean = {
    if (x > 1)  (((x - y) / x) > -0.01)
     ((x - y) > -0.0001)
  }

  /**
   * Returns true if the argument is a "very dangerous" double to have around,
   * namely one that is infinite or NaN.
   */
  def isVeryDangerous(d: Double): Boolean = {
    d == Double.PositiveInfinity || d == Double.NegativeInfinity || d == Double.NaN
  }

  def relativeDifferance(a: Double, b: Double): Double = {
    val absMin: Double = Math.min(Math.abs(a), Math.abs(b))
     Math.abs(a - b) / absMin
  }

  def isDiscreteProb(d: Double, tol: Double): Boolean = {
     d >= 0.0 && d <= 1.0 + tol
  }

  /**
   * If a difference is bigger than this in log terms, then the sum or
   * difference of them will just be the larger (to 12 or so decimal places
   * for double, and 7 or 8 for float).
   */
  val LOGTOLERANCE: Double = 30.0
  private[berkeley] val LOGTOLERANCE_F: Float = 10.0f

  /**
   * Returns the log of the sum of two numbers, which are themselves input in
   * log form. This uses natural logarithms. Reasonable care is taken to do
   * this as efficiently as possible (under the assumption that the numbers
   * might differ greatly in magnitude), with high accuracy, and without
   * numerical overflow. Also, handle correctly the case of arguments being
   * -Inf (e.g., probability 0).
   *
   * @param lx
	 * First number, in log form
   * @param ly
	 * Second number, in log form
   * @ log(exp(lx) + exp(ly))
   */
  def logAdd(lx: Float, ly: Float): Float = {
    var max: Float = 0
    var negDiff: Float = 0
    if (lx > ly) {
      max = lx
      negDiff = ly - lx
    }
    else {
      max = ly
      negDiff = lx - ly
    }
    if (max == Double.NegativeInfinity || negDiff < -LOGTOLERANCE_F) {
       max
    }
    else {
       max + Math.log(1.0f + Math.exp(negDiff)).toFloat
    }
  }

  /**
   * Returns the log of the sum of two numbers, which are themselves input in
   * log form. This uses natural logarithms. Reasonable care is taken to do
   * this as efficiently as possible (under the assumption that the numbers
   * might differ greatly in magnitude), with high accuracy, and without
   * numerical overflow. Also, handle correctly the case of arguments being
   * -Inf (e.g., probability 0).
   *
   * @param lx
	 * First number, in log form
   * @param ly
	 * Second number, in log form
   * @ log(exp(lx) + exp(ly))
   */
  def logAdd(lx: Double, ly: Double): Double = {
    var max: Double = .0
    var negDiff: Double = .0
    if (lx > ly) {
      max = lx
      negDiff = ly - lx
    }
    else {
      max = ly
      negDiff = lx - ly
    }
    if (max == Double.NegativeInfinity || negDiff < -LOGTOLERANCE) {
       max
    }
    else {
       max + Math.log(1.0 + Math.exp(negDiff))
    }
  }

  def logAdd(logV: Array[Float]): Double = {
    var maxIndex: Double = 0
    var max: Double = Double.NegativeInfinity

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }

    if (max == Double.NegativeInfinity)  Double.NegativeInfinity
    val threshold: Double = max - LOGTOLERANCE
    var sumNegativeDifferences: Double = 0.0

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp(logV(i) - max)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (sumNegativeDifferences > 0.0) {
       max + Math.log(1.0 + sumNegativeDifferences)
    }
    else {
       max
    }
  }

  def logNormalize(logV: Array[Double]) {
    val logSum: Double = logAdd(logV)
    if (Double.NaN == logSum) {
      throw new RuntimeException("Bad log-sum")
    }
    if (logSum == 0.0) 
    {
      var i: Int = 0
      while (i < logV.length) {
        {
          logV(i) -= logSum
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def logAdd(logV: Array[Double]): Double = {
    var maxIndex: Double = 0
    var max: Double = Double.NegativeInfinity

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (max == Double.NegativeInfinity)  Double.NegativeInfinity
    val threshold: Double = max - LOGTOLERANCE
    var sumNegativeDifferences: Double = 0.0

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp(logV(i) - max)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (sumNegativeDifferences > 0.0) {
       max + Math.log(1.0 + sumNegativeDifferences)
    }
    else {
       max
    }
  }

  def logAdd(logV: List[Double]): Double = {
    var max: Double = Double.NegativeInfinity
    var maxIndex: Double = 0

    {
      var i: Int = 0
      while (i < logV.size) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (max == Double.NegativeInfinity)  Double.NegativeInfinity
    val threshold: Double = max - LOGTOLERANCE
    var sumNegativeDifferences: Double = 0.0

    {
      var i: Int = 0
      while (i < logV.size) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp(logV(i) - max)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (sumNegativeDifferences > 0.0) {
       max + Math.log(1.0 + sumNegativeDifferences)
    }
    else {
       max
    }
  }

  def logAdd_Old(logV: Array[Float]): Float = {
    var max: Float = Float.NegativeInfinity
    var maxIndex: Float = 0

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (max == Float.NegativeInfinity)  Float.NegativeInfinity
    val threshold: Float = max - LOGTOLERANCE_F
    var sumNegativeDifferences: Float = 0.0f

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp(logV(i) - max)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (sumNegativeDifferences > 0.0) {
       max + Math.log(1.0f + sumNegativeDifferences).toFloat
    }
    else {
       max
    }
  }

  def logAdd(logV: Array[Float], lastIndex: Int): Float = {
    if (lastIndex == 0)  Float.NegativeInfinity
    var max: Float = Float.NegativeInfinity
    var maxIndex: Float = 0

    {
      var i: Int = 0
      while (i < lastIndex) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (max == Float.NegativeInfinity)  Float.NegativeInfinity
    val threshold: Float = max - LOGTOLERANCE_F
    var sumNegativeDifferences: Double = 0.0

    {
      var i: Int = 0
      while (i < lastIndex) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp((logV(i) - max))
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (sumNegativeDifferences > 0.0) {
       max + Math.log(1.0 + sumNegativeDifferences).toFloat
    }
    else {
       max
    }
  }

  def logAdd(logV: Array[Double], lastIndex: Int): Double = {
    if (lastIndex == 0)  Double.NegativeInfinity
    var max: Double = Double.NegativeInfinity
    var maxIndex: Double = 0

    {
      var i: Int = 0
      while (i < lastIndex) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (max == Double.NegativeInfinity)  Double.NegativeInfinity
    val threshold: Double = max - LOGTOLERANCE
    var sumNegativeDifferences: Double = 0.0

    {
      var i: Int = 0
      while (i < lastIndex) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp((logV(i) - max))
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (sumNegativeDifferences > 0.0) {
       max + Math.log(1.0 + sumNegativeDifferences)
    }
    else {
       max
    }
  }

  /**
   * Similar to logAdd, but without the final log. I.e. Sum_i exp(logV_i)
   *
   * @param logV
   * @
   */
  def addExp_Old(logV: Array[Float]): Float = {
    var max: Float = Float.NegativeInfinity
    var maxIndex: Float = 0

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (max == Float.NegativeInfinity)  Float.NegativeInfinity
    val threshold: Float = max - LOGTOLERANCE_F
    var sumNegativeDifferences: Float = 0.0f

    {
      var i: Int = 0
      while (i < logV.length) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp(logV(i) - max)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
     Math.exp(max).toFloat * (1.0f + sumNegativeDifferences)
  }

  def addExp(logV: Array[Float], lastIndex: Int): Float = {
    if (lastIndex == 0)  Float.NegativeInfinity
    var max: Float = Float.NegativeInfinity
    var maxIndex: Float = 0

    {
      var i: Int = 0
      while (i < lastIndex) {
        {
          if (logV(i) > max) {
            max = logV(i)
            maxIndex = i
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (max == Float.NegativeInfinity)  Float.NegativeInfinity
    val threshold: Float = max - LOGTOLERANCE_F
    var sumNegativeDifferences: Float = 0.0f

    {
      var i: Int = 0
      while (i < lastIndex) {
        {
          if (i != maxIndex && logV(i) > threshold) {
            sumNegativeDifferences += Math.exp(logV(i) - max)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
     Math.exp(max).toFloat * (1.0f + sumNegativeDifferences)
  }

  /**
   * Computes n choose k in an efficient way. Works with k == 0 or k == n but
   * undefined if k < 0 or k > n
   *
   * @param n
   * @param kk
   * @ fact(n) / fact(k) * fact(n-k)
   */
  def nChooseK(n: Int, kk: Int): Int = {
    val k = Math.min(kk, n - kk)

    if (k == 0) {
       1
    }
    var accum: Int = n

    {
      var i: Int = 1
      while (i < k) {
        {
          accum *= (n - i)
          accum /= i
        }
        ({
          i += 1; i - 1
        })
      }
    }
     accum / k
  }

  /**
   * exponentiation like we learned in grade school: multiply b by itself e
   * times. Uses power of two trick. e must be nonnegative!!! no checking!!!
   *
   * @param b
	 * base
   * @param ee
	 * exponent
   * @ (b^ee)
   */
  def intPow(b: Int, ee: Int): Int = {
    var e = ee
    if (e == 0) {
       1
    }
    var result: Int = 1
    var currPow: Int = b
    do {
      if ((e & 1) == 1) result *= currPow
      currPow = currPow * currPow
      e >>= 1
    } while (e > 0)
     result
  }

  /**
   * exponentiation like we learned in grade school: multiply b by itself e
   * times. Uses power of two trick. e must be nonnegative!!! no checking!!!
   *
   * @param b
	 * base
   * @param ee
	 * exponent
   * @ b^ee
   */
  def intPow(b: Float, ee: Int): Float = {
    var e = ee
    if (e == 0) {
       1
    }
    var result: Float = 1
    var currPow: Float = b
    do {
      if ((e & 1) == 1) result *= currPow
      currPow = currPow * currPow
      e >>= 1
    } while (e > 0)
     result
  }

  /**
   * exponentiation like we learned in grade school: multiply b by itself e
   * times. Uses power of two trick. e must be nonnegative!!! no checking!!!
   *
   * @param b
	 * base
   * @param ee
	 * exponent
   * @ b^ee
   */
  def intPow(b: Double, ee: Int): Double = {
    var e = ee
    if (e == 0) {
       1
    }
    var result: Float = 1
    var currPow: Double = b
    do {
      if ((e & 1) == 1) result *= currPow
      currPow = currPow * currPow
      e >>= 1
    } while (e > 0)
     result
  }

  /**
   * Find a hypergeometric distribution. This uses exact math, trying fairly
   * hard to avoid numeric overflow by interleaving multiplications and
   * divisions. (To do: make it even better at avoiding overflow, by using
   * loops that will do either a multiple or divide based on the size of the
   * intermediate result.)
   *
   * @param kk
	 * The number of black balls drawn
   * @param n
	 * The total number of balls
   * @param rr
	 * The number of black balls
   * @param mm
	 * The number of balls drawn
   * @ The hypergeometric value
   */
  def hypergeometric(kk: Int, n: Int, rr: Int, mm: Int): Double = {
    var k = kk 
    var r = rr
    var m = mm 
    if (k < 0 || r > n || m > n || n <= 0 || m < 0 | r < 0) {
      throw new IllegalArgumentException("Invalid hypergeometric")
    }
    if (m > n / 2) {
      m = n - m
      k = r - k
    }
    if (r > n / 2) {
      r = n - r
      k = m - k
    }
    if (m > r) {
      val temp: Int = m
      m = r
      r = temp
    }
    if (k < (m + r) - n || k > m) {
       0.0
    }
    if (r == n) {
      if (k == m) {
         1.0
      }
      else {
         0.0
      }
    }
    else if (r == n - 1) {
      if (k == m) {
         (n - m) / n.toDouble
      }
      else if (k == m - 1) {
         m / n.toDouble
      }
      else {
         0.0
      }
    }
    else if (m == 1) {
      if (k == 0) {
         (n - r) / n.toDouble
      }
      else if (k == 1) {
         r / n.toDouble
      }
      else {
         0.0
      }
    }
    else if (m == 0) {
      if (k == 0) {
         1.0
      }
      else {
         0.0
      }
    }
    else if (k == 0) {
      var ans: Double = 1.0
      
      {
        var m0: Int = 0
        while (m0 < m) {
          {
            ans *= ((n - r) - m0)
            ans /= (n - m0)
          }
          ({
            m0 += 1; m0 - 1
          })
        }
      }
       ans
    }
    var ans: Double = 1.0
    
    {
      var nr: Int = n - r
      var n0: Int = n
      while (nr > (n - r) - (m - k)) {
        {
          ans *= nr
          ans /= n0
        }
        ({
          nr -= 1; nr + 1
        })
        ({
          n0 -= 1; n0 + 1
        })
      }
    }
    {
      var k0: Int = 0
      while (k0 < k) {
        {
          ans *= (m - k0)
          ans /= ((n - (m - k0)) + 1)
          ans *= (r - k0)
          ans /= (k0 + 1)
        }
        ({
          k0 += 1; k0 - 1
        })
      }
    }
     ans
  }

  /**
   * Find a one tailed exact binomial test probability. Finds the chance of
   * this or a higher result
   *
   * @param k
	 * number of successes
   * @param n
	 * Number of trials
   * @param p
	 * Probability of a success
   */
  def exactBinomial(k: Int, n: Int, p: Double): Double = {
    var total: Double = 0.0
    
    {
      var m: Int = k
      while (m <= n) {
        {
          var nChooseM: Double = 1.0
          
          {
            var r: Int = 1
            while (r <= m) {
              {
                nChooseM *= (n - r) + 1
                nChooseM /= r
              }
              ({
                r += 1; r - 1
              })
            }
          }
          total += nChooseM * Math.pow(p, m) * Math.pow(1.0 - p, n - m)
        }
        ({
          m += 1; m - 1
        })
      }
    }
     total
  }

  /**
   * Find a one-tailed Fisher's exact probability. Chance of having seen this
   * or a more extreme departure from what you would have expected given
   * independence. I.e., k >= the value passed in. Warning: this was done just
   * for collocations, where you are concerned with the case of k being larger
   * than predicted. It doesn't correctly handle other cases, such as k being
   * smaller than expected.
   *
   * @param kk
	 * The number of black balls drawn
   * @param n
	 * The total number of balls
   * @param rr
	 * The number of black balls
   * @param mm
	 * The number of balls drawn
   * @ The Fisher's exact p-value
   */
  def oneTailedFishersExact(kk: Int, n: Int, rr: Int, mm: Int): Double = {
    var k = kk 
    var r = rr 
    var m = mm
    if (k < 0 || k < (m + r) - n || k > r || k > m || r > n || m > n) {
      throw new IllegalArgumentException("Invalid Fisher's exact: " + "k=" + k + " n=" + n + " r=" + r + " m=" + m + " k<0=" + (k < 0) + " k<(m+r)-n=" + (k < (m + r) - n) + " k>r=" + (k > r) + " k>m=" + (k > m) + " r>n=" + (r > n) + "m>n=" + (m > n))
    }
    if (m > n / 2) {
      m = n - m
      k = r - k
    }
    if (r > n / 2) {
      r = n - r
      k = m - k
    }
    if (m > r) {
      val temp: Int = m
      m = r
      r = temp
    }
    var total: Double = 0.0
    if (k > m / 2) {
      {
        var k0: Int = k
        while (k0 <= m) {
          {
            total += SloppyMath.hypergeometric(k0, n, r, m)
          }
          ({
            k0 += 1; k0 - 1
          })
        }
      }
    }
    else {
      val min: Int = Math.max(0, (m + r) - n)
      
      {
        var k0: Int = min
        while (k0 < k) {
          {
            total += SloppyMath.hypergeometric(k0, n, r, m)
          }
          ({
            k0 += 1; k0 - 1
          })
        }
      }
      total = 1.0 - total
    }
     total
  }

  /**
   * Find a 2x2 chi-square value. Note: could do this more neatly using
   * simplified formula for 2x2 case.
   *
   * @param k
	 * The number of black balls drawn
   * @param n
	 * The total number of balls
   * @param r
	 * The number of black balls
   * @param m
	 * The number of balls drawn
   * @ The Fisher's exact p-value
   */
  def chiSquare2by2(k: Int, n: Int, r: Int, m: Int): Double = {
    val cg: Array[Array[Int]] = Array(Array(k, r - k), Array(m - k, n - (k + (r - k) + (m - k))))
    val cgr: Array[Int] = Array(r, n - r)
    val cgc: Array[Int] = Array(m, n - m)
    var total: Double = 0.0
    
    {
      var i: Int = 0
      while (i < 2) {
        {
          {
            var j: Int = 0
            while (j < 2) {
              {
                val exp: Double = cgr(i).toDouble * cgc(j) / n
                total += (cg(i)(j) - exp) * (cg(i)(j) - exp) / exp
              }
              ({
                j += 1; j - 1
              })
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
     total
  }

  def exp(logX: Double): Double = {
    if (Math.abs(logX) < 0.001)  1 + logX
     Math.exp(logX)
  }

  /**
   * Tests the hypergeometric distribution code, or other cooccurrences provided
   * in this module.
   *
   * @param args
	 * Either none, and the log add rountines are tested, or the
   * following 4 arguments: k (cell), n (total), r (row), m (col)
   */
  def main(args: Array[String]) {
    System.out.println(approxLog(0.0))
  }

  def noNaNDivide(num: Double, denom: Double): Double = {
     if (denom == 0.0) 0.0 else num / denom
  }

  def approxLog(value: Double): Double = {
    if (value < 0.0)  Double.NaN
    if (value == 0.0)  Double.NegativeInfinity
    val r: Double = value - 1
    if (Math.abs(r) < 0.3) {
      val rSquared: Double = r * r
       r - rSquared / 2 + rSquared * r / 3
    }
    val x: Double = (java.lang.Double.doubleToLongBits(value) >> 32) //TODO ok with ???java.lang.Double
     (x - 1072632447) / 1512775
  }

  def approxExp(value: Double): Double = {
    if (Math.abs(value) < 0.1)  1 + value
    val tmp: Long = (1512775 * value + (1072693248 - 60801)).toLong
    java.lang.Double.longBitsToDouble(tmp << 32) //TODO
  }

  def approxPow(a: Double, b: Double): Double = {
    val tmp: Int = (java.lang.Double.doubleToLongBits(a) >> 32).toInt //TODO
    val tmp2: Int = (b * (tmp - 1072632447) + 1072632447).toInt
    java.lang.Double.longBitsToDouble((tmp2.toLong) << 32) //TODO
  }

  def logSubtract(a: Double, b: Double): Double = {
    if (a > b) {
       a + Math.log(1.0 - Math.exp(b - a))
    }
    else {
       b + Math.log(-1.0 + Math.exp(a - b))
    }
  }

  def unsafeSubtract(a: Double, b: Double): Double = {
    if (a == b) {
       0.0
    }
    if (a == Double.NegativeInfinity) {
       a
    }
     a - b
  }

  def unsafeAdd(a: Double, b: Double): Double = {
    if (a == b) {
       0.0
    }
    if (a == Double.PositiveInfinity) {
       a
    }
     a + b
  }

  def logAdd[T](counts: Counter[T]): Double = {
    val arr: Array[Double] = new Array[Double](counts.size)
    var index: Int = 0
    import scala.collection.JavaConversions._
    for (entry <- counts.entrySet) {
      arr(({
        index += 1; index - 1
      })) = entry._2 //?? TODO : getValue???
    }
     SloppyMath.logAdd(arr)
  }
}

final class SloppyMath {
  private def this() {
    this()
  }
}
