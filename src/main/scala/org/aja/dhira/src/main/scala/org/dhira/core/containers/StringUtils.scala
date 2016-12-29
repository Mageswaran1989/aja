package org.dhira.core.containers

import java.io._
import java.net.{SocketTimeoutException, URL, URLConnection}
import java.util._
import java.util.regex.{Matcher, Pattern}

/**
 * Created by mageswaran on 10/9/16.
 */

object StringUtils {
  /**
   * Don't let anyone instantiate this class.
   */
//  private def this() {
//    this()
//  }

  /**
   * Say whether this regular expression can be found inside
   * this String.  This method provides one of the two "missing"
   * convenience methods for regular expressions in the String class
   * in JDK1.4.  This is the one you'll want to use all the time if
   * you're used to Perl.  What were they smoking?
   *
   * @param str   String to search for match in
   * @param regex String to compile as the regular expression
   * @return Whether the regex can be found in str
   */
  def find(str: String, regex: String): Boolean = {
    return Pattern.compile(regex).matcher(str).find
  }

  /**
   * Say whether this regular expression can be found at the beginning of
   * this String.  This method provides one of the two "missing"
   * convenience methods for regular expressions in the String class
   * in JDK1.4.
   *
   * @param str   String to search for match at start of
   * @param regex String to compile as the regular expression
   * @return Whether the regex can be found at the start of str
   */
  def lookingAt(str: String, regex: String): Boolean = {
    return Pattern.compile(regex).matcher(str).lookingAt
  }

  /**
   * Say whether this regular expression matches
   * this String.  This method is the same as the String.matches() method,
   * and is included just to give a call that is parallel to the other
   * static regex methods in this class.
   *
   * @param str   String to search for match at start of
   * @param regex String to compile as the regular expression
   * @return Whether the regex matches the whole of this str
   */
  def matches(str: String, regex: String): Boolean = {
    return Pattern.compile(regex).matcher(str).matches
  }

  private val SLURPBUFFSIZE: Int = 16000

  /**
   * Returns all the text in the given File.
   */
  @throws(classOf[IOException])
  def slurpFile(file: File): String = {
    val r: Reader = new FileReader(file)
    return slurpReader(r)
  }

  def slurpGBFileNoExceptions(filename: String): String = {
    return slurpFileNoExceptions(filename, "GB18030")
  }

  /**
   * Returns all the text in the given file with the given encoding.
   */
  @throws(classOf[IOException])
  def slurpFile(filename: String, encoding: String): String = {
    val r: Reader = new InputStreamReader(new FileInputStream(filename), encoding)
    return slurpReader(r)
  }

