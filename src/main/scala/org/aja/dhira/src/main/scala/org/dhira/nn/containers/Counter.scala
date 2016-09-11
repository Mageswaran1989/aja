package org.aja.dhira.nn.containers


import java.text.NumberFormat
import java.util
import java.util.{Random}

import org.aja.dhira.nn.containers.MapFactory.{HashMapFactory, IdentityHashMapFactory}
import org.dhira.nn.containers.PriorityQueue

import scala.collection.generic.MapFactory
import scala.collection.mutable

/**
 * @author Mageswaran Dhandapani
 * @since  0.1 09-Sep-2016
 * A map from objects to doubles. Includes convenience methods for getting,
 * setting, and incrementing element counts. Objects not in the counter will
 * return a count of zero. The counter is backed by a HashMap (unless specified
 * otherwise with the MapFactory constructor).
 *
 * TODO: check getEntry related APIs in Scala env
 *
 * Quick reference for Scala <-> Java Interoperatibility
 * scala.collection.Iterable <=> java.lang.Iterable
 * scala.collection.Iterable <=> java.util.Collection
 * scala.collection.Iterator <=> java.util.{ Iterator, Enumeration }
 * scala.collection.mutable.Buffer <=> java.util.List
 * scala.collection.mutable.Set <=> java.util.Set
 * scala.collection.mutable.Map <=> java.util.{ Map, Dictionary }
 * scala.collection.mutable.ConcurrentMap <=> java.util.concurrent.ConcurrentMap
 *
 * scala.collection.Seq         => java.util.List
 * scala.collection.mutable.Seq => java.util.List
 * scala.collection.Set         => java.util.Set
 * scala.collection.Map         => java.util.Map
 * java.util.Properties         => scala.collection.mutable.Map[String, String]
 */
@SerialVersionUID(5L)
class Counter[T] extends Serializable {

  var entries: scala.collection.mutable.Map[T, Double] = _
  var mapFactory: MapFactory[T, Double] = new HashMapFactory[T, Double] //Default type
  var defaultValue:Double = 0
  var dirty: Boolean = false
  var cacheTotal: Double = 0

  def getDeflt(): Double = {
    return defaultValue
  }

  def setDeflt(defaultValue: Double) {
    this.defaultValue = defaultValue
  }

  /**
   * The elements in the counter.
   *
   * @return applyTransformToDestination of keys
   */
  def keySet(): Set[T] = {
    entries.keySet
  }

  def entrySet(): Set[(T, Double)] = {
    entries.toSet
  }

  /**
   * The number of entries in the counter (not the total count -- use
   * totalCount() instead).
   */
  def size(): Int = {
    entries.size
  }

  /**
   * True if there are no entries in the counter (false does not mean
   * totalCount > 0)
   */
  def isEmpty(): Boolean = {
    size == 0
  }

  /**
   * Returns whether the counter contains the given key. Note that this is the
   * way to distinguish keys which are in the counter with count zero, and
   * those which are not in the counter (and will therefore return count zero
   * from getCount().
   *
   * @param key
   * @return whether the counter contains the key
   */
  def containsKey(key: T): Boolean = {
    return entries.contains(key)
  }

  /**
   * Get the count of the element, or zero if the element is not in the
   * counter.
   *
   * @param key
   * @return
   */
  def getCount(key: T): Double = {
    entries.get(key).getOrElse(defaultValue)
  }

  /**
   * I know, I know, this should be wrapped in a Distribution class, but it's
   * such a common use...why not. Returns the MLE prob. Assumes all the counts
   * are >= 0.0 and totalCount > 0.0. If the latter is false, return 0.0 (i.e.
   * 0/0 == 0)
   *
   * @author Aria
   * @param key
   * @return MLE prob of the key
   */
  def getProbability(key: T): Double = {
    val count: Double = getCount(key)
    val total: Double = totalCount
    if (total < 0.0) {
      throw new RuntimeException("Can't call getProbability() with totalCount < 0.0")
    }
    if (total > 0.0) count / total else 0.0
  }

  /**
   * Destructively normalize this Counter in place.
   */
  def normalize() {
    val totalCount: Double = totalCount
    import scala.collection.JavaConversions._
    for (key <- keySet) {
      setCount(key, getCount(key) / totalCount)
    }
    dirty = true
  }

  /**
   * Set the count for the given key, clobbering any previous count.
   *
   * @param key
   * @param count
   */
  def setCount(key: T, count: Double) {
    entries.put(key,count)
    dirty = true
  }

