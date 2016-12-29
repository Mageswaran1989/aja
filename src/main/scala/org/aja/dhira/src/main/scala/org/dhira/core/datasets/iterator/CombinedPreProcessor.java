package org.deeplearning4j.datasets.iterator;

import lombok.NonNull;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is special preProcessor, that allows to combine multiple prerpocessors, and apply them to data sequentially.
 *
 * @author raver119@gmail.com
 */
public class CombinedPreProcessor implements DataSetPreProcessor {
    private List<DataSetPreProcessor> preProcessors;

    private CombinedPreProcessor() {

    }
    /**
     * Pre process a dataset sequentially
     *
     * @param toPreProcess the data set to pre process
     */
    @Override
    public void preProcess(DataSet toPreProcess) {
        for(DataSetPreProcessor preProcessor: preProcessors) {
            preProcessor.preProcess(toPreProcess);
        }
    }

    public static class Builder {
        private List<DataSetPreProcessor> preProcessors = new ArrayList<>();

        public Builder() {

        }

        public Builder addPreProcessor(@NonNull DataSetPreProcessor preProcessor) {
            preProcessors.add(preProcessor);
            return this;
        }

        public Builder addPreProcessor(int idx, @NonNull DataSetPreProcessor preProcessor) {
            preProcessors.add(idx, preProcessor);
            return this;
        }


        public CombinedPreProcessor build() {
            CombinedPreProcessor preProcessor = new CombinedPreProcessor();
            preProcessor.preProcessors = this.preProcessors;
            return preProcessor;
        }
    }
}
