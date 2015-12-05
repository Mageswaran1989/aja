import scala.reflect.Manifest
object WhichList {
  def apply[B](value: List[B])(implicit m: Manifest[B]) = m.toString match {
    case "Int" => println( "List[Int]" )
    case "Double" => println( "List[Double]" )
    case "java.lang.String" => println( "List[String]" )
    case x => println( "List[???]" )
  }
}
WhichList(List(1, 2, 3))
WhichList(List(1.1, 2.2, 3.3))
WhichList(List("one", "two", "three"))

// does not work when a previously  constructed list is passed to WhichList.apply.
List(List(1, 2, 3), List(1.1, 2.2, 3.3), List("one", "two", "three")) foreach {
  WhichList(_)
}