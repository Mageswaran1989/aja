package org.aja.tantra.examples.util.regex


/**
 * Created by mageswaran on 28/5/16.
 */

import scala.util.parsing.combinator._

object P extends RegexParsers {
  val plus = "+"
eiprs-  val num = rep("[0-9]".r)
  val expr = num ~ plus ~ num
}

object P1 extends RegexParsers {
  val plus: Parser[String] = "+"
  val num: Parser[Int] = rep("[0-9]".r) map {_.mkString.toInt}
  val expr: Parser[Int] = num ~ plus ~ num map {case l ~ _ ~ r => l + r}
}

object RecursiveP2 extends RegexParsers {
  val plus: Parser[String] = "+"
  val num: Parser[Int] = rep("[0-9]".r) map {_.mkString.toInt}
  val side = "(" ~> expr <~ ")" | num
  val expr: Parser[Int] = (side ~ plus ~ side) map {case l ~ _ ~ r => l + r}
}

object RegexParsersExamlple {
  def main(args: Array[String]) {
    
    println(P.parseAll(P.expr, "123+123")) //[1.8] parsed: ((List(1, 2, 3)~+)~List(1, 2, 3))
    println(P.parseAll(P.expr, "123123"))
    //  [1.7] failure: `+' expected but end of source found
    //123123
    //^

    println(P1.parseAll(P1.expr, "123+123")) //[1.8] parsed: 246

    println(RecursiveP2.parseAll(RecursiveP2.expr, "1+(3+4)")) //[1.8] parsed: 8
    println(RecursiveP2.parseAll(RecursiveP2.expr, "((1+2)+(3+4))+5")) //[1.16] parsed: 15
  }
}

