/*
Perhaps more surprising for the Java programmer, functions are also objects in Scala.
It is therefore possible to pass functions as arguments, to store them in variables,
and to return them from other functions. This ability to manipulate functions as
values is one of the cornerstone of a very interesting programming paradigm called
functional programming.
*/

object Timer
{
  //callback is something similar to void function pointer
  //with no args and no return type
  def oncePerSecond(callback: () => Unit)
  {
    while(true)
    {
      callback();
      Thread sleep 1000 //so called easy way
      Thread.sleep(1000) //our way
    }
  }
  def timeFlies()
  {
    println("Time flies like an arrow ----->")
  }
  def main(args: Array[String])
  {
    oncePerSecond(timeFlies)
  }
  
}


