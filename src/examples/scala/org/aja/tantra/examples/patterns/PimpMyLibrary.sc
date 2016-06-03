//The pimp my library pattern allows one to seemingly add a method to a class by making available an implicit conversion
//from that class to one that implements the method.

//Implicit chains won't works

class A(val m: Int)
class B(val m: Int, val n: Int)
class C(val m: Int, val n: Int, val o: Int) {
  def total = m + n + o
}

object T1 {
  implicit def toA(n: Int): A = new A(n)
  implicit def aToB(a: A): B = new B(a.m, a.m)
  implicit def bToC(b: B): C = new C(b.m, b.n, b.m + b.n)


  //>>>>>>>>>>>>>>>>>>>
  //5 is an integer and total methos is in class C, so 5 -> A -> B -> C
  //println(5.total)
  //println(new A(5).total)

  println("Scala wont do this: " + bToC(aToB(toA(5))).total)
  println("Scala wont do this: " + bToC(aToB(new A(5))).total)

  //<<<<<<<<<<<<<<<<<<<<<

  println(new B(5,5).total)
  println("This how it calcutes the result: " + bToC(new B(5, 5)).total)


  println(new C(5,5,20).total) //no implicit required
}

T1
object T2 {
  val printFlag = false //enable to see what happens...needs more investigation!
  implicit def toA(n: Int) =  {
    if (printFlag) println("Implicitly Creating A" );
    new A(n)
  }
  //Parameterized type A1, search for implicit that converts it to type A
  implicit def aToB[A1](a: A1)(implicit f: A1 => A): B ={
    if (printFlag) println("Implicitly Creating B through f -> " + f.getClass);
    new B(a.m, a.m) }

  implicit def bToC[B1](b: B1)(implicit f: B1 => B): C = {
    if (printFlag) println("Implicitly Creating C through f ->" + f.getClass);
    new C(b.m, b.n, b.m + b.n)
  }

  //5 is an integer and total method is in class C, so 5 -> A -> B -> C
  //{} is used to differentiate the implicit calling
  if (!printFlag) println(5.total)
  if (!printFlag) println("Scala does this: " + bToC(5){m => aToB(m){m => toA(m)}}.total)

  if (printFlag) {
    val res = bToC(5) {
      m =>
        var t = 0
        println("bToC m : " + m);
        aToB(m) {
          println("aToB m : " + m);
          println()
          m => toA(m)
        }
    }.total

    println("Scala does this explained: " + res )
  }
  if (!printFlag) println(new A(10).total)
  if (!printFlag) println("Scala does this: " + bToC(new A(10))(x => aToB(x)(identity)).total)
  if (!printFlag) println(new B(5,5).total)
  if (!printFlag) println("Scala does this: " + bToC(new B(5,5))(identity).total)

  if (!printFlag) println(new C(5,5,20).total) //no implicit required
}

T2

