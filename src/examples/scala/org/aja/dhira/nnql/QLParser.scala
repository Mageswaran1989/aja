package org.aja.dhira.nnql

import scala.util.parsing.combinator._
import scala.util.matching.Regex
/**
 * Created by mageswaran on 29/4/16.
 */

/**
 * [a-z]+ word
 */
object QLParser extends RegexParsers {
  private def word: Parser[String] = """[a-z]*""".r ^^ { _.toString }
  def parseWord(str: String): ParseResult[Any] = parse(word, str)

  private def number = """^\d*\.?\d+$""".r ^^ {_.toInt}
  def parseNumber(str: String): ParseResult[Any] = parse(number, str)
}



//    QLParser.parseWord("""create layer "L1" with 5 neurons""") match {
//      case QLParser.Success(result, _) => println(result.toString)
//      case _ => println("Could not parse the input string.")
//    }
//
//    QLParser.parseNumber("""create layer "L1" with 5 neurons""") match {
//      case QLParser.Success(result, _) => println(result.toString)
//      case QLParser.Failure(msg,_) => println("FAILURE: " + msg)
//      case QLParser.Error(msg,_) => println("ERROR: " + msg)
//    }