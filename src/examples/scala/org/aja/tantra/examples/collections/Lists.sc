//A sequential collection of elements of the same type
//Immutable
//Lists represent a linked list

val l = 1::2::3::Nil

val l1 = List(1,2,3)

val l2 = l ::: l1

l2 :+ 5