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