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

package org.deeplearning4j.earlystopping.saver;

import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;

import java.io.IOException;
import java.nio.charset.Charset;

/** Save the best (and latest/most recent) {@link ComputationGraph}s learned during early stopping training to the local file system.<br>
 * Instances of this class will save 3 files for best (and optionally, latest) models:<br>
 * (a) The network configuration: bestGraphConf.json<br>
 * (b) The network parameters: bestGraphParams.bin<br>
 * (c) The network updater: bestGraphUpdater.bin<br>
 * <br>
 * NOTE: The model updater is an object that contains the internal state for training features such as AdaGrad, Momentum
 * and RMSProp.<br>
 * The updater is <i>not</i> required to use the network at test time; it is saved in case further training is required.
 * Without saving the updater, any further training would result in the updater being recreated, without the benefit
 * of the history/internal state. This could negatively impact training performance after loading the network.
 *
 * @author Alex Black
 */
public class LocalFileGraphSaver implements EarlyStoppingModelSaver<ComputationGraph> {

    private static final String bestFileName = "bestGraph.bin";
    private static final String latestFileName = "latestGraph.bin";

    private String directory;
    private Charset encoding;

    /**Constructor that uses default character set for configuration (json) encoding
     * @param directory Directory to save networks
     */
    public LocalFileGraphSaver(String directory) {
        this(directory, Charset.defaultCharset());
    }

    /**
     * @param directory Directory to save networks
     * @param encoding Character encoding for configuration (json)
     */
    public LocalFileGraphSaver(String directory, Charset encoding){
        this.directory = directory;
        this.encoding = encoding;
    }

    @Override
    public void saveBestModel(ComputationGraph net, double score) throws IOException {
        String confOut = FilenameUtils.concat(directory,bestFileName);
        save(net,confOut);
    }

    @Override
    public void saveLatestModel(ComputationGraph net, double score) throws IOException {
        String confOut = FilenameUtils.concat(directory,latestFileName);
        save(net,confOut);
    }

    private void save(ComputationGraph net, String confOut) throws IOException{
       ModelSerializer.writeModel(net, confOut, true);
    }

    @Override
    public ComputationGraph getBestModel() throws IOException {
        String confOut = FilenameUtils.concat(directory, bestFileName);
        return load(confOut);
    }

    @Override
    public ComputationGraph getLatestModel() throws IOException {
        String confOut = FilenameUtils.concat(directory, latestFileName);
        return load(confOut);
    }

    private ComputationGraph load(String confOut) throws IOException {
        ComputationGraph net = ModelSerializer.restoreComputationGraph(confOut);
        return net;
    }

    @Override
    public String toString(){
        return "LocalFileGraphSaver(dir=" + directory + ")";
    }
}
