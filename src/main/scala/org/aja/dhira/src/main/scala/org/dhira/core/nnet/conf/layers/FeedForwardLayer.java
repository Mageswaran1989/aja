package org.deeplearning4j.nn.conf.layers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.deeplearning4j.nn.conf.InputPreProcessor;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;

/**
 * Created by jeffreytang on 7/21/15.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class FeedForwardLayer extends Layer {
    protected int nIn;
    protected int nOut;

    public FeedForwardLayer(Builder builder) {
        super(builder);
        this.nIn = builder.nIn;
        this.nOut = builder.nOut;
    }


    @Override
    public InputType getOutputType(InputType inputType) {
        if (inputType == null || (inputType.getType() != InputType.Type.FF && inputType.getType() != InputType.Type.CNNFlat)) {
            throw new IllegalStateException("Invalid input type (layer name=\"" + getLayerName() + "\"): expected FeedForward input type. Got: " + inputType);
        }

        return InputType.feedForward(nOut);
    }

    @Override
    public void setNIn(InputType inputType, boolean override) {
        if (inputType == null || (inputType.getType() != InputType.Type.FF && inputType.getType() != InputType.Type.CNNFlat)) {
            throw new IllegalStateException("Invalid input type (layer name=\"" + getLayerName() + "\"): expected FeedForward input type. Got: " + inputType);
        }

        if (nIn <= 0 || override) {
            if(inputType.getType() == InputType.Type.FF){
                InputType.InputTypeFeedForward f = (InputType.InputTypeFeedForward) inputType;
                this.nIn = f.getSize();
            } else {
                InputType.InputTypeConvolutionalFlat f = (InputType.InputTypeConvolutionalFlat) inputType;
                this.nIn = f.getFlattenedSize();
            }
        }
    }

    @Override
    public InputPreProcessor getPreProcessorForInputType(InputType inputType) {
        if (inputType == null) {
            throw new IllegalStateException("Invalid input for layer (layer name = \"" + getLayerName() + "\"): input type is null");
        }

        switch (inputType.getType()) {
            case FF:
            case CNNFlat:
                //FF -> FF and CNN (flattened format) -> FF: no preprocessor necessary
                return null;
            case RNN:
                //RNN -> FF
                return new RnnToFeedForwardPreProcessor();
            case CNN:
                //CNN -> FF
                InputType.InputTypeConvolutional c = (InputType.InputTypeConvolutional)inputType;
                return new CnnToFeedForwardPreProcessor(c.getHeight(),c.getWidth(),c.getDepth());
            default:
                throw new RuntimeException("Unknown input type: " + inputType);
        }
    }

    public abstract static class Builder<T extends Builder<T>> extends Layer.Builder<T> {
        protected int nIn = 0;
        protected int nOut = 0;

        public T nIn(int nIn) {
            this.nIn = nIn;
            return (T) this;
        }

        public T nOut(int nOut) {
            this.nOut = nOut;
            return (T) this;
        }
    }
}
