package org.aja.dhira.nnql

/**
 * Created by mageswaran on 29/5/16.
 */
class PrettyPrinter {
  def apply(expr: Expression): String = expr match {
    case CREATE(arg, body) => p"CREATE $arg $body"
    case Number(i)        => i.toString
    case FunctionApply(fun, arg)   => p"$fun $arg"
    case Variable(name, scope)  => s"$name"
  }

  implicit class PrettyPrinting(val sc: StringContext) {
    def p(args: Expression*) = sc.s((args map parensIfNeeded): _*)
  }

  def parensIfNeeded(expr: Expression) = expr match {
    case v: Variable => apply(v)
    case _      => "(" + apply(expr) + ")"
  }
}

