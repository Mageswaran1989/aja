/**
 * Created by mageswaran on 23/9/15.
 */

class ChecksumAccumulator {
  private var sum = 0
  private[this] var sum1 = 0
  private[ChecksumAccumulator] var sum2 = 0
  var sum3 = 0

  def add(b: Byte): Unit = {
    sum += b
  }
  def checksum(): Int = {
    return ~(sum & 0xFF) + 1
  }
}

class Q {
  private var x: Int = 0
  private[Q] var y: Int = 0

  def printx() = println("Value of x in Q: " + x)
  def printy() = println("Value of y in Q: " + y)
}
class QQ extends Q {
  var x: String = "abc"
  var y: String = "abc"
}

class Foo(private val bar: Int) {
  def otherBar(f: Foo) {
    println(f.bar) // access bar of another foo
  }
}

class Foo1(bar: Int) { //class Foo1(private[this] val bar:Int)
  def otherBar(f: Foo1) {
   // println(f.bar) // access bar of another foo : Error
  }
}

object GetterSetter extends App{

  val c = new ChecksumAccumulator
  println("Only var sum3 is accessible: " + c.sum3)
//  c.sum
  println("Let see what Q & QQ has ")
  val q = new Q
  val qq = new QQ
  val q1: Q = new QQ

  //println(q.x) error
  println(q.printx)
  println(qq.x)
  println(q1.printx)

  val a = new Foo(1)
  a.otherBar(new Foo(3))

  val a1 = new Foo(1)
  a1.otherBar(new Foo(3))

}
