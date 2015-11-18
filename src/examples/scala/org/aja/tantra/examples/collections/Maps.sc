// A collection of key/value pairs
// Immutable and mutable

var m = Map[String, Float]("one" -> 1, "two" -> 2, "three" -> 3)

//m = Map[Int, String]() //Can't do this


m.keys.foreach(println)
m.values.foreach(println)
val m1 = m + ("four" -> 4)
val m2 = m + ("five" -> 5)
val m3 = m1 ++ m2


val lengths = m3 map {t=> (t._1.length(), t._2)}