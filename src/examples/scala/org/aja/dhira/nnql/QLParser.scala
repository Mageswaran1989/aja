package org.aja.dhira.nnql

import scala.util.parsing.combinator._

/**
 * Created by mageswaran on 29/4/16.
 */

object Lexer {
  //Tokenizer
}
/**
 * [a-z]+ word
 */
object QLParser extends RegexParsers {
  private def word: Parser[String] = """[a-z]*""".r ^^ { _.toString }
  def parseWord(str: String): ParseResult[Any] = parse(word, str)

  private def number = """^\d*\.?\d+$""".r ^^ {_.toInt}
  def parseNumber(str: String): ParseResult[Any] = parse(number, str)

}


