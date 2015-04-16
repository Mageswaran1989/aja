/*
anonymous functions: functions without a name
Have you heared of lambda expression!?
 */

object TimerAnonmyous
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
    //oncePerSecond(timeFlies)
    oncePerSecond(() => println("Time flies like an arrow ----->"))
  }
  
}


