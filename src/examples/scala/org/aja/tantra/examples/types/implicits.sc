val name: String = "scala"
name.capitalize.reverse

//Wait as per Predef object, String is from java.lang.String
//from where capitalze and reverse came from?
//implicit def stringWrapper(x: String) = new runtime.RichString(x)
//Compiler implicitly converts String -> RichString

//1. No conversion will be attempted if the object and method combination type check
//successfully.
//2. Only methods with the implicit keyword are considered.
//3. Only implicit methods in the current scope are considered, as well as implicit
//methods defined in the companion object of the target type.
//4. Implicit methods aren’t chained to get from the available type, through intermediate
//  types, to the target type. Only a method that takes a single available type
//instance and returns a target type instance will be considered.
//5. No conversion is attempted if more than one possible conversion method could
//be applied. There must be one and only one possibility.



//Pimp My Library

abstract class SemiGroup[A] {
  def add(x: A, y: A): A
}
abstract class Monoid[A] extends SemiGroup[A] {
  def unit: A
}

implicit object StringMonoid extends Monoid[String] {
  override def unit: String = ""
  override def add(x: String, y: String): String = x concat y
}

implicit object IntegerMonoid extends Monoid[Int]{
  override def unit: Int = 0
  override def add(x: Int, y: Int): Int = x + y
}

def sum[A](xs: List[A])(implicit m: Monoid[A]): A = {
  if (xs.isEmpty) m.unit
  else m.add(xs.head, sum(xs.tail))
}

sum(List(1,2,3,4,5))
sum(List("A","j", "a"))


//Implicit Function Parameters as opposed to currying

def multiplier(x: Int)(implicit factor: Int) = x * factor

implicit val factor = 5

multiplier(5) //compiler uses the implicit valuein the scope
multiplier(5)(10)

//Essentially, implicit function parameters behave as parameters with a default value,
//with the key difference being that the value comes from the surrounding scope. Had
//our factor value resided in a class or object, we would have had to import it into the
//  local scope. If the compiler can’t determine the value to use for an implicit parameter,
//an error of “no implicit argument matching parameter” will occur.


//Use implicits sparingly and cautiously. Also, consider adding an explicit
//return type to “non-trivial” conversion methods.


println("//////////////////////////////////////////////////////////////////")

//Refere PIM My Library pattern