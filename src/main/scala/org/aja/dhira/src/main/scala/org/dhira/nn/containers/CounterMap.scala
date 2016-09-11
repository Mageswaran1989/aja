package org.dhira.nn.containers

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{TimeUnit, ThreadPoolExecutor, ExecutorService}

import org.slf4j.{LoggerFactory, Logger}

/**
 * Created by mageswaran on 10/9/16.
 */
/**
 * Maintains counts of (key, value) pairs.  The map is structured so that for
 * every key, one can getFromOrigin a counter over values.  Example usage: keys might be
 * words with values being POS tags, and the count being the number of
 * occurences of that word/tag pair.  The sub-counters returned by
 * getCounter(word) would be count distributions over tags for that word.
 *
 * @author Dan Klein
 */
@SerialVersionUID(1L)
object CounterMap {
  private var log: Logger = LoggerFactory.getLogger(classOf[Nothing])

  trait CountFunction[V] {
    def count(v1: V, v2: V): Double
  }

  /**
   * Build a counter map by iterating pairwise over the list.
   * This assumes that the given pair wise items are
   * the same symmetrically. (The relation at i and i + 1 are the same)
   * It creates a counter map such that the pairs are:
   * count(v1,v2) and count(v2,v1) are the same
   * @param items the items to iterate over
   * @param countFunction the function to count
   * @param <V> the type to count
   * @return the counter map pairwise
   */
  def runPairWise[V](items: List[V], countFunction: CounterMap.CountFunction[V]): Nothing = {
    val exec: ExecutorService = new ThreadPoolExecutor(Runtime.getRuntime.availableProcessors, Runtime.getRuntime.availableProcessors, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable], new RejectedExecutionHandler() {
      def rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
        try {
          Thread.sleep(1000)
        }
        catch {
          case e: InterruptedException => {
            Thread.currentThread.interrupt
          }
        }
        executor.submit(r)
      }
    })
    val begin: AtomicInteger = new AtomicInteger(0)
    val end: AtomicInteger = new AtomicInteger(items.size - 1)
    val futures: List[Future[V]] = new ArrayList[Future[V]]
    val count: Nothing = parallelCounterMap
    {
      var i: Int = 0
      while (i < items.size / 2) {
        {
          futures.add(exec.submit(new Callable[V]() {
            @throws(classOf[Exception])
            def call: V = {
              val begin2: Int = begin.incrementAndGet
              val end2: Int = end.decrementAndGet
              val v: V = items.get(begin2)
              val v2: V = items.get(end2)
              log.trace("Processing " + "(" + begin2 + "," + end2 + ")")
              if (count.getCount(v, v2) > 0) return v
              val cost: Double = countFunction.count(v, v2)
              count.incrementCount(v, v2, cost)
              count.incrementCount(v2, v, cost)
              return v
            }
          }))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    var futureCount: Int = 0
    import scala.collection.JavaConversions._
    for (future <- futures) {
      try {
        future.get
        log.trace("Done with " + ({
          futureCount += 1; futureCount - 1
        }))
      }
      catch {
        case e: InterruptedException => {
          e.printStackTrace
        }
        case e: ExecutionException => {
          e.printStackTrace
        }
      }
    }
    exec.shutdown
    try {
      exec.awaitTermination(1, TimeUnit.MINUTES)
    }
    catch {
      case e: InterruptedException => {
        e.printStackTrace
      }
    }
    return count
  }

  /**
   * Returns a thread safe counter map
   * @return
   */
  def parallelCounterMap[K, V]: Nothing = {
    val factory: Nothing = new Nothing() {
      def buildMap: Map[K, Double] = {
        return new ConcurrentHashMap[K, Double]
      }
    }
    val totalWords: Nothing = new Nothing(factory, factory)
    return totalWords
  }

  def main(args: Array[String]) {
    val bigramCounterMap: Nothing = new Nothing
    bigramCounterMap.incrementCount("people", "run", 1)
    bigramCounterMap.incrementCount("cats", "growl", 2)
    bigramCounterMap.incrementCount("cats", "scamper", 3)
    System.out.println(bigramCounterMap)
    System.out.println("Entries for cats: " + bigramCounterMap.getCounter("cats"))
    System.out.println("Entries for dogs: " + bigramCounterMap.getCounter("dogs"))
    System.out.println("Count of cats scamper: " + bigramCounterMap.getCount("cats", "scamper"))
    System.out.println("Count of snakes slither: " + bigramCounterMap.getCount("snakes", "slither"))
    System.out.println("Total size: " + bigramCounterMap.totalSize)
    System.out.println("Total count: " + bigramCounterMap.totalCount)
    System.out.println(bigramCounterMap)
  }
}

@SerialVersionUID(1L)
class CounterMap[K, V] extends java.io.Serializable {
  private[berkeley] var mf: Nothing = null
  private[berkeley] var counterMap: Map[K, Nothing] = null
  private[berkeley] var defltVal: Double = 0.0

  protected def ensureCounter(key: K): Nothing = {
    var valueCounter: Nothing = counterMap.get(key)
    if (valueCounter == null) {
      valueCounter = buildCounter(mf)
      valueCounter.setDeflt(defltVal)
      counterMap.put(key, valueCounter)
    }
    return valueCounter
  }

  def getCounters: Collection[Nothing] = {
    return counterMap.values
  }

  /**
   * @return
   */
  protected def buildCounter(mf: Nothing): Nothing = {
    return new Nothing(mf)
  }

  /**
   * Returns the keys that have been inserted into this CounterMap.
   */
  def keySet: Set[K] = {
    return counterMap.keySet
  }

  /**
   * Sets the count for a particular (key, value) pair.
   */
  def setCount(key: K, value: V, count: Double) {
    val valueCounter: Nothing = ensureCounter(key)
    valueCounter.setCount(value, count)
  }

  /**
   * Increments the count for a particular (key, value) pair.
   */
  def incrementCount(key: K, value: V, count: Double) {
    val valueCounter: Nothing = ensureCounter(key)
    valueCounter.incrementCount(value, count)
  }

  /**
   * Gets the count of the given (key, value) entry, or zero if that entry is
   * not present.  Does not createComplex any objects.
   */
  def getCount(key: K, value: V): Double = {
    val valueCounter: Nothing = counterMap.get(key)
    if (valueCounter == null) return defltVal
    return valueCounter.getCount(value)
  }

  /**
   * Gets the sub-counter for the given key.  If there is none, a counter is
   * created for that key, and installed in the CounterMap.  You can, for
   * example, add to the returned empty counter directly (though you shouldn't).
   * This is so whether the key is present or not, modifying the returned
   * counter has the same effect (but don't do it).
   */
  def getCounter(key: K): Nothing = {
    return ensureCounter(key)
  }

  def incrementAll(map: Map[K, V], count: Double) {
    import scala.collection.JavaConversions._
    for (entry <- map.entrySet) {
      incrementCount(entry.getKey, entry.getValue, count)
    }
  }

  def incrementAll(cMap: Nothing) {
    import scala.collection.JavaConversions._
    for (entry <- cMap.counterMap.entrySet) {
      val key: K = entry.getKey
      val innerCounter: Nothing = entry.getValue
      import scala.collection.JavaConversions._
      for (innerEntry <- innerCounter.entrySet) {
        val value: V = innerEntry.getKey
        incrementCount(key, value, innerEntry.getValue)
      }
    }
  }

  /**
   * Gets the total count of the given key, or zero if that key is
   * not present.  Does not createComplex any objects.
   */
  def getCount(key: K): Double = {
    val valueCounter: Nothing = counterMap.get(key)
    if (valueCounter == null) return 0.0
    return valueCounter.totalCount
  }

  /**
   * Returns the total of all counts in sub-counters.  This implementation is
   * linear; it recalculates the total each time.
   */
  def totalCount: Double = {
    var total: Double = 0.0
    import scala.collection.JavaConversions._
    for (entry <- counterMap.entrySet) {
      val counter: Nothing = entry.getValue
      total += counter.totalCount
    }
    return total
  }

  /**
   * Returns the total number of (key, value) entries in the CounterMap (not
   * their total counts).
   */
  def totalSize: Int = {
    var total: Int = 0
    import scala.collection.JavaConversions._
    for (entry <- counterMap.entrySet) {
      val counter: Nothing = entry.getValue
      total += counter.size
    }
    return total
  }

  /**
   * The number of keys in this CounterMap (not the number of key-value entries
   * -- use totalSize() for that)
   */
  def size: Int = {
    return counterMap.size
  }

  /**
   * True if there are no entries in the CounterMap (false does not mean
   * totalCount > 0)
   */
  def isEmpty: Boolean = {
    return size == 0
  }

  /**
   * Finds the key with maximum count.  This is a linear operation, and ties are broken arbitrarily.
   *
   * @return a key with minumum count
   */
  def argMax: Nothing = {
    var maxCount: Double = Double.NEGATIVE_INFINITY
    var maxKey: Nothing = null
    import scala.collection.JavaConversions._
    for (entry <- counterMap.entrySet) {
      val counter: Nothing = entry.getValue
      val localMax: V = counter.argMax
      if (counter.getCount(localMax) > maxCount || maxKey == null) {
        maxKey = new Nothing(entry.getKey, localMax)
        maxCount = counter.getCount(localMax)
      }
    }
    return maxKey
  }

  def toString(maxValsPerKey: Int): String = {
    val sb: StringBuilder = new StringBuilder("[\n")
    import scala.collection.JavaConversions._
    for (entry <- counterMap.entrySet) {
      sb.append("  ")
      sb.append(entry.getKey)
      sb.append(" -> ")
      sb.append(entry.getValue.toString(maxValsPerKey))
      sb.append("\n")
    }
    sb.append("]")
    return sb.toString
  }

  override def toString: String = {
    return toString(20)
  }

  def toString(keyFilter: Collection[String]): String = {
    val sb: StringBuilder = new StringBuilder("[\n")
    import scala.collection.JavaConversions._
    for (entry <- counterMap.entrySet) {
      val keyString: String = entry.getKey.toString
      if (keyFilter != null && !keyFilter.contains(keyString)) {
        continue //todo: continue is not supported
      }
      sb.append("  ")
      sb.append(keyString)
      sb.append(" -> ")
      sb.append(entry.getValue.toString(20))
      sb.append("\n")
    }
    sb.append("]")
    return sb.toString
  }

  def this(cm: Nothing) {
    this()
    `this`
    incrementAll(cm)
  }

  def this() {
    this()
    `this`(false)
  }

  def isEqualTo(map: Nothing): Boolean = {
    var tmp: Boolean = true
    val bigger: Nothing = if (map.size > size) map else this
    import scala.collection.JavaConversions._
    for (k <- bigger.keySet) {
      tmp &= map.getCounter(k).isEqualTo(getCounter(k))
    }
    return tmp
  }

  def this(outerMF: Nothing, innerMF: Nothing) {
    this()
    mf = innerMF
    counterMap = outerMF.buildMap
  }

  def this(identityHashMap: Boolean) {
    this()
    `this`(if (identityHashMap) new Nothing else new Nothing, if (identityHashMap) new Nothing else new Nothing)
  }

  def normalize {
    import scala.collection.JavaConversions._
    for (key <- keySet) {
      getCounter(key).normalize
    }
  }

  def normalizeWithDiscount(discount: Double) {
    import scala.collection.JavaConversions._
    for (key <- keySet) {
      val ctr: Nothing = getCounter(key)
      val totalCount: Double = ctr.totalCount
      import scala.collection.JavaConversions._
      for (value <- ctr.keySet) {
        ctr.setCount(value, (ctr.getCount(value) - discount) / totalCount)
      }
    }
  }

  /**
   * Constructs reverse CounterMap where the count of a pair (k,v)
   * is the count of (v,k) in the current CounterMap
   * @return
   */
  def invert: Nothing = {
    val invertCounterMap: Nothing = new Nothing
    import scala.collection.JavaConversions._
    for (key <- this.keySet) {
      val keyCounts: Nothing = this.getCounter(key)
      import scala.collection.JavaConversions._
      for (`val` <- keyCounts.keySet) {
        val count: Double = keyCounts.getCount(`val`)
        invertCounterMap.setCount(`val`, key, count)
      }
    }
    return invertCounterMap
  }

  /**
   * Scale all entries in <code>CounterMap</code>
   * by <code>scaleFactor</code>
   * @param scaleFactor
   */
  def scale(scaleFactor: Double) {
    import scala.collection.JavaConversions._
    for (key <- keySet) {
      val counts: Nothing = getCounter(key)
      counts.scale(scaleFactor)
    }
  }

  def containsKey(key: K): Boolean = {
    return counterMap.containsKey(key)
  }

  def getPairIterator: Iterator[Nothing] = {
    class PairIterator extends Iterator[Nothing] {
      private[berkeley] var outerIt: Iterator[K] = null
      private[berkeley] var innerIt: Iterator[V] = null
      private[berkeley] var curKey: K = null

      def this() {
        this()
        outerIt = keySet.iterator
      }

      private def advance: Boolean = {
        if (innerIt == null || !innerIt.hasNext) {
          if (!outerIt.hasNext) {
            return false
          }
          curKey = outerIt.next
          innerIt = getCounter(curKey).keySet.iterator
        }
        return true
      }

      def hasNext: Boolean = {
        return advance
      }

      def next: Nothing = {
        advance
        assert(curKey != null)
        return Pair.newPair(curKey, innerIt.next)
      }

      override def remove {
      }
    }
    return new PairIterator
  }

  def getEntrySet: Set[Map.Entry[K, Nothing]] = {
    return counterMap.entrySet
  }

  def removeKey(oldIndex: K) {
    counterMap.remove(oldIndex)
  }

  def setCounter(newIndex: K, counter: Nothing) {
    counterMap.put(newIndex, counter)
  }

  def setDefault(defltVal: Double) {
    this.defltVal = defltVal
    import scala.collection.JavaConversions._
    for (vCounter <- counterMap.values) {
      vCounter.setDeflt(defltVal)
    }
  }
}

