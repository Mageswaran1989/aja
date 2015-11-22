//Link: http://blog.richdougherty.com/2009/04/tail-calls-tailrec-and-trampolines.html
//Recurssive function needs return type
def even(n: Int): Boolean = {
  if (n == 0)
    true
  else
    odd(n-1)
}
def odd(n: Int): Boolean = {
  if (n == 0)
    false
  else
    even(n-1)
}
def isEven(n: Int) = {
  even(n)
}
isEven(4)
isEven(9)
isEven(99)
var t0 = System.nanoTime()
isEven(999)
var t1 = System.nanoTime()
println("Elapsed time: " + (t1 - t0)  + "ns")
//isEven(9999) //We are done! stackoverflow kicks in.
println("///////////////////////////////////////////////////////////")
/*
Trampolines:
A trampoline is a loop that repeatedly runs functions. Each function,
called a thunk, returns the next function for the loop to run.
The trampoline never runs more than one thunk at a time, so if you
break up your program into small enough thunks and bounce each one
off the trampoline, then you can be sure the stack won't grow too big.
 */


//sealed trait is used to make the match...case exhaustive i.e
sealed trait Bounce[A]
case class Call[A](thunk: () => Bounce[A]) extends Bounce[A]
case class Result[A](result: A) extends Bounce[A]
/*
case class: Remember apply() & unapply() methods are implicitly helpful here
 */
def even1(n: Int) : Bounce[Boolean] = {
  if (n == 0)
    Result(true)
  else
    Call(() => odd1(n-1))
}

def odd1(n: Int): Bounce[Boolean] = {
  if (n == 0)
    Result(false)
  else
    Call(() => even1(n-1))
}

def trampolines[A](bounce: Bounce[A]): A = bounce match {
  //unapply() is implicitly called here
  case Call(thunk) => trampolines(thunk())
  case Result(x) => x
}

t0 = System.nanoTime()
trampolines(even1(999))
t1 = System.nanoTime()
println("Elapsed time: " + (t1 - t0)  + "ns")


t0 = System.nanoTime()
trampolines(even1(9999)) //I got some result though I take lot of time!
t1 = System.nanoTime()
println("Elapsed time: " + (t1 - t0)  + "ns")