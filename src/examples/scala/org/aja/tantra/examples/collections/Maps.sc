// A collection of key/value pairs
// Immutable and mutable

var m = Map[String, Float]("one" -> 1, "two" -> 2, "three" -> 3)

//m = Map[Int, String]() //Can't do this


m.keys.foreach(println)

m.values.foreach(println)

val m1 = m + ("four" -> 4)





