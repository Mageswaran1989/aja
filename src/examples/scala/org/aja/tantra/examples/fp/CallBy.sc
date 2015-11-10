/**
 * Created by mdhandapani on 10/8/15.
 * Call-by-Value: the value of the parameter is determined before it is
 * passed to the function.
 */

def CallByValue() {
  def time() = {
    println("Getting time in nano seconds")
    System.nanoTime
  }

  //Execute the passed code and get the result
  def delayed(t: Long) {
    println("In delayed method")
    println("Param: " + t)
  }
  delayed(time())
}

/* Call-by-Name: the value of the parameter is not determined until
 * it is called within the function.
 *
 */
def CallByName() = {
  def time() = {
    println("Getting time in nano seconds")
    System.nanoTime
  }

  //Reference to the passed block is called when actually been used
  //Lazy Evaluation
  def delayed2(t: => Long) {
    println("In delayed method")
    println("Param: " + t)
  }
  delayed2(time())
}

CallByValue()
println()
CallByName()

object StringUtil1 {
  def joiner(strings: List[String], seprator: String = " "): String =
  strings.mkString(seprator)
}
import StringUtil1._
joiner(List("Mageswaran", "Aja"))
joiner(List("Mageswaran", "Aja"), "|")
joiner(List("Mageswaran", "Aja"), seprator = "*")
joiner(strings = List("Mageswaran", "Aja"), "}")

