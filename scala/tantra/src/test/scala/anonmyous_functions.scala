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
  def formatResult(str: String, n: Int, f: Int => Int) : Unit = {
    println("The %s of %d is %d".format(str, n, f(n)))
  }

  def main(args: Array[String])
  {
    formatResult("Increment", 7, (x: Int) => {val r = x + 1; r})
    formatResult("Increment", 7, (x) => x + 1)
    formatResult("Increment", 7, x => x + 1)
    formatResult("Increment", 7, (x: Int) => x + 1)
    
    /*In this last form _ + 1, sometimes called underscore
    syntax for a function literal, we are not even bothering to name the argument to the
    function, using _ represent the sole argument. When using this notation, we can
    only reference the function parameter once in the body of the function */
    formatResult("Increment", 7, _ + 1)
    
    formatResult("Increment", 7, x => {val r = x + 1; r})
    //If you really have patience to create a local variable
    //for a function
    val f = (x: Int) => {val r = x + 1; r}
    formatResult("Increment", 8, f)
    //oncePerSecond(timeFlies)
    oncePerSecond(() => println("Time flies like an arrow ----->"))
  }
  
}


