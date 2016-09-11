package org.dhira.nn.containers

import java.io.Serializable

/**
 * Created by mageswaran on 10/9/16.
 */
object Triple {
  def makeTriple[S, T, U](s: S, t: T, u: U): Triple[S, T, U] = {
    new Triple(s, t, u)
  }
}

class Triple[S, T, U] extends Serializable {
  private[berkeley] var first: S = null
  private[berkeley] var second: T = null
  private[berkeley] var third: U = null

  def this(first: S, second: T, third: U) {
    this()
    this.first = first
    this.second = second
    this.third = third
  }

  def getFirst: S = {
    return first
  }

  def setFirst(first: S) {
    this.first = first
  }

  def getSecond: T = {
    return second
  }

  def setSecond(second: T) {
    this.second = second
  }

  def getThird: U = {
    return third
  }

  def setThird(third: U) {
    this.third = third
  }

  override def hashCode: Int = {
    val prime: Int = 31
    var result: Int = 1
    result = prime * result + (if ((first == null)) 0 else first.hashCode)
    result = prime * result + (if ((second == null)) 0 else second.hashCode)
    result = prime * result + (if ((third == null)) 0 else third.hashCode)
    return result
  }

  override def equals(obj: AnyRef): Boolean = {
    if (this eq obj) return true
    if (obj == null) return false
    if (getClass ne obj.getClass) return false
    val other: Triple = obj.asInstanceOf[Triple]
    if (first == null) {
      if (other.first != null) return false
    }
    else if (!(first == other.first)) return false
    if (second == null) {
      if (other.second != null) return false
    }
    else if (!(second == other.second)) return false
    if (third == null) {
      if (other.third != null) return false
    }
    else if (!(third == other.third)) return false
    return true
  }

  override def toString: String = {
    return String.format("(%s,%s,%s)", first, second, third)
  }
}
