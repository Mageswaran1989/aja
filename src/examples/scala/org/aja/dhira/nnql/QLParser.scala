package org.aja.dhira.nnql

import scala.util.parsing.combinator._

/**
 * Created by mageswaran on 29/4/16.
 */

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.PackratParsers

/**
 *
 */
class QLParser extends StdTokenParsers with PackratParsers {

  val commands =
    """
      | CREATE n NEURONS
      | CREATE n NEURONS WITH INTERCONNECTION
      | CREATE NEURAL LAYER WITH n NEURONS
      | SET layer_name PROPERTY=VALUE
      | CONNECT layer_name1 layer_name2
      | PRINT layer_name1
    """.stripMargin

  type Tokens = StdLexical
  val lexical = new LambdaLexer
  lexical.delimiters ++= Seq("=", ";")

  //NNQL PArser Grammer
  type P[+T] = PackratParser[T]

  lazy val expr = create
//  lazy val lambda: P[Lambda]     = positioned(("λ" | "\\") ~> variable ~ "." ~ expr ^^ { case v ~ "." ~ e  => Lambda(v, e) })
  lazy val create: P[CREATE] = positioned(("create" | "CREATE") ~> variable ~ expr ^^ {case variable ~ e => CREATE(variable,e)})
  lazy val variable: P[Variable]      = positioned(ident ^^ Variable.apply)
  lazy val parens: P[Expression]       = "(" ~> expr <~ ")"
  lazy val number: P[CREATE]     = numericLit ^^ { case n => Number(n.toInt) }

  def parse(str: String): ParseResult[Expression] = {
    val tokens = new lexical.Scanner(str)
    phrase(expr)(tokens)
  }

  lazy val defs = repsep(defn, ";") <~ opt(";") ^^ Map().++
  lazy val defn = ident ~ "=" ~ expr ^^ { case id ~ "=" ~ t => id -> t }

  def definitions(str: String): ParseResult[Map[String, Expression]] = {
    val tokens = new lexical.Scanner(str)
    phrase(defs)(tokens)
  }

}

class LambdaLexer extends StdLexical {
//  override def letter = elem("letter", c => c.isLetter && c != 'λ')
}



