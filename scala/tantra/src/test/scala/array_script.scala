//#!/bin/sh
//exec scala "$0" "$@"
//!#
//
///*
//When you define a variable
//with val, the variable can’t be reassigned, but the object to which it refers
//could potentially still be mutated.
//So in this case, you couldn’t reassign
//greetStrings to a different array; greetStrings will always point to the
//same Array[String] instance with which it was initialized. But you can
//change the elements of that Array[String] over time, so the array itself is
//mutable.
//*/
//
//val greetStrings = new Array[String](3)
////Once created the size cant be changed, but the object is mutable i.e vales at 0,1,2
////can be changed
//
//greetStrings(0) = "Hello"
////is transformed to greetStrings.update(0, "Hello")
//greetStrings(1) = ","
//greetStrings(2) = "world!\n"
//
//for (i <- 0 to 2)
//  print(greetStrings(i))
////greetStrings(i) is transformed to greetStrings.apply(i)
////Thus accessing the element of an array in Scala is simply a method call like any other method call.
//
//
