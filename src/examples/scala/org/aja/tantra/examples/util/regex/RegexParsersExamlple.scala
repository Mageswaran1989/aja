package org.aja.tantra.examples.util.regex


/**
  * Created by mageswaran on 28/5/16.
  */
/*
a ~ b parse a sequence made of a then b
a | b introduce an alternative parser that parses a or b
a? introduce an optional parser
a* introduce on optional and repeatable parser
a+ introduce a repeatable parser
a ~> b like ~ but ignore the left member (a)
a <~ b like ~ but ignore the right member (b)
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

case class Select(val fields: String*)
case class From(val table: String)
case class Count (val field: String)
abstract class Direction
case class Asc(field: String*) extends Direction
case class Desc(field: String*) extends Direction
case class Where(filed: String*)
case class NumberEquals(f: String, num: Int)
case class LessThan(f: String, num: Int)
case class GreaterThan(f: String, num: Int)

object SQLParser extends JavaTokenParsers {
  def selectAll: Parser[Select] = "select" ~ "*" ^^^ (Select("*"))
  def from: Parser[From] = "from" ~> ident ^^ (From(_))
  def select: Parser[Select] = "select" ~ repsep(ident, ",") ^^ { case "select" ~ columns => Select(columns: _*)}
  def count: Parser[Count] = "select" ~ "count" ~> "(" ~> ident <~ ")" ^^ {case field => Count(field)}
  def countExpr = count ~ from
  def expr = selectAll ~ from | select ~ from

  def order: Parser[Direction] = {
    ("order" ~> "by" ~> ident ~ ("asc" | "desc") ^^ {
      case f ~ "asc" => Asc(f)
      case f ~ "desc" => Desc(f)
    }) | ("order" ~> "by" ~> repsep(ident, ",") ~ ("asc" | "desc") ^^ {
      case f ~ "asc" => Asc(f: _*)
      case f ~ "desc" => Desc(f: _*)
    })
  }

  def orderExpr = selectAll ~ from ~ order

  //Lets do this some other day!
  def where: Parser[Where] = "where" ~> rep(predicate) ^^ (Where(_: _*))

  def predicate = (
    ident ~ "=" ~ wholeNumber ^^ { case f ~ "=" ~ i => NumberEquals(f, i.toInt) }
      | ident ~ "<" ~ wholeNumber ^^ { case f ~ "<" ~ i => LessThan(f, i.toInt) }
      | ident ~ ">" ~ wholeNumber ^^ { case f ~ ">" ~ i => GreaterThan(f, i.toInt) })

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

    val sqlParsed = SQLParser.parseAll(SQLParser.expr, "select * from users").getOrElse("Error parsing SQL statement!")
    println(sqlParsed)
    println(sqlParsed.getClass)
    println()
    val sqlParsed1 = SQLParser.parseAll(SQLParser.expr, "select * from users").get match {case lr => println(lr._1); println(lr._2)}
    println()
    val sqlParsed2 = SQLParser.parseAll(SQLParser.expr, "select name,id from users").get match {case lr => println(lr._1); println(lr._2)}
    println()
    val sqlParsed3 = SQLParser.parseAll(SQLParser.countExpr, "select count(name) from users").get match {case lr => println(lr._1); println(lr._2)}
    println()
    val sqlParsed4 = SQLParser.parseAll(SQLParser.orderExpr, "select * from users order by name,age desc").get match {case lmr => println(lmr._1); println(lmr._2);}
    println()
  }
}

