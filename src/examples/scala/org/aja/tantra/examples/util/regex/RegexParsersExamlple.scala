package org.aja.tantra.examples.util.regex


/**
  * Created by mageswaran on 28/5/16.
  */

import scala.util.parsing.combinator._

object P extends RegexParsers {
  val plus = "+"
  val num = rep("[0-9]".r)
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

//"1+2*3-4/5"
object FormulaP extends JavaTokenParsers {
  def addSub:Parser[Double] = mulDiv ~ rep(("+" | "-") ~ mulDiv) ^^ {  //First handle Multiplication or Division
    case d ~ lst =>
      println("addSub")
      println("d+-: " + d)
      println("lst+-: " + lst)
      val res = lst.foldLeft(d)((n,t) =>
        if(t._1=="+")
          n+t._2
        else
          n-t._2)
      println("res+- = " + res)
      res
  }
  def mulDiv:Parser[Double] = number ~ rep(("*" | "/") ~ number) ^^ {
    case d ~ lst =>
      println("mulDiv")
      println("d*/: " + d)
      println("lst*/: " + lst)
      val res = lst.foldLeft(d){(n,t) =>
        println("t*/ :" + t)
        if(t._1=="*")
          n*t._2
        else
          n/t._2}
      println("res*/ = " + res)
      res
  }
  def number:Parser[Double] = floatingPointNumber ^^ (_.toDouble) | "("~> addSub <~")"
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

    println()
    println()
    println()

    println(FormulaP.parseAll(FormulaP.addSub,"1+2*3-4/5").get)


    //    1
    //    mulDiv
    //    d*/: 1.0
    //    lst*/: List()
    //    res*/ = 1.0
    //
    //    2*3
    //    mulDiv
    //    d*/: 2.0
    //    lst*/: List((*~3.0))
    //    t*/ :(*~3.0)
    //    res*/ = 6.0
    //
    //    4/5
    //    mulDiv
    //    d*/: 4.0
    //    lst*/: List((/~5.0))
    //    t*/ :(/~5.0)
    //    res*/ = 0.8
    //
    //    1 + (2*3) - (4/5)
    //    addSub
    //    d+-: 1.0
    //    lst+-: List((+~6.0), (-~0.8))
    //    res+- = 6.2
    //    6.2

  }
}

