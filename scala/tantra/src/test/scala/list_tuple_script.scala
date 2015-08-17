//#!/bin/sh
//exec scala "$0" "$@"
//!#
//
//val oneTwoThree = List(1,2,3)
//
//val oneTwo = List(1, 2)
//val threeFour = List(3, 4)
//val oneTwoThreeFour = oneTwo ::: threeFour
//println(oneTwo + " and " + threeFour + " were not mutated.")
//println("Thus, " + oneTwoThreeFour + " is a new List.")
//
//
///*
//Class List does not offer an append operation, because the time it takes to append to
//a List grows linearly with the size of the List, whereas prepending takes constant time.
//*/
//val twoThree = List(2, 3)
//val fiveTwoThree = 5 :: twoThree
//println(oneTwoThree)
//
//
////with cons operator creating a new List
//val oneTwoFour = 1 :: 2 :: 4 :: Nil
//println(oneTwoFour)
///*
//The reason you need Nil at the end is that :: is defined on class List. If you try to just
//say 1 :: 2 :: 3, it won’t compile because 3 is an Int, which doesn’t have a :: method.
//*/
//
//
////Tuple can have multiple data types grouped toghether
//val pair = (99, "Luftballons")
////._1 is used since the value starts from 1 and not 0 like ML or Haskell
//println(pair._1)
//println(pair._2)
//
//var tuple = ('u', 'r', "the", 1, 4, "me")
//println(tuple)
