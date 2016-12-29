package org.deeplearning4j.nn.conf.layers.setup;


        import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
        import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
        import org.deeplearning4j.nn.conf.inputs.InputType;
        import org.deeplearning4j.nn.conf.layers.*;
        import org.deeplearning4j.nn.conf.preprocessor.*;
        import org.deeplearning4j.nn.layers.convolution.KernelValidationUtil;

        import java.util.HashMap;
        import java.util.Map;

/**
 * DO NOT DELETE UNTIL ALL FUNCTIONALITY MOVED TO NEW SETUP
 *
 * TODO Delete after 0.6.0 - deprecated in 0.5.0
 *
 * Automatic configuration of convolutional layers:
 * Handles all layer wise interactions
 * between convolution/subsampling -> dense/output
 * convolution -> subsampling
 *
 * among others.
 *
 * It does this by tracking a moving window
 * of all the various configurations through
 * out the network.
 *
 * The moving window tracks everything from the
 * out channels of the previous layer
 * as well as the different interactions
 * such as when a shift from
 * convolution to dense happens.
 *
 * @deprecated Use {@link org.deeplearning4j.nn.conf.MultiLayerConfiguration.Builder#setInputType(InputType)} to set nIns
 * and add preprocessors as required. This can be done using {@code builder.setInputType(InputType.convolutional(height, width, channels))}
 */


@Deprecated
public class ConvolutionLayerSetup {

    public static final String CONVOLUTION_LAYER = "ConvolutionLayer";
    public static final String LOCAL_RESPONSE_NORMALIZATION = "LocalResponseNormalization";
    public static final String SUBSAMPLING_LAYER = "SubsamplingLayer";
    public static final String RECURSIVE_AUTO_ENCODER = "RecursiveAutoEncoder";
    public static final String RBM = "RBM";
    public static final String DENSE_LAYER = "DenseLayer";
    public static final String OUTPUT_LAYER = "OutputLayer";
    public static final String GRAVES_LSTM = "GravesLSTM";
    public static final String GRAVES_BIDIRECTIONAL_LSTM = "GravesBidirectionalLSTM";
    public static final String RNN_OUTPUT_LAYER = "RnnOutputLayer";
    public static final String ACTIVATION_LAYER = "ActivationLayer";
    public static final String BATCH_NORMALIZATION = "BatchNormalization";
    protected int lastHeight = -1;
    protected int lastWidth = -1;
    protected int lastOutChannels = -1;
    protected int lastnOut = -1;
    protected int numLayers = -1;
    protected String inLayerName;
    protected String outLayerName;
    protected Map<String,int[]> nOutsPerLayer = new HashMap<>();
    protected Map<String,Integer> nInsPerLayer = new HashMap<>();
    protected MultiLayerConfiguration.Builder conf;
    protected boolean useCNN = true;

    /**
     * Take in the configuration
     *
     * @param builder  the configuration builder
     * @param height   initial height of the data
     * @param width    initial width of the data
     * @param channels initial number of channels in the data
     * @deprecated Use {@link org.deeplearning4j.nn.conf.MultiLayerConfiguration.Builder#setInputType(InputType)} to set nIns
     * and add preprocessors as required. This can be done using {@code builder.setInputType(InputType.convolutional(height, width, channels))}
     * For image data that has been flattened into a row vector per example (shape [minibatchSize,depth*height*width])
     * instead use {@code InputType.convolutionalFlat(height,width,depth)}.
     */
    public ConvolutionLayerSetup(MultiLayerConfiguration.Builder builder,int height,int width,int channels) {

//        builder.setInputType(InputType.convolutionalFlat(height, width, channels));

        conf = builder;
        lastHeight = height;
        lastWidth = width;
        lastOutChannels = channels;

        if(conf instanceof NeuralNetConfiguration.ListBuilder) {
            NeuralNetConfiguration.ListBuilder listBuilder = (NeuralNetConfiguration.ListBuilder) conf;
            numLayers = listBuilder.getLayerwise().size();
        } else {
            numLayers = conf.getConfs().size();
        }
        for(int i = 0; i < numLayers-1; i++) {
            Layer inputLayer = getLayer(i,conf);
            Layer outputLayer = getLayer(i+1,conf);
            updateLayerInputs(i, inputLayer, outputLayer);
        }
    }

    private void storeNInAndNOut(String inName, int out){
        nInsPerLayer.put(inName, out);
        nOutsPerLayer.put(inLayerName, new int[]{lastHeight, lastWidth, lastOutChannels});
    }

