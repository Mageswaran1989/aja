//A sequential collection of elements of the same type
//Immutable
//Lists represent a linked list

val l = 1::2::3::Nil

val l1 = List(1,2,3)

val l2 = l ::: l1

l2 :+ 5

val l3 = List((1,2,3),(4,5,6)).toArray

l1.mkString("|")


def makeStrList(str: String*) = {
  if (str.length == 0)
    List(0)
  else
    str.toList
}

val l4 = makeStrList("a", "b", "cd")//of type List[Any]


//added return type
def makeStrList1(str: String*): List[String] = {
  if (str.length == 0)
    List("") //Fixed compiler error
  else
    str.toList
}

val l5 = makeStrList1("a", "b", "cd")//of type List[String]

val l6 = List("b", "c", "d")

//Any method whose name ends with a : binds to the right, not the left.
val l7 = "a" :: l6
val l8 = l6.::("a")


val head::tail = List(1,2,3,4,5)
head
tail


val tupA = ("Good", "Morning!")
val tupB = ("Guten", "Tag!")
for (tup <- List(tupA, tupB)) {
  tup match {
    case (thingOne, thingTwo) if thingOne == "Good" =>
      println("A two-tuple starting with 'Good'.")
    case (thingOne, thingTwo) =>
      println("This has two things: " + thingOne + " and " + thingTwo)
  }
}

case class Person(name: String, age: Int)
val alice = new Person("Alice", 25)
val bob = new Person("Bob", 32)
val charlie = new Person("Charlie", 32)
for (person <- List(alice, bob, charlie)) {
  person match {
    case Person("Alice", 25) => println("Hi Alice!")
    case Person("Bob", 32) => println("Hi Bob!")
    case Person(name, age) =>
      println("Who are you, " + age + " year-old person named " + name + "?")
  }
}




