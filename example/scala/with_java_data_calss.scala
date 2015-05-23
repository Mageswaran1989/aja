//Scala style of imports
import java.util.{Date, Locale} //Multiple classes in one shot
import java.text.DateFormat //As static import
import java.text.DateFormat._ //* -> _ i.e all symbols are imported

object FrenchDate
{
  def main(args: Array[String])
  {
    val now = new Date
    val df = getDateInstance(LONG, Locale.FRANCE)
    println(df format now) //So called easy way
    println(df.format(now)) //Our tradional way
  }
}
