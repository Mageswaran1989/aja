/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */
package org.deeplearning4j.eval

import org.deeplearning4j.berkeley.Counter
import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.graph.ComputationGraph
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.shape.Shape
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.text.DecimalFormat
import java.util._

/**
 * Evaluation metrics:
 * precision, recall, f1
 *
 * @author Adam Gibson
 */
object Evaluation {
  protected var log: Logger = LoggerFactory.getLogger(classOf[Evaluation])
  protected val DEFAULT_EDGE_VALUE: Double = 0.0

  private def createLabels(numClasses: Int): List[String] = {
    if (numClasses == 1) numClasses = 2
    val list: List[String] = new ArrayList[String](numClasses)
    {
      var i: Int = 0
      while (i < numClasses) {
        {
          list.add(String.valueOf(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return list
  }

  private def createLabelsFromMap(labels: Map[Integer, String]): List[String] = {
    val size: Int = labels.size
    val labelsList: List[String] = new ArrayList[String](size)
    {
      var i: Int = 0
      while (i < size) {
        {
          val str: String = labels.get(i)
          if (str == null) throw new IllegalArgumentException("Invalid labels map: missing key for class " + i + " (expect integers 0 to " + (size - 1) + ")")
          labelsList.add(str)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return labelsList
  }
}

class Evaluation extends Serializable {
  protected var truePositives: Nothing = new Nothing
  protected var falsePositives: Nothing = new Nothing
  protected var trueNegatives: Nothing = new Nothing
  protected var falseNegatives: Nothing = new Nothing
  protected var confusion: Nothing = null
  protected var numRowCounter: Int = 0
  protected var labelsList: List[String] = new ArrayList[String]

  def this() {
    this()
  }

  /**
   * The number of classes to account
   * for in the evaluation
   * @param numClasses the number of classes to account for in the evaluation
   */
  def this(numClasses: Int) {
    this()
    `this`(Evaluation.createLabels(numClasses))
  }

  /**
   * The labels to include with the evaluation.
   * This constructor can be used for
   * generating labeled output rather than just
   * numbers for the labels
   * @param labels the labels to use
   *               for the output
   */
  def this(labels: List[String]) {
    this()
    this.labelsList = labels
    if (labels != null) {
      createConfusion(labels.size)
    }
  }

  /**
   * Use a map to generate labels
   * Pass in a label index with the actual label
   * you want to use for output
   * @param labels a map of label index to label value
   */
  def this(labels: Map[Integer, String]) {
    this()
    `this`(Evaluation.createLabelsFromMap(labels))
  }

  private def createConfusion(nClasses: Int) {
    val classes: List[Integer] = new ArrayList[Integer]
    {
      var i: Int = 0
      while (i < nClasses) {
        {
          classes.add(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    confusion = new Nothing(classes)
  }

  /**
   * Evaluate the output
   * using the given true labels,
   * the input to the multi layer network
   * and the multi layer network to
   * use for evaluation
   * @param trueLabels the labels to ise
   * @param input the input to the network to use
   *              for evaluation
   * @param network the network to use for output
   */
  def eval(trueLabels: INDArray, input: INDArray, network: ComputationGraph) {
    eval(trueLabels, network.output(false, input)(0))
  }

  /**
   * Evaluate the output
   * using the given true labels,
   * the input to the multi layer network
   * and the multi layer network to
   * use for evaluation
   * @param trueLabels the labels to ise
   * @param input the input to the network to use
   *              for evaluation
   * @param network the network to use for output
   */
  def eval(trueLabels: INDArray, input: INDArray, network: Nothing) {
    eval(trueLabels, network.output(input, Layer.TrainingMode.TEST))
  }

  /**
   * Collects statistics on the real outcomes vs the
   * guesses. This is for logistic outcome matrices.
   * <p>
   * Note that an IllegalArgumentException is thrown if the two passed in
   * matrices aren't the same length.
   *
   * @param realOutcomes the real outcomes (labels - usually binary)
   * @param guesses      the guesses/prediction (usually a probability vector)
   */
  def eval(realOutcomes: INDArray, guesses: INDArray) {
    numRowCounter += realOutcomes.shape(0)
    if (confusion == null) {
      var nClasses: Int = realOutcomes.columns
      if (nClasses == 1) nClasses = 2
      labelsList = new ArrayList[String](nClasses)
      {
        var i: Int = 0
        while (i < nClasses) {
          labelsList.add(String.valueOf(i))
          ({
            i += 1; i - 1
          })
        }
      }
      createConfusion(nClasses)
    }
    if (realOutcomes.length != guesses.length) throw new IllegalArgumentException("Unable to evaluate. Outcome matrices not same length")
    val nCols: Int = realOutcomes.columns
    val nRows: Int = realOutcomes.rows
    if (nCols == 1) {
      val binaryGuesses: INDArray = guesses.gt(0.5)
      val tp: Int = binaryGuesses.mul(realOutcomes).sumNumber.intValue
      val fp: Int = binaryGuesses.mul(-1.0).addi(1.0).muli(realOutcomes).sumNumber.intValue
      val fn: Int = binaryGuesses.mul(realOutcomes.mul(-1.0).addi(1.0)).sumNumber.intValue
      val tn: Int = nRows - tp - fp - fn
      confusion.add(1, 1, tp)
      confusion.add(1, 0, fn)
      confusion.add(0, 1, fp)
      confusion.add(0, 0, tn)
      truePositives.incrementCount(1, tp)
      falsePositives.incrementCount(1, fp)
      falseNegatives.incrementCount(1, fp)
      trueNegatives.incrementCount(1, tp)
      truePositives.incrementCount(0, tn)
      falsePositives.incrementCount(0, fn)
      falseNegatives.incrementCount(0, fn)
      trueNegatives.incrementCount(0, tn)
    }
    else {
      val guessIndex: INDArray = Nd4j.argMax(guesses, 1)
      val realOutcomeIndex: INDArray = Nd4j.argMax(realOutcomes, 1)
      val nExamples: Int = guessIndex.length
      {
        var i: Int = 0
        while (i < nExamples) {
          {
            confusion.add(realOutcomeIndex.getDouble(i).toInt, guessIndex.getDouble(i).toInt)
          }
          ({
            i += 1; i - 1
          })
        }
      }
      {
        var col: Int = 0
        while (col < nCols) {
          {
            val colBinaryGuesses: INDArray = guessIndex.eps(col)
            val colRealOutcomes: INDArray = realOutcomes.getColumn(col)
            val colTp: Int = colBinaryGuesses.mul(colRealOutcomes).sumNumber.intValue
            val colFp: Int = colBinaryGuesses.mul(colRealOutcomes.mul(-1.0).addi(1.0)).sumNumber.intValue
            val colFn: Int = colBinaryGuesses.mul(-1.0).addi(1.0).muli(colRealOutcomes).sumNumber.intValue
            val colTn: Int = nRows - colTp - colFp - colFn
            truePositives.incrementCount(col, colTp)
            falsePositives.incrementCount(col, colFp)
            falseNegatives.incrementCount(col, colFn)
            trueNegatives.incrementCount(col, colTn)
          }
          ({
            col += 1; col - 1
          })
        }
      }
    }
  }

  /**
   * Convenience method for evaluation of time series.
   * Reshapes time series (3d) to 2d, then calls eval
   *
   * @see #eval(INDArray, INDArray)
   */
  def evalTimeSeries(labels: INDArray, predicted: INDArray) {
    if (labels.rank == 2 && predicted.rank == 2) eval(labels, predicted)
    if (labels.rank != 3) throw new IllegalArgumentException("Invalid input: labels are not rank 3 (rank=" + labels.rank + ")")
    if (!Arrays.equals(labels.shape, predicted.shape)) {
      throw new IllegalArgumentException("Labels and predicted have different shapes: labels=" + Arrays.toString(labels.shape) + ", predicted=" + Arrays.toString(predicted.shape))
    }
    if (labels.ordering == 'f') labels = Shape.toOffsetZeroCopy(labels, 'c')
    if (predicted.ordering == 'f') predicted = Shape.toOffsetZeroCopy(predicted, 'c')
    val shape: Array[Int] = labels.shape
    labels = labels.permute(0, 2, 1)
    labels = labels.reshape(shape(0) * shape(2), shape(1))
    predicted = predicted.permute(0, 2, 1)
    predicted = predicted.reshape(shape(0) * shape(2), shape(1))
    eval(labels, predicted)
  }

  /**
   * Evaluate a time series, whether the output is masked usind a masking array. That is,
   * the mask array specified whether the output at a given time step is actually present, or whether it
   * is just padding.<br>
   * For example, for N examples, nOut output size, and T time series length:
   * labels and predicted will have shape [N,nOut,T], and outputMask will have shape [N,T].
   *
   * @see #evalTimeSeries(INDArray, INDArray)
   */
  def evalTimeSeries(labels: INDArray, predicted: INDArray, outputMask: INDArray) {
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
                predicted2d.putRow(rowCount, predicted.get(NDArrayIndex.point(ex), NDArrayIndex.all, NDArrayIndex.point(t)))
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

  /**
   * Evaluate a single prediction (one prediction at a time)
   *
   * @param predictedIdx Index of class predicted by the network
   * @param actualIdx    Index of actual class
   */
  def eval(predictedIdx: Int, actualIdx: Int) {
    numRowCounter += 1
    if (confusion == null) {
      throw new UnsupportedOperationException("Cannot evaluate single example without initializing confusion matrix first")
    }
    addToConfusion(predictedIdx, actualIdx)
    if (predictedIdx == actualIdx) {
      incrementTruePositives(predictedIdx)
      import scala.collection.JavaConversions._
      for (clazz <- confusion.getClasses) {
        if (clazz ne predictedIdx) trueNegatives.incrementCount(clazz, 1.0)
      }
    }
    else {
      incrementFalseNegatives(actualIdx)
      incrementFalsePositives(predictedIdx)
      import scala.collection.JavaConversions._
      for (clazz <- confusion.getClasses) {
        if (clazz ne predictedIdx && clazz ne actualIdx) trueNegatives.incrementCount(clazz, 1.0)
      }
    }
  }

  def stats: String = {
    return stats(false)
  }

  /**
   * Method to obtain the classification report as a String
   *
   * @param suppressWarnings whether or not to output warnings related to the evaluation results
   * @return A (multi-line) String with accuracy, precision, recall, f1 score etc
   */
  def stats(suppressWarnings: Boolean): String = {
    var actual: String = null
    var expected: String = null
    val builder: StringBuilder = new StringBuilder().append("\n")
    val warnings: StringBuilder = new StringBuilder
    val classes: List[Integer] = confusion.getClasses
    import scala.collection.JavaConversions._
    for (clazz <- classes) {
      actual = resolveLabelForClass(clazz)
      import scala.collection.JavaConversions._
      for (clazz2 <- classes) {
        val count: Int = confusion.getCount(clazz, clazz2)
        if (count != 0) {
          expected = resolveLabelForClass(clazz2)
          builder.append(String.format("Examples labeled as %s classified by model as %s: %d times%n", actual, expected, count))
        }
      }
      if (!suppressWarnings && truePositives.getCount(clazz) eq 0) {
        if (falsePositives.getCount(clazz) eq 0) {
          warnings.append(String.format("Warning: class %s was never predicted by the model. This class was excluded from the average precision%n", actual))
        }
        if (falseNegatives.getCount(clazz) eq 0) {
          warnings.append(String.format("Warning: class %s has never appeared as a true label. This class was excluded from the average recall%n", actual))
        }
      }
    }
    builder.append("\n")
    builder.append(warnings)
    val df: DecimalFormat = new DecimalFormat("#.####")
    builder.append("\n==========================Scores========================================")
    builder.append("\n Accuracy:  ").append(df.format(accuracy))
    builder.append("\n Precision: ").append(df.format(precision))
    builder.append("\n Recall:    ").append(df.format(recall))
    builder.append("\n F1 Score:  ").append(df.format(f1))
    builder.append("\n========================================================================")
    return builder.toString
  }

  private def resolveLabelForClass(clazz: Integer): String = {
    if (labelsList != null && labelsList.size > clazz) return labelsList.get(clazz)
    return clazz.toString
  }

  /**
   * Returns the precision for a given label
   *
   * @param classLabel the label
   * @return the precision for the label
   */
  def precision(classLabel: Integer): Double = {
    return precision(classLabel, Evaluation.DEFAULT_EDGE_VALUE)
  }

  /**
   * Returns the precision for a given label
   *
   * @param classLabel the label
   * @param edgeCase   What to output in case of 0/0
   * @return the precision for the label
   */
  def precision(classLabel: Integer, edgeCase: Double): Double = {
    val tpCount: Double = truePositives.getCount(classLabel)
    val fpCount: Double = falsePositives.getCount(classLabel)
    if (tpCount == 0 && fpCount == 0) {
      return edgeCase
    }
    return tpCount / (tpCount + fpCount)
  }

  /**
   * Precision based on guesses so far
   * Takes into account all known classes and outputs average precision across all of them
   *
   * @return the total precision based on guesses so far
   */
  def precision: Double = {
    var precisionAcc: Double = 0.0
    var classCount: Int = 0
    import scala.collection.JavaConversions._
    for (classLabel <- confusion.getClasses) {
      val precision: Double = precision(classLabel, -1)
      if (precision != -1) {
        precisionAcc += precision(classLabel)
        classCount += 1
      }
    }
    return precisionAcc / classCount.toDouble
  }

  /**
   * Returns the recall for a given label
   *
   * @param classLabel the label
   * @return Recall rate as a double
   */
  def recall(classLabel: Integer): Double = {
    return recall(classLabel, Evaluation.DEFAULT_EDGE_VALUE)
  }

  /**
   * Returns the recall for a given label
   *
   * @param classLabel the label
   * @param edgeCase   What to output in case of 0/0
   * @return Recall rate as a double
   */
  def recall(classLabel: Integer, edgeCase: Double): Double = {
    val tpCount: Double = truePositives.getCount(classLabel)
    val fnCount: Double = falseNegatives.getCount(classLabel)
    if (tpCount == 0 && fnCount == 0) {
      return edgeCase
    }
    return tpCount / (tpCount + fnCount)
  }

  /**
   * Recall based on guesses so far
   * Takes into account all known classes and outputs average recall across all of them
   *
   * @return the recall for the outcomes
   */
  def recall: Double = {
    var recallAcc: Double = 0.0
    var classCount: Int = 0
    import scala.collection.JavaConversions._
    for (classLabel <- confusion.getClasses) {
      val recall: Double = recall(classLabel, -1.0)
      if (recall != -1.0) {
        recallAcc += recall(classLabel)
        classCount += 1
      }
    }
    return recallAcc / classCount.toDouble
  }

  /**
   * Returns the false positive rate for a given label
   *
   * @param classLabel the label
   * @return fpr as a double
   */
  def falsePositiveRate(classLabel: Integer): Double = {
    return recall(classLabel, Evaluation.DEFAULT_EDGE_VALUE)
  }

  /**
   * Returns the false positive rate for a given label
   *
   * @param classLabel the label
   * @param edgeCase   What to output in case of 0/0
   * @return fpr as a double
   */
  def falsePositiveRate(classLabel: Integer, edgeCase: Double): Double = {
    val fpCount: Double = falsePositives.getCount(classLabel)
    val tnCount: Double = trueNegatives.getCount(classLabel)
    if (fpCount == 0 && tnCount == 0) {
      return edgeCase
    }
    return fpCount / (fpCount + tnCount)
  }

  /**
   * False positive rate based on guesses so far
   * Takes into account all known classes and outputs average fpr across all of them
   *
   * @return the fpr for the outcomes
   */
  def falsePositiveRate: Double = {
    var fprAlloc: Double = 0.0
    var classCount: Int = 0
    import scala.collection.JavaConversions._
    for (classLabel <- confusion.getClasses) {
      val fpr: Double = falsePositiveRate(classLabel, -1.0)
      if (fpr != -1.0) {
        fprAlloc += falsePositiveRate(classLabel)
        classCount += 1
      }
    }
    return fprAlloc / classCount.toDouble
  }

  /**
   * Returns the false negative rate for a given label
   *
   * @param classLabel the label
   * @return fnr as a double
   */
  def falseNegativeRate(classLabel: Integer): Double = {
    return recall(classLabel, Evaluation.DEFAULT_EDGE_VALUE)
  }

  /**
   * Returns the false negative rate for a given label
   *
   * @param classLabel the label
   * @param edgeCase   What to output in case of 0/0
   * @return fnr as a double
   */
  def falseNegativeRate(classLabel: Integer, edgeCase: Double): Double = {
    val fnCount: Double = falseNegatives.getCount(classLabel)
    val tpCount: Double = truePositives.getCount(classLabel)
    if (fnCount == 0 && tpCount == 0) {
      return edgeCase
    }
    return fnCount / (fnCount + tpCount)
  }

  /**
   * False negative rate based on guesses so far
   * Takes into account all known classes and outputs average fnr across all of them
   *
   * @return the fnr for the outcomes
   */
  def falseNegativeRate: Double = {
    var fnrAlloc: Double = 0.0
    var classCount: Int = 0
    import scala.collection.JavaConversions._
    for (classLabel <- confusion.getClasses) {
      val fnr: Double = falseNegativeRate(classLabel, -1.0)
      if (fnr != -1.0) {
        fnrAlloc += falseNegativeRate(classLabel)
        classCount += 1
      }
    }
    return fnrAlloc / classCount.toDouble
  }

  /**
   * False Alarm Rate (FAR) reflects rate of misclassified to classified records
   * http://ro.ecu.edu.au/cgi/viewcontent.cgi?article=1058&context=isw
   *
   * @return the fpr for the outcomes
   */
  def falseAlarmRate: Double = {
    return (falsePositiveRate + falseNegativeRate) / 2.0
  }

  /**
   * Calculate f1 score for a given class
   *
   * @param classLabel the label to calculate f1 for
   * @return the f1 score for the given label
   */
  def f1(classLabel: Integer): Double = {
    val precision: Double = precision(classLabel)
    val recall: Double = recall(classLabel)
    if (precision == 0 || recall == 0) return 0
    return 2.0 * ((precision * recall / (precision + recall)))
  }

  /**
   * TP: true positive
   * FP: False Positive
   * FN: False Negative
   * F1 score: 2 * TP / (2TP + FP + FN)
   *
   * @return the f1 score or harmonic mean based on current guesses
   */
  def f1: Double = {
    val precision: Double = precision
    val recall: Double = recall
    if (precision == 0 || recall == 0) return 0
    return 2.0 * ((precision * recall / (precision + recall)))
  }

  /**
   * Accuracy:
   * (TP + TN) / (P + N)
   *
   * @return the accuracy of the guesses so far
   */
  def accuracy: Double = {
    val nClasses: Int = confusion.getClasses.size
    var countCorrect: Int = 0
    {
      var i: Int = 0
      while (i < nClasses) {
        {
          countCorrect += confusion.getCount(i, i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return countCorrect / getNumRowCounter.toDouble
  }

  /**
   * True positives: correctly rejected
   *
   * @return the total true positives so far
   */
  def truePositives: Map[Integer, Integer] = {
    return convertToMap(truePositives, confusion.getClasses.size)
  }

  /**
   * True negatives: correctly rejected
   *
   * @return the total true negatives so far
   */
  def trueNegatives: Map[Integer, Integer] = {
    return convertToMap(trueNegatives, confusion.getClasses.size)
  }

  /**
   * False positive: wrong guess
   *
   * @return the count of the false positives
   */
  def falsePositives: Map[Integer, Integer] = {
    return convertToMap(falsePositives, confusion.getClasses.size)
  }

  /**
   * False negatives: correctly rejected
   *
   * @return the total false negatives so far
   */
  def falseNegatives: Map[Integer, Integer] = {
    return convertToMap(falseNegatives, confusion.getClasses.size)
  }

  /**
   * Total negatives true negatives + false negatives
   *
   * @return the overall negative count
   */
  def negative: Map[Integer, Integer] = {
    return addMapsByKey(trueNegatives, falsePositives)
  }

  /**
   * Returns all of the positive guesses:
   * true positive + false negative
   */
  def positive: Map[Integer, Integer] = {
    return addMapsByKey(truePositives, falseNegatives)
  }

  private def convertToMap(counter: Nothing, maxCount: Int): Map[Integer, Integer] = {
    val map: Map[Integer, Integer] = new HashMap[Integer, Integer]
    {
      var i: Int = 0
      while (i < maxCount) {
        {
          map.put(i, counter.getCount(i).asInstanceOf[Int])
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return map
  }

  private def addMapsByKey(first: Map[Integer, Integer], second: Map[Integer, Integer]): Map[Integer, Integer] = {
    val out: Map[Integer, Integer] = new HashMap[Integer, Integer]
    val keys: Set[Integer] = new HashSet[Integer](first.keySet)
    keys.addAll(second.keySet)
    import scala.collection.JavaConversions._
    for (i <- keys) {
      var f: Integer = first.get(i)
      var s: Integer = second.get(i)
      if (f == null) f = 0
      if (s == null) s = 0
      out.put(i, f + s)
    }
    return out
  }

  def incrementTruePositives(classLabel: Integer) {
    truePositives.incrementCount(classLabel, 1.0)
  }

  def incrementTrueNegatives(classLabel: Integer) {
    trueNegatives.incrementCount(classLabel, 1.0)
  }

  def incrementFalseNegatives(classLabel: Integer) {
    falseNegatives.incrementCount(classLabel, 1.0)
  }

  def incrementFalsePositives(classLabel: Integer) {
    falsePositives.incrementCount(classLabel, 1.0)
  }

  /**
   * Adds to the confusion matrix
   *
   * @param real  the actual guess
   * @param guess the system guess
   */
  def addToConfusion(real: Integer, guess: Integer) {
    confusion.add(real, guess)
  }

  /**
   * Returns the number of times the given label
   * has actually occurred
   *
   * @param clazz the label
   * @return the number of times the label
   *         actually occurred
   */
  def classCount(clazz: Integer): Int = {
    return confusion.getActualTotal(clazz)
  }

  def getNumRowCounter: Int = {
    return numRowCounter
  }

  def getClassLabel(clazz: Integer): String = {
    return resolveLabelForClass(clazz)
  }

  /**
   * Returns the confusion matrix variable
   *
   * @return confusion matrix variable for this evaluation
   */
  def getConfusionMatrix: Nothing = {
    return confusion
  }

  /**
   * Merge the other evaluation object into this one. The result is that this Evaluation instance contains the counts
   * etc from both
   *
   * @param other Evaluation object to merge into this one.
   */
  def merge(other: Evaluation) {
    if (other == null) return
    truePositives.incrementAll(other.truePositives)
    falsePositives.incrementAll(other.falsePositives)
    trueNegatives.incrementAll(other.trueNegatives)
    falseNegatives.incrementAll(other.falseNegatives)
    if (confusion == null) {
      if (other.confusion != null) confusion = new Nothing(other.confusion)
    }
    else {
      if (other.confusion != null) confusion.add(other.confusion)
    }
    numRowCounter += other.numRowCounter
    if (labelsList.isEmpty) labelsList.addAll(other.labelsList)
  }

  /**
   * Get a String representation of the confusion matrix
   */
  def confusionToString: String = {
    val nClasses: Int = confusion.getClasses.size
    var maxLabelSize: Int = 0
    import scala.collection.JavaConversions._
    for (s <- labelsList) {
      maxLabelSize = Math.max(maxLabelSize, s.length)
    }
    val labelSize: Int = Math.max(maxLabelSize + 5, 10)
    val sb: StringBuilder = new StringBuilder
    sb.append("%-3d")
    sb.append("%-")
    sb.append(labelSize)
    sb.append("s | ")
    val headerFormat: StringBuilder = new StringBuilder
    headerFormat.append("   %-").append(labelSize).append("s   ")
    {
      var i: Int = 0
      while (i < nClasses) {
        {
          sb.append("%7d")
          headerFormat.append("%7d")
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val rowFormat: String = sb.toString
    val out: StringBuilder = new StringBuilder
    val headerArgs: Array[AnyRef] = new Array[AnyRef](nClasses + 1)
    headerArgs(0) = "Predicted:"
    {
      var i: Int = 0
      while (i < nClasses) {
        headerArgs(i + 1) = i
        ({
          i += 1; i - 1
        })
      }
    }
    out.append(String.format(headerFormat.toString, headerArgs)).append("\n")
    out.append("   Actual:\n")
    {
      var i: Int = 0
      while (i < nClasses) {
        {
          val args: Array[AnyRef] = new Array[AnyRef](nClasses + 2)
          args(0) = i
          args(1) = labelsList.get(i)
          {
            var j: Int = 0
            while (j < nClasses) {
              {
                args(j + 2) = confusion.getCount(i, j)
              }
              ({
                j += 1; j - 1
              })
            }
          }
          out.append(String.format(rowFormat, args))
          out.append("\n")
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return out.toString
  }
}