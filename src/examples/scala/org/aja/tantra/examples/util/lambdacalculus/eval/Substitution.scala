package org.aja.tantra.examples.util.lambdacalculus
package org.aja.tantra.examples.util.lambdacalculus.eval

class Substitution(argV: Var, replacement: Expr) {

  val binder = new Binder(Map())

  def apply(term: Expr): Expr = term match {
    case Var(argV.name, argV.scope) => binder.bind(replacement, argV.scope.parent.get)
    case Var(_, _)                  => term
    case Apply(fun, arg)            => Apply(apply(fun), apply(arg))
    case Lambda(arg, body)          => Lambda(arg, apply(body))
  }

}
