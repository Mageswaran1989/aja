/* 
Let's look at another example, currying, which converts a
 function of N arguments into a function of one argument that returns another
 function as its result. Here again, there is only one implementation that
 typechecks.
 */
def curry[A,B,C](f: (A, B) => C): A => (B => C)

