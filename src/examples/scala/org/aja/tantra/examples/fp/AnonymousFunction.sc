/**
 * Created by mdhandapani on 10/8/15.
 */

/*
anonymous functions: functions without a name
(args) => function body
Have you heared of lambda expression!?
 */


val inc = (x:Int) => x + 1
println(s"Value of 4 + 1: ${inc(4)}")
val userDir = () => { System.getProperty("user.dir") }
println(userDir())

def inc1 = (n: Int) => n + 1
inc1.getClass
inc1(1)

def inc2() = (n: Int) => n + 1
inc2().getClass
//inc2(2) Error

def inc2_1 = inc2()
inc2_1(1)
val inc2_2 = inc2()
inc2_2(1)
println("///////////////////////////////////////////////////////////////////")
//callback is something similar to void function pointer
//with no args and no return type
object WhileLoop {
  def oncePerSecond(callback: () => Unit) {
    var i = 5
    while (i > 0) {
      callback()
      Thread sleep 1000 //so called easy way
      Thread.sleep(1000) //our way
      i = i - 1
    }
  }
}
//oncePerSecond(timeFlies)
WhileLoop.oncePerSecond(() => println("Time flies like an arrow ----->"))
def timeFlies()
{
  println("Time flies like an arrow ----->")
}

