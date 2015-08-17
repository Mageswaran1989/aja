//#!/bin/sh
//exec scala "$0" "$@"
//!#
//
///*
//Arrays  = Mutable
//Lists   = Immutable
//
//Maps/Sets can be both mutable and immutable
//*/
//
///*
//Different maps available as a library routines:
//HashMap
//TreeMap
//SynchronizedMap
//*/
//
////immutable Map trait is automatically imported into any Scala source file
//var captial = Map("India" -> "New Delhi",
//                  "France" -> "Paris")
//captial += ("Japan" -> "Tokyao")
//println("Captial od India is " + captial("India"))
//
////with factory method
//
//val romanNumeral = Map(
//    1 -> "I",
//      2 -> "II",
//        3 -> "III",
//          4 -> "IV",
//            5 -> "V"
//          )
//println(romanNumeral(4))
//
//
////by default immutable HashSet is imported
//val movieSet = Set("Hitch", "Poltergeist", "Shrek")
//println(movieSet)
//
//
//import scala.collection.mutable.HashSet
//
//
////you need to parameterize it with a type (in this case, String)
//// Same like Array and List
//val jetSet = new HashSet[String]
////jetSet.+=("Lear")
//jetSet += "Lear"
//jetSet += ("Boeing", "Airbus")
//println(jetSet.contains("Cessna"))
//
//import scala.collection.mutable.HashMap
//
//var treasureMap = new HashMap[Int, String]
//treasureMap += ((1).->("Go to island."))
////This -> method, which you can invoke on any object in a Scala program, returns a two-element tuple
////containing the key and value.
//treasureMap += (2 -> "Find big X on the ground")
//treasureMap += (3 -> "Dig")
//println((treasureMap(2)))
//
//
