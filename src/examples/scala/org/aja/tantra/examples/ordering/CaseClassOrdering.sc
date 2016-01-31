

/**
 * Created by mageswaran on 30/1/16.
 */
//object CaseClassOrdering {

case class AWithoutOrdering(tag:String, load:Int)
val l = List(AWithoutOrdering("words",50),AWithoutOrdering("article",2),AWithoutOrdering("lines",7))

//l.sorted //Error


case class AWithOrdering(tag: String, load: Int) extends Ordered[AWithOrdering] {
  // Required as of Scala 2.11 for reasons unknown - the companion to Ordered
  // should already be in implicit scope
  import scala.math.Ordered.orderingToOrdered

  def compare(that: AWithOrdering): Int = (this.tag, this.load) compare (that.tag, that.load)
}

val l1 = List(AWithOrdering("words",50),AWithOrdering("article",2),AWithOrdering("lines",7))

//}
