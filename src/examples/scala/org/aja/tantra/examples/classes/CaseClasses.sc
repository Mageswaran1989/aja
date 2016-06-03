/**
 * Created by mdhandapani on 10/8/15.
 */

//Sealed Class Hierarchies
//Basically to avoid default case in the match and cover all possible cases
//Avoid sealed case class hierarchies if the hierarchy changes frequently
//  (for an appropriate definition of “frequently”).

//sealed abstract class HttpMethod()
sealed trait HttpMethod //triat can't take constructor parameters
case class Connect(body: String) extends HttpMethod
case class Delete (body: String) extends HttpMethod
case class Get    (body: String) extends HttpMethod
case class Head   (body: String) extends HttpMethod
case class Options(body: String) extends HttpMethod
case class Post   (body: String) extends HttpMethod
case class Put    (body: String) extends HttpMethod
case class Trace  (body: String) extends HttpMethod

//No default case is necessary, since we cover all the possibilities.
def handle (method: HttpMethod) = method match {
  case Connect (body) => println("connect: " + body)
  case Delete  (body) => println("delete: "  + body)
  case Get     (body) => println("get: "     + body)
  case Head    (body) => println("head: "    + body)
  case Options (body) => println("options: " + body)
  case Post    (body) => println("post: "    + body)
  case Put     (body) => println("put: "     + body)
  case Trace   (body) => println("trace: "   + body)
  //If you comment above line. you will get ----->
  //    Warning:(125, 36) match may not be exhaustive.
  //It would fail on the following input: Trace(_)
  //def handle (method: HttpMethod) = method match {
}
val methods = List(
  Connect("connect body..."),
  Delete ("delete body..."),
  Get    ("get body..."),
  Head   ("head body..."),
  Options("options body..."),
  Post   ("post body..."),
  Put    ("put body..."),
  Trace  ("trace body..."))

methods.foreach { method => handle(method) }

//Cons: Caseclassses are not suited for inheritance
//Below code demonstrates the case with

//
//sealed abstract class HttpMethod() {
//  def body: String
//  def bodyLength = body.length
//}
//
//case class Connect(body: String) extends HttpMethod
//case class Delete (body: String) extends HttpMethod
//case class Get    (body: String) extends HttpMethod
//case class Head   (body: String) extends HttpMethod
//case class Options(body: String) extends HttpMethod
//case class Post   (body: String) extends HttpMethod
//case class Put    (body: String) extends HttpMethod
//case class Trace  (body: String) extends HttpMethod
//
//def handle (method: HttpMethod) = method match {
//  case Connect (body) => println("connect: " + body)
//  case Delete  (body) => println("delete: "  + body)
//  case Get     (body) => println("get: "     + body)
//  case Head    (body) => println("head: "    + body)
//  case Options (body) => println("options: " + body)
//  case Post    (body) => println("post: "    + body)
//  case Put     (body) => println("put: "     + body)
//  case Trace   (body) => println("trace: "   + body)
//}
//
//val methods = List(
//  Connect("connect body..."),
//  Delete ("delete body..."),
//  Get    ("get body..."),
//  Head   ("head body..."),
//  Options("options body..."),
//  Post   ("post body..."),
//  Put    ("put body..."),
//  Trace  ("trace body..."))
//
//methods.foreach { method =>
//  handle(method)
//  println("body length? " + method.bodyLength)
//}


////////////////////////////////////////////////////////////////////////
//
 /*In Java, a tree would be represented using an abstract super-class for the trees,
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


// (x + x) + (7 + y)
val exp: Tree = Sum(Sum(Var("x"),Var("x")),Sum(Const(7), Var("y")))
val env: Environment = { case "x" => 5 case "y" => 7 }
println("Expression: " + exp)
println("Evaluation with x=5, y=7: " + eval(exp, env))
println("Derivative relative to x:\n " + derive(exp, "x"))
println("Derivative relative to y:\n " + derive(exp, "y"))
///////////////////////////////////////////////////////////////////
//Todo: http://www.alessandrolacava.com/blog/scala-case-classes-in-depth/
case class Point(x: Double, y: Double)
//No "case" keyword!
abstract class Shape() {
  def draw(): Unit
}
case class Circle(center: Point, radius: Double) extends Shape() {
  def draw() = println("Circle.draw: " + this)
}
case class Rectangle(lowerLeft: Point, height: Double, width: Double)
  extends Shape() {
  def draw() = println("Rectangle.draw: " + this)
}
case class Triangle(point1: Point, point2: Point, point3: Point)
  extends Shape() {
  def draw() = println("Triangle.draw: " + this)
}

val shapesList = List(
  Circle(Point(0.0, 0.0), 1.0),
  Circle(Point(5.0, 2.0), 3.0),
  Rectangle(Point(0.0, 0.0), 2, 5),
  Rectangle(Point(-2.0, -1.0), 4, 3),
  Triangle(Point(0.0, 0.0), Point(1.0, 0.0), Point(0.0, 1.0)))
val shape1 = shapesList.head // grab the first one.
println("shape1: "+shape1+". hash = "+shape1.hashCode)
for (shape2 <- shapesList) {
  println("shape2: "+shape2+". 1 == 2 ? "+(shape1 == shape2))
}

def matchOn(shape: Shape) = shape match {
  case Circle(center, radius) =>
    println("Circle: center = "+center+", radius = "+radius)
  case Rectangle(ll, h, w) =>
    println("Rectangle: lower-left = "+ll+", height = "+h+", width = "+w)
  case Triangle(p1, p2, p3) =>
    println("Triangle: point1 = "+p1+", point2 = "+p2+", point3 = "+p3)
  case _ =>
    println("Unknown shape!"+shape)
}

shapesList.foreach { shape => matchOn(shape) }


//Copy method

val circle1 = Circle(Point(0.0, 0.0), 2.0)

val circle2 = circle1 copy (radius = 4.0)

println(circle1)
println(circle2)
///////////////////////////////////////////////////////////////////
//Inheritance in case classes -> Not possible
case class Point1(x: Double, y: Double)
abstract case class Shape1(id: String) {
  def draw(): Unit
}

//Error:(104, 13) case class Circle1 has case ancestor org.aja.tantra.examples.classes.A$A58.A$A58.Shape1, but case-to-case inheritance is prohibited. To overcome this limitation, use extractors to pattern match on non-leaf nodes.
//case class Circle1(override val id: String, center: Point1, radius: Double)
//^
//case class Circle1(override val id: String, center: Point1, radius: Double)
//  extends Shape1(id) {
//  def draw() = println("Circle.draw: " + this)
//}
////////////////////////////////////////////////////////////////////
