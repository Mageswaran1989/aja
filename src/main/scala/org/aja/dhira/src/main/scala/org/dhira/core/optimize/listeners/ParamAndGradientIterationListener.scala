package org.dhira.core.optimize.listeners

import lombok.Builder
import org.dhira.core.nnet.api.Model
import org.dhira.core.optimize.api.IterationListener
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.ops.transforms.Transforms
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption


/**
 * An iteration listener that provides details on parameters and gradients at each iteration during traning.
 * Attempts to provide much of the same information as the UI histogram iteration listener, but in a text-based
 * format (for example, when learning on a system accessed via SSH etc).
 * i.e., is intended to aid network tuning and debugging<br>
 * This iteration listener is set up to calculate mean, min, max, and mean absolute value
 * of each type of parameter and gradient in the network at each iteration.<br>
 * These
 *
 *
 * @author Alex Black
 */
object ParamAndGradientIterationListener {
  private val MAX_WRITE_FAILURE_MESSAGES: Int = 10
  private val logger: Logger = LoggerFactory.getLogger(classOf[ParamAndGradientIterationListener])
}

class ParamAndGradientIterationListener extends IterationListener {
  private var isInvoked: Boolean = false
  private var iterations: Int = 1
  private var totalIterationCount: Long = 0
  private var printMean: Boolean = true
  private var printHeader: Boolean = true
  private var printMinMax: Boolean = true
  private var printMeanAbsValue: Boolean = true
  private var file: File = null
  private var filePath: Path = null
  private var outputToConsole: Boolean = false
  private var outputToFile: Boolean = false
  private var outputToLogger: Boolean = false
  private var delimiter: String = "\t"
  private var writeFailureCount: Int = 0

  /** Default constructor for output to console only every iteration, tab delimited */
//  def this() {
//   // this(1, true, true, true, true, true, false, false, null, "\t")
//  }

  /** Full constructor with all options.
    * Note also: ParamAndGradientIterationListener.builder() can be used instead of this constructor.
    * @param iterations calculate and report values every 'iterations' iterations
    * @param printHeader Whether to output a header row (i.e., names for each column)
    * @param printMean Calculate and display the mean of parameters and gradients
    * @param printMinMax Calculate and display the min/max of the parameters and gradients
    * @param printMeanAbsValue Calculate and display the mean absolute value
    * @param outputToConsole If true, display the values to the console (System.out.println())
    * @param outputToFile If true, write the values to a file, one per line
    * @param outputToLogger If true, log the values
    * @param file File to write values to. May be null, not used if outputToFile == false
    * @param delimiter delimiter (for example, "\t" or "," etc)
    */
  @Builder def this(iterations: Int, printHeader: Boolean, printMean: Boolean, printMinMax: Boolean,
                    printMeanAbsValue: Boolean, outputToConsole: Boolean, outputToFile: Boolean,
                    outputToLogger: Boolean, file: File, delimiter: String) {
    this()
    this.printHeader = printHeader
    this.printMean = printMean
    this.printMinMax = printMinMax
    this.printMeanAbsValue = printMeanAbsValue
    this.iterations = iterations
    this.file = file
    if (this.file != null) {
      this.filePath = file.toPath
    }
    this.outputToConsole = outputToConsole
    this.outputToFile = outputToFile
    this.outputToLogger = outputToLogger
    this.delimiter = delimiter
  }

  def invoked: Boolean = {
    return isInvoked
  }

  def invoke {
    isInvoked = true
  }

