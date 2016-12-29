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

package org.deeplearning4j.nn.conf.layers;

import lombok.*;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.ParamInitializer;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.params.GRUParamInitializer;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Collection;
import java.util.Map;

/** Gated Recurrent Unit RNN Layer.<br>
 * The GRU was recently proposed by Cho et al. 2014 - http://arxiv.org/abs/1406.1078<br>
 * It is similar to the LSTM architecture in that both use a gating structure within each unit
 * to attempt to capture long-term dependencies and deal with the vanishing gradient problem.
 * A GRU layer contains fewer parameters than an equivalent size LSTM layer, and some research
 * (such as http://arxiv.org/abs/1412.3555) suggests it may outperform LSTM layers (given an
 * equal number of parameters) in some cases.
 * @author Alex Black
 */
@Data @NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GRU extends BaseRecurrentLayer {

    private GRU(Builder builder) {
    	super(builder);
    }

    @Override
    public Layer instantiate(NeuralNetConfiguration conf, Collection<IterationListener> iterationListeners, int layerIndex, INDArray layerParamsView, boolean initializeParams) {
        org.deeplearning4j.nn.layers.recurrent.GRU ret
                = new org.deeplearning4j.nn.layers.recurrent.GRU(conf);
        ret.setListeners(iterationListeners);
        ret.setIndex(layerIndex);
        ret.setParamsViewArray(layerParamsView);
        Map<String, INDArray> paramTable = initializer().init(conf, layerParamsView, initializeParams);
        ret.setParamTable(paramTable);
        ret.setConf(conf);
        return ret;
    }

    @Override
    public ParamInitializer initializer() {
        return GRUParamInitializer.getInstance();
    }

    @AllArgsConstructor
    public static class Builder extends BaseRecurrentLayer.Builder<Builder> {      
        @Override
        @SuppressWarnings("unchecked")
        public GRU build() {
            return new GRU(this);
        }
    }
}
