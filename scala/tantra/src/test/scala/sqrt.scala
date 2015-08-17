//#!/bin/sh
//exec scala "$0" "$@"
//!#
//
//def sqrtIter(guess: Double, x: Double): Double =
//    if(isGoodEnough(guess, x))
//      guess
//    else
//      sqrtIter(improveGuess(guess, x), x)
//
//def isGoodEnough(guess: Double, x: Double): Boolean =
//  Math.abs(guess * guess - x) < 0.001
//
//def improveGuess(guess: Double, x: Double): Double =
//  (guess + x/guess) /2
//
//def sqrt(x: Double) = sqrtIter(1.0, x)
//
//println (sqrt(2))
//println (sqrt(4))
//println (sqrt(9))
//
//
