/**
 * Created by mdhandapani on 10/8/15.
 * Call-by-Value: the value of the parameter is determined before it is
 * passed to the function.
 */

//A by-name parameter is specified by omitting the parentheses that normally accompany
//a function parameter, as follows:
def myCallByNameFunction(callByNameParameter: => Int) = 5//or
//Without this syntactic shortcut, this method definition would look like the following:
def myCallByNameFunction1(callByNameParameter: () => Int) = 7

println("////////////////////////////////////////////////////////////////////////")

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
//Can be viewed as pointer to the function, that waits for explicit invocation
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
println("////////////////////////////////////////////////////////////////////////")
object StringUtil1 {
  def joiner(strings: List[String], seprator: String = " "): String =
  strings.mkString(seprator)
}
import StringUtil1._
joiner(List("Mageswaran", "Aja"))
joiner(List("Mageswaran", "Aja"), "|")
joiner(List("Mageswaran", "Aja"), seprator = "*")
joiner(strings = List("Mageswaran", "Aja"), "}")

println("////////////////////////////////////////////////////////////////////////")

//() is added to the parameter to show the difference
//def whileAwesome(condition: () => Boolean)(body: () => Unit): Unit = {

//**** Remove the => and see what happens in the condition?
def whileAwesome(condition: => Boolean)(body: => Unit): Unit = {
  //if (condition()) {
  if (condition) {
    body
    whileAwesome(condition)(body)
  }
}

var count = 0
//whileAwesome(() => count < 5) {
whileAwesome(count < 5) {
  println("still awesome")
  count += 1
}