  /**
   * Returns all the text in the given file with the given encoding.
   * If the file cannot be read (non-existent, etc.),
   * then and only then the method returns <code>null</code>.
   */
  def slurpFileNoExceptions(filename: String, encoding: String): String = {
    try {
      return slurpFile(filename, encoding)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException
      }
    }
  }

  @throws(classOf[IOException])
  def slurpGBFile(filename: String): String = {
    return slurpFile(filename, "GB18030")
  }

  /**
   * Returns all the text from the given Reader.
   *
   * @return The text in the file.
   */
  def slurpReader(reader: Reader): String = {
    val r: BufferedReader = new BufferedReader(reader)
    val buff: StringBuilder = new StringBuilder
    try {
      val chars: Array[Char] = new Array[Char](SLURPBUFFSIZE)
      while (true) {
        val amountRead: Int = r.read(chars, 0, SLURPBUFFSIZE)
        if (amountRead < 0) {
          break //todo: break is not supported
        }
        buff.append(chars, 0, amountRead)
      }
      r.close
    }
    catch {
      case e: Exception => {
        throw new RuntimeException
      }
    }
    return buff.toString
  }

  /**
   * Returns all the text in the given file
   *
   * @return The text in the file.
   */
  @throws(classOf[IOException])
  def slurpFile(filename: String): String = {
    return slurpReader(new FileReader(filename))
  }

  /**
   * Returns all the text in the given File.
   *
   * @return The text in the file.  May be an empty string if the file
   *         is empty.  If the file cannot be read (non-existent, etc.),
   *         then and only then the method returns <code>null</code>.
   */
  def slurpFileNoExceptions(file: File): String = {
    try {
      return slurpReader(new FileReader(file))
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        return null
      }
    }
  }

  /**
   * Returns all the text in the given File.
   *
   * @return The text in the file.  May be an empty string if the file
   *         is empty.  If the file cannot be read (non-existent, etc.),
   *         then and only then the method returns <code>null</code>.
   */
  def slurpFileNoExceptions(filename: String): String = {
    try {
      return slurpFile(filename)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        return null
      }
    }
  }

  /**
   * Returns all the text at the given URL.
   */
  @throws(classOf[IOException])
  def slurpGBURL(u: URL): String = {
    return slurpURL(u, "GB18030")
  }

  /**
   * Returns all the text at the given URL.
   */
  def slurpGBURLNoExceptions(u: URL): String = {
    try {
      return slurpGBURL(u)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        return null
      }
    }
  }

  /**
   * Returns all the text at the given URL.
   */
  def slurpURLNoExceptions(u: URL, encoding: String): String = {
    try {
      return slurpURL(u, encoding)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        return null
      }
    }
  }

  /**
   * Returns all the text at the given URL.
   */
  @throws(classOf[IOException])
  def slurpURL(u: URL, encoding: String): String = {
    val lineSeparator: String = System.getProperty("line.separator")
    val uc: URLConnection = u.openConnection
    uc.setReadTimeout(30000)
    var is: InputStream = null
    try {
      is = uc.getInputStream
    }
    catch {
      case e: SocketTimeoutException => {
        System.err.println("Time out. Return empty string")
        return ""
      }
    }
    val br: BufferedReader = new BufferedReader(new InputStreamReader(is, encoding))
    var temp: String = null
    val buff: StringBuilder = new StringBuilder(16000)
    while ((({
      temp = br.readLine; temp
    })) != null) {
      buff.append(temp)
      buff.append(lineSeparator)
    }
    br.close
    return buff.toString
  }

  /**
   * Returns all the text at the given URL.
   */
  @throws(classOf[IOException])
  def slurpURL(u: URL): String = {
    val lineSeparator: String = System.getProperty("line.separator")
    val uc: URLConnection = u.openConnection
    val is: InputStream = uc.getInputStream
    val br: BufferedReader = new BufferedReader(new InputStreamReader(is))
    var temp: String = null
    val buff: StringBuilder = new StringBuilder(16000)
    while ((({
      temp = br.readLine; temp
    })) != null) {
      buff.append(temp)
      buff.append(lineSeparator)
    }
    br.close
    return buff.toString
  }

  /**
   * Returns all the text at the given URL.
   */
  def slurpURLNoExceptions(u: URL): String = {
    try {
      return slurpURL(u)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        return null
      }
    }
  }

  /**
   * Returns all the text at the given URL.
   */
  @throws(classOf[Exception])
  def slurpURL(path: String): String = {
    return slurpURL(new URL(path))
  }

  /**
   * Returns all the text at the given URL. If the file cannot be read (non-existent, etc.),
   * then and only then the method returns <code>null</code>.
   */
  def slurpURLNoExceptions(path: String): String = {
    try {
      return slurpURL(path)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        return null
      }
    }
  }

  /**
   * Joins each elem in the Collection with the given glue. For example, given a
   * list
   * of Integers, you can createComplex a comma-separated list by calling
   * <tt>join(numbers, ", ")</tt>.
   */
  def join(l: Iterable[_], glue: String): String = {
    val sb: StringBuilder = new StringBuilder
    var first: Boolean = true
    import scala.collection.JavaConversions._
    for (o <- l) {
      if (!first) {
        sb.append(glue)
      }
      sb.append(o.toString)
      first = false
    }
    return sb.toString
  }

  /**
   * Joins each elem in the List with the given glue. For example, given a
   * list
   * of Integers, you can createComplex a comma-separated list by calling
   * <tt>join(numbers, ", ")</tt>.
   */
  def join(l: List[_], glue: String): String = {
    val sb: StringBuilder = new StringBuilder
    {
      var i: Int = 0
      while (i < l.size) {
        {
          if (i > 0) {
            sb.append(glue)
          }
          val x: AnyRef = l.get(i)
          sb.append(x.toString)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sb.toString
  }

  /**
   * Joins each elem in the array with the given glue. For example, given a list
   * of ints, you can createComplex a comma-separated list by calling
   * <tt>join(numbers, ", ")</tt>.
   */
  def join(elements: Array[AnyRef], glue: String): String = {
    return (join(Arrays.asList(elements), glue))
  }

  /**
   * Joins elems with a space.
   */
  def join(l: List[_]): String = {
    return join(l, " ")
  }

  /**
   * Joins elems with a space.
   */
  def join(elements: Array[AnyRef]): String = {
    return (join(elements, " "))
  }

  /**
   * Splits on whitespace (\\s+).
   */
  def split(s: String): List[_] = {
    return (split(s, "\\s+"))
  }

  /**
   * Splits the given string using the given regex as delimiters.
   * This method is the same as the String.split() method (except it throws
   * the results in a List),
   * and is included just to give a call that is parallel to the other
   * static regex methods in this class.
   *
   * @param str   String to split up
   * @param regex String to compile as the regular expression
   * @return List of Strings resulting from splitting on the regex
   */
  def split(str: String, regex: String): List[_] = {
    return (Arrays.asList(str.split(regex)))
  }

  /**
   * Return a String of length a minimum of totalChars characters by
   * padding the input String str with spaces.  If str is already longer
   * than totalChars, it is returned unchanged.
   */
  def pad(str: String, totalChars: Int): String = {
    if (str == null) str = "null"
    val slen: Int = str.length
    val sb: StringBuilder = new StringBuilder(str)
    {
      var i: Int = 0
      while (i < totalChars - slen) {
        {
          sb.append(" ")
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sb.toString
  }

  /**
   * Pads the toString value of the given Object.
   */
  def pad(obj: AnyRef, totalChars: Int): String = {
    return pad(obj.toString, totalChars)
  }

  /**
   * Pad or trim so as to produce a string of exactly a certain length.
   *
   * @param str The String to be padded or truncated
   * @param num The desired length
   */
  def padOrTrim(str: String, num: Int): String = {
    if (str == null) str = "null"
    val leng: Int = str.length
    if (leng < num) {
      val sb: StringBuilder = new StringBuilder(str)
      {
        var i: Int = 0
        while (i < num - leng) {
          {
            sb.append(" ")
          }
          ({
            i += 1; i - 1
          })
        }
      }
      return sb.toString
    }
    else if (leng > num) {
      return str.substring(0, num)
    }
    else {
      return str
    }
  }

  /**
   * Pad or trim the toString value of the given Object.
   */
  def padOrTrim(obj: AnyRef, totalChars: Int): String = {
    return padOrTrim(obj.toString, totalChars)
  }

  /**
   * Pads the given String to the left with spaces to ensure that it's
   * at least totalChars long.
   */
  def padLeft(str: String, totalChars: Int): String = {
    if (str == null) str = "null"
    val sb: StringBuilder = new StringBuilder
    {
      var i: Int = 0
      while (i < totalChars - str.length) {
        {
          sb.append(" ")
        }
        ({
          i += 1; i - 1
        })
      }
    }
    sb.append(str)
    return sb.toString
  }

  def padLeft(obj: AnyRef, totalChars: Int): String = {
    return padLeft(obj.toString, totalChars)
  }

  def padLeft(i: Int, totalChars: Int): String = {
    return padLeft(new Integer(i), totalChars)
  }

  def padLeft(d: Double, totalChars: Int): String = {
    return padLeft(new Double(d), totalChars)
  }

  /**
   * Returns s if it's at most maxWidth chars, otherwise chops right side to fit.
   */
  def trim(s: String, maxWidth: Int): String = {
    if (s.length <= maxWidth) {
      return (s)
    }
    return (s.substring(0, maxWidth))
  }

  def trim(obj: AnyRef, maxWidth: Int): String = {
    return trim(obj.toString, maxWidth)
  }

  /**
   * Returns a "clean" version of the given filename in which spaces have
   * been converted to dashes and all non-alphaneumeric chars are underscores.
   */
  def fileNameClean(s: String): String = {
    val chars: Array[Char] = s.toCharArray
    val sb: StringBuilder = new StringBuilder
    {
      var i: Int = 0
      while (i < chars.length) {
        {
          val c: Char = chars(i)
          if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c == '_')) {
            sb.append(c)
          }
          else {
            if (c == ' ' || c == '-') {
              sb.append('_')
            }
            else {
              sb.append("x" + c.toInt + "x")
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sb.toString
  }

  /**
   * Returns the index of the <i>n</i>th occurrence of ch in s, or -1
   * if there are less than n occurrences of ch.
   */
  def nthIndex(s: String, ch: Char, n: Int): Int = {
    var index: Int = 0

    {
      var i: Int = 0
      while (i < n) {
        {
          if (index == s.length - 1) {
            return -1
          }
          index = s.indexOf(ch, index + 1)
          if (index == -1) {
            return (-1)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return index
  }

  /**
   * This returns a string from decimal digit smallestDigit to decimal digit
   * biggest digit. Smallest digit is labeled 1, and the limits are
   * inclusive.
   */
  def truncate(n: Int, smallestDigit: Int, biggestDigit: Int): String = {
    val numDigits: Int = biggestDigit - smallestDigit + 1
    val result: Array[Char] = new Array[Char](numDigits)
    {
      var j: Int = 1
      while (j < smallestDigit) {
        {
          n = n / 10
        }
        ({
          j += 1; j - 1
        })
      }
    }
    {
      var j: Int = numDigits - 1
      while (j >= 0) {
        {
          result(j) = Character.forDigit(n % 10, 10)
          n = n / 10
        }
        ({
          j -= 1; j + 1
        })
      }
    }
    return new String(result)
  }

  /**
   * Parses command line arguments into a Map. Arguments of the form
   * <p/>
   * -flag1 arg1a arg1b ... arg1m -flag2 -flag3 arg3a ... arg3n
   * <p/>
   * will be parsed so that the flag is a key in the Map (including
   * the hyphen) and its value will be a {@link String[] } containing
   * the optional arguments (if present).  The non-flag values not
   * captured as flag arguments are collected into a String[] array
   * and returned as the value of <code>null</code> in the Map.  In
   * this invocation, flags cannot take arguments, so all the {@link
   * String} array values other than the value for <code>null</code>
   * will be zero-length.
   *
   * @param args
   * @return a { @link Map} of flag names to flag argument { @link
   * String[]} arrays.
   */
  def argsToMap(args: Array[String]): Map[String, Array[String]] = {
    return argsToMap(args, new HashMap[String, Integer])
  }

  /**
   * Parses command line arguments into a Map. Arguments of the form
   * <p/>
   * -flag1 arg1a arg1b ... arg1m -flag2 -flag3 arg3a ... arg3n
   * <p/>
   * will be parsed so that the flag is a key in the Map (including
   * the hyphen) and its value will be a {@link String[] } containing
   * the optional arguments (if present).  The non-flag values not
   * captured as flag arguments are collected into a String[] array
   * and returned as the value of <code>null</code> in the Map.  In
   * this invocation, the maximum number of arguments for each flag
   * can be specified as an {@link Integer} value of the appropriate
   * flag key in the <code>flagsToNumArgs</code> {@link Map}
   * argument. (By default, flags cannot take arguments.)
   * <p/>
   * Example of usage:
   * <p/>
   * <code>
   * Map flagsToNumArgs = new HashMap();
   * flagsToNumArgs.put("-x",new Integer(2));
   * flagsToNumArgs.put("-d",new Integer(1));
   * Map result = argsToMap(args,flagsToNumArgs);
   * </code>
   *
   * @param args           the argument array to be parsed
   * @param flagsToNumArgs a { @link Map} of flag names to { @link
   * Integer} values specifying the maximum number of allowed
   * arguments for that flag (default 0).
   * @return a { @link Map} of flag names to flag argument { @link
   * String[]} arrays.
   */
  def argsToMap(args: Array[String], flagsToNumArgs: Map[String, Integer]): Map[String, Array[String]] = {
    val result: Map[String, Array[String]] = new HashMap[String, Array[String]]
    val remainingArgs: List[String] = new ArrayList[String]
    var key: String = null
    {
      var i: Int = 0
      while (i < args.length) {
        {
          key = args(i)
          if (key.charAt(0) == '-') {
            val maxFlagArgs: Integer = flagsToNumArgs.get(key)
            val max: Int = if (maxFlagArgs == null) 0 else maxFlagArgs.intValue
            val flagArgs: List[String] = new ArrayList[String]
            {
              var j: Int = 0
              while (j < max && i + 1 < args.length && args(i + 1).charAt(0) != '-') {
                {
                  flagArgs.add(args(i + 1))
                }
                ({
                  i += 1; i - 1
                })
                ({
                  j += 1; j - 1
                })
              }
            }
            if (result.containsKey(key)) {
              val newFlagArg: Array[String] = new Array[String](result.get(key).length + flagsToNumArgs.get(key))
              val oldNumArgs: Int = result.get(key).length
              System.arraycopy(result.get(key), 0, newFlagArg, 0, oldNumArgs)

              {
                var j: Int = 0
                while (j < flagArgs.size) {
                  {
                    newFlagArg(j + oldNumArgs) = flagArgs.get(j)
                  }
                  ({
                    j += 1; j - 1
                  })
                }
              }
            }
            else result.put(key, flagArgs.toArray(Array[String]))
          }
          else {
            remainingArgs.add(args(i))
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    result.put(null, remainingArgs.toArray(Array[String]))
    return result
  }

  private val PROP: String = "prop"

  def argsToProperties(args: Array[String]): Properties = {
    return argsToProperties(args, new HashMap[_, _])
  }

  /**
   * Analagous to {@link #argsToMap}.  However, there are several key differences between this method and {@link #argsToMap}:
   * <ul>
   * <li> Hyphens are stripped from flag names </li>
   * <li> Since Properties objects are String to String mappings, the default number of arguments to a flag is
   * assumed to be 1 and not 0. </li>
   * <li> Furthermore, the list of arguments not bound to a flag is mapped to the "" property, not null </li>
   * <li> The special flag "-prop" will load the property file specified by it's argument. </li>
   * <li> The value for flags without arguments is applyTransformToDestination to "true" </li>
   */
  def argsToProperties(args: Array[String], flagsToNumArgs: Map[_, _]): Properties = {
    val result: Properties = new Properties
    val remainingArgs: List[String] = new ArrayList[String]
    var key: String = null

    {
      var i: Int = 0
      while (i < args.length) {
        {
          key = args(i)
          if (key.charAt(0) == '-') {
            key = key.substring(1)
            val maxFlagArgs: Integer = flagsToNumArgs.get(key).asInstanceOf[Integer]
            val max: Int = if (maxFlagArgs == null) 1 else maxFlagArgs.intValue
            val flagArgs: List[String] = new ArrayList[String]
            {
              var j: Int = 0
              while (j < max && i + 1 < args.length && args(i + 1).charAt(0) != '-') {
                {
                  flagArgs.add(args(i + 1))
                }
                ({
                  i += 1; i - 1
                })
                ({
                  j += 1; j - 1
                })
              }
            }
            if (flagArgs.isEmpty) {
              result.setProperty(key, "true")
            }
            else {
              result.setProperty(key, join(flagArgs, " "))
              if (key.equalsIgnoreCase(PROP)) {
                try {
                  result.load(new BufferedInputStream(new FileInputStream(result.getProperty(PROP))))
                }
                catch {
                  case e: IOException => {
                    e.printStackTrace
                  }
                }
              }
            }
          }
          else {
            remainingArgs.add(args(i))
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    result.setProperty("", join(remainingArgs, " "))
    return result
  }

  /**
   * This method converts a comma-separated String (with whitespace
   * optionally allowed after the comma) representing properties
   * to a Properties object.  Each property is "property=value".  The value
   * for properties without an explicitly given value is applyTransformToDestination to "true".
   */
  def stringToProperties(str: String): Properties = {
    val result: Properties = new Properties
    val props: Array[String] = str.trim.split(",\\s*")
    {
      var i: Int = 0
      while (i < props.length) {
        {
          val term: String = props(i)
          val divLoc: Int = term.indexOf('=')
          var key: String = null
          var value: String = null
          if (divLoc >= 0) {
            key = term.substring(0, divLoc)
            value = term.substring(divLoc + 1)
          }
          else {
            key = term
            value = "true"
          }
          result.setProperty(key, value)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return result
  }

  /**
   * Prints to a file.  If the file already exists, appends if
   * <code>append=true</code>, and overwrites if <code>append=false</code>
   */
  def printToFile(file: File, message: String, append: Boolean) {
    var fw: FileWriter = null
    var pw: PrintWriter = null
    try {
      fw = new FileWriter(file, append)
      pw = new PrintWriter(fw)
      pw.print(message)
    }
    catch {
      case e: Exception => {
        System.out.println("Exception: in printToFile " + file.getAbsolutePath + " " + message)
        e.printStackTrace
      }
    } finally {
      if (pw != null) {
        pw.close
      }
    }
  }

  /**
   * Prints to a file.  If the file does not exist, rewrites the file;
   * does not append.
   */
  def printToFile(file: File, message: String) {
    printToFile(file, message, false)
  }

  /**
   * Prints to a file.  If the file already exists, appends if
   * <code>append=true</code>, and overwrites if <code>append=false</code>
   */
  def printToFile(filename: String, message: String, append: Boolean) {
    printToFile(new File(filename), message, append)
  }

  /**
   * Prints to a file.  If the file does not exist, rewrites the file;
   * does not append.
   */
  def printToFile(filename: String, message: String) {
    printToFile(new File(filename), message, false)
  }

  /**
   * A simpler form of command line argument parsing.
   * Dan thinks this is highly superior to the overly complexified code that
   * comes before it.
   * Parses command line arguments into a Map. Arguments of the form
   * -flag1 arg1 -flag2 -flag3 arg3
   * will be parsed so that the flag is a key in the Map (including the hyphen)
   * and the
   * optional argument will be its value (if present).
   *
   * @param args
   * @return A Map from keys to possible values (String or null)
   */
  def parseCommandLineArguments(args: Array[String]): Map[_, _] = {
    val result: Map[String, String] = new HashMap[String, String]
    var key: String = null
    var value: String = null

    {
      var i: Int = 0
      while (i < args.length) {
        {
          key = args(i)
          if (key.charAt(0) == '-') {
            if (i + 1 < args.length) {
              value = args(i + 1)
              if (value.charAt(0) != '-') {
                result.put(key, value)
                i += 1
              }
              else {
                result.put(key, null)
              }
            }
            else {
              result.put(key, null)
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return result
  }

  def stripNonAlphaNumerics(orig: String): String = {
    val sb: StringBuilder = new StringBuilder
    var c: Char = 0

    {
      var i: Int = 0
      while (i < orig.length) {
        {
          c = orig.charAt(i)
          if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
            sb.append(c)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sb.toString
  }

  def printStringOneCharPerLine(s: String) {
    {
      var i: Int = 0
      while (i < s.length) {
        {
          val c: Int = s.charAt(i)
          System.out.println(c + " \'" + c.toChar + "\' ")
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def escapeString(s: String, charsToEscape: Array[Char], escapeChar: Char): String = {
    val result: StringBuilder = new StringBuilder
    {
      var i: Int = 0
      while (i < s.length) {
        {
          val c: Char = s.charAt(i)
          if (c == escapeChar) {
            result.append(escapeChar)
          }
          else {
            {
              var j: Int = 0
              while (j < charsToEscape.length) {
                {
                  if (c == charsToEscape(j)) {
                    result.append(escapeChar)
                    break //todo: break is not supported
                  }
                }
                ({
                  j += 1; j - 1
                })
              }
            }
          }
          result.append(c)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return result.toString
  }

  /**
   * This function splits the String s into multiple Strings using the
   * splitChar.  However, it provides an quoting facility: it is possible to
   * quote strings with the quoteChar.
   * If the quoteChar occurs within the quotedExpression, it must be prefaced
   * by the escapeChar
   *
   * @param s         The String to split
   * @param splitChar
   * @param quoteChar
   * @return An array of Strings that s is split into
   */
  def splitOnCharWithQuoting(s: String, splitChar: Char, quoteChar: Char, escapeChar: Char): Array[String] = {
    val result: List[String] = new ArrayList[String]
    var i: Int = 0
    val length: Int = s.length
    var b: StringBuilder = new StringBuilder
    while (i < length) {
      var curr: Char = s.charAt(i)
      if (curr == splitChar) {
        if (b.length > 0) {
          result.add(b.toString)
          b = new StringBuilder
        }
        i += 1
      }
      else if (curr == quoteChar) {
        i += 1
        while (i < length) {
          curr = s.charAt(i)
          if (curr == escapeChar) {
            b.append(s.charAt(i + 1))
            i += 2
          }
          else if (curr == quoteChar) {
            i += 1
            break //todo: break is not supported
          }
          else {
            b.append(s.charAt(i))
            i += 1
          }
        }
      }
      else {
        b.append(curr)
        i += 1
      }
    }
    if (b.length > 0) {
      result.add(b.toString)
    }
    return result.toArray(new Array[String](0))
  }

  /**
   * Computes the longest common substring of s and t.
   * The longest common substring of a and b is the longest run of
   * characters that appear in order inside both a and b. Both a and b
   * may have other extraneous characters along the way. This is like
   * edit distance but with no substitution and a higher number means
   * more similar. For example, the LCS of "abcD" and "aXbc" is 3 (abc).
   */
  def longestCommonSubstring(s: String, t: String): Int = {
    var d: Int = null
    var n: Int = 0
    var m: Int = 0
    var i: Int = 0
    var j: Int = 0
    var s_i: Char = 0
    var t_j: Char = 0
    n = s.length
    m = t.length
    if (n == 0) {
      return 0
    }
    if (m == 0) {
      return 0
    }
    d = new Array[Array[Int]](n + 1, m + 1)
    {
      i = 0
      while (i <= n) {
        {
          d(i)(0) = 0
        }
        ({
          i += 1; i - 1
        })
      }
    }
    {
      j = 0
      while (j <= m) {
        {
          d(0)(j) = 0
        }
        ({
          j += 1; j - 1
        })
      }
    }
    {
      i = 1
      while (i <= n) {
        {
          s_i = s.charAt(i - 1)
          {
            j = 1
            while (j <= m) {
              {
                t_j = t.charAt(j - 1)
                if (s_i == t_j) {
                  d(i)(j) = SloppyMath.max(d(i - 1)(j), d(i)(j - 1), d(i - 1)(j - 1) + 1)
                }
                else {
                  d(i)(j) = Math.max(d(i - 1)(j), d(i)(j - 1))
                }
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
    if (false) {
      val numChars: Int = Math.ceil(Math.log(d(n)(m)) / Math.log(10)).toInt
      {
        i = 0
        while (i < numChars + 3) {
          {
            System.err.print(' ')
          }
          ({
            i += 1; i - 1
          })
        }
      }
      {
        j = 0
        while (j < m) {
          {
            System.err.print("" + t.charAt(j) + " ")
          }
          ({
            j += 1; j - 1
          })
        }
      }
      System.err.println
      {
        i = 0
        while (i <= n) {
          {
            System.err.print((if (i == 0) ' ' else s.charAt(i - 1)) + " ")
            {
              j = 0
              while (j <= m) {
                {
                  System.err.print("" + d(i)(j) + " ")
                }
                ({
                  j += 1; j - 1
                })
              }
            }
            System.err.println
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    return d(n)(m)
  }

  /**
   * Computes the Levenshtein (edit) distance of the two given Strings.
   */
  def editDistance(s: String, t: String): Int = {
    var d: Int = null
    var n: Int = 0
    var m: Int = 0
    var i: Int = 0
    var j: Int = 0
    var s_i: Char = 0
    var t_j: Char = 0
    var cost: Int = 0
    n = s.length
    m = t.length
    if (n == 0) {
      return m
    }
    if (m == 0) {
      return n
    }
    d = new Array[Array[Int]](n + 1, m + 1)
    {
      i = 0
      while (i <= n) {
        {
          d(i)(0) = i
        }
        ({
          i += 1; i - 1
        })
      }
    }
    {
      j = 0
      while (j <= m) {
        {
          d(0)(j) = j
        }
        ({
          j += 1; j - 1
        })
      }
    }
    {
      i = 1
      while (i <= n) {
        {
          s_i = s.charAt(i - 1)
          {
            j = 1
            while (j <= m) {
              {
                t_j = t.charAt(j - 1)
                if (s_i == t_j) {
                  cost = 0
                }
                else {
                  cost = 1
                }
                d(i)(j) = SloppyMath.min(d(i - 1)(j) + 1, d(i)(j - 1) + 1, d(i - 1)(j - 1) + cost)
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
    return d(n)(m)
  }

  /**
   * Computes the WordNet 2.0 POS tag corresponding to the PTB POS tag s.
   *
   * @param s a Penn TreeBank POS tag.
   */
  def pennPOSToWordnetPOS(s: String): String = {
    if (s.matches("NN|NNP|NNS|NNPS")) {
      return "noun"
    }
    if (s.matches("VB|VBD|VBG|VBN|VBZ|VBP|MD")) {
      return "verb"
    }
    if (s.matches("JJ|JJR|JJS|CD")) {
      return "adjective"
    }
    if (s.matches("RB|RBR|RBS|RP|WRB")) {
      return "adverb"
    }
    return null
  }

  /**
   * Uppercases the first character of a string.
   *
   * @param s a string to capitalize
   * @return a capitalized version of the string
   */
  def capitalize(s: String): String = {
    if (s.charAt(0) >= 'a') {
      return (s.charAt(0) + ('A' - 'a')).toChar + s.substring(1)
    }
    else {
      return s
    }
  }

  def allMatches(str: String, regex: String): List[Matcher] = {
    val p: Pattern = Pattern.compile(regex)
    val matches: List[Matcher] = new ArrayList[Matcher]
    while (true) {
      val m: Matcher = p.matcher(str)
      if (!m.find) break //todo: break is not supported
      matches.add(m)
      str = str.substring(m.end)
    }
    return matches
  }

  @throws(classOf[IOException])
  def main(args: Array[String]) {
    val s: Array[String] = Array("there once was a man", "this one is a manic", "hey there", "there once was a mane", "once in a manger.", "where is one match?")
    {
      var i: Int = 0
      while (i < 6) {
        {
          {
            var j: Int = 0
            while (j < 6) {
              {
                System.out.println("s1: " + s(i))
                System.out.println("s2: " + s(j))
                System.out.println("edit distance: " + editDistance(s(i), s(j)))
                System.out.println("LCS:           " + longestCommonSubstring(s(i), s(j)))
                System.out.println
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
  }

