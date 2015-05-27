 /*function composition, which feeds the output of
 one function in as the input to another function. Again, the implementation of this
 function is fully determined by its type signature.
 Implement the higher-order function that composes two
 functions.
 */

 def compose[A,B,C](f: B => C, g: A => B): A => C