    private void updateLayerInputs(int i, Layer inputLayer, Layer outputLayer){
        int lastLayerNumber = numLayers - 1;
        inLayerName = (inputLayer.getLayerName() != null) ? inputLayer.getLayerName(): Integer.toString(i);
        outLayerName = (outputLayer.getLayerName() != null) ? outputLayer.getLayerName(): Integer.toString(i+1);

        if(i < lastLayerNumber){
            switch (inputLayer.getClass().getSimpleName()){
                case CONVOLUTION_LAYER:
                    ConvolutionLayer convolutionLayer = (ConvolutionLayer) inputLayer;
                    if(i == 0) {
                        conf.inputPreProcessor(i, new FeedForwardToCnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                        lastnOut = convolutionLayer.getNOut();
                        convolutionLayer.setNIn(lastOutChannels);
                    }
                    getConvolutionOutputSize(new int[]{lastHeight, lastWidth}, convolutionLayer.getKernelSize(), convolutionLayer.getPadding(), convolutionLayer.getStride());
                    lastOutChannels = convolutionLayer.getNOut();
                    switch (outputLayer.getClass().getSimpleName()) {
                        case CONVOLUTION_LAYER:
                            ConvolutionLayer nextConv = (ConvolutionLayer) outputLayer;
                            //set next layer's convolution input channels to be equal to this layer's out channels
                            lastOutChannels = lastnOut = convolutionLayer.getNOut();
                            storeNInAndNOut(inLayerName, lastnOut);
                            nextConv.setNIn(lastnOut);
                            break;
                        case LOCAL_RESPONSE_NORMALIZATION:
                        case SUBSAMPLING_LAYER:
                            lastOutChannels = lastnOut = convolutionLayer.getNOut();
                            storeNInAndNOut(inLayerName, lastnOut);
                            break;
                        case RECURSIVE_AUTO_ENCODER:
                        case RBM:
                        case DENSE_LAYER:
                        case OUTPUT_LAYER:
                            FeedForwardLayer feedForwardLayer = (FeedForwardLayer) outputLayer;
                            lastOutChannels = convolutionLayer.getNOut();
                            lastnOut = lastHeight * lastWidth * lastOutChannels;
                            storeNInAndNOut(inLayerName, lastnOut); // required to be before inputPreProcessor to update lastHeight and lastWidth
                            feedForwardLayer.setNIn(lastnOut);
                            conf.inputPreProcessor(i + 1, new CnnToFeedForwardPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case GRAVES_LSTM:
                        case GRAVES_BIDIRECTIONAL_LSTM:
                        case RNN_OUTPUT_LAYER:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            lastnOut = lastHeight * lastWidth * lastOutChannels;
                            storeNInAndNOut(inLayerName, lastnOut);
                            feedForwardLayer.setNIn(lastnOut);
                            conf.inputPreProcessor(i + 1, new CnnToRnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case ACTIVATION_LAYER:
                            feedForwardLayer = (ActivationLayer) outputLayer;
                            lastOutChannels = lastnOut = convolutionLayer.getNOut();
                            storeNInAndNOut(inLayerName, lastnOut);
                            feedForwardLayer.setNOut(lastnOut);
                            useCNN = true;
                            break;
                        case BATCH_NORMALIZATION:
                            feedForwardLayer= (BatchNormalization) outputLayer;
                            lastOutChannels = lastnOut = convolutionLayer.getNOut();
                            storeNInAndNOut(inLayerName, lastnOut);
                            feedForwardLayer.setNOut(lastnOut);
                            useCNN = true;
                            break;

                    }
                    break;
                case SUBSAMPLING_LAYER:
                    SubsamplingLayer subsamplingLayer = (SubsamplingLayer) inputLayer;
                    getConvolutionOutputSize(new int[]{lastHeight, lastWidth}, subsamplingLayer.getKernelSize(), subsamplingLayer.getPadding(), subsamplingLayer.getStride());
                    if (i == 0) throw new UnsupportedOperationException("Unsupported path: first layer shouldn't be " + inLayerName);
                    switch (outputLayer.getClass().getSimpleName()) {
                        case CONVOLUTION_LAYER:
                            ConvolutionLayer nextConv = (ConvolutionLayer) outputLayer;
                            storeNInAndNOut(outLayerName, lastOutChannels);
                            nextConv.setNIn(lastOutChannels);
                            break;
                        case SUBSAMPLING_LAYER:
                            storeNInAndNOut(inLayerName, lastnOut);
                            break;
                        case RECURSIVE_AUTO_ENCODER:
                        case RBM:
                        case DENSE_LAYER:
                        case OUTPUT_LAYER:
                            FeedForwardLayer feedForwardLayer = (FeedForwardLayer) outputLayer;
                            lastnOut = lastHeight * lastWidth * lastOutChannels;
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer.setNIn(lastnOut);
                            conf.inputPreProcessor(i + 1,  new CnnToFeedForwardPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case GRAVES_LSTM:
                        case GRAVES_BIDIRECTIONAL_LSTM:
                        case RNN_OUTPUT_LAYER:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            lastnOut = lastHeight * lastWidth * lastOutChannels;
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer.setNIn(lastnOut);
                            conf.inputPreProcessor(i + 1, new CnnToRnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case ACTIVATION_LAYER:
                        case BATCH_NORMALIZATION:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            storeNInAndNOut(inLayerName, lastnOut);
                            feedForwardLayer.setNOut(lastnOut);
                            useCNN = true;
                            break;
                    }
                    break;
                case GRAVES_LSTM:
                case GRAVES_BIDIRECTIONAL_LSTM:
                    if (i == 0) throw new UnsupportedOperationException("Apply nIn attribute to the layer configuration for " + inLayerName);
                    FeedForwardLayer feedForwardLayer = (FeedForwardLayer) inputLayer;
                    switch (outputLayer.getClass().getSimpleName()) {
                        // ffn -> ccn
                        case CONVOLUTION_LAYER:
                            convolutionLayer = (ConvolutionLayer) outputLayer;
                            conf.inputPreProcessor(i, new RnnToCnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            lastnOut = convolutionLayer.getNOut();
                            convolutionLayer.setNIn(lastnOut);
                            break;
                        case SUBSAMPLING_LAYER:
                            throw new UnsupportedOperationException("Subsampling Layer should be connected to Convolution, LocalResponseNormalization or BatchNormalization Layer");
                        case GRAVES_LSTM:
                        case GRAVES_BIDIRECTIONAL_LSTM:
                        case RNN_OUTPUT_LAYER:
                            FeedForwardLayer feedForwardLayer2 = (FeedForwardLayer) outputLayer;
                            lastnOut = feedForwardLayer.getNOut();
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer2.setNIn(lastnOut);
                            break;
                        case RECURSIVE_AUTO_ENCODER:
                        case RBM:
                        case DENSE_LAYER:
                        case OUTPUT_LAYER:
                            feedForwardLayer2 = (FeedForwardLayer) outputLayer;
                            lastnOut = feedForwardLayer.getNOut();
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer2.setNIn(lastnOut);
                            conf.inputPreProcessor(i+1, new RnnToFeedForwardPreProcessor());
                            break;
                        case BATCH_NORMALIZATION: // TODO when implemented put with activation
                            throw new UnsupportedOperationException("Currently not implemented for " + inLayerName);
                        case ACTIVATION_LAYER:
                            feedForwardLayer2 = (FeedForwardLayer) outputLayer;
                            lastnOut = feedForwardLayer.getNOut();
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer2.setNOut(lastnOut);
                            conf.inputPreProcessor(i+1, new RnnToFeedForwardPreProcessor());
                            useCNN = false;
                            break;
                    }
                    break;
                case RECURSIVE_AUTO_ENCODER:
                case RBM:
                case DENSE_LAYER:
                    if (i == 0) throw new UnsupportedOperationException("Apply nIn attribute to the layer configuration for " + inLayerName);
                    feedForwardLayer = (FeedForwardLayer) inputLayer;
                    switch (outputLayer.getClass().getSimpleName()) {
                        case CONVOLUTION_LAYER:
                            convolutionLayer = (ConvolutionLayer) outputLayer;
                            conf.inputPreProcessor(i+1, new FeedForwardToCnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            lastnOut = lastOutChannels;
                            convolutionLayer.setNIn(lastnOut);
                            break;
                        case SUBSAMPLING_LAYER:
                            conf.inputPreProcessor(i+1, new FeedForwardToCnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            lastnOut = lastOutChannels;
                            storeNInAndNOut(inLayerName, lastnOut);
                            break;
                        case RECURSIVE_AUTO_ENCODER:
                        case RBM:
                        case DENSE_LAYER:
                        case OUTPUT_LAYER:
                            FeedForwardLayer feedForwardLayer2 = (FeedForwardLayer) outputLayer;
                            lastnOut = feedForwardLayer.getNOut();
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer2.setNIn(lastnOut);
                            break;
                        case GRAVES_LSTM:
                        case GRAVES_BIDIRECTIONAL_LSTM:
                        case RNN_OUTPUT_LAYER:
                            feedForwardLayer2 = (FeedForwardLayer) outputLayer;
                            lastnOut = feedForwardLayer.getNOut();
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer2.setNIn(lastnOut);
                            conf.inputPreProcessor(i+1, new FeedForwardToRnnPreProcessor());
                            break;
                        case BATCH_NORMALIZATION:
                            BatchNormalization bnLayer = (BatchNormalization) outputLayer;
                            // cudnn currently set for only 4 D on BatchNorm; thus, hack around it with following
                            lastnOut = feedForwardLayer.getNOut();
                            lastHeight = 1;
                            lastWidth = 1;
                            lastOutChannels = lastnOut;
                            storeNInAndNOut(outLayerName, lastnOut);
                            bnLayer.setNOut(lastnOut);
                            conf.inputPreProcessor(i+1, new FeedForwardToCnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case ACTIVATION_LAYER:
                            feedForwardLayer2 = (FeedForwardLayer) outputLayer;
                            lastnOut = feedForwardLayer.getNOut();
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer2.setNOut(lastnOut);
                            useCNN = false;
                            break;
                    }
                    break;
                case ACTIVATION_LAYER:
                case BATCH_NORMALIZATION:
                    if (i == 0) throw new UnsupportedOperationException("Unsupported path: first layer shouldn't be " + inLayerName);
                    switch (outputLayer.getClass().getSimpleName()) {
                        case CONVOLUTION_LAYER:
                            convolutionLayer = (ConvolutionLayer) outputLayer;
                            storeNInAndNOut(outLayerName, lastOutChannels);
                            convolutionLayer.setNIn(lastnOut);
                            break;
                        case SUBSAMPLING_LAYER:
                            storeNInAndNOut(inLayerName, lastnOut);
                            break;
                        case RECURSIVE_AUTO_ENCODER:
                        case RBM:
                        case DENSE_LAYER:
                        case OUTPUT_LAYER:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            lastnOut = lastHeight * lastWidth * lastOutChannels;
                            storeNInAndNOut(outLayerName, lastnOut); // required to be before inputPreProcessor to update lastHeight and lastWidth
                            feedForwardLayer.setNIn(lastnOut);
                            conf.inputPreProcessor(i + 1, new CnnToFeedForwardPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case GRAVES_LSTM:
                        case GRAVES_BIDIRECTIONAL_LSTM:
                        case RNN_OUTPUT_LAYER:
                            if(useCNN) {
                                feedForwardLayer = (FeedForwardLayer) outputLayer;
                                lastnOut = lastHeight * lastWidth * lastOutChannels;
                                storeNInAndNOut(outLayerName, lastnOut);
                                feedForwardLayer.setNIn(lastnOut);
                                conf.inputPreProcessor(i + 1, new CnnToRnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            } else {
                                feedForwardLayer = (FeedForwardLayer) outputLayer;
                                storeNInAndNOut(outLayerName, lastnOut);
                                feedForwardLayer.setNIn(lastnOut);
                                conf.inputPreProcessor(i+1, new FeedForwardToRnnPreProcessor());
                            }
                            break;
                        case BATCH_NORMALIZATION:
                        case ACTIVATION_LAYER:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            storeNInAndNOut(outLayerName, lastnOut); // required to be before inputPreProcessor to update lastHeight and lastWidth
                            feedForwardLayer.setNOut(lastnOut);
                            break;
                        case LOCAL_RESPONSE_NORMALIZATION:
                            throw new UnsupportedOperationException("LocalResponse should not follow " + inLayerName);
                    }
                    break;
                case LOCAL_RESPONSE_NORMALIZATION:
                    if (i == 0) throw new UnsupportedOperationException("Unsupported path: first layer shouldn't be " + inLayerName);
                    switch (outputLayer.getClass().getSimpleName()) {
                        //lrn -> cnn
                        case CONVOLUTION_LAYER:
                            ConvolutionLayer nextConv = (ConvolutionLayer) outputLayer;
                            storeNInAndNOut(outLayerName, lastOutChannels);
                            nextConv.setNIn(lastnOut);
                            break;
                        //lrn -> feedforward || rnn
                        case RECURSIVE_AUTO_ENCODER:
                        case RBM:
                        case DENSE_LAYER:
                        case OUTPUT_LAYER:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            lastnOut = lastHeight * lastWidth * lastOutChannels;
                            storeNInAndNOut(outLayerName, lastnOut); // required to be before inputPreProcessor to update lastHeight and lastWidth
                            feedForwardLayer.setNIn(lastnOut);
                            conf.inputPreProcessor(i + 1, new CnnToFeedForwardPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case GRAVES_LSTM:
                        case GRAVES_BIDIRECTIONAL_LSTM:
                        case RNN_OUTPUT_LAYER:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            lastnOut = lastHeight * lastWidth * lastOutChannels;
                            storeNInAndNOut(outLayerName, lastnOut);
                            feedForwardLayer.setNIn(lastnOut);
                            conf.inputPreProcessor(i + 1, new CnnToRnnPreProcessor(lastHeight, lastWidth, lastOutChannels));
                            break;
                        case BATCH_NORMALIZATION:
                            throw new UnsupportedOperationException("BaseNormalization should not follow a LocalResponse layer.");
                        case ACTIVATION_LAYER:
                            feedForwardLayer = (FeedForwardLayer) outputLayer;
                            storeNInAndNOut(outLayerName, lastnOut); // required to be before inputPreProcessor to update lastHeight and lastWidth
                            feedForwardLayer.setNOut(lastnOut);
                            useCNN = true;
                            break;
                    }
                    break;
                case RNN_OUTPUT_LAYER:
                case OUTPUT_LAYER:
                    throw new UnsupportedOperationException("OutputLayer should be the last layer");
            }
        } else
            throw new UnsupportedOperationException("Unsupported path: final " + inputLayer.getClass().getSimpleName() + " layer");
    }

    // cnn -> batch -> cnn
    // cnn -> batch -> dnn -> batch -> cnn
    // cnn -> batch -> act -> cnn
    // cnn-> batch -> act -> dnn
    // cnn-> batch -> act -> rnn


    // dnn-> batch -> act -> cnn
    // dnn-> batch -> act -> dnn
    // dnn-> batch -> act -> rnn

    // rnn-> batch -> act -> cnn
    // rnn-> batch -> act -> dnn
    // rnn-> batch -> act -> rnn



    private void getConvolutionOutputSize(int[] input, int[] kernel, int[] padding, int[] stride) {
        int[] ret = new int[input.length];
        KernelValidationUtil.validateShapes(input[0], input[1],
                kernel[0], kernel[1], stride[0], stride[1],padding[0], padding[1]);

        for(int i = 0; i < ret.length; i++) {
            ret[i] = (input[i] - kernel[i] + (2 * padding[i])) / stride[i] + 1;
        }
        lastHeight = ret[0];
        lastWidth = ret[1];
    }

    public Layer getLayer(int i, MultiLayerConfiguration.Builder builder) {
        if(builder instanceof NeuralNetConfiguration.ListBuilder) {
            NeuralNetConfiguration.ListBuilder listBuilder = (NeuralNetConfiguration.ListBuilder) builder;
            if(listBuilder.getLayerwise().get(i) == null)
                throw new IllegalStateException("Undefined layer " + i);
            return listBuilder.getLayerwise().get(i).getLayer();
        }

        return builder.getConfs().get(i).getLayer();
    }

    public int getLastHeight() {
        return lastHeight;
    }

    public void setLastHeight(int lastHeight) {
        this.lastHeight = lastHeight;
    }

    public int getLastWidth() {
        return lastWidth;
    }

    public void setLastWidth(int lastWidth) {
        this.lastWidth = lastWidth;
    }

    public int getLastOutChannels() {
        return lastOutChannels;
    }

    public void setLastOutChannels(int lastOutChannels) {
        this.lastOutChannels = lastOutChannels;
    }

    public Map<String, int[]> getOutSizesEachLayer() {
        return nOutsPerLayer;
    }

    public void setOutSizesEachLayer(Map<String, int[]> outSizesEachLayer) {
        this.nOutsPerLayer = outSizesEachLayer;
    }

    public Map<String, Integer> getnInForLayer() {
        return nInsPerLayer;
    }

    public void setnInForLayer(Map<String, Integer> nInForLayer) {
        this.nInsPerLayer = nInForLayer;
    }
}