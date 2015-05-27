#!/bin/sh
exec scala "$0" "$@"
!#

def greet() = println("Hello, world!")
greet()
greet // Bad style

//parameterless function
def greeting = "Hello, World!"
//according to Uniform Access Principle", parameterless function can be changed to
//"val" without affecting the client code.
//val greeting = "Hello, World!"

//Client Code
//should be called without ()
println(greeting)

println("Hello," + args(0) + "!")

var i =0
while (i < args.length) {
  println(args(i))
  i+=1
}

//with function literal
//partially applied function : with one statement and one argument
//args.foreach(arg => print(arg))
args.foreach((arg: String) => print(arg))
//pass the the one args from "foreach" to "println" function
args.foreach(println)

for (arg <- args)
  println(arg)

1 + 2
(1).+(2)

//polymorphiv constructors
class RepeatGreeter(greetingMsg: String, count: Int) {

  if (greetingMsg == null) //this will be inserted into first constructor's body
    throw new NullPointerException("greetingMsg is null")

  //handle the instantiation through primary constructor
  def this(greetingMsg: String) = this(greetingMsg, 1) //auxillarry constructor
  
  def greet() = {
    for(i <- 1 to count)
      println(greetingMsg)
  }
}

val g1 = new RepeatGreeter("Aja", 3)
g1.greet()

val g2 = new RepeatGreeter("AjaLib")
g2.greet()

//test to check null exception
/*val g3 = new RepeatGreeter
g3.greet()*/
