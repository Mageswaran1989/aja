//#!/bin/bash
//scala $0 $@
//exit
//!#
//
///*
// There are many ways to create and use function-like values
// in Scala.
// */
//
//
//  // Implicit function:
//  def id(x : Int) : Int = x
//
//  // Anonymous function:
//  val anonId = (x : Int) => x
//
//
//  // class with apply method:
//  class Identity {
//      def apply(x : Int) = x
//  }
//  val myId = new Identity
//
//  // f(x) => f.apply(x)
//
//  // object with apply method:
//  object Id {
//      def apply(x : Int) = x
//  }
//
//
//  // anonymous class with apply method:
//  val myOtherId = new {
//      def apply(x : Int) = x
//  }
//
//  // case blocks also act as functions:
//  val myCaseID : Int => Int = {
//      case x => x
//  }
//
//
//  println(id(3))
//  // Prints:
//  // 3
//
//  println(anonId(3))
//  // Prints:
//  // 3
//
//  println(Id.apply(3))
//  // Prints:
//  // 3
//
//  println(myId.apply(3))
//  // Prints:
//  // 3
//
//  println(Id(3))
//  // Prints:
//  // 3
//
//  println(myId(3))
//  // Prints:
//  // 3
//
//  println(myOtherId(3))
//  // Prints:
//  // 3
//
//  println(myCaseID(3))
//  // Prints:
//  // 3
//
//
//  // Multi-argument functions:
//  def h(x : Int, y : Int) : Int = x + y
//
//  // A Curried multi-argument function:
//  def hC (x : Int) (y : Int) : Int = x + y
//
//  // Wrong: hC 3 4
//  // Right: hC (3) (4)
//
//  // Wrong: hC (3)
//  // Right: hC (3) _
//
//  // Wrong: hC _ (4)
//  // Right: hC (_:Int) (4)
//
//  val plus3 = hC (_:Int) (3)
//  val plus_3 = hC (3) _
//
//  println(plus3(10))
//  // Prints:
//  // 13
//
//
//  // A procedure:
//  def proc(a : Int) { // Implicitly : Unit
//      println("I'm a procedure.")
//  }
//
//  proc(10)
//  // Prints:
//  // I'm a procedure.
//
//
//  // An argument-less function:
//  def argless : Unit = println("argless got called!")
//
//  argless
//  argless
//  // Prints:
//  // argless got called
//  // argless got called
//
//
//  // Lazy fields are argless functions that cache their result:
//  class LazyClass {
//      lazy val x = { println("Evaluating x") ; 3 }
//  }
//
//  val lc = new LazyClass
//  println(lc.x)
//  println(lc.x)
//  println(lc.x)
//  // Prints:
//  // Evaluating x
//  // 3
//  // 3
//  // 3
//
//  // Parameters can be evaluated lazily by-name:
//  def lazyId(x : => Int) : Int = {
//      x
//      x
//      x
//      return x ;
//  }
//
//  println(lazyId { println("used!") ; 3 })
//  // Prints:
//  // used!
//  // used!
//  // used!
//  // used!
//  // 3
