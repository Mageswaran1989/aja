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

import java.io.PrintWriter
import java.io.StringWriter
import java.net.InetAddress
import java.net.URI
import java.net.URISyntaxException
import java.net.UnknownHostException
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util._

/**
 * General string utils
 */
object StringUtils {
  private var decimalFormat: DecimalFormat = null

  /**
   * Make a string representation of the exception.
   * @param e The exception to stringify
   * @return A string with exception name and call stack.
   */
  def stringifyException(e: Throwable): String = {
    val stm: StringWriter = new StringWriter
    val wrt: PrintWriter = new PrintWriter(stm)
    e.printStackTrace(wrt)
    wrt.close
    return stm.toString
  }

  /**
   * Given a full hostname, return the word upto the first dot.
   * @param fullHostname the full hostname
   * @return the hostname to the first dot
   */
  def simpleHostname(fullHostname: String): String = {
    val offset: Int = fullHostname.indexOf('.')
    if (offset != -1) {
      return fullHostname.substring(0, offset)
    }
    return fullHostname
  }

  private var oneDecimal: DecimalFormat = new DecimalFormat("0.0")

  /**
   * Given an integer, return a string that is in an approximate, but human
   * readable format.
   * It uses the bases 'k', 'm', and 'g' for 1024, 1024**2, and 1024**3.
   * @param number the number to format
   * @return a human readable form of the integer
   */
  def humanReadableInt(number: Long): String = {
    val absNumber: Long = Math.abs(number)
    var result: Double = number
    var suffix: String = ""
    if (absNumber < 1024) {
    }
    else if (absNumber < 1024 * 1024) {
      result = number / 1024.0
      suffix = "k"
    }
    else if (absNumber < 1024 * 1024 * 1024) {
      result = number / (1024.0 * 1024)
      suffix = "m"
    }
    else {
      result = number / (1024.0 * 1024 * 1024)
      suffix = "g"
    }
    return oneDecimal.format(result) + suffix
  }

  /**
   * Format a percentage for presentation to the user.
   * @param done the percentage to format (0.0 to 1.0)
   * @param digits the number of digits past the decimal point
   * @return a string representation of the percentage
   */
  def formatPercent(done: Double, digits: Int): String = {
    val percentFormat: DecimalFormat = new DecimalFormat("0.00%")
    val scale: Double = Math.pow(10.0, digits + 2)
    val rounded: Double = Math.floor(done * scale)
    percentFormat.setDecimalSeparatorAlwaysShown(false)
    percentFormat.setMinimumFractionDigits(digits)
    percentFormat.setMaximumFractionDigits(digits)
    return percentFormat.format(rounded / scale)
  }

  /**
   * Given an array of strings, return a comma-separated list of its elements.
   * @param strs Array of strings
   * @return Empty string if strs.length is 0, comma separated list of strings
   *         otherwise
   */
  def arrayToString(strs: Array[String]): String = {
    if (strs.length == 0) {
      return ""
    }
    val sbuf: StringBuilder = new StringBuilder
    sbuf.append(strs(0))
    {
      var idx: Int = 1
      while (idx < strs.length) {
        {
          sbuf.append(",")
          sbuf.append(strs(idx))
        }
        ({
          idx += 1; idx - 1
        })
      }
    }
    return sbuf.toString
  }

