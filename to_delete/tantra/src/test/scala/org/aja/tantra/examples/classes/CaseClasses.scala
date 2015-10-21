package org.aja.tantra.examples.classes

/**
 * Created by mdhandapani on 10/8/15.
 */
/*
In Java, a tree would be represented using an abstract super-class for the trees,
and one concrete sub-class per node or leaf. In a functional programming language,
one would use an algebraic data-type for the same purpose. Scala provides the concept
of case classes which is somewhat in between the two. Here is how they can be
used to define the type of the trees for our example:
*/

//Wondering what is this, even we do!

abstract class Tree
case class Sum(l: Tree, r: Tree) extends Tree
case class Var(n: String) extends Tree
case class Const(v: Int) extends Tree

/*
The fact that classes Sum, Var and Const are declared as case classes means that they
differ from standard classes in several respects:

- no need of "new" keyword, jus write val v = Var("Hello, world")
- getter functions are automatically defined for the constructor parameters, i.e v.n
- default definitions for methods equals and hashCode are provided, which work
  on the structure of the instances and not on their identity
- a default definition for method toString is provided, and prints the value in a
  “source form” (e.g. the tree for expression x+1 prints as Sum(Var(x),Const(1))),
- instances of these classes can be decomposed through pattern matching as
  we will see below
*/


object  CaseClasses {

  //should be something similar to typdef we guess!
  type Environment = String => Int

  def eval(t: Tree, env: Environment): Int = t match
  {
    case Sum(l, r) => eval(l, env) + eval(r, env)
    case Var(n)    => env(n)
    case Const(v)  => v
  }

  def derive(t: Tree, v: String): Tree = t match
  {
    case Sum(l, r) => Sum(derive(l,v), derive(r,v))
    case Var(n) if (v == n) => Const(1)
    case _ => Const(0)
  }

  def main(args: Array[String])
  {
    // (x + x) + (7 + y)
    val exp: Tree = Sum(Sum(Var("x"),Var("x")),Sum(Const(7), Var("y")))
    val env: Environment = { case "x" => 5 case "y" => 7 }
    println("Expression: " + exp)
    println("Evaluation with x=5, y=7: " + eval(exp, env))
    println("Derivative relative to x:\n " + derive(exp, "x"))
    println("Derivative relative to y:\n " + derive(exp, "y"))
  }

}
