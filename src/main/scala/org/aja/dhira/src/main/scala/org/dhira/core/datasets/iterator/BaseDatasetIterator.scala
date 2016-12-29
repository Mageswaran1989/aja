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
package org.dhira.core.datasets.iterator

import lombok.Getter
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.dataset.api.DataSetPreProcessor
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.dataset.api.iterator.fetcher.BaseDataFetcher

//import java.util.List

/**
 * Baseline implementation includes
 * control over the data fetcher and some basic
 * getters for metadata
 * @author Adam Gibson
 *
 */
class BaseDatasetIterator extends DataSetIterator {
  protected var mBatch: Int = 0
  protected var mNumExamples: Int = 0
  protected var mFetcher: BaseDataFetcher = null
  @Getter protected var preProcessor: DataSetPreProcessor = null

  def this(batch: Int, numExamples: Int, fetcher: BaseDataFetcher) {
    this()
    this.mBatch = batch

    this.mNumExamples = if (numExamples < 0) fetcher.totalExamples else numExamples
    this.mFetcher = fetcher
  }

  def hasNext: Boolean = {
    mFetcher.hasMore && mFetcher.cursor < numExamples
  }

  def next: DataSet = {
    mFetcher.fetch(batch)
    val result: DataSet = mFetcher.next
    if (preProcessor != null) {
      preProcessor.preProcess(result)
    }
    return result
  }

  def next(num: Int): DataSet = {
    mFetcher.fetch(num)
    val next: DataSet = mFetcher.next
    if (preProcessor != null) preProcessor.preProcess(next)
    return next
  }

  override def remove {
    throw new UnsupportedOperationException
  }

  def totalExamples: Int = {
    mFetcher.totalExamples
  }

  def inputColumns: Int = {
    return mFetcher.inputColumns
  }

  def totalOutcomes: Int = {
    return mFetcher.totalOutcomes
  }

  def resetSupported: Boolean = {
    return true
  }

  def asyncSupported: Boolean = {
    return true
  }

  def reset {
    mFetcher.reset
  }

  def batch(): Int = {
    mBatch
  }

  def cursor: Int = {
    mFetcher.cursor
  }

  def numExamples: Int = {
    mNumExamples
  }

  def setPreProcessor(preProcessor: DataSetPreProcessor) {
    this.preProcessor = preProcessor.asInstanceOf[DataSetPreProcessor]
  }

  def getLabels: List[String] = { //TODO Some
    List() //before it was null
  }

  override def getPreProcessor: DataSetPreProcessor = ???
}