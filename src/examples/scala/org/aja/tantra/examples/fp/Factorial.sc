/**
 * Created by mdhandapani on 10/8/15.
 */

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

def factorial2(n: BigInt): BigInt = {
  if (n <= 0)
    1
  else
    n * factorial2(n-1)
}
val fact2 = factorial2(30)

println("//////////////////////////////////////////////////////////////////////")

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

// A new String is constructed from "...",
//hence formedString.format()
println("Values of fact is %d".format(fact))

println("Values of fact1 is %d".format(fact1))

println("Values of 30! is %d".format(fact2))

println("//////////////////////////////////////////////////////////////////////")

//With match and case

def factorial3(i: Int): Int = i match {
  case _ if i == 1 => println("factorial3(1) = " + i); i
  case _ => {
    println(i + "*" + "factorial3(" + (i-1) + ") => ")
    i * factorial3(i-1)
  }
}

println()
val f = factorial3(8)

//TODO: convert the above sample in to TCO one! ignore prints.