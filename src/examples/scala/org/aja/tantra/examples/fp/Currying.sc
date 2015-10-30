/**
 * Created by mdhandapani on 10/8/15.
 */
/*
Let's look at another example, currying, which converts a
 function of N arguments into a function of one argument that returns another
 function as its result. Here again, there is only one implementation that
 typechecks.
 */
//def curry[A,B,C](f: (A, B) => C): A => (B => C)
///* Implement uncurry, which reverses the
// transformation of curry. Note that since => associates to the right, A => (B
//   => C) can be written as A => B => C.
//*/
//
//def uncurry[A,B,C](f: A => B => C): (A, B) => C

def someComplexFunction(a: Int)(b: Int) = a + b

val partialSum = someComplexFunction(2)_
val sum = partialSum(3)



def filter(xs: List[Int], p: Int => Boolean): List[Int] =
 if (xs.isEmpty) xs
 else if (p(xs.head)) xs.head :: filter(xs.tail, p)
 else filter(xs.tail, p)

def modN(n: Int)(x: Int) = ((x % n) == 0)

val nums = List(1, 2, 3, 4, 5, 6, 7, 8)

filter(nums, modN(2))
filter(nums, modN(3))

///Curring vs PartialFunction
//http://stackoverflow.com/questions/14309501/scala-currying-vs-partially-applied-functions

def modN1(n: Int, x: Int) = ((x%n) == 0)

modN1(5, _: Int)

def modNCurried(n: Int)(x: Int) = ((x % n) == 0)

// modNCurried(5) //error
val modNCurriedPartial = modNCurried(5)_
modNCurriedPartial(5)

modN1 _
modNCurried _

(modN1 _).curried
modNCurried _

//Scala doesn't seem to be able to infer the type when partially applying "normal" functions:
//modN1(5, _) //error

//Whereas that information is available for functions written using multiple parameter list notation:
modNCurried(5) _

