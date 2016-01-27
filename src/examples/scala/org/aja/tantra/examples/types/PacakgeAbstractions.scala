//Fine-Grained Visibility Rules

package encodedstring {

trait EncodedString {
  protected[encodedstring] val string: String
  val separator: EncodedString.Separator.Delimiter
  override def toString = string
  def toTokens = string.split(separator.toString).toList
}

object EncodedString {
  object Separator extends Enumeration {
    type Delimiter = Value
    val COMMA = Value(",")
    val TAB   = Value("\t")
  }
  def apply(s: String, sep: Separator.Delimiter) = sep match {
    case Separator.COMMA => impl.CSV(s)
    case Separator.TAB   => impl.TSV(s)
  }
  def unapply(es: EncodedString) = Some(Pair(es.string, es.separator))
}

package impl {
  //overrides the trait member in the constructor
private[encodedstring] case class CSV(override val string: String)
  extends EncodedString {
  override val separator = EncodedString.Separator.COMMA
}
private[encodedstring] case class TSV(override val string: String)
  extends EncodedString {
  override val separator = EncodedString.Separator.TAB
}
}

}

object PacakgeAbstractions extends App {

  import  encodedstring._
  import encodedstring.EncodedString._

  def p(s: EncodedString) = {
    println("EncodedString: " + s)
    s.toTokens foreach (x => println("token: " + x))
  }

  val csv = EncodedString("Scala,is,great!", Separator.COMMA)
  val tsv = EncodedString("Scala\tis\tgreat!", Separator.TAB)

  p(csv)
  p(tsv)

  println( "\nExtraction:" )
  List(csv, "ProgrammingScala", tsv, 3.14159) foreach {
    case EncodedString(str, delim) =>
      println( "EncodedString: \"" + str + "\", delimiter: \"" + delim + "\"" )
    case s: String => println( "String: " + s )
    case x => println( "Unknown Value: " + x )
  }

//  val csv = impl.CSV("comma,separated,values") //Error

}

//In this simple example, it wasn’t essential to make the concrete types private to the
//component. However, we have a very minimal interface to clients of the component,
//and we are free to modify the implementation as we see fit with little risk of forcing
//client code modifications. A common cause of maintenance paralysis in mature appli-
//cations is the presence of too many dependencies between concrete types, which be-
//come difficult to modify since they force changes to client code. So, for larger, more
//sophisticated components, this clear separation of abstraction from implementation
//can keep the code maintainable and reusable for a long time.
