package org.aja.tantra.examples.util.lambdacalculus


/**
 * Created by mdhandapani on 27/5/16.
 *
 * Reference: http://zeroturnaround.com/rebellabs/what-is-lambda-calculus-and-why-should-you-care/
 *            https://zeroturnaround.com/rebellabs/parsing-lambda-calculus-in-scala/
 * Parser: Turns the source code into a model understandable to the rest of the interpreter
 * AST (Abstract Syntax Tree) : The model for the code, with syntax details removed
 * Semantics Actions : Construction of the model during parsing
 * Pretty Printing: Turning the model back into syntax
 */

/**
 *
λ-calculus Parser Notes:

variable: x
lambda abstraction (function): λx.x
(where . separates the function argument and body)
function application: x y

0 = λs.λz. z the function s is applied to the argument z zero times
1 = λs.λz. s z the function s is applied once
2 = λs.λz. s (s z) the function s is applied twice
(the names s and z are short for successor and zero)

 "x" (String literal) parse the keyword/delimiter "x"
a ~ b parse a, and then parse b (sequence)
a | b parse a, or parse b (or)
a ^^ f parse a, apply function f to result (semantic action)

 */

import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.PackratParsers

class LambdaParser extends StdTokenParsers with PackratParsers {
  type Tokens = StdLexical
  val lexical = new LambdaLexer
  lexical.delimiters ++= Seq("\\", "λ", ".", "(", ")", "=", ";")

  type P[+T] = PackratParser[T]
  lazy val expr: P[Expr]         = application | notApp
  lazy val application: P[Apply] = positioned(expr ~ notApp ^^ { case left ~ right => Apply(left, right) })
  lazy val notApp                = variable | number | parens | lambda
  lazy val lambda: P[Lambda]     = positioned(("λ" | "\\") ~> variable ~ "." ~ expr ^^ { case v ~ "." ~ e  => Lambda(v, e) })
  lazy val variable: P[Var]      = positioned(ident ^^ Var.apply)
  lazy val parens: P[Expr]       = "(" ~> expr <~ ")"
  lazy val number: P[Lambda]     = numericLit ^^ { case n => CNumber(n.toInt) }

  def parse(str: String): ParseResult[Expr] = {
    val tokens = new lexical.Scanner(str)
    phrase(expr)(tokens)
  }

  lazy val defs = repsep(defn, ";") <~ opt(";") ^^ Map().++
  lazy val defn = ident ~ "=" ~ expr ^^ { case id ~ "=" ~ t => id -> t }

  def definitions(str: String): ParseResult[Map[String, Expr]] = {
    val tokens = new lexical.Scanner(str)
    phrase(defs)(tokens)
  }

}

class LambdaLexer extends StdLexical {
  override def letter = elem("letter", c => c.isLetter && c != 'λ')
}
