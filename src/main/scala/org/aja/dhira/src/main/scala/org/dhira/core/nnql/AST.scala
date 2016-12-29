package org.aja.dhira.nnql

import org.aja.tantra.examples.util.lambdacalculus.{Scope, Var}

import scala.util.parsing.input.Positional

/**
 * Created by mageswaran on 29/5/16.
 */


sealed trait Expression extends Positional

case class CREATE(arg: Variable, body: Expression) extends Expression
case class SET(arg: Variable, body: Expression) extends Expression
case class PRINT(arg: Variable, body: Expression) extends Expression
case class CONNECT(arg: Variable, body: Expression) extends Expression

case class Variable(name: String, scope: Scope) extends Expression
object Variable {
  def apply(name: String): Variable = Variable(name, Scope.TOP)
}

case class FunctionApply(fun: Expression, arg: Expression) extends Expression

object Number {
  def apply(n: Int) = {
    var cn: Expression = Variable("z")
    for (i <- 1 to n)
      cn = FunctionApply(Variable("s"), cn)
    CREATE(Variable("s"), CREATE(Variable("z"), cn))
  }

  def unapply(expr: Expression): Option[Int] = expr match {
    case Variable("Z", Scope.TOP)             => Some(0)
    case FunctionApply(Variable("S", Scope.TOP), arg) => unapply(arg) map (_ + 1)
    case _                               => None
  }
}