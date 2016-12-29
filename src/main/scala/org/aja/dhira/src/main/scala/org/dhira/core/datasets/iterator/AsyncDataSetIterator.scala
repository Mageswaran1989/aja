package org.dhira.core.datasets.iterator

import org.nd4j.linalg.api.ops.executioner.GridExecutioner
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.DataSetPreProcessor
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.ConcurrentModificationException
import java.util.List
import java.util.NoSuchElementException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * AsyncDataSetIterator takes an existing DataSetIterator and loads one or more DataSet objects
 * from it using a separate thread.
 * For data sets where DataSetIterator.next() is long running (limited by disk read or processing time
 * for example) this may improve performance by loading the next DataSet asynchronously (i.e., while
 * training is continuing on the previous DataSet). Obviously this may use additional memory.<br>
 * Note however that due to asynchronous loading of data, next(int) is not supported.
 * <p>
 * PLEASE NOTE: If used together with CUDA backend, please use it with caution.
 *
 * @author Alex Black
 * @author raver119@gmail.com
 */
object AsyncDataSetIterator {
  protected val logger: Logger = LoggerFactory.getLogger(classOf[AsyncDataSetIterator])
}

class AsyncDataSetIterator extends DataSetIterator {
  private final var baseIterator: DataSetIterator = null
  private final var blockingQueue: BlockingQueue[DataSet] = null
  private var thread: Thread = null
  private var runnable: IteratorRunnable = null

  /**
   * Create an AsyncDataSetIterator with a specified queue size.
   *
   * @param baseIterator The DataSetIterator to load data from asynchronously
   * @param queueSize    size of the queue (max number of elements to load into queue)
   */
  def this(baseIterator: DataSetIterator, queueSize: Int) {
    this()
    if (queueSize <= 0) throw new IllegalArgumentException("Queue size must be > 0")
    val defaultSize = if (queueSize < 2) 2 else queueSize
    this.baseIterator = baseIterator
    if (this.baseIterator.resetSupported) this.baseIterator.reset
    blockingQueue = new LinkedBlockingDeque[DataSet](defaultSize)
    runnable = new IteratorRunnable(baseIterator.hasNext)
    thread = new Thread(runnable)
    val deviceId: Integer = Nd4j.getAffinityManager.getDeviceForCurrentThread
    Nd4j.getAffinityManager.attachThreadToDevice(thread, deviceId)
    thread.setDaemon(true)
    thread.start
  }

  /**
   * Create an AsyncDataSetIterator with a queue size of 1 (i.e., only load a
   * single additional DataSet)
   *
   * @param baseIterator The DataSetIterator to load data from asynchronously
   */
  def this(baseIterator: DataSetIterator) {
    this(baseIterator, 8)
  }



  def next(num: Int): DataSet = {
    throw new UnsupportedOperationException("Next(int) not supported for AsyncDataSetIterator")
  }

  def totalExamples: Int = {
    baseIterator.totalExamples
  }

  def inputColumns: Int = {
    baseIterator.inputColumns
  }

  def totalOutcomes: Int = {
    baseIterator.totalOutcomes
  }

  def resetSupported: Boolean = {
    baseIterator.resetSupported
  }

  def asyncSupported: Boolean = {
    false
  }

  def reset {
    if (!resetSupported) throw new UnsupportedOperationException("Cannot reset Async iterator wrapping iterator that does not support reset")
    runnable.killRunnable = true
    if (runnable.isAlive.get) {
      thread.interrupt
    }
    try {
      runnable.runCompletedSemaphore.tryAcquire(5, TimeUnit.SECONDS)
    }
    catch {
      case e: InterruptedException => {
      }
    }
    blockingQueue.clear
    baseIterator.reset
    runnable = new AsyncDataSetIterator#IteratorRunnable(baseIterator.hasNext)
    thread = new Thread(runnable)
    val deviceId: Integer = Nd4j.getAffinityManager.getDeviceForCurrentThread
    Nd4j.getAffinityManager.attachThreadToDevice(thread, deviceId)
    thread.setDaemon(true)
    thread.start
  }

  def batch: Int = {
    return baseIterator.batch
  }

  def cursor: Int = {
    return baseIterator.cursor
  }

