package org.deeplearning4j.eval

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.transforms.Abs
import org.nd4j.linalg.api.shape.Shape
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex

import scala.collection.mutable.ListBuffer
/**
 * Evaluation method for the evaluation of regression algorithms.<br>
 * Provides the following metrics, for each column:<br>
 * - MSE: mean squared error<br>
 * - MAE: mean absolute error<br>
 * - RMSE: root mean squared error<br>
 * - RSE: relative squared error<br>
 * - correlation coefficient<br>
 * See for example: http://www.saedsayad.com/model_evaluation_r.htm
 * For classification, see {@link Evaluation}
 *
 */
object RegressionEvaluation {
  val DEFAULT_PRECISION: Int = 5

  private def createDefaultColumnNames(nColumns: Int): List[String] = {
    (0 until nColumns).map(i => "col_" + i).toList
  }
}

class RegressionEvaluation {
  private var columnNames: List[String] = null
  private var precision: Int = 0
  private var exampleCount: Int = 0
  private var labelsSumPerColumn: INDArray = null
  private var sumSquaredErrorsPerColumn: INDArray = null
  private var sumAbsErrorsPerColumn: INDArray = null
  private var currentMean: INDArray = null
  private var currentPredictionMean: INDArray = null
  private var m2Actual: INDArray = null
  private var sumOfProducts: INDArray = null
  private var sumSquaredLabels: INDArray = null
  private var sumSquaredPredicted: INDArray = null

  /** Create a regression evaluation object with specified precision for the stats() method
    * @param columnNames Names of the columns
    */
  def this(columnNames: List[String], precision: Int) {
    this()
    this.columnNames = columnNames
    this.precision = precision
    val n: Int = columnNames.size
    labelsSumPerColumn = Nd4j.zeros(n)
    sumSquaredErrorsPerColumn = Nd4j.zeros(n)
    sumAbsErrorsPerColumn = Nd4j.zeros(n)
    currentMean = Nd4j.zeros(n)
    m2Actual = Nd4j.zeros(n)
    currentPredictionMean = Nd4j.zeros(n)
    sumOfProducts = Nd4j.zeros(n)
    sumSquaredLabels = Nd4j.zeros(n)
    sumSquaredPredicted = Nd4j.zeros(n)
  }


  /** Create a regression evaluation object with the specified number of columns, and specified precision
    * for the stats() method.
    * @param nColumns Number of columns
    */
  def this(nColumns: Int, precision: Int) {
    this(RegressionEvaluation.createDefaultColumnNames(nColumns), precision)
  }

  /** Create a regression evaluation object with the specified number of columns, and default precision
    * for the stats() method.
    * @param nColumns Number of columns
    */
  def this(nColumns: Int) {
    this(RegressionEvaluation.createDefaultColumnNames(nColumns), RegressionEvaluation.DEFAULT_PRECISION)
  }


  /** Create a regression evaluation object with default precision for the stats() method
    * @param columnNames Names of the columns
    */
  def this(columnNames: String*) {
    this(columnNames.toList, RegressionEvaluation.DEFAULT_PRECISION)
  }

  /** Create a regression evaluation object with default precision for the stats() method
    * @param columnNames Names of the columns
    */
  def this(columnNames: List[String]) {
    this(columnNames, RegressionEvaluation.DEFAULT_PRECISION)
  }


  def eval(labels: INDArray, predictions: INDArray) {
    labelsSumPerColumn.addi(labels.sum(0))
    val error: INDArray = predictions.sub(labels)
    val absErrorSum: INDArray = Nd4j.getExecutioner.execAndReturn(new Abs(error.dup)).sum(0)
    val squaredErrorSum: INDArray = error.mul(error).sum(0)
    sumAbsErrorsPerColumn.addi(absErrorSum)
    sumSquaredErrorsPerColumn.addi(squaredErrorSum)
    sumOfProducts.addi(labels.mul(predictions).sum(0))
    sumSquaredLabels.addi(labels.mul(labels).sum(0))
    sumSquaredPredicted.addi(predictions.mul(predictions).sum(0))
    val nRows: Int = labels.size(0)
    currentMean.muli(exampleCount).addi(labels.sum(0)).divi(exampleCount + nRows)
    currentPredictionMean.muli(exampleCount).addi(predictions.sum(0)).divi(exampleCount + nRows)
    exampleCount += nRows
  }

  /**
   * Convenience method for evaluation of time series.
   * Reshapes time series (3d) to 2d, then calls eval
   * @see #eval(INDArray, INDArray)
   */
  def evalTimeSeries(labels: INDArray, predictions: INDArray) {
    if (labels.rank == 2 && predictions.rank == 2) eval(labels, predictions)
    if (labels.rank != 3) throw new IllegalArgumentException("Invalid input: labels are not rank 3 (rank=" + labels.rank + ")")
    if (!Arrays.equals(labels.shape, predictions.shape)) {
      throw new IllegalArgumentException("Labels and predicted have different shapes: labels=" + Arrays.toString(labels.shape) + ", predictions=" + Arrays.toString(predictions.shape))
    }
    if (labels.ordering == 'f') labels = Shape.toOffsetZeroCopy(labels, 'c')
    if (predictions.ordering == 'f') predictions = Shape.toOffsetZeroCopy(predictions, 'c')
    val shape: Array[Int] = labels.shape
    labels = labels.permute(0, 2, 1)
    labels = labels.reshape(shape(0) * shape(2), shape(1))
    predictions = predictions.permute(0, 2, 1)
    predictions = predictions.reshape(shape(0) * shape(2), shape(1))
    eval(labels, predictions)
  }

