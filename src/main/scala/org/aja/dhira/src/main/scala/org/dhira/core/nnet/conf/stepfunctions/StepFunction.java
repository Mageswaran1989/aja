package org.deeplearning4j.nn.conf.stepfunctions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.io.Serializable;

/**
 * Custom step function for line search.
 */
@JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT)
@JsonSubTypes(value={
        @JsonSubTypes.Type(value = DefaultStepFunction.class, name = "default"),
        @JsonSubTypes.Type(value = GradientStepFunction.class, name = "gradient"),
        @JsonSubTypes.Type(value = NegativeDefaultStepFunction.class, name = "negativeDefault"),
        @JsonSubTypes.Type(value = NegativeGradientStepFunction.class, name = "negativeGradient"),
        })
public class StepFunction implements Serializable, Cloneable {

    private static final long serialVersionUID = -1884835867123371330L;

    @Override
    public StepFunction clone() {
        try {
            StepFunction clone = (StepFunction) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
