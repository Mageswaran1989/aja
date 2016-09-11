/**
 *  Rewritter with sorted ListMap in Counter class
 */





//package org.dhira.nn.containers
//
//import java.io.Serializable
//import java.text.NumberFormat
//import java.util
//import scala.collection.JavaConversions._
//
//import org.aja.dhira.nn.containers.Counter
//
///**
// * Created by mageswaran on 10/9/16.
// */
///**
// * A priority queue based on a binary heap.  Note that this implementation does
// * not efficiently support containment, removal, or element promotion
// * (decreaseKey) -- these methods are therefore not yet implemented.  It is a maximum
// * priority queue, so next() gives the highest-priority object.
// *
// * @author Dan Klein
// */
//@SerialVersionUID(1L)
//object PriorityQueue {
//
////  def main(args: Array[String]) {
////    val pq: Nothing = new Nothing
////    println(pq)
////    pq.put("one", 1)
////    println(pq)
////    pq.put("three", 3)
////    println(pq)
////    pq.put("one", 1.1)
////    println(pq)
////    pq.put("two", 2)
////    println(pq)
////    System.out.println(pq.toString(2, false))
////    while (pq.hasNext) {
////      System.out.println(pq.next)
////    }
////  }
//}
//
//@SerialVersionUID(1L)
//class PriorityQueue[E] extends Iterator[E] with Serializable with Cloneable with PriorityQueueInterface[E] {
//  private[containers] var localSize: Int = 0
//  private[containers] var capacity: Int = 0
//  private[containers] var elements: List[E] = _ //Keys
//  private[containers] var priorities: Array[Double] = _ //Values or Counts
//
//  protected def grow(newCapacity: Int) {
//    val newElements: List[E] = new Array[E](newCapacity).toList
//    val newPriorities: Array[Double] = new Array[Double](newCapacity)
//    if (size > 0) {
//      newElements.addAll(elements)
//      System.arraycopy(priorities, 0, newPriorities, 0, priorities.length)
//    }
//    elements = newElements
//    priorities = newPriorities
//    capacity = newCapacity
//  }
//
//  protected def parent(loc: Int): Int = {
//    return (loc - 1) / 2
//  }
//
//  protected def leftChild(loc: Int): Int = {
//    return 2 * loc + 1
//  }
//
//  protected def rightChild(loc: Int): Int = {
//    return 2 * loc + 2
//  }
//
//  protected def heapifyUp(loc: Int) {
//    if (loc == 0) return
//    val parent: Int = parent(loc)
//    if (priorities(loc) > priorities(parent)) {
//      swap(loc, parent)
//      heapifyUp(parent)
//    }
//  }
//
//  protected def heapifyDown(loc: Int) {
//    var max: Int = loc
//    val leftChildValue: Int = leftChild(loc)
//    if (leftChildValue < size) {
//      val priority: Double = priorities(loc)
//      val leftChildPriority: Double = priorities(leftChildValue)
//      if (leftChildPriority > priority) max = leftChildValue
//      val rightChildValue: Int = rightChild(loc)
//      if (rightChildValue < size) {
//        val rightChildPriority: Double = priorities(rightChild(loc))
//        if (rightChildPriority > priority && rightChildPriority > leftChildPriority) max = rightChildValue
//      }
//    }
//    if (max == loc) return
//    swap(loc, max)
//    heapifyDown(max)
//  }
//
//  protected def swap(loc1: Int, loc2: Int) {
//    val tempPriority: Double = priorities(loc1)
//    val tempElement: E = elements(loc1)
//    priorities(loc1) = priorities(loc2)
//    elements = elements.updated(loc1, elements(loc2)) //TODO check
//    priorities(loc2) = tempPriority
//    elements = elements.updated(loc2, tempElement) //TODO check
//  }
//
//  protected def removeFirst {
//    if (size < 1) return
////    swap(0, size - 1) //swap
////    size -= 1
//    elements = elements.tail //remove the first item in the list
//    heapifyDown(0) //??? TODO check what for
//  }
//
//  def hasNext: Boolean = {
//    return !isEmpty
//  }
//
//  def next: E = {
//    val first: E = peek
//    removeFirst
//    return first
//  }
//
//  override def remove {
//    throw new UnsupportedOperationException
//  }
//
//  def peek: E = {
//    if (size > 0) return elements(0)
//    throw new NoSuchElementException
//  }
//
//  def getPriority: Double = {
//    if (size > 0) return priorities(0)
//    throw new NoSuchElementException
//  }
//
//  override def size(): Int = {
//    localSize
//  }
//
//  override def isEmpty: Boolean = {
//    size == 0
//  }
//
//  def add(key: E, priority: Double): Boolean = {
//    if (size == capacity) {
//      grow(2 * capacity + 1)
//    }
//    elements = key :: elements //add key TODO check
//    priorities(size) = priority
//    heapifyUp(size)
//    size += 1
//    return true
//  }
//
//  def put(key: E, priority: Double) {
//    add(key, priority)
//  }
//
//  /**
//   * Returns a representation of the queue in decreasing priority order.
//   */
//  override def toString: String = {
//    return toString(size, false)
//  }
//
//  /**
//   * Returns a representation of the queue in decreasing priority order,
//   * displaying at most maxKeysToPrint elements and optionally printing
//   * one element per line.
//   *
//   * @param maxKeysToPrint maximum number of keys to print
//   * @param multiline if is set to true, prints each element on new line. Prints elements in one line otherwise.
//   */
//  def toString(maxKeysToPrint: Int, multiline: Boolean): String = {
//    val pq: PriorityQueue[E] = clone
//    val sb: StringBuilder = new StringBuilder(if (multiline) "" else "[")
//    var numKeysPrinted: Int = 0
//    val f: NumberFormat = NumberFormat.getInstance
//    f.setMaximumFractionDigits(5)
//    while (numKeysPrinted < maxKeysToPrint && pq.hasNext) {
//      val priority: Double = pq.getPriority
//      val element: E = pq.next
//      sb.append(if (element == null) "null" else element.toString)
//      sb.append(" : ")
//      sb.append(f.format(priority))
//      if (numKeysPrinted < size - 1) sb.append(if (multiline) "\n" else ", ")
//      numKeysPrinted += 1
//    }
//    if (numKeysPrinted < size) sb.append("...")
//    if (!multiline) sb.append("]")
//    return sb.toString
//  }
//
//  /**
//   * Returns a counter whose keys are the elements in this priority queue, and
//   * whose counts are the priorities in this queue.  In the event there are
//   * multiple instances of the same element in the queue, the counter's count
//   * will be the sum of the instances' priorities.
//   *
//   * @return
//   */
//  def asCounter: Counter[E] = {
//    val pq: PriorityQueue[E] = clone
//    val counter: Counter[E] = new Counter[E]
//    while (pq.hasNext) {
//      val priority: Double = pq.getPriority
//      val element: E = pq.next
//      counter.incrementCount(element, priority)
//    }
//    counter
//  }
//
//  /**
//   * Returns a clone of this priority queue.  Modifications to one will not
//   * affect modifications to the other.
//   */
//  override def clone: PriorityQueue[E] = {
//    val clonePQ: PriorityQueue[E] = new PriorityQueue[E]
//    clonePQ.localSize = size
//    clonePQ.capacity = capacity
//    clonePQ.elements = new util.ArrayList[Any](capacity)
//    clonePQ.priorities = new Array[Double](capacity)
//    if (size > 0) {
//      clonePQ.elements.addAll(elements)
//      System.arraycopy(priorities, 0, clonePQ.priorities, 0, size)
//    }
//    return clonePQ
//  }
//
////  def this() {
////    this(15)
////  }
//
//  def this(capacity: Int) {
//    this()
//    var legalCapacity: Int = 0
//    while (legalCapacity < capacity) {
//      legalCapacity = 2 * legalCapacity + 1
//    }
//    grow(legalCapacity)
//  }
//}
