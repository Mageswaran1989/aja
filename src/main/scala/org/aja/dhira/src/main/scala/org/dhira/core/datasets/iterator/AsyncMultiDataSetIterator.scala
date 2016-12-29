package org.deeplearning4j.datasets.iterator

import org.nd4j.linalg.api.ops.executioner.GridExecutioner
import org.nd4j.linalg.dataset.api.MultiDataSet
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator
import org.nd4j.linalg.factory.Nd4j
import java.util.ConcurrentModificationException
import java.util.NoSuchElementException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * Async prefetching iterator wrapper for MultiDataSetIterator implementations
 * <p>
 * PLEASE NOTE: If used together with CUDA backend, please use it with caution.
 *
 * @author Alex Black
 * @author raver119@gmail.com
 */
class AsyncMultiDataSetIterator extends MultiDataSetIterator {
  private final var iterator: MultiDataSetIterator = null
  private final var queue: LinkedBlockingQueue[MultiDataSet] = null
  private var runnable: IteratorRunnable = null
  private var thread: Thread = null

  def this(iterator: MultiDataSetIterator, queueLength: Int) {
    this()
    if (queueLength <= 0) throw new IllegalArgumentException("Queue size must be > 0")
    val defaultSize = if (queueLength < 2) 2 else queueLength
    this.iterator = iterator
    if (this.iterator.resetSupported) this.iterator.reset
    this.queue = new LinkedBlockingQueue[MultiDataSet](defaultSize)
    runnable = new IteratorRunnable(iterator.hasNext)
    thread = new Thread(runnable)
    val deviceId: Integer = Nd4j.getAffinityManager.getDeviceForCurrentThread
    Nd4j.getAffinityManager.attachThreadToDevice(thread, deviceId)
    thread.setDaemon(true)
    thread.start
  }

  def next(num: Int): MultiDataSet = {
    throw new UnsupportedOperationException("Next(int) not supported for AsyncDataSetIterator")
  }

  def setPreProcessor(preProcessor: MultiDataSetPreProcessor) {
    iterator.setPreProcessor(preProcessor)
  }

  def resetSupported: Boolean = {
    return iterator.resetSupported
  }

  def asyncSupported: Boolean = {
    return false
  }

  def reset {
    if (!resetSupported) throw new UnsupportedOperationException("Cannot reset Async iterator wrapping iterator that does not support reset")
    runnable.killRunnable = true
    if (runnable.isAlive) {
      thread.interrupt
    }
    try {
      runnable.runCompletedSemaphore.tryAcquire(5, TimeUnit.SECONDS)
    }
    catch {
      case e: InterruptedException => {
      }
    }
    queue.clear
    iterator.reset
    runnable = new AsyncMultiDataSetIterator#IteratorRunnable(iterator.hasNext)
    thread = new Thread(runnable)
    val deviceId: Integer = Nd4j.getAffinityManager.getDeviceForCurrentThread
    Nd4j.getAffinityManager.attachThreadToDevice(thread, deviceId)
    thread.setDaemon(true)
    thread.start
  }

  def hasNext: Boolean = {
    if (!queue.isEmpty) return true
    if (runnable.isAlive) {
      return runnable.hasLatch
    }
    else {
      if (!runnable.killRunnable && runnable.exception != null) {
        throw runnable.exception
      }
      return runnable.hasLatch
    }
  }

  def next: MultiDataSet = {
    if (!hasNext) {
      throw new NoSuchElementException
    }
    if (runnable.exception != null) {
      throw runnable.exception
    }
    if (!queue.isEmpty) {
      runnable.feeder.decrementAndGet
      return queue.poll
    }
    try {
      while (runnable.exception == null) {
        val ds: MultiDataSet = queue.poll(5, TimeUnit.SECONDS)
        if (ds != null) {
          runnable.feeder.decrementAndGet
          return ds
        }
        if (runnable.killRunnable) {
          throw new ConcurrentModificationException("Reset while next() is waiting for element?")
        }
        if (!runnable.isAlive && queue.isEmpty) {
          if (runnable.exception != null) throw new RuntimeException("Exception thrown in base iterator", runnable.exception)
          throw new IllegalStateException("Unexpected state occurred for AsyncMultiDataSetIterator: runnable died or no data available")
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

  override def remove {
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

  private[AsyncMultiDataSetIterator] class IteratorRunnable extends Runnable {
    @volatile
    var killRunnable: Boolean = false
    @volatile
    var isAlive: Boolean = true
    @volatile
    var exception: RuntimeException = null
    var runCompletedSemaphore: Semaphore = new Semaphore(0)
    var lock: ReentrantReadWriteLock = new ReentrantReadWriteLock
    var feeder: AtomicLong = new AtomicLong(0)

    def this(hasNext: Boolean) {
      this()
      this.isAlive = hasNext
    }

    def hasLatch: Boolean = {
      if (feeder.get > 0 || !queue.isEmpty) return true
      try {
        lock.readLock.lock
        var result: Boolean = iterator.hasNext || feeder.get != 0 || !queue.isEmpty
        if (!isAlive) return result
        else while (isAlive) {
          result = feeder.get != 0 || !queue.isEmpty || iterator.hasNext
          if (result) return true
        }
        return result
      } finally {
        lock.readLock.unlock
      }
    }

    def run {
      try {
        while (!killRunnable && iterator.hasNext) {
          feeder.incrementAndGet
          lock.writeLock.lock
          val ds: MultiDataSet = iterator.next
          if (Nd4j.getExecutioner.isInstanceOf[Nothing]) (Nd4j.getExecutioner.asInstanceOf[Nothing]).flushQueueBlocking
          lock.writeLock.unlock
          queue.put(ds)
        }
        isAlive = false
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
        isAlive = false
        runCompletedSemaphore.release
      }
    }
  }

}