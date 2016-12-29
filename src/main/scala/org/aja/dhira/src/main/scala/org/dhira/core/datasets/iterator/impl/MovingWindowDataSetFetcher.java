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

package org.deeplearning4j.datasets.iterator.impl;

import org.deeplearning4j.datasets.fetchers.BaseDataFetcher;
import org.deeplearning4j.util.MovingWindowMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Moving window data fetcher. Handles rotation of matrices in all directions
 * to generate more examples.
 *
 *
 * @author Adam Gibson
 */
public class MovingWindowDataSetFetcher extends BaseDataFetcher {

    private DataSet data;
    private int windowRows = 28,windowColumns = 28;
    private int cursor = 0;

    public MovingWindowDataSetFetcher(DataSet data,int windowRows,int windowColumns) {
        this.data = data;
        this.windowRows = windowRows;
        this.windowColumns = windowColumns;
        List<DataSet> list = data.asList();
        List<DataSet> flipped = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            INDArray label = list.get(i).getLabels();
            List<INDArray> windows = new MovingWindowMatrix(list.get(i).getFeatureMatrix(),windowRows,windowColumns,true).windows(true);
            for(int j = 0; j < windows.size(); j++) {
                flipped.add(new DataSet(windows.get(i),label));
            }
            flipped.add(list.get(i));
        }

        this.data = DataSet.merge(flipped);

    }

    /**
     * Fetches the next dataset. You need to call this
     * to getFromOrigin a new dataset, otherwise {@link #next()}
     * just returns the last data applyTransformToDestination fetch
     *
     * @param numExamples the number of examples to fetch
     */
    @Override
    public void fetch(int numExamples) {
          initializeCurrFromList(data.get(ArrayUtil.range(cursor, cursor + numExamples)).asList());

    }
}
