/*
Factorial code in legacy C
#include <stdio.h>

int fact(int n)
{
    if (n <= 0)
      return 1;
    else
      return (n * fact(n-1));
}
int main(void)
{
    int f = fact(5);
    printf("Factorial of 5 is %d\n ", f);
} 
*/

//import scala.annotation.tailrec

//@annotation.tailrec //enable to get compiler error
def factorial(n: Int): Int = {
  if (n <= 0)
    1
  else
    n * factorial(n-1) // does more than reuturning values
}

val fact = factorial(5)

//println(s"Value of fact is $fact") //works only on 2.10+

// A new String is constructed from "...", 
//hence formedString.format()
println("Values of fact is %d".format(fact))

// Tail call function, which does nothing but 
//returning values. This will be optimized as
//iterative loops by compiler, saving the stack
//TailCallOptimization - TCO
def factorial1(n: Int): Int =
{
  @annotation.tailrec
  def go(n: Int, acc: Int): Int =
  {
    if (n <= 0)
      acc
    else
      go(n-1, n*acc)
  }

  go(n, 1)
}

val fact1 = factorial1(5)

println("Values of fact1 is %d".format(fact1))
