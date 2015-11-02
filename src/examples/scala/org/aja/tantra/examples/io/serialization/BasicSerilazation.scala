package org.aja.tantra.examples.io.serialization

import java.io._


/**
 * Created by mageswaran on 31/10/15.
 * Link: http://erikerlandson.github.io/blog/2015/03/31/hygienic-closures-for-scala-function-serialization/
 */

class Person(name: String, age: Int) extends Serializable{
  override def toString = s"Person($name,$age)"
}

object BasicSerilazation extends App {

  val aja = new Person("Mageswaran", 27)
  val oos = new ObjectOutputStream(new FileOutputStream("/tmp/testSerialization"))
  oos.writeObject(aja)
  oos.close()

  val ois = new ObjectInputStream(new FileInputStream("/tmp/testSerialization"))
  val ajaReadObj = ois.readObject().asInstanceOf[Person]
  ois.close()

  println(ajaReadObj)

  /////////////////////////////////////////////////////////////////////////////////

  def write[A](obj: A, fname: String) = {
    import java.io._
    try {
      new ObjectOutputStream(new FileOutputStream(fname)).writeObject(obj)
    } catch {
      case e: NotSerializableException => println(e)
    }
  }

  /////////////////////////////////////////////////////////////////////////////////

  object tesObj {
    val v = 7

    def fun() = (x: Int) => x + v //closure with v
  }

  write(tesObj.fun(), "/tmp/aja")
  println("Size of /tmp/aja is " + new File("/tmp/aja").length() + " bytes")

  /////////////////////////////////////////////////////////////////////////////////

  class tesObjVer1 {
    val v = 1
    def fun = (x: Int) => x + v //closure with v
  }

  write(new tesObjVer1().fun, "/tmp/aja1")
  //println("Size of /tmp/aja1 is " + new File("/tmp/aja1").length() + " bytes") //ignore as this is of no use
  val f = new tesObjVer1
  println("Valus of new tesObjVer1.fun(4) :" + f.fun(19))

  /////////////////////////////////////////////////////////////////////////////////

  class tesObjVer2 extends Serializable {
    val v = 1
    def fun() = (x: Int) => x + v //closure with v
  }

  write(new tesObjVer2().fun(), "/tmp/aja2")
  println("Size of /tmp/aja2 is " + new File("/tmp/aja2").length() + " bytes")
  val obj = new tesObjVer2
  val fun = obj.fun
  println("Valus of new tesObjVer1.fun(4) :" + fun(9) )

  /////////////////////////////////////////////////////////////////////////////////

  class tesObjVer3 extends Serializable {
    val v = 7
    val bigList = (0 to 100000).toList
    def fun() = (x: Int) => x + v //closure with v
  }

  write(new tesObjVer3().fun(), "/tmp/aja3")
  println("Size of /tmp/aja3 is " + new File("/tmp/aja3").length() / 1024 / 1024 + " MBytes")

  /////////////////////////////////////////////////////////////////////////////////

  class tesObjVer4 {
    val v = 7
    def fun() = (x: Int) => {
      val vLocalCopy = v //Local variable didn't help
      (x + vLocalCopy)
    }
  }

  write(new tesObjVer4().fun(), "/tmp/aja4")
  println("Size of /tmp/aja4 is " + new File("/tmp/aja4").length() + " bytes")


  /////////////////////////////////////////////////////////////////////////////////

  //A shimp function
  // use shim function to enclose *only* the values of 'v'
  def closureFunction[E,D,R](enclosed: E)(gen: E => (D => R)) = gen(enclosed)
  //closureFunction: [E, D, R](enclosed: E)(gen: E => (D => R))D => R

  class tesObjVer5 {
    val v = 7
    def fun() = closureFunction(v){ v =>
      val vLocalCopy = v
      (x: Int) => vLocalCopy + x
    }
  }

  write(new tesObjVer5().fun(), "/tmp/aja5")
  println("Size of /tmp/aja5 is " + new File("/tmp/aja5").length() + " bytes")

  val obj5 = new tesObjVer5
  val fun5 = obj.fun
  println("Valus of new tesObjVer5.fun(4) :" + fun(9) )

}
