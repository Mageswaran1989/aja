//package org.aja.tantra.examples.util.lambdacalculus.regex
//
///**
// * Created by mageswaran on 28/5/16.
// */
//
//
//trait Parser[+A] extends (Stream[Character]=>Result[A])
//
//sealed trait Result[+A]
//
//case class Success[+A](value: A, rem: Stream[Character]) extends Result[A]
//
//case class Failure(msg: String) extends Result[Nothing]
//
//object RegexpParsers {
//  implicit def keyword(str: String) = new Parser[String] {
//    def apply(s: Stream[Character]) = {
//      val trunc = s take str.length
//      lazy val errorMessage = "Expected '%s' got '%s'".format(str, trunc.mkString)
//
//      if (trunc lengthCompare str.length != 0)
//        Failure(errorMessage)
//      else {
//        val succ = trunc.zipWithIndex forall {
//          case (c, i) => c == str(i)
//        }
//
//        if (succ) Success(str, s drop str.length)
//        else Failure(errorMessage)
//      }
//    }
//  }
//}
//
//class RegexParsers extends RegexpParsers {
//
//  val ID = """[a-zA-Z]([a-zA-Z0-9]|_[a-zA-Z0-9])*"""r
//
//  val NUM = """[1-9][0-9]*"""r
//
//  def program = clazz*
//
//  def classPrefix = "class" ~ ID ~ "(" ~ formals ~ ")"
//
//  def classExt = "extends" ~ ID ~ "(" ~ actuals ~ ")"
//
//  def clazz = classPrefix ~ opt(classExt) ~ "{" ~ (member*) ~ "}"
//
//  def formals = repsep(ID ~ ":" ~ ID, ",")
//
//  def actuals = expr*
//
//  def member = (
//    "val" ~ ID ~ ":" ~ ID ~ "=" ~ expr
//      | "var" ~ ID ~ ":" ~ ID ~ "=" ~ expr
//      | "def" ~ ID ~ "(" ~ formals ~ ")" ~ ":" ~ ID ~ "=" ~ expr
//      | "def" ~ ID ~ ":" ~ ID ~ "=" ~ expr
//      | "type" ~ ID ~ "=" ~ ID
//    )
//
//  def expr: Parser[Expr] = factor ~ (
//    "+" ~ factor
//      | "-" ~ factor
//    )*
//
//  def factor = term ~ ("." ~ ID ~ "(" ~ actuals ~ ")")*
//
//  def term = (
//    "(" ~ expr ~ ")"
//      | ID
//      | NUM
//    )
//}