//Link : https://code.google.com/p/scala-discovery/wiki/Linearization

class C1 {
  def m = List("C1")
}
trait T1 extends C1 {
  override def m = {"T1" :: super.m}
}
trait T2 extends C1 {
  override def m = {"T2" :: super.m}
}
trait T3 extends C1 {
  override def m = {"T3" :: super.m}
}
class C2 extends T1 with T2 with T3 {
  override def m = { "C2" :: super.m}
}
new C2().m
//C2, T3, T2, T1, C1
//Steps:
//1. C2
//1. C2 T1 T2 T3
//2. C2 T3 T2 T1
//3. C2 T3 C1 T2 C1 T1 C1
//4. C2 T3 T2 T1 C1
//6. C2 T3 T2 T1 C1 java.lang.Object scala.Any

//1. Start with the class declaration and and drop the other keywords
//2. Reverse the order of the list, except keep the first item  at the beginning,
//3. Replace each item in the list except the first  with its linearization
//4. Remove the classes on the left that appears twice on the right
//??5. Insert a right-associative list-concatenation operator between each element in the list
//6. Append the standard Scala classes ScalaObject, AnyRef, Any


//To print class Linearization
import scala.reflect.runtime.universe._
//val tpe = typeOf[LinearRegressionModel]
val tpe = typeOf[C2]
tpe.baseClasses foreach { s => println(s.fullName) }

class C3A extends T2 {
  override def m = "C3A" :: super.m
}

class C3 extends C3A with T1 with T2 with T3 {
  override def m = "C3" :: super.m
}

new C3().m
//List(C3, T3, T1, C3A, T2, C1)


//No need of override keyword when implementing abstract methods
