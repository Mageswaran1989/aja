/**
 *  Rewritter with sorted ListMap in Counter class
 */


//package org.dhira.nn.containers
//
///**
// * Created by mageswaran on 10/9/16.
// */
//trait PriorityQueueInterface[E] {
//  /**
//   * Returns true if the priority queue is non-empty
//   */
//  def hasNext: Boolean
//
//  /**
//   * Returns the element in the queue with highest priority, and pops it from
//   * the queue.
//   */
//  def next: E
//
//  /**
//   * Not supported -- next() already removes the head of the queue.
//   */
//  def remove
//
//  /**
//   * Returns the highest-priority element in the queue, but does not pop it.
//   */
//  def peek: E
//
//  /**
//   * Gets the priority of the highest-priority element of the queue.
//   */
//  def getPriority: Double
//
//  /**
//   * Number of elements in the queue.
//   */
//  def size: Int
//
//  /**
//   * True if the queue is empty (size == 0).
//   */
//  def isEmpty: Boolean
//
//  /**
//   * Adds a key to the queue with the given priority.  If the key is already in
//   * the queue, it will be added an additional time, NOT promoted/demoted.
//   *
//   * @param key
//   * @param priority
//   */
//  def put(key: E, priority: Double)
//}