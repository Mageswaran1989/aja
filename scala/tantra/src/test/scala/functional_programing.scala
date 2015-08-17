//#!/bin/bash
//scala $0 $@
//exit
//!#
//
////For now "case" keyword helps reducing
////your work of writing a class, constructor
////and its members
//case class Player (name: String, score: Int)
//
////Notice Unit == void
//def printWinner (p : Player) : Unit =
//  println ("Winner is " + p.name + "! with a score of " + p.score)
//
////Note : There is no "return" here.
////       Reason is simple the function is Referential Transparency
////       Simply put kind of inline expression evaluator, but with defined as
////       function. Since this doesn't alter the program state at any time
////       this is termed as Pure Function.
//def winner (p1: Player, p2: Player) =
//  if (p1.score > p2.score)
//    p1
//  else
//    p2
//
////declareWinner is decoupled between printing and compution part
//def declareWinner (p1: Player, p2: Player) =
//  printWinner(winner(p1, p2))
//
////normal way of writing function
///*
//def declareWinner (p1: Player, p2: Player) =
//  if (p1.score > p2.score)
//    printWinner(p1)
//  else
//    printWinner(p2)
//*/
//
//val p1 = Player("Goku", 100)
//val p2 = Player("SuperMan", 67)
//
//declareWinner(p1, p2)
//
//val pList = List(
//  Player("Goku", 100),
//  Player("SuperMan", 67),
//  Player("BatMan", 89) )
//
////The power of pure function comes in handy when we want to
////pass the function itself to an anothet funtion.
////After all thats the modularity right!
//val topPlayer = pList.reduceLeft(winner)
//
//printWinner(topPlayer)
