//A sequential collection of elements of the same type
//Immutable
//Lists represent a linked list

//Mutable List : ListBuffer, Linked List
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

//////////////////////////////////////////////////////////////////////////////

val list = List(1, 2.2, "three", 'four) //=> with help of apply
list match {
  case List(x, y, _*) => println("x = "+ x +", y = " + y) //=> with help of unapplySeq
  case _ => throw new Exception("No match! " + list)
}

///////////////////////////////////////////////////////////////////////////////

//Huray! here goes Map-Reduce
val list1 = List(0,1,2,3,4,5)
list1.map(_ * 2).reduce(_ + _)

///////////////////////////////////////////////////////////////////////

val list2 = List("Programming", "Scala")
val list3 = "People" :: "should" :: "read" :: list2

// :: operator binds to the right
val list4 = ("People" :: ("should" :: ("read" :: list2)))
val list5 = list2.::("read").::("should").::("People")
//In terms of performance, prepending is O(1).
//Consider last item as first and start prepending the items in to the lost

//////////////////////////////////////////////////////////////////////////

val list6 = List(1,2,3,4,5,6)

//acc is supplied 10 intitally and keeps accumulating the value
//currentElement is current element in the list
val prod = list6.foldLeft(10)((suppliedAccmulator, currElement) => (suppliedAccmulator * currElement))

//((((((10 * 1) * 2) * 3) * 4) * 5) * 6)
//((((((10) * 2) * 3) * 4) * 5) * 6)
//(((((20) * 3) * 4) * 5) * 6)
//((((60) * 4) * 5) * 6)
//(((240) * 5) * 6)
//((1200) * 6)
//(7200)

//It turns out that foldLeft and reduceLeft have one very important advantage over their
//“right-handed” brethren: they are tail-call recursive, and as such they can benefit from
//tail-call optimization

val prod1 = list6.foldRight(10)((suppliedAccmulator, currElement) => (suppliedAccmulator * currElement))

//(1 * (2 * (3 * (4 * (5 * (6 * 10))))))
//(1 * (2 * (3 * (4 * (5 * (60))))))
//(1 * (2 * (3 * (4 * (300)))))
//(1 * (2 * (3 * (1200))))
//(1 * (2 * (3600)))
//(1 * (7200))
//(7200)

println((1 to 1000000) reduceLeft(_ + _))
println((1 to 1000000) reduceRight(_ + _))


val path = "/opt/aja/data/20_newsgroups/alt.atheism/49960"

val dir = path.split("/").takeRight(2).head


//------------------------------------------------------------------------------

//1. Find the last element of a list.
val l = List(1,2,3,4,5,6,7,8)
l.last

def lastItem[A](l : List[A]): A = l match {
  //last item
  case h :: Nil => h
  //element :: List items
  case _ :: tail => lastItem(tail)
  case _         => throw new NoSuchFieldError
}

val last = lastItem(l)

////////////////////////////////////////////

//2.Find the last but one element of a list
l.init
l.init.tail
l.init.last

def lastBfrItem[A](l: List[A]) : A = l match {
  case h :: _ :: Nil => h
  case _ :: tail => lastBfrItem(tail)
  case _        => throw new NoSuchElementException
}

val lastBfrElem = lastBfrItem(l)

l.takeRight(3).head


def lastNthRecursive[A](n: Int, l: List[A]) : A = {
  def lastNthR[A](count: Int, resultList: List[A], currList: List[A]): A = currList match {
    case Nil if count > 0 => throw new NoSuchElementException
    case Nil => resultList.head
    case _ :: tail => lastNthR(count - 1,
      if (count > 0)
        resultList
      else
        resultList.tail,
      tail)
  }

  if (n <=0)
    throw new IllegalArgumentException
  else
    lastNthR(n ,l, l)
}


val d = lastNthRecursive(2, l)

//3. Find the Kth element of a list.
val f1= l(2)

// Not that much harder without.
def nthRecursive[A](n: Int, ls: List[A]): A = (n, ls) match {
  case (0, h :: _   ) => h
  case (n, _ :: tail) => nthRecursive(n - 1, tail)
  case (_, Nil      ) => throw new NoSuchElementException
}

val f = nthRecursive(2, l)

//////////////////////////////////////////////////////////////////////////////
//4.Find the number of elements of a list.
l.length

def findLength[A](list: List[A]): Int = {
  def length[A](result: Int, l: List[A]) : Int = l match {
    case Nil => result
    case _ :: tail => length(result + 1, tail)
  }

  length(0, list)
}

findLength(l)

///////////////////////////////////////////////////////////////////////////////
//5.Reverse a list.
l.reverse

l.head

//O(n^2)
def reverseList[A](l: List[A]): List[A] = l match {
  case Nil => Nil
  case h :: tail => reverseList(tail) ::: List(h)
}

val g = reverseList(l)

//Tail recursive
def reverseListTR[A](list: List[A]) : List[A] = {

  def reverse1[A](result: List[A], currList: List[A]): List[A] = currList match {
    case Nil => result
    case h :: tail => reverse1(h::result, tail)
  }

  reverse1(Nil, list)
}

val j = reverseListTR(l)

//////////////////////////////////////////////////////////////////////
//6. Find out whether a list is a palindrome.
val palindromeList = List(1,2,3,2,1)

palindromeList.reverse

def isPalindrome[A](list: List[A]): Boolean = {
  list.equals(list.reverse)
  //or list === list.reverse
}

val p = isPalindrome(palindromeList)

//////////////////////////////////////////////////////////////////////
//7.Flatten List
val strings = List(List("abc", "def"), "ghi", List(1,2,3))

val packetList= List(List(1, 1), 2, List(3, List(5, 8)))

def flatten[A](list: List[Any]): List[Any] =  list flatMap {
  case item : List[_] => flatten(item)
  case e => List(e)
}


val y = flatten(strings)

/////////////////////////////////////////////////////////////////
//8.Eliminate consecutive duplicates of list elements.
val listWithMultipleOccurence = List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)
listWithMultipleOccurence.distinct


listWithMultipleOccurence.dropWhile(_ == 'a')