  /**
   * Evaluate a time series, whether the output is masked usind a masking array. That is,
   * the mask array specified whether the output at a given time step is actually present, or whether it
   * is just padding.<br>
   * For example, for N examples, nOut output size, and T time series length:
   * labels and predicted will have shape [N,nOut,T], and outputMask will have shape [N,T].
   * @see #evalTimeSeries(INDArray, INDArray)
   */
  def evalTimeSeries(labels: INDArray, predictions: INDArray, outputMask: INDArray) {
    val totalOutputExamples: Int = outputMask.sumNumber.intValue
    val outSize: Int = labels.size(1)
    val labels2d: INDArray = Nd4j.create(totalOutputExamples, outSize)
    val predicted2d: INDArray = Nd4j.create(totalOutputExamples, outSize)
    var rowCount: Int = 0
    {
      var ex: Int = 0
      while (ex < outputMask.size(0)) {
        {
          {
            var t: Int = 0
            while (t < outputMask.size(1)) {
              {
                if (outputMask.getDouble(ex, t) == 0.0) continue //todo: continue is not supported
                labels2d.putRow(rowCount, labels.get(NDArrayIndex.point(ex), NDArrayIndex.all, NDArrayIndex.point(t)))
                predicted2d.putRow(rowCount, predictions.get(NDArrayIndex.point(ex), NDArrayIndex.all, NDArrayIndex.point(t)))
                rowCount += 1
              }
              ({
                t += 1; t - 1
              })
            }
          }
        }
        ({
          ex += 1; ex - 1
        })
      }
    }
    eval(labels2d, predicted2d)
  }

  def stats: String = {
    var maxLabelLength: Int = 0
    import scala.collection.JavaConversions._
    for (s <- columnNames) maxLabelLength = Math.max(maxLabelLength, s.length)
    val labelWidth: Int = maxLabelLength + 5
    val columnWidth: Int = precision + 10
    val format: String = "%-" + labelWidth + "s" + "%-" + columnWidth + "." + precision + "e" + "%-" + columnWidth + "." + precision + "e" + "%-" + columnWidth + "." + precision + "e" + "%-" + columnWidth + "." + precision + "e" + "%-" + columnWidth + "." + precision + "e"
    val sb: StringBuilder = new StringBuilder
    val headerFormat: String = "%-" + labelWidth + "s" + "%-" + columnWidth + "s" + "%-" + columnWidth + "s" + "%-" + columnWidth + "s" + "%-" + columnWidth + "s" + "%-" + columnWidth + "s"
    sb.append(String.format(headerFormat, "Column", "MSE", "MAE", "RMSE", "RSE", "R^2"))
    sb.append("\n")
    {
      var i: Int = 0
      while (i < columnNames.size) {
        {
          val mse: Double = meanSquaredError(i)
          val mae: Double = meanAbsoluteError(i)
          val rmse: Double = rootMeanSquaredError(i)
          val rse: Double = relativeSquaredError(i)
          val corr: Double = correlationR2(i)
          sb.append(String.format(format, columnNames.get(i), mse, mae, rmse, rse, corr))
          sb.append("\n")
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return sb.toString
  }

  def numColumns: Int = {
    columnNames.size
  }

  def meanSquaredError(column: Int): Double = {
    sumSquaredErrorsPerColumn.getDouble(column) / exampleCount
  }

  def meanAbsoluteError(column: Int): Double = {
    sumAbsErrorsPerColumn.getDouble(column) / exampleCount
  }

  def rootMeanSquaredError(column: Int): Double = {
    Math.sqrt(sumSquaredErrorsPerColumn.getDouble(column) / exampleCount)
  }

  def correlationR2(column: Int): Double = {
    val sumxiyi: Double = sumOfProducts.getDouble(column)
    val predictionMean: Double = this.currentPredictionMean.getDouble(column)
    val labelMean: Double = this.currentMean.getDouble(column)
    val sumSquaredLabels: Double = this.sumSquaredLabels.getDouble(column)
    val sumSquaredPredicted: Double = this.sumSquaredPredicted.getDouble(column)
    var r2: Double = sumxiyi - exampleCount * predictionMean * labelMean
    r2 /= Math.sqrt(sumSquaredLabels - exampleCount * labelMean * labelMean) * Math.sqrt(sumSquaredPredicted - exampleCount * predictionMean * predictionMean)
    r2
  }

  def relativeSquaredError(column: Int): Double = {
    val numerator: Double = sumSquaredPredicted.getDouble(column) - 2 * sumOfProducts.getDouble(column) + sumSquaredLabels.getDouble(column)
    val denominator: Double = sumSquaredLabels.getDouble(column) - exampleCount * currentMean.getDouble(column) * currentMean.getDouble(column)
    if (Math.abs(denominator) > Nd4j.EPS_THRESHOLD) {
      numerator / denominator
    }
    else {
      Double.PositiveInfinity
    }
  }
}