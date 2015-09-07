package org.aja.tej.examples.usecases.matrix

import org.aja.tej.utils.CommandLineOptions
import org.aja.tej.utils.CommandLineOptions.Opt
import org.aja.tej.utils.FileUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 4/9/15.
 */

/**
 * A special-purpose matrix case class. Each cell is given the value
 * i*N + j for indices (i,j), counting from 0.
 * Note: Must be serializable, which is automatic for case classes.
 */
case class Matrix(m: Int, n: Int) {
  assert(m > 0 && n > 0, "m and n must be > 0")

  private def makeRow(start: Long): Array[Long] =
    Array.iterate(start, n)(i => i+1)

  private val repr: Array[Array[Long]] =
    Array.iterate(makeRow(0), m)(rowi => makeRow(rowi(0) + n))

  /** Return row i, <em>indexed from 0</em>. */
  def apply(i: Int): Array[Long]  = repr(i)

  /** Return the (i,j) element, <em>indexed from 0</em>. */
  def apply(i: Int, j: Int): Long = repr(i)(j)

  private val cellFormat = {
    val maxEntryLength = (m*n - 1).toString.length
    s"%${maxEntryLength}d"
  }

  private def rowString(rowI: Array[Long]) =
    rowI map (cell => cellFormat.format(cell)) mkString ", "

  override def toString = repr map rowString mkString "\n"
}


/**
 * Use an explicitly-parallel algorithm to sum perform statistics on
 * rows in matrices.
 */
object Multiplication {

  case class Dimensions(m: Int, n: Int)

  def main(args: Array[String]): Unit = {

    /** A function to generate an Opt for handling the matrix dimensions. */
    def dims(value: String): Opt = Opt(
      name   = "dims",
      value  = value,
      help   = s"-d | --dims  nxm     The number of rows (n) and columns (m) (default: $value)",
      parser = {
        case ("-d" | "--dims") +: nxm +: tail => (("dims", nxm), tail)
      })

    val options = CommandLineOptions(
      this.getClass.getSimpleName,
      CommandLineOptions.outputPath("output/matrix-math"),
      CommandLineOptions.master("local"),
      CommandLineOptions.quiet,
      dims("5x10"))

    val argz   = options(args.toList)
    val master = argz("master")
    val quiet  = argz("quiet").toBoolean
    val out    = argz("output-path")
    if (master.startsWith("local")) {
      if (!quiet) println(s" **** Deleting old output (if any), $out:")
      FileUtils.rmrf(out)
    }

    val dimsRE = """(\d+)\s*x\s*(\d+)""".r
    val dimensions = argz("dims") match {
      case dimsRE(m, n) => Dimensions(m.toInt, n.toInt)
      case s =>
        println(s"""Expected matrix dimensions 'NxM', but got this: $s""")
        sys.exit(1)
    }

    val sc = new SparkContext(master, "Matrix (4)")

    try {
      // Set up a mxn matrix of numbers.
      val matrix = Matrix(dimensions.m, dimensions.n)

      // Average rows of the matrix in parallel:
      val sums_avgs = sc.parallelize(1 to dimensions.m).map { i =>
        // Matrix indices count from 0.
        // "_ + _" is the same as "(count1, count2) => count1 + count2".
        val sum = matrix(i-1) reduce (_ + _)
        (sum, sum/dimensions.n)
      }.collect    // convert to an array

      // Make a new sequence of strings with the formatted output, then we'll
      // dump to the output location.
      val outputLines = Vector(          // Scala's Vector, not MLlib's version!
        s"${dimensions.m}x${dimensions.n} Matrix:") ++ sums_avgs.zipWithIndex.map {
        case ((sum, avg), index) =>
          f"Row #${index}%2d: Sum = ${sum}%4d, Avg = ${avg}%3d"
      }
      val output = sc.makeRDD(outputLines)  // convert back to an RDD
      if (!quiet) println(s"Writing output to: $out")
      output.saveAsTextFile(out)

    } finally {
      sc.stop()
    }

    // Exercise: Try different values of m, n.
    // Exercise: Try other statistics, like standard deviation.
    // Exercise: Try other statistics, like standard deviation. Are the average
    //   and standard deviation very meaningful here?
  }
}
