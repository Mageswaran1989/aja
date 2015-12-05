package org.aja.tantra.examples.classes

/**
 * Created by mageswaran on 10/11/15.
 */

//When overriding a concrete member, Scala requires the override key-
//word. It is optional when a subtype defines (“overrides”) an abstract
//member. Conversely, don’t use override unless you are actually over-
//riding a member.

abstract class AbstractClass {
  type type1
  val field: Int
  def abstractMethod: Unit
  def concreteMethod = println("AbstractClass: concreteMethod")
//  def concreteClassMethod = println("ConcreteClass: concreteClassMethod in AbstractClass " +
//                                     "throws error when added without " +
//                                      "knowledge of ConcreteClass by the developer")
  final def finalMethod: Unit = println("AbstractClass: finalMethod")
  val concreteVal = 1

  val VariableSpecificToBaseClass = 5
}

class ConcreteClass extends AbstractClass {
  type type1 = this.type
  val field: Int = 1
  def abstractMethod: Unit = println("ConcreteClass: abstractMethod")
  override def concreteMethod = println("AbstractClass: concreteMethod -> overrided in ConcreteClass")
  def concreteClassMethod = {
    println("ConcreteClass: concreteClassMethod")
  }
  //override def finalMethod: Unit = println("AbstractClass: finalMethod in ConcreteClass") //Error
  override val concreteVal = 5 //override -> to say special operation

  //val VariableSpecificToBaseClass = 6 //Error: needs override
}

final class FinalClass
//class ExtendsFinalClass extends FinalClass //Error: Illegal inheritance from final class

/////////////////////////////////////////////////////////////////////////////////////////////////////

//Overriding the immutable
trait T1 {
  val name = "T1"
}

class Base

class ClassWithT1 extends Base with T1 {
  override val name = "ClassWithT1"
}

class ClassExtendsT1 extends T1 {
  override val name = "ClassExtendsT1"
}


/////////////////////////////////////////////////////////////////////////////////////////////////////

trait T2 {
//  Avoid var fields when possible (in classes as well as traits). Consider
//  public var fields especially risky.
  var value = 1
}

class TestTraitVar

/////////////////////////////////////////////////////////////////////////////////////////////////////

object Override extends App {
  new ConcreteClass().abstractMethod
  new ConcreteClass().concreteMethod
  println("ConcreteClass().concreteVal: " + new ConcreteClass().concreteVal)

  val c = new ClassWithT1()
  println(c.name)
  val c2 = new ClassExtendsT1()
  println(c2.name)

  val testTraitVar = new TestTraitVar with T2
  println("Value of T2.value = " + testTraitVar.value)

//  Since the body of the trait is executed before the body of the class using it,
//  reassigning the field value happens after the initial assignment in the trait’s body.
//  That reassignment could happen too late if the field is used in the trait’s body in some calculation
//  that will become invalid by a reassignment later!

  val testTraitVar1 = new TestTraitVar with T2 { value = 5 }
  println("Value of T2.value = " + testTraitVar1.value)

}
