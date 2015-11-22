// A fxed-size sequential collection of elements of the same type
// Mutable

val a = new Array[String](3)
//a = new Array[String](4) //cant change the reference to the Array

a(0) = "Mageswaran"
a(1) = "Aja"
a(2) = "Scala"

a.foreach(println)

val a1 = Array("Mageswaran", "Aja", "Scala")
a1.foreach(println)