  def iterationDone(model: Model, iteration: Int) {
    totalIterationCount += 1
    if (totalIterationCount == 1 && printHeader) {
      val params: Map[String, INDArray] = model.paramTable
      model.conf.getVariables
      val sb: StringBuilder = new StringBuilder
      sb.append("n")
      sb.append(delimiter)
      sb.append("score")
      import scala.collection.JavaConversions._
      for (s <- params.keySet) {
        if (printMean) sb.append(delimiter).append(s).append("_mean")
        if (printMinMax) {
          sb.append(delimiter).append(s).append("_min").append(delimiter).append(s).append("_max")
        }
        if (printMeanAbsValue) sb.append(delimiter).append(s).append("_meanAbsValue")
        if (printMean) sb.append(delimiter).append(s).append("_meanG")
        if (printMinMax) {
          sb.append(delimiter).append(s).append("_minG").append(delimiter).append(s).append("_maxG")
        }
        if (printMeanAbsValue) sb.append(delimiter).append(s).append("_meanAbsValueG")
      }
      sb.append("\n")
      if (outputToFile) {
        try {
          Files.write(filePath, sb.toString.getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        }
        catch {
          case e: IOException => {
            if (({
              writeFailureCount += 1; writeFailureCount - 1
            }) < ParamAndGradientIterationListener.MAX_WRITE_FAILURE_MESSAGES) {
              ParamAndGradientIterationListener.logger.warn("Error writing to file: {}", e)
            }
            if (writeFailureCount == ParamAndGradientIterationListener.MAX_WRITE_FAILURE_MESSAGES) {
              ParamAndGradientIterationListener.logger.warn("Max file write messages displayed. No more failure messages will be printed")
            }
          }
        }
      }
      if (outputToLogger) ParamAndGradientIterationListener.logger.info(sb.toString)
      if (outputToConsole) System.out.println(sb.toString)
    }
    if (totalIterationCount % iterations != 0) return
    val params: Map[String, INDArray] = model.paramTable
    val grads: Map[String, INDArray] = model.gradient.gradientForVariable
    val sb: StringBuilder = new StringBuilder
    sb.append(totalIterationCount)
    sb.append(delimiter)
    sb.append(model.score)
    import scala.collection.JavaConversions._
    for (entry <- params.entrySet) {
      val currParams: INDArray = entry.getValue
      val currGrad: INDArray = grads.get(entry.getKey)
      if (printMean) {
        sb.append(delimiter)
        sb.append(currParams.meanNumber.doubleValue)
      }
      if (printMinMax) {
        sb.append(delimiter)
        sb.append(currParams.minNumber.doubleValue)
        sb.append(delimiter)
        sb.append(currParams.maxNumber.doubleValue)
      }
      if (printMeanAbsValue) {
        sb.append(delimiter)
        val abs: INDArray = Transforms.abs(currParams.dup)
        sb.append(abs.meanNumber.doubleValue)
      }
      if (printMean) {
        sb.append(delimiter)
        sb.append(currGrad.meanNumber.doubleValue)
      }
      if (printMinMax) {
        sb.append(delimiter)
        sb.append(currGrad.minNumber.doubleValue)
        sb.append(delimiter)
        sb.append(currGrad.maxNumber.doubleValue)
      }
      if (printMeanAbsValue) {
        sb.append(delimiter)
        val abs: INDArray = Transforms.abs(currGrad.dup)
        sb.append(abs.meanNumber.doubleValue)
      }
    }
    sb.append("\n")
    val out: String = sb.toString
    if (outputToLogger) ParamAndGradientIterationListener.logger.info(out)
    if (outputToConsole) System.out.print(out)
    if (outputToFile) {
      try {
        Files.write(filePath, out.getBytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
      }
      catch {
        case e: IOException => {
          if (({
            writeFailureCount += 1; writeFailureCount - 1
          }) < ParamAndGradientIterationListener.MAX_WRITE_FAILURE_MESSAGES) {
            ParamAndGradientIterationListener.logger.warn("Error writing to file: {}", e)
          }
          if (writeFailureCount == ParamAndGradientIterationListener.MAX_WRITE_FAILURE_MESSAGES) {
            ParamAndGradientIterationListener.logger.warn("Max file write messages displayed. No more failure messages will be printed")
          }
        }
      }
    }
  }
}