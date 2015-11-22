import java.util.Random

//Title: Pattern matching

def printNumbers(number: Int) = number match {
  case 1 => println("Number one!")
  case 2 => println("Number two!")
  case 3 => println("Number three!")
}

printNumbers(1)
case class Person1(name: String, age: Int)
val p = Person1("Aja", 1)
val p1 = Person1("Mageswaran", 27)
def validate(p: Person1) = p match {
  case Person1("Aja", 1) => println("Allowed here!")
  case _ => println("Sorry!")
}

validate(p)
validate(p1)
println("///////////////////////////////////////////////////////////////")

val randNumber = new Random().nextInt(10)
randNumber match {
case 7 => println("Wow we are lucky!")
case otherNumber => otherNumber}

println("///////////////////////////////////////////////////////////////")

val willWork = List(1, 3, 23, 90)
val willNotWork = List(4, 18, 52)
val empty = List()
for (l <- List(willWork, willNotWork, empty)) {
  l match {
    case List(_, 3, _, _) => println("Four elements, with the 2nd being '3'.")
    case List(_*) => println("Any other list with 0 or more elements.")
  }
}
println("///////////////////////////////////////////////////////////////")

val BookExtractorRE = """Book: title=([^,]+),\s+authors=(.+)""".r
val MagazineExtractorRE = """Magazine: title=([^,]+),\s+issue=(.+)""".r
val catalog = List(
  "Book: title=Programming Scala, authors=Dean Wampler, Alex Payne",
  "Magazine: title=The New Yorker, issue=January 2009",
  "Book: title=War and Peace, authors=Leo Tolstoy",
  "Magazine: title=The Atlantic, issue=February 2009",
  "BadData: text=Who put this here??"
)
for (item <- catalog) {
  item match {
    case BookExtractorRE(title, authors) =>
      println("Book \"" + title + "\", written by " + authors)
    case MagazineExtractorRE(title, issue) =>
      println("Magazine \"" + title + "\", issue " + issue)
    case entry => println("Unrecognized entry: " + entry)
  }
}
println("///////////////////////////////////////////////////////////////")
class Role
case object Manager extends Role
case object Developer extends Role
case class Person(name: String, age: Int, role: Role)
val alice = new Person("Alice", 25, Developer)
val bob = new Person("Bob", 32, Manager)
val charlie = new Person("Charlie", 32, Developer)

//@ to delcare variable in pattern
for (item <- Map(1 -> alice, 2 -> bob, 3 -> charlie)) {
  item match {
    case (id, p @ Person(_, _, Manager)) => println(p + "is overpaid.\n")
    case (id, p @ Person(_, _, _)) => println(p + " is underpaid.\n")
  }
}

//p @ => flatens below code
//item match {
//  case (id, p: Person) => p.role match {
//    case Manager => format("%s is overpaid.\n", p)
//    case _ => format("%s is underpaid.\n", p)
//  }
//}


