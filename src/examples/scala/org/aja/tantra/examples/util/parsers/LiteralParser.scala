package org.aja.tantra.examples.util.parsers

import scala.util.parsing.combinator.{JavaTokenParsers, Parsers}
import scala.util.parsing.input.CharSequenceReader

/**
 * Created by mageswaran on 28/5/16.
 * Reference: http://henkelmann.eu/2011/01/28/an_introduction_to_scala_parser_combinators-part_2_literal_expressions
 * To parse something like: !(x*0.005 + max(sin(y), 1) >= 2)
 */

sealed trait Expression {
  def eval(env:Map[String,Literal]): Literal
}

sealed trait Literal extends Expression {
  def eval(env:Map[String, Literal]) = this
  def doubleValue:Double
  def boolValue:Boolean
  def stringValue:String
}

case class NumberLiteral(literal:Double) extends Literal{
  def doubleValue = literal
  def boolValue   = literal != 0.0
  def stringValue = literal.toString
  override def toString  = literal.toString
}

case class BooleanLiteral(literal:Boolean) extends Literal{
  def doubleValue = if (literal) 1.0 else 0.0
  def boolValue   = literal
  def stringValue = literal.toString
  override def toString  = literal.toString
}

case class StringLiteral(s:String) extends Literal{
  val literal = s.substring(1,s.length-1)//TODO: apply missing escapes
  def doubleValue = literal.toDouble
  def boolValue   = if (literal.toLowerCase == "false") false else true
  def stringValue = literal
  override def toString  = s
}

case class Variable(name:String) extends Expression {
  def eval(env:Map[String,Literal]) = env(name)
  override def toString = name
}

class LParser extends JavaTokenParsers {
  def boolean:Parser[Expression] = ("true" | "false")  ^^ {s => new BooleanLiteral(s.toBoolean)}
  def string  :Parser[Expression] = super.stringLiteral ^^ {
    s => new StringLiteral(s)}
  def double  :Parser[Expression] = (decimalNumber | floatingPointNumber) ^^ {
    s => new NumberLiteral(s.toDouble)}
  def int     :Parser[Expression] = wholeNumber ^^ {
    s => new NumberLiteral(s.toInt)}

  def literal :Parser[Expression] = boolean | string | double | int

  def variable:Parser[Expression] = ident ^^ {
    s => new Variable(s)}

  def expression:Parser[Expression] = literal | variable
}


object LiteralParser extends LParser {

  private def parsing[T](s:String)(implicit p:Parser[T]):T = {
    //wrap the parser in the phrase parse to make sure all input is consumed
    val phraseParser = phrase(p)
    //we need to wrap the string in a reader so our parser can digest it
    val input = new CharSequenceReader(s)
    phraseParser(input) match {
      case Success(t,_)     => t
      case NoSuccess(msg,_) => throw new IllegalArgumentException(
        "Could not parse '" + s + "': " + msg)
    }
  }

  def main(args: Array[String]) {

    //Reference: http://henkelmann.eu/2011/01/29/an_introduction_to_scala_parser_combinators-part_3_unit_tests
    implicit val parserToTest = boolean
     println(parsing("true"))
    println(parsing("false"))
    println(parsing("True"))
  }
}