  def numExamples: Int = {
    return baseIterator.numExamples
  }

  def setPreProcessor(preProcessor: DataSetPreProcessor) {
    baseIterator.setPreProcessor(preProcessor)
  }

  def getPreProcessor: DataSetPreProcessor = {
    return baseIterator.getPreProcessor
  }

  def getLabels: List[String] = {
    return baseIterator.getLabels
  }

  def hasNext: Boolean = {
    if (!blockingQueue.isEmpty) {
      return true
    }
    if (runnable.isAlive.get) {
      return runnable.hasLatch
    }
    else {
      if (!runnable.killRunnable && runnable.exception != null) {
        throw runnable.exception
      }
      return runnable.hasLatch
    }
  }

  def next: DataSet = {
    if (!hasNext) {
      throw new NoSuchElementException
    }
    if (runnable.exception != null) {
      throw runnable.exception
    }
    if (!blockingQueue.isEmpty) {
      runnable.feeder.decrementAndGet
      return blockingQueue.poll
    }
    try {
      while (runnable.exception == null) {
        val ds: DataSet = blockingQueue.poll(2, TimeUnit.SECONDS)
        if (ds != null) {
          runnable.feeder.decrementAndGet
          return ds
        }
        if (runnable.killRunnable) {
          throw new ConcurrentModificationException("Reset while next() is waiting for element?")
        }
        if (!runnable.isAlive.get && blockingQueue.isEmpty) {
          if (runnable.exception != null) throw new RuntimeException("Exception thrown in base iterator", runnable.exception)
          throw new IllegalStateException("Unexpected state occurred for AsyncDataSetIterator: runnable died or no data available")
        }
      }
      throw runnable.exception
    }
    catch {
      case e: InterruptedException => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Shut down the async data set iterator thread
   * This is not typically necessary if using a single AsyncDataSetIterator
   * (thread is a daemon thread and so shouldn't block the JVM from exiting)
   * Behaviour of next(), hasNext() etc methods after shutdown of async iterator is undefined
   */
  def shutdown {
    if (thread.isAlive) {
      runnable.killRunnable = true
      thread.interrupt
    }
  }

  private[AsyncDataSetIterator] class IteratorRunnable extends Runnable {
    //>>> TODO see how to make following variables as private
    @volatile
    var killRunnable: Boolean = false
    @volatile
    var isAlive: AtomicBoolean = new AtomicBoolean(true)
    @volatile
    var exception: RuntimeException = null
    var runCompletedSemaphore: Semaphore = new Semaphore(0)
    var lock: ReentrantReadWriteLock = new ReentrantReadWriteLock
    var feeder: AtomicLong = new AtomicLong(0)
    //<<<

    def this(hasNext: Boolean) {
      this()
      this.isAlive.set(hasNext)
    }

    def hasLatch: Boolean = {
      if (feeder.get > 0 || !blockingQueue.isEmpty) return true
      try {
        lock.readLock.lock
        var result: Boolean = baseIterator.hasNext || feeder.get != 0 || !blockingQueue.isEmpty
        if (!isAlive.get) return result
        else while (isAlive.get) {
          result = feeder.get != 0 || !blockingQueue.isEmpty || baseIterator.hasNext
          if (result) return true
        }
        return result
      } finally {
        lock.readLock.unlock
      }
    }

    def run {
      try {
        while (!killRunnable && baseIterator.hasNext) {
          feeder.incrementAndGet
          lock.writeLock.lock
          val ds: DataSet = baseIterator.next
          if (Nd4j.getExecutioner.isInstanceOf[Nothing]) (Nd4j.getExecutioner.asInstanceOf[Nothing]).flushQueueBlocking
          lock.writeLock.unlock
          blockingQueue.put(ds)
        }
        isAlive.set(false)
      }
      catch {
        case e: InterruptedException => {
          if (killRunnable) {
            return
          }
          else exception = new RuntimeException("Runnable interrupted unexpectedly", e)
        }
        case e: RuntimeException => {
          exception = e
          if (lock.writeLock.isHeldByCurrentThread) {
            lock.writeLock.unlock
          }
        }
      } finally {
        isAlive.set(false)
        runCompletedSemaphore.release
      }
    }
  }

  override def remove {
  }
}