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

package org.deeplearning4j.datasets.iterator;

import lombok.Getter;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.List;

/**
 * A wrapper for a dataset to sample from.
 * This will randomly sample from the given dataset.
 * @author Adam GIbson
 */
public class SamplingDataSetIterator implements DataSetIterator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2700563801361726914L;
	private DataSet sampleFrom;
	private int batchSize;
	private int totalNumberSamples;
	private int numTimesSampled;
	@Getter private DataSetPreProcessor preProcessor;

  /**
   *
   * @param sampleFrom the dataset to sample from
   * @param batchSize the batch size to sample
   * @param totalNumberSamples the sample size
   */
 	public SamplingDataSetIterator(DataSet sampleFrom, int batchSize,
			int totalNumberSamples) {
		super();
		this.sampleFrom = sampleFrom;
		this.batchSize = batchSize;
		this.totalNumberSamples = totalNumberSamples;
	}

	@Override
	public boolean hasNext() {
		return numTimesSampled < totalNumberSamples;
	}

	@Override
	public DataSet next() {
		DataSet ret = sampleFrom.sample(batchSize);
		numTimesSampled+= batchSize;
		return ret;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int totalExamples() {
		return totalNumberSamples * batchSize;
	}

	@Override
	public int inputColumns() {
		return sampleFrom.numInputs();
	}

	@Override
	public int totalOutcomes() {
		return sampleFrom.numOutcomes();
	}

	@Override
	public boolean resetSupported(){
		return true;
	}

	@Override
	public boolean asyncSupported() {
		return true;
	}

	@Override
	public void reset() {
		numTimesSampled = 0;
	}

	@Override
	public int batch() {
		return batchSize;
	}

	@Override
	public int cursor() {
		return numTimesSampled;
	}

	@Override
	public int numExamples() {
		return sampleFrom.numExamples();
	}

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        this.preProcessor = (DataSetPreProcessor) preProcessor;
    }

	@Override
	public List<String> getLabels() {
		return null;
	}


	@Override
	public DataSet next(int num) {
		DataSet ret = sampleFrom.sample(num);
		numTimesSampled++;
		return ret;
	}
	
	

}
