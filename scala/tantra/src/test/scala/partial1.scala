/*Implement partial1 and write down a concrete usage
of it. There is only one possible implementation that compiles. We don't have any
concrete types here, so we can only stick things together using the local 'rules of
the universe' established by the type signature. The style of reasoning required here
is very common in functional programming—we are simply manipulating symbols
in a very abstract way, similar to how we would reason when solving an algebraic
equation.
*/

/* partial1 is polymorphic function with three types
namely A, B & C. 

Arguments are...
- First argument is of a variable type.
- Second type is a function that takes two args of types A & B and 
  return of type C.
Return type is...
- A function that takes type B and return C

*/
//def partial1[A,B,C](a: A, f: (A,B) => C): B => C

