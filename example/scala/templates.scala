class Reference[T] {
  private var contents: T = _
  def set(value: T) { contents = value }
  def get: T = contents
}

/*
The class Reference is parametrized by a type, called T, which is the type of its element.
This type is used in the body of the class as the type of the contents variable,
the argument of the set method, and the return type of the get method.
The above code sample introduces variables in Scala, which should not require further
explanations. It is however interesting to see that the initial value given to that
variable is _, which represents a default value. This default value is 0 for numeric
types, false for the Boolean type, () for the Unit type and null for all object types.
*/

object IntegerReference {
  def main(args: Array[String]) {
    val cell = new Reference[Int]
    cell.set(13)
    println("Reference contains the half of " + (cell.get * 2))
  }
}

