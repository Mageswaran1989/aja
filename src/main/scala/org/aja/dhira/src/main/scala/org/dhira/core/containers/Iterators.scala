/**
 * Was used in keepKeysHelper() in Counter class which was replaced with take()
 */

//package org.dhira.nn.containers
//
//import java.util.{Arrays, Iterator}
//
///**
// * Created by mageswaran on 10/9/16.
// */
//object Iterators {
//  def fillList[T](it: Iterator[_ <: T], lst: List[T]) {
//    while (it.hasNext) {
//      lst.add(it.next)
//    }
//  }
//
//  def fillList[T](it: Iterator[_ <: T]): List[T] = {
//    val lst: List[T] = new ArrayList[T]
//    fillList(it, lst)
//    return lst
//  }
//
//  /**
//   * WraTps a base iterator with a transformation function.
//   */
//  abstract class Transform[S, T] extends Iterator[T] {
//    private var base: Iterator[S] = null
//
//    def this(base: Iterator[S]) {
//      this()
//      this.base = base
//    }
//
//    def hasNext: Boolean = {
//      return base.hasNext
//    }
//
//    def next: T = {
//      return transform(base.next)
//    }
//
//    protected def transform(next: S): T
//
//    override def remove {
//      base.remove
//    }
//  }
//
//  /**
//   * Wraps an iterator as an iterable
//   *
//   * @param <T>
//   * @param it
//   * @return
//   */
//  def newIterable[T](it: Iterator[T]): Iterable[T] = {
//    return new Iterable[T]() {
//      def iterator: Iterator[T] = {
//        return it
//      }
//    }
//  }
//
//  /**
//   * Wraps an iterator as an iterable
//   *
//   * @param <T>
//   * @param it
//   * @return
//   */
//  def able[T](it: Iterator[T]): Iterable[T] = {
//    return new Iterable[T]() {
//      private[berkeley] var used: Boolean = false
//
//      def iterator: Iterator[T] = {
//        if (used) throw new RuntimeException("One use iterable")
//        used = true
//        return it
//      }
//    }
//  }
//
//  /**
//   * Executes calls to next() in a different thread
//   *
//   * @param <T>
//   * @param base
//   * @param numThreads
//   * @return
//   */
//  def thread[T](base: Iterator[T]): Iterator[T] = {
//    return new Iterator[T]() {
//      private[berkeley] var els: ArrayBlockingQueue[T] = new ArrayBlockingQueue[T](2)
//      private var finishedLoading: Boolean = false
//      private var running: Boolean = false
//      private[berkeley] var thread: Thread = new Thread(new Runnable() {
//        def run {
//          while (base.hasNext) {
//            try {
//              els.put(base.next)
//            }
//            catch {
//              case e: InterruptedException => {
//                throw new RuntimeException(e)
//              }
//            }
//          }
//          finishedLoading = true
//        }
//      })
//
//      def hasNext: Boolean = {
//        return !(finishedLoading && els.isEmpty)
//      }
//
//      def next: T = {
//        if (!running) thread.start
//        running = true
//        try {
//          return els.take
//        }
//        catch {
//          case e: InterruptedException => {
//            throw new RuntimeException(e)
//          }
//        }
//      }
//
//      override def remove {
//        throw new UnsupportedOperationException
//      }
//    }
//  }
//
//  def zip[S, T](s: Iterator[S], t: Iterator[T]): Iterator[Nothing] = {
//    return new Iterator[Nothing]() {
//      def hasNext: Boolean = {
//        return s.hasNext && t.hasNext
//      }
//
//      def next: Nothing = {
//        return Pair.newPair(s.next, t.next)
//      }
//
//      override def remove {
//        throw new UnsupportedOperationException
//      }
//    }
//  }
//
//  /**
//   * Provides a max number of elements for an underlying base iterator.
//   */
//  def maxLengthIterator[T](base: Iterator[T], max: Int): Iterator[T] = {
//    return new Iterator[T]() {
//      private[berkeley] var count: Int = 0
//
//      def hasNext: Boolean = {
//        return base.hasNext && count < max
//      }
//
//      def next: T = {
//        if (!hasNext) throw new NoSuchElementException("No more elements")
//        count += 1
//        return base.next
//      }
//
//      override def remove {
//        throw new UnsupportedOperationException
//      }
//    }
//  }
//
//  /**
//   * Wraps a two-level iteration scenario in an iterator. Each key of the keys
//   * iterator returns an iterator (via the factory) over T's.
//   *
//   * The IteratorIterator loops through the iterator associated with each key
//   * until all the keys are used up.
//   */
//  class IteratorIterator[T] extends Iterator[T] {
//    private[berkeley] var current: Iterator[T] = null
//    private[berkeley] var keys: Iterator[_] = null
//    private[berkeley] var iterFactory: Nothing = null
//
//    def this(keys: Iterator[_], iterFactory: Nothing) {
//      this()
//      this.keys = keys
//      this.iterFactory = iterFactory
//      current = getNextIterator
//    }
//
//    private def getNextIterator: Iterator[T] = {
//      var next: Iterator[T] = null
//      while (next == null) {
//        if (!keys.hasNext) break //todo: break is not supported
//        next = iterFactory.newInstance(keys.next)
//        if (!next.hasNext) next = null
//      }
//      return next
//    }
//
//    def hasNext: Boolean = {
//      return current != null
//    }
//
//    def next: T = {
//      val next: T = current.next
//      if (!current.hasNext) current = getNextIterator
//      return next
//    }
//
//    override def remove {
//      throw new UnsupportedOperationException
//    }
//  }
//
//  /**
//   * Creates an iterator that only returns items of a base iterator that pass
//   * a filter.
//   *
//   * Null items cannot be returned from the base iterator.
//   */
//  class FilteredIterator[T] extends Iterator[T] {
//    private[berkeley] var filter: Nothing = null
//    private[berkeley] var next: T = null
//    private var base: Iterator[T] = null
//
//    def this(filter: Nothing, base: Iterator[T]) {
//      this()
//      `super`
//      this.filter = filter
//      this.base = base
//      loadNext
//    }
//
//    def this(filter: Nothing, items: Iterable[T]) {
//      this()
//      `this`(filter, items.iterator)
//    }
//
//    private def loadNext {
//      next = null
//      while (next == null && base.hasNext) {
//        next = base.next
//        if (!filter.accept(next)) next = null
//      }
//    }
//
//    def hasNext: Boolean = {
//      return next != null
//    }
//
//    def next: T = {
//      val old: T = next
//      loadNext
//      return old
//    }
//
//    override def remove {
//      throw new UnsupportedOperationException
//    }
//  }
//
//  class TransformingIterator[I, O] extends Iterator[O] {
//    private var transformer: Nothing = null
//    private var inputIterator: Iterator[I] = null
//
//    def this(inputIterator: Iterator[I], transformer: Nothing) {
//      this()
//      this.inputIterator = inputIterator
//      this.transformer = transformer
//    }
//
//    def hasNext: Boolean = {
//      return inputIterator.hasNext
//    }
//
//    def next: O = {
//      return transformer.call(inputIterator.next)
//    }
//
//    override def remove {
//      inputIterator.remove
//    }
//  }
//
//  def filter[T](iterator: Iterator[T], filter: Nothing): Iterator[T] = {
//    return new Iterators1.FilteredIterator[T](filter, iterator)
//  }
//
//  def concat[T](args: Iterable[Iterator[_ <: T]]): Iterator[T] = {
//    val factory: Nothing = new Nothing() {
//      def newInstance(args: AnyRef*): Iterator[T] = {
//        return args(0).asInstanceOf[Iterator[T]]
//      }
//    }
//    return new Iterators1.IteratorIterator[T](Arrays.asList(args).iterator, factory)
//  }
//
//  def concat[T](args: Iterator[_ <: T]*): Iterator[T] = {
//    val factory: Nothing = new Nothing() {
//      def newInstance(args: AnyRef*): Iterator[T] = {
//        return args(0).asInstanceOf[Iterator[T]]
//      }
//    }
//    return new Iterators1.IteratorIterator[T](Arrays.asList(args).iterator, factory)
//  }
//
//  def oneItemIterator[U](item: U): Iterator[U] = {
//    return new Iterator[U]() {
//      private[berkeley] var unused: Boolean = true
//
//      def hasNext: Boolean = {
//        return unused
//      }
//
//      def next: U = {
//        unused = false
//        return item
//      }
//
//      override def remove {
//        throw new UnsupportedOperationException
//      }
//    }
//  }
//
//  def emptyIterator: Iterator[_] = {
//    return new Iterator[_]() {
//      def hasNext: Boolean = {
//        return false
//      }
//
//      def next: AnyRef = {
//        throw new NoSuchElementException
//      }
//
//      override def remove {
//        throw new UnsupportedOperationException
//      }
//    }
//  }
//
//  def concat[T](a: Iterable[T], b: Iterable[T]): Iterable[T] = {
//    return able(concat(a.iterator, b.iterator))
//  }
//
//  def nextList[T](iterators: List[Iterator[T]]): List[T] = {
//    val items: List[T] = new ArrayList[T](iterators.size)
//    import scala.collection.JavaConversions._
//    for (iter <- iterators) {
//      items.add(iter.next)
//    }
//    return items
//  }
//
//  def objectIterator(instream: ObjectInputStream): Iterator[AnyRef] = {
//    return new Iterator[AnyRef]() {
//      private[berkeley] var next: AnyRef = softRead
//
//      def hasNext: Boolean = {
//        return next != null
//      }
//
//      private def softRead: AnyRef = {
//        try {
//          return instream.readObject
//        }
//        catch {
//          case e: IOException => {
//            return null
//          }
//          case e: ClassNotFoundException => {
//            return null
//          }
//        }
//      }
//
//      def next: AnyRef = {
//        val curr: AnyRef = next
//        next = softRead
//        return curr
//      }
//
//      override def remove {
//        throw new UnsupportedOperationException
//      }
//    }
//  }
//}
//
//class Iterators {
//  private def this() {
//    this()
//  }
//}
