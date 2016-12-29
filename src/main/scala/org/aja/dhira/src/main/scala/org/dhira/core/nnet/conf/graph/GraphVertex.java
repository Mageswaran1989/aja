/*
 *
 *  * Copyright 2016 Skymind,Inc.
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

package org.deeplearning4j.nn.conf.graph;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.inputs.InvalidInputTypeException;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.Serializable;

/**
 * A GraphVertex is a vertex in the computation graph. It may contain Layer, or define some arbitrary forward/backward pass
 * behaviour based on the inputs
 *
 * @author Alex Black
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ElementWiseVertex.class, name = "ElementWiseVertex"),
        @JsonSubTypes.Type(value = MergeVertex.class, name = "MergeVertex"),
        @JsonSubTypes.Type(value = SubsetVertex.class, name = "SubsetVertex"),
        @JsonSubTypes.Type(value = LayerVertex.class, name = "LayerVertex"),
        @JsonSubTypes.Type(value = LastTimeStepVertex.class, name = "LastTimeStepVertex"),
        @JsonSubTypes.Type(value = DuplicateToTimeSeriesVertex.class, name = "DuplicateToTimeSeriesVertex"),
        @JsonSubTypes.Type(value = PreprocessorVertex.class, name = "PreprocessorVertex")
})
public abstract class GraphVertex implements Cloneable, Serializable {

    @Override
    public abstract GraphVertex clone();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public abstract int numParams(boolean backprop);

    /**
     * Create a {@link org.deeplearning4j.nn.graph.vertex.GraphVertex} instance, for the given computation graph,
     * given the configuration instance.
     *
     * @param graph            The computation graph that this GraphVertex is to be part of
     * @param name             The name of the GraphVertex object
     * @param idx              The index of the GraphVertex
     * @param paramsView       A view of the full parameters array
     * @param initializeParams If true: initialize the parameters. If false: make no change to the values in the paramsView array
     * @return The implementation GraphVertex object (i.e., implementation, no the configuration)
     */
    public abstract org.deeplearning4j.nn.graph.vertex.GraphVertex instantiate(ComputationGraph graph, String name, int idx,
                                                                               INDArray paramsView, boolean initializeParams);

    /**
     * Determine the type of output for this GraphVertex, given the specified inputs. Given that a GraphVertex may do arbitrary
     * processing or modifications of the inputs, the output types can be quite different to the input type(s).<br>
     * This is generally used to determine when to add preprocessors, as well as the input sizes etc for layers
     *
     * @param vertexInputs The inputs to this vertex
     * @return The type of output for this vertex
     * @throws InvalidInputTypeException If the input type is invalid for this type of GraphVertex
     */
    public abstract InputType getOutputType(InputType... vertexInputs) throws InvalidInputTypeException;

}
