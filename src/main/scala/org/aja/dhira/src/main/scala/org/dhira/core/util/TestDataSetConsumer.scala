package org.dhira.core.util

import lombok.NonNull
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * Class that consumes DataSets with specified delays, suitable for testing
 *
 * @author raver119@gmail.com
 */
object TestDataSetConsumer {
  protected val logger: Logger = LoggerFactory.getLogger(classOf[TestDataSetConsumer])
}

class TestDataSetConsumer {
  private var iterator: DataSetIterator = null
  private var delay: Long = 0L
  private var count: AtomicLong = new AtomicLong(0)

  def this(delay: Long) {
    this()
    this.delay = delay
  }

  def this(@NonNull iterator: DataSetIterator, delay: Long) {
    this()
    this.iterator = iterator
    this.delay = delay
  }

  /**
   * This method cycles through iterator, whie iterator.hasNext() returns true. After each cycle execution time is simulated either using Thread.sleep() or empty cycle
   *
   * @param consumeWithSleep
   * @return
   */
  def consumeWhileHasNext(consumeWithSleep: Boolean): Long = {
    if (iterator == null) throw new RuntimeException("Can't use consumeWhileHasNext() if iterator isn't set")
    while (iterator.hasNext) {
      val ds: DataSet = iterator.next
      this.consumeOnce(ds, consumeWithSleep)
    }
    return count.get
  }

  /**
   * This method consumes single DataSet, and spends delay time simulating execution of this dataset
   *
   * @param dataSet
   * @param consumeWithSleep
   * @return
   */
  def consumeOnce(@NonNull dataSet: DataSet, consumeWithSleep: Boolean): Long = {
    val timeMs: Long = System.currentTimeMillis + delay
    while (System.currentTimeMillis < timeMs) {
      if (consumeWithSleep) try {
        Thread.sleep(delay)
      }
      catch {
        case e: Exception => {
          throw new RuntimeException(e)
        }
      }
    }
    count.incrementAndGet
    if (count.get % 100 == 0) TestDataSetConsumer.logger.info("Passed {} datasets...", count.get)
    return count.get
  }

  def getCount: Long = {
    return count.get
  }
}