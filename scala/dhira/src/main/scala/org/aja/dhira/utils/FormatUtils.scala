package org.aja.dhira.utils

/**
 * Created by mageswaran on 19/7/15.
 */
object FormatUtils {
  import java.text.DecimalFormat

  private val NO_ROUNDING_ERROR = -1.0
  private val ROUNDING_ERROR = 1e-5
  private val ONE_ROUNDING_ERROR = 1.0 - ROUNDING_ERROR

  /**
   * <p>Class to format the output of double floating point, integer and string values.
   * The client has the option to specify whether the value has to be rounded to the next Int.
   * If the rounding error is 0.001  3.9999 will be rounded to 4 and 6.0003 will be rounded to 6
   * </p>
   * @constructor Create a format type for floating point values
   * @param align defines the alignment for the display of the value (i.e. '%6s')
   * @param fmtStr for Decimal values (i.e. '#,##0.000')
   * @param roundingError Specify the maximum adjustment for rounding error. Default No rounding
   * error
   */
  protected class FormatType(
                              align: String,
                              fmtStr: String,
                              roundingError: Double = NO_ROUNDING_ERROR) {
    val fmt = new DecimalFormat(fmtStr)

    /**
     * Format a floating point value
     * @param x floating point value
     */
    def toString(x: Double): String = s"${String.format(align, fmt.format(conv(x)))}"

    def toString(x: (Double, Double)): String = s"${
      String.format(align, fmt.format(conv(x._1)), fmt.format(conv(x._2)))}"

    /**
     * Format an integer value
     * @param n integer
     */
    def toString(n: Int): String = String.format(align, n.toString)
    /**
     * Format a string
     * @param s characters string
     */
    def toString(s: String): String =  String.format(align, s)
    /**
     * Format a parameterized type T
     * @param t value of type T to format
     */
    def toString[T](t: T): String = String.format(align, t.toString)

    private def conv(x: Double): Double = roundingError match {
      case NO_ROUNDING_ERROR => x
      case ROUNDING_ERROR => {
        val xFloor = x.floor
        if(x - xFloor < ROUNDING_ERROR) xFloor
        else if(x - xFloor > ONE_ROUNDING_ERROR) xFloor+1.0
        else x
      }
    }
  }

  /**
   * Short format as 6.004912491 => 6.004
   */
  object ShortFormat extends FormatType("%8s", "#,##0.000")

  /**
   * Short format as 6.004912491 => 6.004 : (Double, Double)
   */
  object ShortFormatDouble extends FormatType("%8s\t%8s", "#,##0.000")
  /**
   * Short format with rounding error adjustment as 6.0049124 => 6.000
   */
  object ShortFormatRoundingError extends FormatType("%8s", "#,##0.000", ROUNDING_ERROR)

  /**
   * Medium format as 6.004912491 => 6.00491
   */
  object MediumFormat extends FormatType("%11s", "#,##0.00000")

  /**
   * Method to format a single floating point value using a given format
   * @param x value of type Double
   * @param label for y-Axis or values
   * @param fmt Format type used in the representation of the time series values
   */
  def format(x: Double, label: String, fmt: FormatType): String =
    if(label.length >1) s"$label ${fmt.toString(x)}" else fmt.toString(x)

  def format(x: (Double, Double), label: String, fmt: FormatType): String =
    if(label.length >1) s"$label ${fmt.toString(x)}" else fmt.toString(x)
}
