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

package org.deeplearning4j.nn.graph.vertex.impl;

import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.graph.vertex.BaseGraphVertex;
import org.deeplearning4j.nn.graph.vertex.VertexIndices;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.util.Arrays;

/** A MergeVertex is used to combine the activations of two or more layers/GraphVertex by means of concatenation/merging.<br>
 * Exactly how this is done depends on the type of input.<br>
 * For 2d (feed forward layer) inputs: MergeVertex([numExamples,layerSize1],[numExamples,layerSize2]) -> [numExamples,layerSize1 + layerSize2]<br>
 * For 3d (time series) inputs: MergeVertex([numExamples,layerSize1,timeSeriesLength],[numExamples,layerSize2,timeSeriesLength])
 *      -> [numExamples,layerSize1 + layerSize2,timeSeriesLength]<br>
 * For 4d (convolutional) inputs: MergeVertex([numExamples,depth1,width,height],[numExamples,depth2,width,height])
 *      -> [numExamples,depth1 + depth2,width,height]<br>
 * @author Alex Black
 */
public class MergeVertex extends BaseGraphVertex {

    private int[][] forwardPassShapes;
    private int fwdPassRank;

    public MergeVertex(ComputationGraph graph, String name, int vertexIndex){
        this(graph,name,vertexIndex,null,null);
    }

    public MergeVertex(ComputationGraph graph, String name, int vertexIndex, VertexIndices[] inputVertices, VertexIndices[] outputVertices) {
        super(graph, name, vertexIndex, inputVertices, outputVertices);
    }

    @Override
    public String toString() {
        return "MergeVertex(id=" + this.getVertexIndex() + ",name=\"" + this.getVertexName() + "\")";
    }

    @Override
    public boolean hasLayer() {
        return false;
    }

    @Override
    public boolean isOutputVertex() {
        return false;
    }

    @Override
    public Layer getLayer() {
        return null;
    }

    @Override
    public INDArray doForward(boolean training) {
        if(!canDoForward()) throw new IllegalStateException("Cannot do forward pass: inputs not set");

        if(inputs.length == 1){
            //No-op case
            int[] shape = inputs[0].shape();
            forwardPassShapes = new int[][]{Arrays.copyOf(shape, shape.length)};
            return inputs[0];
        }

        forwardPassShapes = new int[inputs.length][0];
        int nExamples = inputs[0].size(0);
        int nOut = 0;
        fwdPassRank = inputs[0].rank();
        for( int i=0; i<inputs.length; i++ ){
            int[] currShape = inputs[i].shape();
            if(fwdPassRank != currShape.length){
                throw new IllegalStateException("Cannot merge activations with different ranks: first activations have rank " + fwdPassRank +
                        ", activations[" + i + "] have rank " + currShape.length + " (shape="+Arrays.toString(currShape)+")");
            }
            forwardPassShapes[i] = Arrays.copyOf(currShape,currShape.length);
            if(currShape[0] != nExamples){
                throw new IllegalStateException("Cannot merge activations with different number of examples (activations[0] shape: "
                        + Arrays.toString(inputs[0].shape()) + ", activations[" + i + "] shape: " + Arrays.toString(inputs[i].shape()));
            }

            nOut += currShape[1];   //Same dimension for all of CNNs, FF, RNNs
        }

        int nOutCumulative = 0;
        INDArray out;
        switch(inputs[0].rank()) {
            case 2:
                //Standard feedforward inputs...
                out = Nd4j.create(nExamples, nOut);

                for (INDArray activation : inputs) {
                    int[] currShape = activation.shape();
                    out.get(NDArrayIndex.all(), NDArrayIndex.interval(nOutCumulative, nOutCumulative + currShape[1]))
                            .assign(activation);
                    nOutCumulative += currShape[1];
                }
                break;
            case 3:
                //Time series inputs...
                int tsLength = inputs[0].size(2);
                out = Nd4j.create(nExamples, nOut, tsLength);

                for (INDArray activation : inputs) {
                    int[] currShape = activation.shape();
                    out.get(NDArrayIndex.all(), NDArrayIndex.interval(nOutCumulative, nOutCumulative + currShape[1]), NDArrayIndex.all())
                            .assign(activation);
                    nOutCumulative += currShape[1];
                }

                break;
            case 4:
                fwdPassRank = 4;
                int[] outShape = Arrays.copyOf(inputs[0].shape(),4);
                outShape[1] = nOut;
                out = Nd4j.create(outShape);

                //Input activations: [minibatch,depth,width,height]
                for( INDArray activation : inputs ){
                    out.get(NDArrayIndex.all(), NDArrayIndex.interval(nOutCumulative, nOutCumulative + activation.size(1)), NDArrayIndex.all(), NDArrayIndex.all())
                            .assign(activation);
                    nOutCumulative += activation.size(1);
                }

                break;
            default:
                throw new UnsupportedOperationException("Cannot merge activations with rank 4 or more");
        }

        return out;
    }

    @Override
    public Pair<Gradient, INDArray[]> doBackward(boolean tbptt) {
        if(!canDoBackward()) throw new IllegalStateException("Cannot do backward pass: errors not set");

        if(forwardPassShapes.length == 1){
            //No op case
            return new Pair<>(null,epsilons);
        }

        //Split the epsilons in the opposite way that the activations were merged
        INDArray[] out = new INDArray[forwardPassShapes.length];
        for( int i=0; i<out.length; i++ ) out[i] = Nd4j.create(forwardPassShapes[i]);

        int cumulative = 0;
        switch(fwdPassRank){
            case 2:
                //Standard
                for( int i=0; i<forwardPassShapes.length; i++ ){
                    out[i].assign(epsilons[0].get(NDArrayIndex.all(),   //All rows
                            NDArrayIndex.interval(cumulative, cumulative + forwardPassShapes[i][1])));     //subset of columns
                    cumulative += forwardPassShapes[i][1];
                }
                break;
            case 3:
                for( int i=0; i<forwardPassShapes.length; i++ ){
                    out[i].assign(epsilons[0].get(NDArrayIndex.all(),   //All rows
                            NDArrayIndex.interval(cumulative, cumulative + forwardPassShapes[i][1]), //subset of columns
                            NDArrayIndex.all()));   //All time steps

                    cumulative += forwardPassShapes[i][1];
                }
                break;
            case 4:
                for( int i=0; i<forwardPassShapes.length; i++ ){
                    out[i].assign(epsilons[0].get(NDArrayIndex.all(),
                            NDArrayIndex.interval(cumulative, cumulative + forwardPassShapes[i][1]),   //Subset of depth
                            NDArrayIndex.all(),     //Width
                            NDArrayIndex.all()));    //height
                    cumulative += forwardPassShapes[i][1];
                }
                break;
            default:
                throw new RuntimeException("Invalid rank during forward pass (not 2, 3, 4)"); //Should never happen
        }

        return new Pair<>(null,out);
    }

    @Override
    public void setBackpropGradientsViewArray(INDArray backpropGradientsViewArray) {
        if(backpropGradientsViewArray != null) throw new RuntimeException("Vertex does not have gradients; gradients view array cannot be set here");
    }
}