  /**
   * Set the count for the given key if it is larger than the previous one;
   *
   * @param key
   * @param count
   */
  def put(key: T, count: Double, keepHigher: Boolean) {
    if (keepHigher && entries.contains(key)) {
      val oldCount: Double = entries(key)
      if (count > oldCount) {
        entries.put(key, count)
      }
    }
    else {
      entries.put(key, count)
    }
    dirty = true
  }

  /**
   * Will return a sample from the counter, will throw exception if any of the
   * counts are < 0.0 or if the totalCount() <= 0.0
   */
  def sample(rand: Random): T = {
    val total: Double = totalCount
    if (total <= 0.0) {
      throw new RuntimeException(String.format("Attempting to sample() with totalCount() %.3f%n", total))
    }
    var sum: Double = 0.0
    val r: Double = rand.nextDouble
    import scala.collection.JavaConversions._
    for (entry <- entries.entrySet) {
      val count: Double = entry.getValue
      val frac: Double = count / total
      sum += frac
      if (r < sum) {
        return entry.getKey
      }
    }
    throw new IllegalStateException("Shoudl've have returned a sample by now....")
  }

  /**
   * Will return a sample from the counter, will throw exception if any of the
   * counts are < 0.0 or if the totalCount() <= 0.0
   *
   * @return
   *
   * @author aria42
   */
  def sample(): T = {
    sample(new Random)
  }

  def removeKey(key: T) {
    setCount(key, 0.0)
    dirty = true
    removeKeyFromEntries(key)
  }

  /**
   * @param key
   */
  protected def removeKeyFromEntries(key: T) {
    entries.remove(key)
  }

  /**
   * Set's the key's count to the maximum of the current count and val. Always
   * sets to val if key is not yet present.
   *
   * @param key
   * @param value
   */
  def setMaxCount(key: T, value: Double) {
    val mapValue: Double = entries.get(key).getOrElse(defaultValue)
    if (value > mapValue) {
      setCount(key, value)
      dirty = true
    }
  }

  /**
   * Set's the key's count to the minimum of the current count and val. Always
   * sets to val if key is not yet present.
   *
   * @param key
   * @param value
   */
  def setMinCount(key: T, value: Double) {
    val value: Double = entries.get(key).getOrElse(defaultValue)
    if (value  < value) {
      setCount(key, value)
      dirty = true
    }
  }

  /**
   * Increment a key's count by the given amount.
   *
   * @param key
   * @param increment
   */
  def incrementCount(key: T, increment: Double): Double = {
    val newVal: Double = getCount(key) + increment
    setCount(key, newVal)
    dirty = true
    newVal
  }

  /**
   * Increment each element in a given collection by a given amount.
   */
  def incrementAll(collection: Iterable[_ <: T], count: Double) {
    for (key <- collection) {
      incrementCount(key, count)
    }
    dirty = true
  }

  def incrementAll[U <: T](counter: Counter[U]) {
    for (key <- counter.keySet()) {
      val count: Double = counter.getCount(key)
      incrementCount(key, count)
    }
    dirty = true
  }

  /**
   * Finds the total of all counts in the counter. This implementation
   * iterates through the entire counter every time this method is called.
   *
   * @return the counter's total
   */
  def totalCount(): Double = {
    if (!dirty) {
      return cacheTotal
    }
    var total: Double = 0.0
    import scala.collection.JavaConversions._
    for (entry <- entries.entrySet) {
      total += entry.getValue
    }
    cacheTotal = total
    dirty = false
    return total
  }

  def getSortedKeys(): List[T] = {
    entries.keys.toList
  }

  /**
   * Finds the key with maximum count. This is a linear operation, and ties
   * are broken arbitrarily.
   *
   * @return a key with minumum count
   */
  def argMax(): T = {
    var maxCount: Double = Double.NegativeInfinity
    var maxKey: T = null.asInstanceOf[T] //???
    import scala.collection.JavaConversions._
    for (entry <- entries.entrySet) {
      if (entry.getValue > maxCount || maxKey == null) {
        maxKey = entry.getKey
        maxCount = entry.getValue
      }
    }
    maxKey
  }

  def min: Double = {
    return maxMinHelp(false)
  }

  def max: Double = {
    return maxMinHelp(true)
  }

