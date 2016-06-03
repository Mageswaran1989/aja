#Referential Transparency 
You can call a function anywhere and be confident that it will always behave the
same way. If no global state is modified, concurrent invocation of the function is
straightforward and reliable.

#Unit - Return type ~ Pure side effect function
A function that returns Unit implies that the function has pure side ef-
fects, meaning that if it does any useful work, that work must be all side
effects, since the function doesn’t return anything.


#Higher-order function
When a function takes other functions as arguments or returns a function, it is called
a higher-order function. In mathematics, two examples of higher-order functions from
calculus are derivation and integration.

#Variable Immutability
- The word “variable” takes on a new meaning in functional programming. If you come
from a procedural or object-oriented programming background, you are accustomed
to variables that are mutable. In functional programming, variables are immutable.
This is another consequence of the mathematical orientation. In the expression y =
sin(x), once you pick x, then y is fixed. As another example, if you increment the integer
3 by 1, you don’t “modify the 3 object,” you create a new value to represent 4.
- Immutable data also implies that lots of copies will be made, which can be expensive.
Functional data structures optimize for this problem (see [Okasaki1998]) and many of
the built-in Scala types are efficient at creating new copies from existing copies.

#Memoization
 Even in “pure” functional libraries, it is
common to perform internal optimizations like caching previously computed values
(sometimes called memoization). Caching introduces side effects, as the state of the
cache is modified.

#Recursion to Loop ~ Tail Call Optimization
- The tail-call optimization won’t be applied when a method that calls
itself might be overridden in a derived type. 
- The method must be private or final, defined in an object, or nested in another method (like fact
earlier). 
- The new @tailrec annotation in version 2.8 will trigger an error
if the compiler can’t optimize the annotated method.
