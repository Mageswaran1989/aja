package org.dhira.nn.nnet.layers

import lombok._
import org.deeplearning4j.nn.api.Layer
import org.deeplearning4j.nn.api.ParamInitializer
import org.deeplearning4j.nn.conf.InputPreProcessor
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.params.ConvolutionParamInitializer
import org.deeplearning4j.optimize.api.IterationListener
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.convolution.Convolution
import java.util.Collection
import java.util.Map

/**
 * @author Adam Gibson
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true) object ConvolutionLayer {

  @AllArgsConstructor class Builder extends FeedForwardLayer.Builder[ConvolutionLayer.Builder] {
    private var convolutionType: Convolution.Type = Convolution.Type.VALID
    private var kernelSize: Array[Int] = Array[Int](5, 5)
    private var stride: Array[Int] = Array[Int](1, 1)
    private var padding: Array[Int] = Array[Int](0, 0)

    def this(kernelSize: Array[Int], stride: Array[Int], padding: Array[Int]) {
      this()
      this.kernelSize = kernelSize
      this.stride = stride
      this.padding = padding
    }

    def this(kernelSize: Array[Int], stride: Array[Int]) {
      this()
      this.kernelSize = kernelSize
      this.stride = stride
    }

    def this(kernelSize: Int*) {
      this()
      this.kernelSize = kernelSize
    }

    def this() {
      this()
    }

    def convolutionType(convolutionType: Convolution.Type): ConvolutionLayer.Builder = {
      this.convolutionType = convolutionType
      return this
    }

    /**
     * Size of the convolution
     * rows/columns
     * @param kernelSize the height and width of the
     *                   kernel
     * @return
     */
    def kernelSize(kernelSize: Int*): ConvolutionLayer.Builder = {
      this.kernelSize = kernelSize
      return this
    }

    def stride(stride: Int*): ConvolutionLayer.Builder = {
      this.stride = stride
      return this
    }

    def padding(padding: Int*): ConvolutionLayer.Builder = {
      this.padding = padding
      return this
    }

    @SuppressWarnings(Array("unchecked")) def build: ConvolutionLayer = {
      return new ConvolutionLayer(this)
    }
  }

}

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true) class ConvolutionLayer extends FeedForwardLayer {
  protected var convolutionType: Convolution.Type = null
  protected var kernelSize: Array[Int] = null
  protected var stride: Array[Int] = null
  protected var padding: Array[Int] = null

  /**
   * ConvolutionLayer
   * nIn in the input layer is the number of channels
   * nOut is the number of filters to be used in the net or in other words the depth
   * The builder specifies the filter/kernel size, the stride and padding
   * The pooling layer takes the kernel size
   */
  private def this(builder: ConvolutionLayer.Builder) {
    this()
    `super`(builder)
    this.convolutionType = builder.convolutionType
    if (builder.kernelSize.length != 2) throw new IllegalArgumentException("Kernel size of should be rows x columns (a 2d array)")
    this.kernelSize = builder.kernelSize
    if (builder.stride.length != 2) throw new IllegalArgumentException("Stride should include stride for rows and columns (a 2d array)")
    this.stride = builder.stride
    if (builder.padding.length != 2) throw new IllegalArgumentException("Padding should include padding for rows and columns (a 2d array)")
    this.padding = builder.padding
  }

  def clone: ConvolutionLayer = {
    val clone: ConvolutionLayer = super.clone.asInstanceOf[ConvolutionLayer]
    if (clone.kernelSize != null) clone.kernelSize = clone.kernelSize.clone
    if (clone.stride != null) clone.stride = clone.stride.clone
    if (clone.padding != null) clone.padding = clone.padding.clone
    return clone
  }

  def instantiate(conf: NeuralNetConfiguration, iterationListeners: Collection[Nothing], layerIndex: Int, layerParamsView: INDArray, initializeParams: Boolean): Nothing = {
    val ret: Nothing = new Nothing(conf)
    ret.setListeners(iterationListeners)
    ret.setIndex(layerIndex)
    ret.setParamsViewArray(layerParamsView)
    val paramTable: Map[String, INDArray] = initializer.init(conf, layerParamsView, initializeParams)
    ret.setParamTable(paramTable)
    ret.setConf(conf)
    return ret
  }

  def initializer: Nothing = {
    return ConvolutionParamInitializer.getInstance
  }

  def getOutputType(inputType: Nothing): Nothing = {
    if (inputType == null || inputType.getType ne InputType.Type.CNN) {
      throw new IllegalStateException("Invalid input for Convolution layer (layer name=\"" + getLayerName + "\"): Expected CNN input, got " + inputType)
    }
    return InputTypeUtil.getOutputTypeCnnLayers(inputType, kernelSize, stride, padding, nOut, getLayerName)
  }

  def setNIn(inputType: Nothing, `override`: Boolean) {
    if (inputType == null || inputType.getType ne InputType.Type.CNN) {
      throw new IllegalStateException("Invalid input for Convolution layer (layer name=\"" + getLayerName + "\"): Expected CNN input, got " + inputType)
    }
    if (nIn <= 0 || `override`) {
      val c: Nothing = inputType.asInstanceOf[Nothing]
      this.nIn = c.getDepth
    }
  }

  def getPreProcessorForInputType(inputType: Nothing): Nothing = {
    if (inputType == null) {
      throw new IllegalStateException("Invalid input for Convolution layer (layer name=\"" + getLayerName + "\"): input is null")
    }
    return InputTypeUtil.getPreProcessorForInputTypeCnnLayers(inputType, getLayerName)
  }
}