  private def maxMinHelp(max: Boolean): Double = {
    var maxCount: Double = if (max) Double.NegativeInfinity else Double.PositiveInfinity
    import scala.collection.JavaConversions._
    for (entry <- entries.entrySet) {
      if ((max && entry.getValue > maxCount) || (!max && entry.getValue < maxCount)) {
        maxCount = entry.getValue
      }
    }
    return maxCount
  }

  /**
   * Returns a string representation with the keys ordered by decreasing
   * counts.
   *
   * @return string representation
   */
  override def toString: String = {
    toString(keySet.size)
  }

  def toStringSortedByKeys: String = {
    val sb: StringBuilder = new StringBuilder("[")
    val f: NumberFormat = NumberFormat.getInstance
    f.setMaximumFractionDigits(5)
    var numKeysPrinted: Int = 0
    import scala.collection.JavaConversions._
    for (element <- new java.util.TreeSet[T](keySet)) {
      sb.append(element.toString)
      sb.append(" : ")
      sb.append(f.format(getCount(element)))
      if (numKeysPrinted < size - 1) sb.append(", ")
      numKeysPrinted += 1
    }
    if (numKeysPrinted < size) sb.append("...")
    sb.append("]")
    return sb.toString
  }

  /**
   * Returns a string representation which includes no more than the
   * maxKeysToPrint elements with largest counts.
   *
   * @param maxKeysToPrint
   * @return partial string representation
   */
  def toString(maxKeysToPrint: Int): String = {
    //return asPriorityQueue.toString(maxKeysToPrint, false) TODO implement
    new String("TODO")
  }

  /**
   * Returns a string representation which includes no more than the
   * maxKeysToPrint elements with largest counts and optionally prints
   * one element per line.
   *
   * @param maxKeysToPrint
   * @return partial string representation
   */
  def toString(maxKeysToPrint: Int, multiline: Boolean): String = {
    //asPriorityQueue.toString(maxKeysToPrint, multiline) TODO implement
    new String("TODO")
  }



  def this(mf: MapFactory[T, Double]) {
    this()
    this.mapFactory = mf
    this.entries = mapFactory.buildMap
  }

  def this(identityHashMap: Boolean) {
    this(if (identityHashMap) new MapFactory.IdentityHashMapFactory[T,Double]
    else new HashMapFactory[T, Double])
  }


  def this(mapCounts: Map[_ <: T, Double]) {
    this()
    this.entries = new mutable.HashMap[T, Double]
    import scala.collection.JavaConversions._
    for (entry <- mapCounts.entrySet) {
      incrementCount(entry.getKey, entry.getValue)
    }
  }

  def this(counter: Counter[T]) {
    this()
    incrementAll(counter)
  }

  def this(collection: Iterable[_ <: T]) {
    this()
    incrementAll(collection, 1.0)
  }

  def pruneKeysBelowThreshold(cutoff: Double) {
    entries = entries.filter(kv => kv._2 < cutoff)
    dirty = true
  }

  def getEntrySet(): Set[(T, Double)] = {
    return entries.toSet
  }

  def isEqualTo(counter: Counter): Boolean = {
    var tmp: Boolean = true
    val bigger: Counter = if (counter.size > size) counter else this
    import scala.collection.JavaConversions._
    for (e <- bigger.keySet) {
      tmp &= counter.getCount(e) eq getCount(e)
    }
    return tmp
  }

  def clear {
    entries = mapFactory.buildMap
    dirty = true
  }

  /**
   * Builds a priority queue whose elements are the counter's elements, and
   * whose priorities are those elements' counts in the counter.
   */
//  def asPriorityQueue: PriorityQueue[T] = {
//    val pq: PriorityQueue[T] = new PriorityQueue[T](entries.size)
//    import scala.collection.JavaConversions._
//    for (entry <- entries.entrySet) {
//      pq.add(entry.getKey, entry.getValue)
//    }
//    return pq
//  }
  def asPriorityQueue() = {
    import scala.collection.immutable.ListMap
    // low to high
    ListMap(entries.toSeq.sortWith(_._2 < _._2):_*).toMap
  }

  /**
   * Warning: all priorities are the negative of their counts in the counter
   * here
   *
   * @return
   */
//  def asMinPriorityQueue: PriorityQueue[T] = {
//    val pq: PriorityQueue[T] = new PriorityQueue[T](entries.size)
//    import scala.collection.JavaConversions._
//    for (entry <- entries.entrySet) {
//      pq.add(entry.getKey, -entry.getValue)
//    }
//    return pq
//  }

