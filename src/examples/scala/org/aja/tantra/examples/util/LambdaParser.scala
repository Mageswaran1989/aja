package org.aja.tantra.examples.util

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


  */
  



sealed trait Expr
case class Var(name: String) extends Expr
case class Lambda(arg: Var, body: Expr) extends Expr
case class Apply(fun: Expr, arg: Expr) extends Expr

class PrettyPrinter {
  def apply(expr: Expr): String = expr match {
    case Lambda(arg, body) => p"λ$arg.$body"
    case Apply(fun, arg)   => p"$fun $arg"
    case Var(name)         => s"$name"
  }

  implicit class PrettyPrinting(val sc: StringContext) {
    def p(args: Expr*) = sc.s((args map parensIfNeeded):_*)
  }

  def parensIfNeeded(expr: Expr) = expr match {
    case Var(name) => name
    case _         => "(" + apply(expr) + ")"
  }

}

object LambdaParser extends App {

  val id = Lambda(Var("x"), Var("x"))

  val one = Lambda(Var("s"), Lambda(Var("z"), Apply(Var("s"), Var("z"))))
  val pretty = new PrettyPrinter()
  println(pretty(one))

}
