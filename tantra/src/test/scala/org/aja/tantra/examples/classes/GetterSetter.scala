package org.aja.tantra.examples.classes

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
}
class QQ extends Q {
  var x: String = "abc"
  override var y: String = "abc"
}

object GetterSetter {

  val c = new ChecksumAccumulator
  println("Only var sum3 is accessible: " + c.sum3)
}