  def asMinPriorityQueue() = {
    import scala.collection.immutable.ListMap
    //  high to low
    ListMap(entries.toSeq.sortWith(_._2 > _._2):_*).toMap
  }

  def keepTopNKeys(keepN: Int) {
    keepKeysHelper(keepN, true)
  }

  def keepBottomNKeys(keepN: Int) {
    keepKeysHelper(keepN, false)
  }

  private def keepKeysHelper(keepN: Int, top: Boolean) {
    val tmp: Counter[T] = new Counter[T]()
    var n: Int = 0
    val iter =  mutable.Iterable(if (top) asPriorityQueue else asMinPriorityQueue)
    iter.take(keepN) //TODO check this!
//    for (e <- mutable.Iterable(if (top) asPriorityQueue else asMinPriorityQueue)) {
//      if (n <= keepN) tmp.setCount(e, getCount(e))
//      n += 1
//    }
    clear
    incrementAll(tmp)
    dirty = true
  }

  /**
   * Sets all counts to the given value, but does not remove any keys
   */
  def setAllCounts(value: Double) {
    import scala.collection.JavaConversions._
    for (e <- keySet) {
      setCount(e, value)
    }
  }

  def dotProduct(other: Counter[T]): Double = {
    val thizCounts: List[Double] = getEntrySet().map(_._2).filter(_ != 0.0).toList
    val otherCounts:  List[Double] = other.getEntrySet().map(_._2).filter(_ != 0.0).toList
    thizCounts.zip(otherCounts).map(xy => xy._1 * xy._2).sum //TODO test
  }

  def scale(c: Double) {
    entries.map(_._2 * c)
  }

  def scaledClone(c: Double): Counter[T] = {
    val newCounter: Counter[T] = new Counter[T]()
    entrySet().map(kv => newCounter.setCount(kv._1, kv._2 * c))

    newCounter //TODO test
  }

  def difference(counter: Counter[T]): Counter[T] = {
    val clone: Counter[T] = new Counter[T](this)
    for (key <- counter.keySet) {
      val count: Double = counter.getCount(key)
      clone.incrementCount(key, -1 * count)
    }
    clone
  }

  def toLogSpace: Counter[T] = {
    val newCounter: Counter[T] = new Counter[T](this)
    import scala.collection.JavaConversions._
    for (key <- newCounter.keySet) {
      newCounter.setCount(key, Math.log(getCount(key)))
    }
    return newCounter
  }

  def approxEquals(other: Counter, tol: Double): Boolean = {
    import scala.collection.JavaConversions._
    for (key <- keySet) {
      if (Math.abs(getCount(key) - other.getCount(key)) > tol) return false
    }
    import scala.collection.JavaConversions._
    for (key <- other.keySet) {
      if (Math.abs(getCount(key) - other.getCount(key)) > tol) return false
    }
    return true
  }

  def setDirty(dirty: Boolean) {
    this.dirty = dirty
  }

  def toStringTabSeparated: String = {
    val sb: StringBuilder = new StringBuilder
    import scala.collection.JavaConversions._
    for (key <- getSortedKeys) {
      sb.append(key.toString + "\t" + getCount(key) + "\n")
    }
    return sb.toString
  }

  def canEqual(a: Any) = a.isInstanceOf[Counter[T]]

  override def equals(other: Any): Boolean = {
    other match {
      case that: Counter[T] => getClass().ne(that.getClass) &&
        dirty != that.dirty && that.cacheTotal != cacheTotal &&
        that.defaultValue != defaultValue &&
        !(if (entries != null) !(entries == that.entries) else that.entries != null)
      case _ => false
    } //TODO check this!
  }

  override def hashCode: Int = {
    var result: Int = 0
    var temp: Long = 0L
    result = if (entries != null) entries.hashCode else 0
    result = 31 * result + (if (dirty) 1 else 0)
    temp = java.lang.Double.doubleToLongBits(cacheTotal) //TODO doubleToLongBits???
    result = 31 * result + (temp ^ (temp >>> 32)).toInt
    result = 31 * result + (if (mapFactory != null) mapFactory.hashCode else 0)
    temp = java.lang.Double.doubleToLongBits(defaultValue) //TODO doubleToLongBits???
    result = 31 * result + (temp ^ (temp >>> 32)).toInt
    return result
  }
}
