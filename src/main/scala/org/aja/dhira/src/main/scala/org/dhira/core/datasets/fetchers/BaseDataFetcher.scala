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
package org.dhira.core.datasets.fetchers


import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.iterator.fetcher.DataSetFetcher
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.util.FeatureUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.List

/**
 * A base class for assisting with creation of matrices
 * with the data applyTransformToDestination fetcher
 * @author Adam Gibson
 *
 */
@SerialVersionUID(-859588773699432365L)
object BaseDataFetcher {
  protected val log: Logger = LoggerFactory.getLogger(classOf[BaseDataFetcher])
}

@SerialVersionUID(-859588773699432365L)
abstract class BaseDataFetcher extends DataSetFetcher {
  protected var cursorPos: Int = 0
  protected var numOutcomes: Int = -1
  protected var inputCols: Int = -1
  protected var curr: DataSet = null
  protected var totExamples: Int = 0

  /**
   * Creates a feature vector
   * @param numRows the number of examples
   * @ a feature vector
   */
  protected def createInputMatrix(numRows: Int): INDArray = {
    Nd4j.create(numRows, inputColumns)
  }

  /**
   * Creates an output label matrix
   * @param outcomeLabel the outcome label to use
   * @ a binary vector where 1 is transform to the
   *         index specified by outcomeLabel
   */
  protected def createOutputVector(outcomeLabel: Int): INDArray = {
    FeatureUtil.toOutcomeVector(outcomeLabel, numOutcomes)
  }

  protected def createOutputMatrix(numRows: Int): INDArray = {
    Nd4j.create(numRows, numOutcomes)
  }

  /**
   * Initializes this data transform fetcher from the passed in datasets
   * @param examples the examples to use
   */
  protected def initializeCurrFromList(examples: List[DataSet]) {
    if (examples.isEmpty) BaseDataFetcher.log.warn("Warning: empty dataset from the fetcher")
    curr = null
    val inputs: INDArray = createInputMatrix(examples.size)
    val labels: INDArray = createOutputMatrix(examples.size)
    
    for (i <- 0 until examples.size() ){
      val data: INDArray = examples.get(i).getFeatureMatrix
      val label: INDArray = examples.get(i).getLabels
      inputs.putRow(i, data)
      labels.putRow(i, label)
    }
    
    curr = new DataSet(inputs, labels)
    examples.clear
  }

  /**
   * Sets a list of label names to the curr dataset
   */
  def setLabelNames(names: List[String]) {
    curr.setLabelNames(names)
  }

  def getLabelName(i: Int): String = {
     curr.getLabelNames.get(i)
  }

  def hasMore: Boolean = {
     cursor < totalExamples
  }

  def next: DataSet = {
     curr
  }

  def totalOutcomes: Int = {
     numOutcomes
  }

  def inputColumns(): Int = {
     inputCols
  }

  def totalExamples(): Int = {
     totExamples
  }

  def reset {
    cursorPos = 0
  }

  def cursor(): Int = {
     cursorPos
  }
}