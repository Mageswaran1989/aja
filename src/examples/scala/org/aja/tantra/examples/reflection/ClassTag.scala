package org.aja.tantra.examples.reflection
import scala.reflect._
import scala.reflect.runtime.universe._

/**
 * Created by mageswaran on 25/3/16.
 */

//A TypeTag is completely compiler-generated, that means that the compiler creates and fills in a TypeTag when one calls a method expecting such a TypeTag. There exist three different forms of tags:
//
//scala.reflect.ClassTag
//scala.reflect.api.TypeTags#TypeTag
//scala.reflect.api.TypeTags#WeakTypeTag


object ClassTagExamples {

  val tt = typeTag[Int]

  val ct = classTag[String]

  def paramInfo[T](x: T)(implicit tag: TypeTag[T]): Unit = {
    val targs = tag.tpe match { case TypeRef(_, _, args) => args }
    println(s"type of $x has type arguments $targs")
  }

  def add = (x: Int,y: Int) => x + y

  paramInfo(add)
  paramInfo(42)
  paramInfo(List(1, 2))

  //  The above example rewritten to use context bounds is as follows:
  def paramInfo1[T: TypeTag](x: T): Unit = {
    val targs = typeOf[T] match { case TypeRef(_, _, args) => args }
    println(s"type of $x has type arguments $targs")
  }

  paramInfo(add)
  paramInfo(42)
  paramInfo(List(1, 2))


  def weakParamInfo[T](x: T)(implicit tag: WeakTypeTag[T]): Unit = {
    val targs = tag.tpe match { case TypeRef(_, _, args) => args }
    println(s"type of $x has type arguments $targs")
  }

  def foo[T] = weakParamInfo(List[T]())
  foo[Int]

  //http://stackoverflow.com/questions/12218641/scala-what-is-a-typetag-and-how-do-i-use-it

  def meth[A](xs: List[A]) = xs match {
    case _: List[String] => "list of strings"
    case _: List[Foo] => "list of foos"
  }

  class Foo{class Bar}

  val f1 = new Foo
  val b1 = new f1.Bar
  val f2 = new Foo
  val b2 = new f2.Bar

  def m(f: Foo)(b: f.Bar)(implicit ev: TypeTag[f.Bar]) = ev


  val ev1 = m(f1)(b1)
  val ev2 = m(f2)(b2)

  ev1 == ev2
  ev1.tpe =:= ev2.tpe

  def meth1[T : TypeTag](xs: List[T]) = typeOf[T] match {
    case t if t =:= typeOf[String] => "list of strings"
    case t if t <:< typeOf[Foo] => "list of foos"
  }

  meth1(List("string"))
  meth1(List(new Foo))

  //  At this point, it is extremely important to understand to use =:= (type equality) and <:< (subtype relation)
  //  for equality checks. Do never use == or !=, unless you absolutely know what you do

  typeOf[List[java.lang.String]] =:= typeOf[List[Predef.String]] //true
  typeOf[List[java.lang.String]] == typeOf[List[Predef.String]] //false

  //Generics
  def createArr[A : ClassTag](seq: A*) = Array[A](seq: _*)
  createArr(1,2,3)
  createArr("a","b","c")

  classTag[Int]
  classTag[Int].runtimeClass
  classTag[Int].newArray(3)
  classTag[List[Int]]

  typeTag[List[Int]]
  val res1 = typeTag[List[Int]].tpe
  val res2 = typeOf[List[Int]]
  res1 =:= res2

  val l = List(1,2,3)
  def getTypeTag[T: TypeTag](obj: T) = typeTag[T]
  val theType = getTypeTag(l).tpe

  val decls = theType.declarations.take(10)
}
