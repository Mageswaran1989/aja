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
