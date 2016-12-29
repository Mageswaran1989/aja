package org.dhira.core.containers

import java.io.Serializable
import java.util.Comparator

/**
 * Created by mageswaran on 10/9/16.
 */
/**
 * A generic-typed pair of objects.
 * @author Mageswaran Dhandapani
 */
@SerialVersionUID(42)
object Pair {

  class FirstComparator[S <: Comparable[(_$1) forSome {type _$1 >: S}], T] extends Comparator[Pair[S, T]] {
    def compare(p1: Pair[S, T], p2: Pair[S, T]): Int = {
       p1.getFirst.compareTo(p2.getFirst)
    }
  }

  class ReverseFirstComparator[S <: Comparable[(_$1) forSome {type _$1 >: S}], T] extends Comparator[Pair[S, T]] {
    def compare(p1: Pair[S, T], p2: Pair[S, T]): Int = {
       p2.getFirst.compareTo(p1.getFirst)
    }
  }

  class SecondComparator[S, T <: Comparable[(_$1) forSome {type _$1 >: T}]] extends Comparator[Pair[S, T]] {
    def compare(p1: Pair[S, T], p2: Pair[S, T]): Int = {
       p1.getSecond.compareTo(p2.getSecond)
    }
  }

  class ReverseSecondComparator[S, T <: Comparable[(_$1) forSome {type _$1 >: T}]] extends Comparator[Pair[S, T]] {
    def compare(p1: Pair[S, T], p2: Pair[S, T]): Int = {
       p2.getSecond.compareTo(p1.getSecond)
    }
  }

  def newPair[S, T](first: S, second: T): Pair[S, T] = {
     new Pair[S, T](first, second)
  }

  def makePair[S, T](first: S, second: T): Pair[S, T] = {
     new Pair[S, T](first, second)
  }

  class LexicographicPairComparator[F, S] extends Comparator[Pair[F, S]] {
    private[containers] var firstComparator: Comparator[F] = null
    private[containers] var secondComparator: Comparator[S] = null

    def compare(pair1: Pair[F, S], pair2: Pair[F, S]): Int = {
      val firstCompare: Int = firstComparator.compare(pair1.getFirst, pair2.getFirst)
      if (firstCompare != 0)  firstCompare
       secondComparator.compare(pair1.getSecond, pair2.getSecond)
    }

    def this(firstComparator: Comparator[F], secondComparator: Comparator[S]) {
      this()
      this.firstComparator = firstComparator
      this.secondComparator = secondComparator
    }
  }

  class DefaultLexicographicPairComparator[F <: Comparable[F], S <: Comparable[S]] extends Comparator[Pair[F, S]] {
    def compare(o1: Pair[F, S], o2: Pair[F, S]): Int = {
      val firstCompare: Int = o1.getFirst.compareTo(o2.getFirst)
      if (firstCompare != 0) {
         firstCompare
      }
       o1.getSecond.compareTo(o2.getSecond)
    }
  }

}

@SerialVersionUID(42)
class Pair[F, S] extends Serializable with Comparable[Nothing] {
  private[containers] var first: F = _
  private[containers] var second: S = _

  def getFirst: F = {
     first
  }

  def getSecond: S = {
     second
  }

  def setFirst(pFirst: F) {
    first = pFirst
  }

  def setSecond(pSecond: S) {
    second = pSecond
  }

  def reverse: Nothing = {
     new Nothing(second, first)
  }

  override def equals(o: Any): Boolean = {
    if (this eq o)  true
    if (!(o.isInstanceOf[Pair[F, S]]))  false
    val pair: Pair[F, S] = o.asInstanceOf[Pair[F, S]]
     !(if (first != null) !(first == pair.first) else pair.first != null) && !(if (second != null) !(second == pair.second) else pair.second != null)
  }

  override def hashCode: Int = {
    var result: Int = 0
    result = (if (first != null) first.hashCode else 0)
    result = 29 * result + (if (second != null) second.hashCode else 0)
     result
  }

  override def toString: String = {
     "(" + getFirst + ", " + getSecond + ")"
  }

  def this(first: F, second: S) {
    this()
    this.first = first
    this.second = second
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
   * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
   * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
   * <tt>y.compareTo(x)</tt> throws an exception.)
   * <p/>
   * <p>The implementor must also ensure that the relation is transitive:
   * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
   * <tt>x.compareTo(z)&gt;0</tt>.
   * <p/>
   * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
   * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
   * all <tt>z</tt>.
   * <p/>
   * <p>It is strongly recommended, but <i>not</i> strictly required that
   * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
   * class that implements the <tt>Comparable</tt> interface and violates
   * this condition should clearly indicate this fact.  The recommended
   * language is "Note: this class has a natural ordering that is
   * inconsistent with equals."
   * <p/>
   * <p>In the foregoing description, the notation
   * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
   * <i>signum</i> function, which is defined to  one of <tt>-1</tt>,
   * <tt>0</tt>, or <tt>1</tt> according to whether the value of
   * <i>expression</i> is negative, zero or positive.
   *
   * @param o the object to be compared.
   * @ a negative integer, zero, or a positive integer as this object
   *         is less than, equal to, or greater than the specified object.
   * @throws NullPointerException if the specified object is null
   * @throws ClassCastException   if the specified object's type prevents it
   *                              from being compared to this object.
   */
  def compareTo(o: Nothing): Int = {
     new Pair.DefaultLexicographicPairComparator[_ <: Comparable[F], _ <: Comparable[S]]().compare(this, o)
  }
}