  /**
   * Given an array of bytes it will convert the bytes to a hex string
   * representation of the bytes
   * @param bytes
   * @param start start index, inclusively
   * @param end end index, exclusively
   * @return hex string representation of the byte array
   */
  def byteToHexString(bytes: Array[Byte], start: Int, end: Int): String = {
    if (bytes == null) {
      throw new IllegalArgumentException("bytes == null")
    }
    val s: StringBuilder = new StringBuilder

    {
      var i: Int = start
      while (i < end) {
        {
          s.append(String.format("%02x", bytes(i)))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return s.toString
  }

  /** Same as byteToHexString(bytes, 0, bytes.length). */
  def byteToHexString(bytes: Array[Byte]): String = {
    byteToHexString(bytes, 0, bytes.length)
  }

  /**
   * Given a hexstring this will return the byte array corresponding to the
   * string
   * @param hex the hex String array
   * @return a byte array that is a hex string representation of the given
   *         string. The size of the byte array is therefore hex.length/2
   */
  def hexStringToByte(hex: String): Array[Byte] = {
    val bts: Array[Byte] = Array.fill[Byte](hex.length / 2)(0)

    {
      var i: Int = 0
      while (i < bts.length) {
        {
          bts(i) = Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16).toByte
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return bts
  }

  /**
   *
   * @param uris
   */
  def uriToString(uris: Array[URI]): String = {
    if (uris == null) {
      return null
    }
    val ret: StringBuilder = new StringBuilder(uris(0).toString)

    {
      var i: Int = 1
      while (i < uris.length) {
        {
          ret.append(",")
          ret.append(uris(i).toString)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return ret.toString
  }

  /**
   *
   * @param strings
   */
  def stringToURI(strings: Array[String]): Array[URI] = {
    if (strings == null) return null
    val uris: Array[URI] = Array.fill[URI](str.length)(new URI(""))

    for (i <- 0 until strings.length) {
      try {
        uris(i) = new URI(strings(i))
      }
      catch {
        case ur: URISyntaxException => {
          System.out.println("Exception in specified URI's " + StringUtils.stringifyException(ur))
          uris(i) = null
        }
      }
      uris
    }
  }

  /**
   *
   * Given a finish and start time in long milliseconds, returns a
   * String in the format Xhrs, Ymins, Z sec, for the time difference between two times.
   * If finish time comes before start time then negative valeus of X, Y and Z wil return.
   *
   * @param finishTime finish time
   * @param startTime start time
   */
  def formatTimeDiff(finishTime: Long, startTime: Long): String = {
    val timeDiff: Long = finishTime - startTime
    return formatTime(timeDiff)
  }

  /**
   *
   * Given the time in long milliseconds, returns a
   * String in the format Xhrs, Ymins, Z sec.
   *
   * @param timeDiff The time difference to format
   */
  def formatTime(timeDiff: Long): String = {
    val buf: StringBuilder = new StringBuilder
    val hours: Long = timeDiff / (60 * 60 * 1000)
    var rem: Long = (timeDiff % (60 * 60 * 1000))
    val minutes: Long = rem / (60 * 1000)
    rem = rem % (60 * 1000)
    val seconds: Long = rem / 1000
    if (hours != 0) {
      buf.append(hours)
      buf.append("hrs, ")
    }
    if (minutes != 0) {
      buf.append(minutes)
      buf.append("mins, ")
    }
    buf.append(seconds)
    buf.append("sec")
    return buf.toString
  }

  /**
   * Formats time in ms and appends difference (finishTime - startTime)
   * as returned by formatTimeDiff().
   * If finish time is 0, empty string is returned, if start time is 0
   * then difference is not appended to return value.
   * @param dateFormat date format to use
   * @param finishTime fnish time
   * @param startTime start time
   * @return formatted value.
   */
  def getFormattedTimeWithDiff(dateFormat: DateFormat, finishTime: Long, startTime: Long): String = {
    val buf: StringBuilder = new StringBuilder
    if (0 != finishTime) {
      buf.append(dateFormat.format(new Date(finishTime)))
      if (0 != startTime) {
        buf.append(" (" + formatTimeDiff(finishTime, startTime) + ")")
      }
    }
    return buf.toString
  }

  /**
   * Returns an arraylist of strings.
   * @param str the comma separated string values
   * @return the arraylist of the comma separated string values
   */
  def getStrings(str: String): Array[String] = {
    val values: Collection[String] = getStringCollection(str)
    if (values.isEmpty) {
      return null
    }
    return values.toArray(new Array[String](values.size))
  }

  /**
   * Returns a collection of strings.
   * @param str comma separated string values
   * @return an <code>ArrayList</code> of string values
   */
  def getStringCollection(str: String): Collection[String] = {
    val values: List[String] = new ArrayList[String]
    if (str == null) return values
    val tokenizer: StringTokenizer = new StringTokenizer(str, ",")
    while (tokenizer.hasMoreTokens) {
      values.add(tokenizer.nextToken)
    }
    return values
  }

  /**
   * Splits a comma separated value <code>String</code>, trimming leading and trailing whitespace on each value.
   * @param str a comma separated <String> with values
   * @return a <code>Collection</code> of <code>String</code> values
   */
  def getTrimmedStringCollection(str: String): Collection[String] = {
    return Arrays.asList(getTrimmedStrings(str))
  }

  /**
   * Splits a comma separated value <code>String</code>, trimming leading and trailing whitespace on each value.
   * @param str a comma separated <String> with values
   * @return an array of <code>String</code> values
   */
  def getTrimmedStrings(str: String): Array[String] = {
    if (null == str || ("" == str.trim)) {
      return emptyStringArray
    }
    return str.trim.split("\\s*,\\s*")
  }

  val emptyStringArray: Array[String] = Array()
  val COMMA: Char = ','
  val ESCAPE_CHAR: Char = '\\'

  /**
   * Split a string using the default separator
   * @param str a string that may have escaped separator
   * @return an array of strings
   */
  def split(str: String): Array[String] = {
    return split(str, ESCAPE_CHAR, COMMA)
  }

  /**
   * Split a string using the given separator
   * @param str a string that may have escaped separator
   * @param escapeChar a char that be used to escape the separator
   * @param separator a separator char
   * @return an array of strings
   */
  def split(str: String, escapeChar: Char, separator: Char): Array[String] = {
    if (str == null) {
      return null
    }
    val strList: ArrayList[String] = new ArrayList[String]
    val split: StringBuilder = new StringBuilder
    var index: Int = 0
    while ((({
      index = findNext(str, separator, escapeChar, index, split); index
    })) >= 0) {
      index += 1
      strList.add(split.toString)
      split.setLength(0)
    }
    strList.add(split.toString)
    var last: Int = strList.size
    while (({
      last -= 1; last
    }) >= 0 && ("" == strList.get(last))) {
      strList.remove(last)
    }
    return strList.toArray(new Array[String](strList.size))
  }

  /**
   * Finds the first occurrence of the separator character ignoring the escaped
   * separators starting from the index. Note the substring between the index
   * and the position of the separator is passed.
   * @param str the source string
   * @param separator the character to find
   * @param escapeChar character used to escape
   * @param start from where to search
   * @param split used to pass back the extracted string
   */
  def findNext(str: String, separator: Char, escapeChar: Char, start: Int, split: StringBuilder): Int = {
    var numPreEscapes: Int = 0

    {
      var i: Int = start
      while (i < str.length) {
        {
          val curChar: Char = str.charAt(i)
          if (numPreEscapes == 0 && curChar == separator) {
            return i
          }
          else {
            split.append(curChar)
            numPreEscapes = if ((curChar == escapeChar)) (({
              numPreEscapes += 1; numPreEscapes
            })) % 2
            else 0
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return -1
  }

  /**
   * Escape commas in the string using the default escape char
   * @param str a string
   * @return an escaped string
   */
  def escapeString(str: String): String = {
    return escapeString(str, ESCAPE_CHAR, COMMA)
  }

  /**
   * Escape <code>charToEscape</code> in the string
   * with the escape char <code>escapeChar</code>
   *
   * @param str string
   * @param escapeChar escape char
   * @param charToEscape the char to be escaped
   * @return an escaped string
   */
  def escapeString(str: String, escapeChar: Char, charToEscape: Char): String = {
    return escapeString(str, escapeChar, Array[Char](charToEscape))
  }

  private def hasChar(chars: Array[Char], character: Char): Boolean = {
    chars.exists(_ == character)
  }


  /**
   * @param charsToEscape array of characters to be escaped
   */
  def escapeString(str: String, escapeChar: Char, charsToEscape: Array[Char]): String = {
    if (str == null) {
      return null
    }
    val result: StringBuilder = new StringBuilder

    {
      var i: Int = 0
      while (i < str.length) {
        {
          val curChar: Char = str.charAt(i)
          if (curChar == escapeChar || hasChar(charsToEscape, curChar)) {
            result.append(escapeChar)
          }
          result.append(curChar)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return result.toString
  }

  /**
   * Unescape commas in the string using the default escape char
   * @param str a string
   * @return an unescaped string
   */
  def unEscapeString(str: String): String = {
    return unEscapeString(str, ESCAPE_CHAR, COMMA)
  }

  /**
   * Unescape <code>charToEscape</code> in the string
   * with the escape char <code>escapeChar</code>
   *
   * @param str string
   * @param escapeChar escape char
   * @param charToEscape the escaped char
   * @return an unescaped string
   */
  def unEscapeString(str: String, escapeChar: Char, charToEscape: Char): String = {
    return unEscapeString(str, escapeChar, Array[Char](charToEscape))
  }

  /**
   * @param charsToEscape array of characters to unescape
   */
  def unEscapeString(str: String, escapeChar: Char, charsToEscape: Array[Char]): String = {
    if (str == null) {
      return null
    }
    val result: StringBuilder = new StringBuilder(str.length)
    var hasPreEscape: Boolean = false

    {
      var i: Int = 0
      while (i < str.length) {
        {
          val curChar: Char = str.charAt(i)
          if (hasPreEscape) {
            if (curChar != escapeChar && !hasChar(charsToEscape, curChar)) {
              throw new IllegalArgumentException("Illegal escaped string " + str + " unescaped " + escapeChar + " at " + (i - 1))
            }
            result.append(curChar)
            hasPreEscape = false
          }
          else {
            if (hasChar(charsToEscape, curChar)) {
              throw new IllegalArgumentException("Illegal escaped string " + str + " unescaped " + curChar + " at " + i)
            }
            else if (curChar == escapeChar) {
              hasPreEscape = true
            }
            else {
              result.append(curChar)
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (hasPreEscape) {
      throw new IllegalArgumentException("Illegal escaped string " + str + ", not expecting " + escapeChar + " in the end.")
    }
    return result.toString
  }

  /**
   * Return hostname without throwing exception.
   * @return hostname
   */
  def getHostname: String = {
    try {
      return "" + InetAddress.getLocalHost
    }
    catch {
      case uhe: UnknownHostException => {
        return "" + uhe
      }
    }
  }

  /**
   * Return a message for logging.
   * @param prefix prefix keyword for the message
   * @param msg content of the message
   * @return a message for logging
   */
  private def toStartupShutdownString(prefix: String, msg: Array[String]): String = {
    val b: StringBuilder = new StringBuilder(prefix)
    b.append("\n/************************************************************")
    for (s <- msg) b.append("\n" + prefix + s)
    b.append("\n************************************************************/")
    return b.toString
  }

  /**
   * The traditional binary prefixes, kilo, mega, ..., exa,
   * which can be represented by a 64-bit integer.
   * TraditionalBinaryPrefix symbol are case insensitive.
   */
  object TraditionalBinaryPrefix extends Enumeration {
    type TraditionalBinaryPrefix = Value
    val KILO, MEGA, GIGA, TERA, PETA, EXA = Value

    /**
     * @return The TraditionalBinaryPrefix object corresponding to the symbol.
     */
    def valueOf(sym: Char): StringUtils.TraditionalBinaryPrefix = {
      var symbol = sym.toUpper
      for (prefix <- TraditionalBinaryPrefix.values) {
        if (symbol == prefix.symbol) {
          return prefix
        }
      }
      throw new IllegalArgumentException("Unknown symbol '" + symbol + "'")
    }

    /**
     * Convert a string to long.
     * The input string is first be trimmed
     * and then it is parsed with traditional binary prefix.
     *
     * For example,
     * "-1230k" will be converted to -1230 * 1024 = -1259520;
     * "891g" will be converted to 891 * 1024^3 = 956703965184;
     *
     * @param ss input string
     * @return a long value represented by the input string.
     */
    def string2long(ss: String): Long = {
      val s = ss.trim
      val lastpos: Int = s.length - 1
      val lastchar: Char = s.charAt(lastpos)
      if (Character.isDigit(lastchar)) return s.toLong
      else {
        val prefix: Long = TraditionalBinaryPrefix.valueOf(lastchar).value
        val num: Long = (s.substring(0, lastpos)).toLong
        if (num > (Long.MaxValue / prefix) || num < (Long.MinValue / prefix)) {
          throw new IllegalArgumentException(s + " does not fit in a Long")
        }
        return num * prefix
      }
    }
  }

  final class TraditionalBinaryPrefix (val value: Long, symbol: Char) {
    private[util] def this(value: Long) {
      this(value, toString.charAt(0))
    }
  }

  /**
   * Escapes HTML Special characters present in the string.
   * @param string
   * @return HTML Escaped String representation
   */
  def escapeHTML(string: String): String = {
    if (string == null) {
      return null
    }
    val sb: StringBuilder = new StringBuilder
    var lastCharacterWasSpace: Boolean = false
    val chars: Array[Char] = string.toCharArray
    for (c <- chars) {
      if (c == ' ') {
        if (lastCharacterWasSpace) {
          lastCharacterWasSpace = false
          sb.append("&nbsp;")
        }
        else {
          lastCharacterWasSpace = true
          sb.append(" ")
        }
      }
      else {
        lastCharacterWasSpace = false
        c match {
          case '<' =>
            sb.append("&lt;")
//            break //todo: break is not supported
          case '>' =>
            sb.append("&gt;")
//            break //todo: break is not supported
          case '&' =>
            sb.append("&amp;")
//            break //todo: break is not supported
          case '"' =>
            sb.append("&quot;")
//            break //todo: break is not supported
          case _ =>
            sb.append(c)
//            break //todo: break is not supported
        }
      }
    }
    return sb.toString
  }

  /**
   * Return an abbreviated English-language desc of the byte length
   */
  def byteDesc(len: Long): String = {
    var `val`: Double = .0
    var ending: String = null
    if (len < 1024 * 1024) {
      `val` = (1.0 * len) / 1024
      ending = " KB"
    }
    else if (len < 1024 * 1024 * 1024) {
      `val` = (1.0 * len) / (1024 * 1024)
      ending = " MB"
    }
    else if (len < 1024L * 1024 * 1024 * 1024) {
      `val` = (1.0 * len) / (1024 * 1024 * 1024)
      ending = " GB"
    }
    else if (len < 1024L * 1024 * 1024 * 1024 * 1024) {
      `val` = (1.0 * len) / (1024L * 1024 * 1024 * 1024)
      ending = " TB"
    }
    else {
      `val` = (1.0 * len) / (1024L * 1024 * 1024 * 1024 * 1024)
      ending = " PB"
    }
    return limitDecimalTo2(`val`) + ending
  }

  def limitDecimalTo2(d: Double): String = {
    return decimalFormat.format(d)
  }

  /**
   * Concatenates strings, using a separator.
   *
   * @param separator Separator to join with.
   * @param strings Strings to join.
   */
  def join(separator: CharSequence, strings: Iterable[String]): String = {
    val sb: StringBuilder = new StringBuilder
    var first: Boolean = true
    import scala.collection.JavaConversions._
    for (s <- strings) {
      if (first) {
        first = false
      }
      else {
        sb.append(separator)
      }
      sb.append(s)
    }
    return sb.toString
  }

  /**
   * Concatenates stringified objects, using a separator.
   *
   * @param separator Separator to join with.
   * @param objects Objects to join.
   */
  def joinObjects(separator: CharSequence, objects: Iterable[_ <: AnyRef]): String = {
    val sb: StringBuilder = new StringBuilder
    var first: Boolean = true
    import scala.collection.JavaConversions._
    for (o <- objects) {
      if (first) {
        first = false
      }
      else {
        sb.append(separator)
      }
      sb.append(String.valueOf(o))
    }
    return sb.toString
  }

  try {
    val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
    decimalFormat = numberFormat.asInstanceOf[DecimalFormat]
    decimalFormat.applyPattern("#.##")
  }
}

class StringUtils {
  private def this() {
    this()
  }
}