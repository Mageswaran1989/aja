package org.aja.tantra.examples.classes

/**
 * Created by mageswaran on 10/11/15.
 */

//////////////////////////////////////////////////////////////////////////////////////////

package scopeA {
class Class1 {
  private[scopeA] val scopeA_privateField = 1
  protected[scopeA] val scopeA_protectedField = 2

  private[Class1] val class1_privateField = 3
  protected[Class1] val class1_protectedField = 4

  private[this] val this_privateField = 5
  protected[this] val this_protectedField = 6
}
class Class2 extends Class1 {
  val field1  = scopeA_privateField
  val field2 = scopeA_protectedField
  //val  field3  = class1_privateField //Error
  val field4 = class1_protectedField
  //val  field5   = this_privateField //Error
  val field = this_protectedField
}
}


package scopeB {
class Class2B extends scopeA.Class1 {
  //val field1 = scopeA_privateField // ERROR
  val field2 = scopeA_protectedField
  //val field3 = class1_privateField  // ERROR
  val field4 = class1_protectedField
  //val field5 = this_privateField  // ERROR
  val field6 = this_protectedField
}
}

//////////////////////////////////////////////////////////////////////////////////////////

//The private[this] members are only visible to the same instance.
// private[this] is equivalent to Java’s package private visibility.

package scopeAThis {
class PrivateClass1(private[this] val privateField1: Int) {

  private[this] val privateField2 = 1

  //  def equalFields(other: PrivateClass1) =
  //    (privateField1 == other.privateField1) &&
  //      (privateField2 == other.privateField2) &&
  //      (nested == other.nested) //Error

  class Nested {
    private[this] val nestedField = 1
  }

  private[this] val nested = new Nested
}

class PrivateClass2 extends PrivateClass1(1) {
  //  val field1 = privateField1  // ERROR
  //  val field2 = privateField2  // ERROR
  //  val nField = new Nested().nestedField  // ERROR
}

class PrivateClass3 {
  val privateClass1 = new PrivateClass1(1)
  //  val privateField1 = privateClass1.privateField1  // ERROR
  //  val privateField2 = privateClass1.privateField2  // ERROR
  //  val privateNField = privateClass1.nested.nestedField // ERROR
}
}

//////////////////////////////////////////////////////////////////////////////////////////

package scopeA {
private[this] class PrivateClass11

package scopeA2 {
private[this] class PrivateClass22
}

//  class PrivateClass33 extends PrivateClass11  // ERROR
//  protected class PrivateClass44 extends PrivateClass11 // ERROR
private class PrivateClass55 extends PrivateClass11
private[this] class PrivateClass66 extends PrivateClass11

//  private[this] class PrivateClass77 extends scopeA2.PrivateClass22 // ERROR
}

package scopeB {
//  class PrivateClass11B extends scopeA.PrivateClass11 // ERROR
}

//////////////////////////////////////////////////////////////////////////////////////////

package scopeAPrivate {
class PrivateClass1(private[PrivateClass1] val privateField1: Int) {
  private[PrivateClass1] val privateField2 = 1

  def equalFields(other: PrivateClass1) =
    (privateField1 == other.privateField1) &&
      (privateField2 == other.privateField2) &&
      (nested  == other.nested)

  class Nested {
    private[Nested] val nestedField = 1
  }

  private[PrivateClass1] val nested = new Nested
  //val nestedNested = nested.nestedField   // ERROR
}

class PrivateClass2 extends PrivateClass1(1) {
  //  val field1 = privateField1  // ERROR
  //  val field2 = privateField2  // ERROR
  //  val nField = new Nested().nestedField  // ERROR
}

class PrivateClass3 {
  val privateClass1 = new PrivateClass1(1)
  //  val privateField1 = privateClass1.privateField1  // ERROR
  //  val privateField2 = privateClass1.privateField2  // ERROR
  //  val privateNField = privateClass1.nested.nestedField // ERROR
}
}

//////////////////////////////////////////////////////////////////////////////////////////

package scopeA {
class PrivateClassType1 {
  class NestedType {
    private[PrivateClassType1] val nestedField = 1
  }

  private[PrivateClassType1] val nested = new NestedType
  val nestedNested = nested.nestedField
}

class PrivateClassType2 extends PrivateClassType1 {
  //val nField = new NestedType().nestedField   // ERROR
}

class PrivateClassType3 {
  val privateClass1 = new PrivateClassType1
  //val privateNField = privateClass1.nested.nestedField // ERROR
}
}

//////////////////////////////////////////////////////////////////////////////////////////

package scopeA_Private_pk_type {
  private[scopeA_Private_pk_type] class PrivateClass1

  package scopeA2 {
  private [scopeA2] class PrivateClass2
  private [scopeA_Private_pk_type]  class PrivateClass3
  }

  class PrivateClass4 extends PrivateClass1
  protected class PrivateClass5 extends PrivateClass1
  private class PrivateClass6 extends PrivateClass1
  private[this] class PrivateClass7 extends PrivateClass1

  //private[this] class PrivateClass8 extends scopeA2.PrivateClass2 // ERROR
  private[this] class PrivateClass9 extends scopeA2.PrivateClass3
}

package scopeB_Private_pk_type {
  //class PrivateClass1B extends scopeA_Private_pk_type.PrivateClass1 // ERROR
}

//////////////////////////////////////////////////////////////////////////////////////////

package ScopeAPrivatePkg {
class PrivateClass1 {
  private[ScopeAPrivatePkg] val privateField = 1

  class Nested {
    private[ScopeAPrivatePkg] val nestedField = 1
  }

  private[ScopeAPrivatePkg] val nested = new Nested
}

class PrivateClass2 extends PrivateClass1 {
  val field  = privateField
  val nField = new Nested().nestedField
}

class PrivateClass3 {
  val privateClass1 = new PrivateClass1
  val privateField  = privateClass1.privateField
  val privateNField = privateClass1.nested.nestedField
}

package ScopeAPrivatePkg2 {
class PrivateClass4 {
  private[ScopeAPrivatePkg2] val field1 = 1
  private[ScopeAPrivatePkg]  val field2 = 2
}
}

class PrivateClass5 {
  val privateClass4 = new ScopeAPrivatePkg2.PrivateClass4
  //val field1 = privateClass4.field1  // ERROR
  val field2 = privateClass4.field2
}
}

package ScopeBPrivatePkg {
class PrivateClass1B extends ScopeAPrivatePkg.PrivateClass1 {
  //val field1 = privateField   // ERROR
  val privateClass1 = new ScopeAPrivatePkg.PrivateClass1
  //val field2 = privateClass1.privateField  // ERROR
}
}

//////////////////////////////////////////////////////////////////////////////////////////

object ScopedVisibility {

}
