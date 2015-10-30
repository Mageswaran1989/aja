//Title: Pattern matching

def printNumbers(number: Int) = number match {
  case 1 => println("Number one!")
  case 2 => println("Number two!")
  case 3 => println("Number three!")
}

printNumbers(1)

case class Person(name: String, age: Int)

val p = Person("Aja", 1)
val p1 = Person("Mageswaran", 27)

def validate(p: Person) = p match {
  case Person("Aja", 1) => println("Allowed here!")
  case _ => println("Sorry!")
}

validate(p)
validate(p1)