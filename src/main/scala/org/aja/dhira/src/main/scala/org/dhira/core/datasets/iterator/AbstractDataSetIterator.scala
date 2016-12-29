package org.dhira.core.datasets.iterator

import lombok.NonNull
import org.dhira.core.containers.Pair
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.DataSetPreProcessor
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.factory.Nd4j
import java.util.Queue
import java.util.concurrent.LinkedBlockingQueue

import scala.collection.mutable.ArrayBuffer

/**
 * This is simple DataSetIterator implementation, that builds DataSetIterator out of
 * INDArray/float[]/double[] pairs.
 * Suitable for model feeding with externally originated data.
 *
 * PLEASE NOTE: If total number of input elements % batchSize != 0, reminder will be ignored
 *
 * @author raver119@gmail.com
 */
abstract class AbstractDataSetIterator[T] extends DataSetIterator {
  private var preProcessor: DataSetPreProcessor = null
  @transient
  private var iterable: Iterable[Pair[T,T]] = null
  @transient
  private var iterator: Iterator[Pair[T,T]] = null
  private final var batchSize: Int = 0
  private final val queue: Queue[DataSet] = new LinkedBlockingQueue[DataSet](1)
  private var labels: List[String] = null
  private var numFeatures: Int = -1
  private var numLabels: Int = -1

  protected def this(@NonNull iterable: Iterable[Pair[T,T]], batchSize: Int) {
    this()
    if (batchSize < 1) throw new IllegalStateException("batchSize can't be < 1")
    this.iterable = iterable
    this.iterator = this.iterable.iterator
    this.batchSize = batchSize
    fillQueue
  }

  /**
   * Like the standard next method but allows a
   * customizable number of examples returned
   *
   * @param num the number of examples
   * @return the next data applyTransformToDestination
   */
  def next(num: Int): DataSet = {
    throw new IllegalStateException("next(int) isn't supported for this DataSetIterator")
  }

  /**
   * Total examples in the iterator
   *
   * @return
   */
  def totalExamples: Int = {
    0
  }

  /**
   * Input columns for the dataset
   *
   * @return
   */
  def inputColumns: Int = {
    numFeatures
  }

  /**
   * The number of labels for the dataset
   *
   * @return
   */
  def totalOutcomes(): Int = {
    numLabels
  }

  def resetSupported(): Boolean = {
    iterable != null
  }

  def asyncSupported(): Boolean = {
    true
  }

  /**
   * Resets the iterator back to the beginning
   */
  def reset() {
    queue.clear
    if (iterable != null) iterator = iterable.iterator
  }

  /**
   * Batch size
   *
   * @return
   */
  def batch(): Int = {
    return batchSize
  }

  /**
   * The current cursor if applicable
   *
   * @return
   */
  def cursor(): Int = {
    0
  }

  /**
   * Total number of examples in the dataset
   *
   * @return
   */
  def numExamples(): Int = {
    totalExamples
  }

  /**
   * Set a pre processor
   *
   * @param preProcessor a pre processor to set
   */
  def setPreProcessor(preProcessor: DataSetPreProcessor) {
    this.preProcessor = preProcessor
  }

  /**
   * Get dataset iterator record reader labels
   */
  def getLabels: List[String] = {
    labels
  }

  /**
   * Returns {@code true} if the iteration has more elements.
   * (In other words, returns {@code true} if {@link #next} would
   * return an element rather than throwing an exception.)
   *
   * @return { @code true} if the iteration has more elements
   */
  def hasNext: Boolean = {
    fillQueue
    return !queue.isEmpty
  }

  protected def fillQueue() {
    if (queue.isEmpty) {
      var labels: INDArray = null
      var features: INDArray = null
      var cLabels: List[INDArray] = List() //TODO val
      var cFeatures: List[INDArray] = List() //TODO val
      for (cnt <- 0 until batchSize) {
        if (iterator.hasNext) {
          val pair: Pair[T,T] = iterator.next
          if (numFeatures < 1) {
            if (pair.getFirst.isInstanceOf[INDArray]) {
              numFeatures = (pair.getFirst.asInstanceOf[INDArray]).length
              numLabels = (pair.getSecond.asInstanceOf[INDArray]).length
            }
            else if (pair.getFirst.isInstanceOf[Array[Float]]) {
              numFeatures = (pair.getFirst.asInstanceOf[Array[Float]]).length
              numLabels = (pair.getSecond.asInstanceOf[Array[Float]]).length
            }
            else if (pair.getFirst.isInstanceOf[Array[Double]]) {
              numFeatures = (pair.getFirst.asInstanceOf[Array[Double]]).length
              numLabels = (pair.getSecond.asInstanceOf[Array[Double]]).length
            }
          }
          if (pair.getFirst.isInstanceOf[INDArray]) {
            cFeatures = (pair.getFirst.asInstanceOf[INDArray]) :: cFeatures
            cLabels = (pair.getSecond.asInstanceOf[INDArray]) :: cLabels
          }
          else if (pair.getFirst.isInstanceOf[Array[Float]]) {
            cFeatures = Nd4j.create(pair.getFirst.asInstanceOf[Array[Float]]) :: cFeatures
            cLabels = Nd4j.create(pair.getSecond.asInstanceOf[Array[Float]]) :: cLabels
          }
          else if (pair.getFirst.isInstanceOf[Array[Double]]) {
            cFeatures = Nd4j.create(pair.getFirst.asInstanceOf[Array[Double]]) :: cFeatures
            cLabels = Nd4j.create(pair.getSecond.asInstanceOf[Array[Double]]) :: cLabels
          }
        }
      }

      if (cFeatures.size == batchSize) {
        import scala.collection.JavaConverters._
        features = Nd4j.vstack(cFeatures.asJava)
        labels = Nd4j.vstack(cLabels.asJava)
        val dataSet: DataSet = new DataSet(features, labels)
        queue.add(dataSet)
      }
    }
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration
   * @throws NoSuchElementException if the iteration has no more elements
   */
  @throws(classOf[NoSuchElementException])
  def next(): DataSet = {
    if (queue.isEmpty) throw new NoSuchElementException
    val dataSet: DataSet = queue.poll
    if (preProcessor != null) preProcessor.preProcess(dataSet)
    dataSet
  }

  /**
   * Removes from the underlying collection the last element returned
   * by this iterator (optional operation).  This method can be called
   * only once per call to {@link #next}.  The behavior of an iterator
   * is unspecified if the underlying collection is modified while the
   * iteration is in progress in any way other than by calling this
   * method.
   *
   * @throws UnsupportedOperationException if the { @code remove}
   *                                                      operation is not supported by this iterator
   * @throws IllegalStateException         if the { @code next} method has not
   *                                                      yet been called, or the { @code remove} method has already
   *                                                                                      been called after the last call to the { @code next}
   * method
   * @implSpec The default implementation throws an instance of
   *           { @link UnsupportedOperationException} and performs no other action.
   */
  override def remove() {
  }

  def getPreProcessor: DataSetPreProcessor = {
    preProcessor
  }
}