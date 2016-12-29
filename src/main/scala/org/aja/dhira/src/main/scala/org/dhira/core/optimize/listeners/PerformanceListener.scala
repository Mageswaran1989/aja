package org.dhira.core.optimize.listeners

import org.dhira.core.optimize.api.IterationListener
import org.dhira.core.nnet.api.Model
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.shape.Shape
import org.nd4j.linalg.util.ArrayUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * Simple IterationListener that tracks time spend on training per iteration.
 *
 * @author raver119@gmail.com
 */
object PerformanceListener {
  private val logger: Logger = LoggerFactory.getLogger(classOf[PerformanceListener])

  class Builder {
    private var frequency: Int = 1
    private var reportScore: Boolean = false
    private var reportSample: Boolean = true
    private var reportBatch: Boolean = true
    private var reportIteration: Boolean = true
    private var reportTime: Boolean = true

    def this() {
      this()
    }

    /**
     * This method defines, if iteration number should be reported together with other data
     *
     * @param reallyReport
     * @return
     */
    def reportIteration(reallyReport: Boolean): PerformanceListener.Builder = {
      this.reportIteration = reallyReport
      return this
    }

    /**
     * This method defines, if time per iteration should be reported together with other data
     *
     * @param reallyReport
     * @return
     */
    def reportTime(reallyReport: Boolean): PerformanceListener.Builder = {
      this.reportTime = reallyReport
      return this
    }

    /**
     * This method defines, if samples/sec should be reported together with other data
     *
     * @param reallyReport
     * @return
     */
    def reportSample(reallyReport: Boolean): PerformanceListener.Builder = {
      this.reportSample = reallyReport
      return this
    }

    /**
     * This method defines, if batches/sec should be reported together with other data
     *
     * @param reallyReport
     * @return
     */
    def reportBatch(reallyReport: Boolean): PerformanceListener.Builder = {
      this.reportBatch = reallyReport
      return this
    }

    /**
     * This method defines, if score should be reported together with other data
     *
     * @param reallyReport
     * @return
     */
    def reportScore(reallyReport: Boolean): PerformanceListener.Builder = {
      this.reportScore = reallyReport
      return this
    }

    /**
     * Desired IterationListener activation frequency
     *
     * @param frequency
     * @return
     */
    def setFrequency(frequency: Int): PerformanceListener.Builder = {
      this.frequency = frequency
      return this
    }

    /**
     * This method returns configured PerformanceListener instance
     *
     * @return
     */
    def build: PerformanceListener = {
      val listener: PerformanceListener = new PerformanceListener(frequency, reportScore)
      listener.reportIteration = this.reportIteration
      listener.reportTime = this.reportTime
      listener.reportBatch = this.reportBatch
      listener.reportSample = this.reportSample
      return listener
    }
  }

}

class PerformanceListener extends IterationListener {
  private final var frequency: Int = 0
  private var samplesPerSec: Double = 0.0f
  private var batchesPerSec: Double = 0.0f
  private var lastTime: Long = 0L
  private var iterationCount: AtomicLong = new AtomicLong(0)
  private var reportScore: Boolean = false
  private var reportSample: Boolean = true
  private var reportBatch: Boolean = true
  private var reportIteration: Boolean = true
  private var reportTime: Boolean = true

  def this(frequency: Int, reportScore: Boolean) {
    this()
    this.frequency = frequency
    this.reportScore = reportScore
    this.lastTime = System.currentTimeMillis
  }

  def this(frequency: Int) {
    this(frequency, false)
  }

  def invoked: Boolean = {
    return false
  }

  def invoke {
  }

  def iterationDone(model: Model, iteration: Int) {
    if (iterationCount.getAndIncrement % frequency == 0) {
      val currentTime: Long = System.currentTimeMillis
      val timeSpent: Long = currentTime - lastTime
      val timeSec: Float = timeSpent / 1000f
      val input: INDArray = model.input
      val tadLength: Long = Shape.getTADLength(input.shape, ArrayUtil.range(1, input.rank))
      val numSamples: Long = input.lengthLong / tadLength
      samplesPerSec = numSamples / timeSec
      batchesPerSec = 1 / timeSec
      val builder: StringBuilder = new StringBuilder
      if (reportIteration) builder.append("iteration ").append(iterationCount.get).append("; ")
      if (reportTime) builder.append("iteration time: ").append(timeSpent).append(" ms; ")
      if (reportSample) builder.append("samples/sec: ").append(String.format("%.3f", samplesPerSec)).append("; ")
      if (reportBatch) builder.append("batches/sec: ").append(String.format("%.3f", batchesPerSec)).append("; ")
      if (reportScore) builder.append("score: ").append(model.score).append(";")
      PerformanceListener.logger.info(builder.toString)
    }
    lastTime = System.currentTimeMillis
  }
}