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
package org.dhira.core.nnet.conf

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import lombok.Data
import lombok.NoArgsConstructor
import org.apache.commons.lang3.ClassUtils
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.distribution.Distribution
import org.deeplearning4j.nn.conf.distribution.NormalDistribution
import org.deeplearning4j.nn.conf.layers.Layer
import org.deeplearning4j.nn.conf.stepfunctions.StepFunction
import org.deeplearning4j.nn.params.DefaultParamInitializer
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.factory.Nd4j
import org.reflections.Reflections
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.Serializable
import java.lang.reflect.Modifier
import java.util._

/**
 * A Serializable configuration
 * for neural nets that covers per layer parameters
 *
 * @author Adam Gibson
 */
@Data
@NoArgsConstructor object NeuralNetConfiguration {
  private val log: Logger = LoggerFactory.getLogger(classOf[NeuralNetConfiguration])

  /**
   * Fluent interface for building a list of configurations
   */
  class ListBuilder extends MultiLayerConfiguration.Builder {
    private var layerwise: Map[Integer, NeuralNetConfiguration.Builder] = null
    private var globalConfig: NeuralNetConfiguration.Builder = null

    def this(globalConfig: NeuralNetConfiguration.Builder, layerMap: Map[Integer, NeuralNetConfiguration.Builder]) {
      this()
      this.globalConfig = globalConfig
      this.layerwise = layerMap
    }

    def this(globalConfig: NeuralNetConfiguration.Builder) {
      this()
      `this`(globalConfig, new HashMap[Integer, NeuralNetConfiguration.Builder])
    }

    def backprop(backprop: Boolean): NeuralNetConfiguration.ListBuilder = {
      this.backprop = backprop
      return this
    }

    def pretrain(pretrain: Boolean): NeuralNetConfiguration.ListBuilder = {
      this.pretrain = pretrain
      return this
    }

    def layer(ind: Int, layer: Nothing): NeuralNetConfiguration.ListBuilder = {
      if (layerwise.containsKey(ind)) {
        layerwise.get(ind).layer(layer)
      }
      else {
        layerwise.put(ind, globalConfig.clone.layer(layer))
      }
      return this
    }

    def getLayerwise: Map[Integer, NeuralNetConfiguration.Builder] = {
      return layerwise
    }

    /**
     * Build the multi layer network
     * based on this neural network and
     * overr ridden parameters
     * @return the configuration to build
     */
    def build: Nothing = {
      val list: List[NeuralNetConfiguration] = new ArrayList[NeuralNetConfiguration]
      if (layerwise.isEmpty) throw new IllegalStateException("Invalid configuration: no layers defined")
      {
        var i: Int = 0
        while (i < layerwise.size) {
          {
            if (layerwise.get(i) == null) {
              throw new IllegalStateException("Invalid configuration: layer number " + i + " not specified. Expect layer " + "numbers to be 0 to " + (layerwise.size - 1) + " inclusive (number of layers defined: " + layerwise.size + ")")
            }
            if (layerwise.get(i).getLayer == null) throw new IllegalStateException("Cannot construct network: Layer config for" + "layer with index " + i + " is not defined)")
            list.add(layerwise.get(i).build)
          }
          ({
            i += 1; i - 1
          })
        }
      }
      return new Nothing().backprop(backprop).inputPreProcessors(inputPreProcessors).pretrain(pretrain).backpropType(backpropType).tBPTTForwardLength(tbpttFwdLength).tBPTTBackwardLength(tbpttBackLength).redistributeParams(redistributeParams).cnnInputSize(this.cnnInputSize).setInputType(this.inputType).confs(list).build
    }
  }

  /**
   * Create a neural net configuration from json
   * @param json the neural net configuration from json
   * @return
   */
  def fromYaml(json: String): NeuralNetConfiguration = {
    val mapper: ObjectMapper = mapperYaml
    try {
      val ret: NeuralNetConfiguration = mapper.readValue(json, classOf[NeuralNetConfiguration])
      return ret
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Create a neural net configuration from json
   * @param json the neural net configuration from json
   * @return
   */
  def fromJson(json: String): NeuralNetConfiguration = {
    val mapper: ObjectMapper = mapper
    try {
      val ret: NeuralNetConfiguration = mapper.readValue(json, classOf[NeuralNetConfiguration])
      return ret
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Object mapper for serialization of configurations
   * @return
   */
  def mapperYaml: ObjectMapper = {
    return mapperYaml
  }

  private val mapperYaml: ObjectMapper = initMapperYaml

  private def initMapperYaml: ObjectMapper = {
    val ret: ObjectMapper = new ObjectMapper(new YAMLFactory)
    configureMapper(ret)
    return ret
  }

  /**
   * Object mapper for serialization of configurations
   * @return
   */
  def mapper: ObjectMapper = {
    return mapper
  }

  /** Reinitialize and return the Jackson/json ObjectMapper with additional named types.
    * This can be used to add additional subtypes at runtime (i.e., for JSON mapping with
    * types defined outside of the main DL4J codebase)
    */
  def reinitMapperWithSubtypes(additionalTypes: Collection[NamedType]): ObjectMapper = {
    mapper.registerSubtypes(additionalTypes.toArray(new Array[NamedType](additionalTypes.size)))
    mapper = mapper.copy
    return mapper
  }

  private var mapper: ObjectMapper = initMapper

  private def initMapper: ObjectMapper = {
    val ret: ObjectMapper = new ObjectMapper
    configureMapper(ret)
    return ret
  }

  private def configureMapper(ret: ObjectMapper) {
    ret.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    ret.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    ret.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
    ret.enable(SerializationFeature.INDENT_OUTPUT)
    val reflections: Reflections = new Reflections
    val subTypes: Set[Class[_ <: Nothing]] = reflections.getSubTypesOf(classOf[Nothing])
    val ac: AnnotatedClass = AnnotatedClass.construct(classOf[Nothing], ret.getSerializationConfig.getAnnotationIntrospector, null)
    val types: Collection[NamedType] = ret.getSubtypeResolver.collectAndResolveSubtypes(ac, ret.getSerializationConfig, ret.getSerializationConfig.getAnnotationIntrospector)
    val registeredSubtypes: Set[Class[_]] = new HashSet[Class[_]]
    import scala.collection.JavaConversions._
    for (nt <- types) {
      registeredSubtypes.add(nt.getType)
    }
    val toRegister: List[NamedType] = new ArrayList[NamedType]
    import scala.collection.JavaConversions._
    for (c <- subTypes) {
      if (Modifier.isAbstract(c.getModifiers) || Modifier.isInterface(c.getModifiers)) {
        continue //todo: continue is not supported
      }
      if (!registeredSubtypes.contains(c)) {
        var name: String = null
        if (ClassUtils.isInnerClass(c)) {
          val c2: Class[_] = c.getDeclaringClass
          name = c2.getSimpleName + "$" + c.getSimpleName
        }
        else {
          name = c.getSimpleName
        }
        toRegister.add(new NamedType(c, name))
        log.debug("Registering custom Layer class for JSON serialization: {}", c)
      }
    }
    ret.registerSubtypes(toRegister.toArray(new Array[NamedType](toRegister.size)))
  }

  @Data class Builder extends Cloneable {
    protected var activationFunction: String = "sigmoid"
    protected var weightInit: Nothing = WeightInit.XAVIER
    protected var biasInit: Double = 0.0
    protected var dist: Nothing = null
    protected var learningRate: Double = 1e-1
    protected var biasLearningRate: Double = Double.NaN
    protected var learningRateSchedule: Map[Integer, Double] = null
    protected var lrScoreBasedDecay: Double = .0
    protected var l1: Double = Double.NaN
    protected var l2: Double = Double.NaN
    protected var dropOut: Double = 0
    protected var updater: Nothing = Updater.SGD
    protected var momentum: Double = Double.NaN
    protected var momentumSchedule: Map[Integer, Double] = null
    protected var epsilon: Double = Double.NaN
    protected var rho: Double = Double.NaN
    protected var rmsDecay: Double = Double.NaN
    protected var adamMeanDecay: Double = Double.NaN
    protected var adamVarDecay: Double = Double.NaN
    protected var layer: Nothing = null
    protected var leakyreluAlpha: Double = 0.01
    protected var miniBatch: Boolean = true
    protected var numIterations: Int = 5
    protected var maxNumLineSearchIterations: Int = 5
    protected var seed: Long = System.currentTimeMillis
    protected var useRegularization: Boolean = false
    protected var optimizationAlgo: Nothing = OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT
    protected var stepFunction: Nothing = null
    protected var useDropConnect: Boolean = false
    protected var minimize: Boolean = true
    protected var gradientNormalization: Nothing = GradientNormalization.None
    protected var gradientNormalizationThreshold: Double = 1.0
    protected var learningRatePolicy: Nothing = LearningRatePolicy.None
    protected var lrPolicyDecayRate: Double = Double.NaN
    protected var lrPolicySteps: Double = Double.NaN
    protected var lrPolicyPower: Double = Double.NaN

    /** Process input as minibatch vs full dataset.
      * Default set to true. */
    def miniBatch(miniBatch: Boolean): NeuralNetConfiguration.Builder = {
      this.miniBatch = miniBatch
      return this
    }

    /**
     * Use drop connect: multiply the weight by a binomial sampling wrt the dropout probability.
     * Dropconnect probability is set using {@link #dropOut(double)}; this is the probability of retaining a weight
     * @param useDropConnect whether to use drop connect or not
     * @return the
     */
    def useDropConnect(useDropConnect: Boolean): NeuralNetConfiguration.Builder = {
      this.useDropConnect = useDropConnect
      return this
    }

    /** Objective function to minimize or maximize cost function
      * Default set to minimize true. */
    def minimize(minimize: Boolean): NeuralNetConfiguration.Builder = {
      this.minimize = minimize
      return this
    }

    /** Maximum number of line search iterations.
      * Only applies for line search optimizers: Line Search SGD, Conjugate Gradient, LBFGS
      * is NOT applicable for standard SGD
      * @param maxNumLineSearchIterations > 0
      * @return
      */
    def maxNumLineSearchIterations(maxNumLineSearchIterations: Int): NeuralNetConfiguration.Builder = {
      this.maxNumLineSearchIterations = maxNumLineSearchIterations
      return this
    }

    /** Layer class. */
    def layer(layer: Nothing): NeuralNetConfiguration.Builder = {
      this.layer = layer
      return this
    }

    /** Step function to apply for back track line search.
      * Only applies for line search optimizers: Line Search SGD, Conjugate Gradient, LBFGS
      * Options: DefaultStepFunction (default), NegativeDefaultStepFunction
      * GradientStepFunction (for SGD), NegativeGradientStepFunction */
    def stepFunction(stepFunction: Nothing): NeuralNetConfiguration.Builder = {
      this.stepFunction = stepFunction
      return this
    }

    /** <b>Deprecated</b><br>
      * Create a ListBuilder (for creating a MultiLayerConfiguration) with the specified number of layers, not including input.
      * @param size number of layers in the network
      * @deprecated Manually specifying number of layers in  is not necessary. Use { @link #list()} or { @link #list(Layer...)} methods.
      **/
    def list(size: Int): NeuralNetConfiguration.ListBuilder = {
      return list
    }

    /** Create a ListBuilder (for creating a MultiLayerConfiguration)<br>
      * Usage:<br>
      * <pre>
      * {@code .list()
         * .layer(0,new DenseLayer.Builder()...build())
         * ...
         * .layer(n,new OutputLayer.Builder()...build())
         * }
      * </pre>
      * */
    def list: NeuralNetConfiguration.ListBuilder = {
      return new NeuralNetConfiguration.ListBuilder(this)
    }

    /** Create a ListBuilder (for creating a MultiLayerConfiguration) with the specified layers<br>
      * Usage:<br>
      * <pre>
      * {@code .list(
         *      new DenseLayer.Builder()...build(),
         *      ...,
         *      new OutputLayer.Builder()...build())
         * }
      * </pre>
      * @param layers The layer configurations for the network
      */
    def list(layers: Nothing*): NeuralNetConfiguration.ListBuilder = {
      if (layers == null || layers.length == 0) throw new IllegalArgumentException("Cannot create network with no layers")
      val layerMap: Map[Integer, NeuralNetConfiguration.Builder] = new HashMap[Integer, NeuralNetConfiguration.Builder]
      {
        var i: Int = 0
        while (i < layers.length) {
          {
            val b: NeuralNetConfiguration.Builder = this.clone
            b.layer(layers(i))
            layerMap.put(i, b)
          }
          ({
            i += 1; i - 1
          })
        }
      }
      return new NeuralNetConfiguration.ListBuilder(this, layerMap)
    }

    /**
     * Create a GraphBuilder (for creating a ComputationGraphConfiguration).
     */
    def graphBuilder: Nothing = {
      return new Nothing(this)
    }

    /** Number of optimization iterations. */
    def iterations(numIterations: Int): NeuralNetConfiguration.Builder = {
      this.numIterations = numIterations
      return this
    }

    /** Random number generator seed. Used for reproducability between runs */
    def seed(seed: Int): NeuralNetConfiguration.Builder = {
      this.seed = seed.toLong
      Nd4j.getRandom.setSeed(seed)
      return this
    }

    /** Random number generator seed. Used for reproducability between runs */
    def seed(seed: Long): NeuralNetConfiguration.Builder = {
      this.seed = seed
      Nd4j.getRandom.setSeed(seed)
      return this
    }

    /**
     * Optimization algorithm to use. Most common: OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT
     *
     * @param optimizationAlgo    Optimization algorithm to use when training
     */
    def optimizationAlgo(optimizationAlgo: Nothing): NeuralNetConfiguration.Builder = {
      this.optimizationAlgo = optimizationAlgo
      return this
    }

    /** Whether to use regularization (l1, l2, dropout, etc */
    def regularization(useRegularization: Boolean): NeuralNetConfiguration.Builder = {
      this.useRegularization = useRegularization
      return this
    }

    override def clone: NeuralNetConfiguration.Builder = {
      try {
        val clone: NeuralNetConfiguration.Builder = super.clone.asInstanceOf[NeuralNetConfiguration.Builder]
        if (clone.layer != null) clone.layer = clone.layer.clone
        if (clone.stepFunction != null) clone.stepFunction = clone.stepFunction.clone
        return clone
      }
      catch {
        case e: CloneNotSupportedException => {
          throw new RuntimeException(e)
        }
      }
    }

    /** Activation function / neuron non-linearity
      * Typical values include:<br>
      * "relu" (rectified linear), "tanh", "sigmoid", "softmax",
      * "hardtanh", "leakyrelu", "maxout", "softsign", "softplus"
      */
    def activation(activationFunction: String): NeuralNetConfiguration.Builder = {
      this.activationFunction = activationFunction
      return this
    }

    def leakyreluAlpha(leakyreluAlpha: Double): NeuralNetConfiguration.Builder = {
      this.leakyreluAlpha = leakyreluAlpha
      return this
    }

    /** Weight initialization scheme.
      * @see org.deeplearning4j.nn.weights.WeightInit
      */
    def weightInit(weightInit: Nothing): NeuralNetConfiguration.Builder = {
      this.weightInit = weightInit
      return this
    }

    /**
     * Constant for bias initialization. Default: 0.0
     *
     * @param biasInit    Constant for bias initialization
     */
    def biasInit(biasInit: Double): NeuralNetConfiguration.Builder = {
      this.biasInit = biasInit
      return this
    }

    /** Distribution to sample initial weights from. Used in conjunction with
      * .weightInit(WeightInit.DISTRIBUTION).
      */
    def dist(dist: Nothing): NeuralNetConfiguration.Builder = {
      this.dist = dist
      return this
    }

    /** Learning rate. Defaults to 1e-1 */
    def learningRate(learningRate: Double): NeuralNetConfiguration.Builder = {
      this.learningRate = learningRate
      return this
    }

    /** Bias learning rate. Set this to apply a different learning rate to the bias */
    def biasLearningRate(biasLearningRate: Double): NeuralNetConfiguration.Builder = {
      this.biasLearningRate = biasLearningRate
      return this
    }

    /** Learning rate schedule. Map of the iteration to the learning rate to apply at that iteration. */
    def learningRateSchedule(learningRateSchedule: Map[Integer, Double]): NeuralNetConfiguration.Builder = {
      this.learningRateSchedule = learningRateSchedule
      return this
    }

    /** Rate to decrease learningRate by when the score stops improving.
      * Learning rate is multiplied by this rate so ideally keep between 0 and 1. */
    def learningRateScoreBasedDecayRate(lrScoreBasedDecay: Double): NeuralNetConfiguration.Builder = {
      this.lrScoreBasedDecay = lrScoreBasedDecay
      return this
    }

    /** L1 regularization coefficient.
      * Use with .regularization(true)
      */
    def l1(l1: Double): NeuralNetConfiguration.Builder = {
      this.l1 = l1
      return this
    }

    /** L2 regularization coefficient
      * Use with .regularization(true)
      */
    def l2(l2: Double): NeuralNetConfiguration.Builder = {
      this.l2 = l2
      return this
    }

    /**
     * Dropout probability. This is the probability of <it>retaining</it> an activation. So dropOut(x) will keep an
     * activation with probability x, and set to 0 with probability 1-x.<br>
     * dropOut(0.0) is disabled (default).
     *
     * @param dropOut    Dropout probability (probability of retaining an activation)
     */
    def dropOut(dropOut: Double): NeuralNetConfiguration.Builder = {
      this.dropOut = dropOut
      return this
    }

    /** Momentum rate
      * Used only when Updater is set to {@link Updater#NESTEROVS}
      */
    def momentum(momentum: Double): NeuralNetConfiguration.Builder = {
      this.momentum = momentum
      return this
    }

    /** Momentum schedule. Map of the iteration to the momentum rate to apply at that iteration
      * Used only when Updater is set to {@link Updater#NESTEROVS}
      */
    def momentumAfter(momentumAfter: Map[Integer, Double]): NeuralNetConfiguration.Builder = {
      this.momentumSchedule = momentumAfter
      return this
    }

    /** Gradient updater. For example, Updater.SGD for standard stochastic gradient descent,
      * Updater.NESTEROV for Nesterov momentum, Updater.RSMPROP for RMSProp, etc.
      * @see Updater
      */
    def updater(updater: Nothing): NeuralNetConfiguration.Builder = {
      this.updater = updater
      return this
    }

    /**
     * Ada delta coefficient
     * @param rho
     */
    def rho(rho: Double): NeuralNetConfiguration.Builder = {
      this.rho = rho
      return this
    }

    /**
     * Epsilon value for updaters: Adagrad and Adadelta.
     *
     * @param epsilon    Epsilon value to use for adagrad or
     */
    def epsilon(epsilon: Double): NeuralNetConfiguration.Builder = {
      this.epsilon = epsilon
      return this
    }

    /** Decay rate for RMSProp. Only applies if using .updater(Updater.RMSPROP)
      */
    def rmsDecay(rmsDecay: Double): NeuralNetConfiguration.Builder = {
      this.rmsDecay = rmsDecay
      return this
    }

    /** Mean decay rate for Adam updater. Only applies if using .updater(Updater.ADAM) */
    def adamMeanDecay(adamMeanDecay: Double): NeuralNetConfiguration.Builder = {
      this.adamMeanDecay = adamMeanDecay
      return this
    }

    /** Variance decay rate for Adam updater. Only applies if using .updater(Updater.ADAM) */
    def adamVarDecay(adamVarDecay: Double): NeuralNetConfiguration.Builder = {
      this.adamVarDecay = adamVarDecay
      return this
    }

    /** Gradient normalization strategy. Used to specify gradient renormalization, gradient clipping etc.
      * @param gradientNormalization Type of normalization to use. Defaults to None.
      * @see GradientNormalization
      */
    def gradientNormalization(gradientNormalization: Nothing): NeuralNetConfiguration.Builder = {
      this.gradientNormalization = gradientNormalization
      return this
    }

    /** Threshold for gradient normalization, only used for GradientNormalization.ClipL2PerLayer,
      * GradientNormalization.ClipL2PerParamType, and GradientNormalization.ClipElementWiseAbsoluteValue<br>
      * Not used otherwise.<br>
      * L2 threshold for first two types of clipping, or absolute value threshold for last type of clipping.
      */
    def gradientNormalizationThreshold(threshold: Double): NeuralNetConfiguration.Builder = {
      this.gradientNormalizationThreshold = threshold
      return this
    }

    /** Learning rate decay policy. Used to adapt learning rate based on policy.
      * @param policy Type of policy to use. Defaults to None.
      */
    def learningRateDecayPolicy(policy: Nothing): NeuralNetConfiguration.Builder = {
      this.learningRatePolicy = policy
      return this
    }

    /** Set the decay rate for the learning rate decay policy.
      * @param lrPolicyDecayRate rate.
      */
    def lrPolicyDecayRate(lrPolicyDecayRate: Double): NeuralNetConfiguration.Builder = {
      this.lrPolicyDecayRate = lrPolicyDecayRate
      return this
    }

    /** Set the number of steps used for learning decay rate steps policy.
      * @param lrPolicySteps number of steps
      */
    def lrPolicySteps(lrPolicySteps: Double): NeuralNetConfiguration.Builder = {
      this.lrPolicySteps = lrPolicySteps
      return this
    }

    /** Set the power used for learning rate inverse policy.
      * @param lrPolicyPower power
      */
    def lrPolicyPower(lrPolicyPower: Double): NeuralNetConfiguration.Builder = {
      this.lrPolicyPower = lrPolicyPower
      return this
    }

    private def updaterValidation(layerName: String) {
      if ((!Double.isNaN(momentum) || !Double.isNaN(layer.getMomentum)) && layer.getUpdater ne Updater.NESTEROVS) log.warn("Layer \"" + layerName + "\" momentum has been set but will not be applied unless the updater is set to NESTEROVS.")
      if ((momentumSchedule != null || layer.getMomentumSchedule != null) && layer.getUpdater ne Updater.NESTEROVS) log.warn("Layer \"" + layerName + "\" momentum schedule has been set but will not be applied unless the updater is set to NESTEROVS.")
      if ((!Double.isNaN(adamVarDecay) || (!Double.isNaN(layer.getAdamVarDecay))) && layer.getUpdater ne Updater.ADAM) log.warn("Layer \"" + layerName + "\" adamVarDecay is set but will not be applied unless the updater is set to Adam.")
      if ((!Double.isNaN(adamMeanDecay) || !Double.isNaN(layer.getAdamMeanDecay)) && layer.getUpdater ne Updater.ADAM) log.warn("Layer \"" + layerName + "\" adamMeanDecay is set but will not be applied unless the updater is set to Adam.")
      if ((!Double.isNaN(rho) || !Double.isNaN(layer.getRho)) && layer.getUpdater ne Updater.ADADELTA) log.warn("Layer \"" + layerName + "\" rho is set but will not be applied unless the updater is set to ADADELTA.")
      if ((!Double.isNaN(rmsDecay) || (!Double.isNaN(layer.getRmsDecay))) && layer.getUpdater ne Updater.RMSPROP) log.warn("Layer \"" + layerName + "\" rmsdecay is set but will not be applied unless the updater is set to RMSPROP.")
      layer.getUpdater match {
        case NESTEROVS =>
          if (Double.isNaN(momentum) && Double.isNaN(layer.getMomentum)) {
            layer.setMomentum(0.9)
            log.warn("Layer \"" + layerName + "\" momentum is automatically set to 0.9. Add momentum to configuration to change the value.")
          }
          else if (Double.isNaN(layer.getMomentum)) layer.setMomentum(momentum)
          if (momentumSchedule != null && layer.getMomentumSchedule == null) layer.setMomentumSchedule(momentumSchedule)
          else if (momentumSchedule == null && layer.getMomentumSchedule == null) layer.setMomentumSchedule(new HashMap[Integer, Double])
          break //todo: break is not supported
        case ADAM =>
          if (Double.isNaN(adamMeanDecay) && Double.isNaN(layer.getAdamMeanDecay)) {
            layer.setAdamMeanDecay(0.9)
            log.warn("Layer \"" + layerName + "\" adamMeanDecay is automatically set to 0.9. Add adamVarDecay to configuration to change the value.")
          }
          else if (Double.isNaN(layer.getAdamMeanDecay)) layer.setAdamMeanDecay(adamMeanDecay)
          if (Double.isNaN(adamVarDecay) && Double.isNaN(layer.getAdamVarDecay)) {
            layer.setAdamVarDecay(0.999)
            log.warn("Layer \"" + layerName + "\" adamVarDecay is automatically set to 0.999. Add adamVarDecay to configuration to change the value.")
          }
          else if (Double.isNaN(layer.getAdamVarDecay)) layer.setAdamVarDecay(adamVarDecay)
          break //todo: break is not supported
        case ADADELTA =>
          if (Double.isNaN(layer.getRho)) layer.setRho(rho)
          if (Double.isNaN(epsilon) && Double.isNaN(layer.getEpsilon)) {
            layer.setEpsilon(1e-6)
            log.warn("Layer \"" + layerName + "\" AdaDelta epsilon is automatically set to 1e-6. Add epsilon to configuration to change the value.")
          }
          else if (Double.isNaN(layer.getEpsilon)) {
            layer.setEpsilon(epsilon)
          }
          break //todo: break is not supported
        case ADAGRAD =>
          if (Double.isNaN(epsilon) && Double.isNaN(layer.getEpsilon)) {
            layer.setEpsilon(1e-6)
          }
          else if (Double.isNaN(layer.getEpsilon)) {
            layer.setEpsilon(epsilon)
          }
          break //todo: break is not supported
        case RMSPROP =>
          if (Double.isNaN(rmsDecay) && Double.isNaN(layer.getRmsDecay)) {
            layer.setRmsDecay(0.95)
            log.warn("Layer \"" + layerName + "\" rmsDecay is automatically set to 0.95. Add rmsDecay to configuration to change the value.")
          }
          else if (Double.isNaN(layer.getRmsDecay)) layer.setRmsDecay(rmsDecay)
          break //todo: break is not supported
      }
    }

    private def learningRateValidation(layerName: String) {
      if (learningRatePolicy ne LearningRatePolicy.None && Double.isNaN(lrPolicyDecayRate)) {
        if (!(learningRatePolicy eq LearningRatePolicy.Schedule && learningRateSchedule != null) && !(learningRatePolicy eq LearningRatePolicy.Poly && !Double.isNaN(lrPolicyPower))) throw new IllegalStateException("Layer \"" + layerName + "\" learning rate policy decay rate (lrPolicyDecayRate) must be set to use learningRatePolicy.")
      }
      learningRatePolicy match {
        case Inverse =>
        case Poly =>
          if (Double.isNaN(lrPolicyPower)) throw new IllegalStateException("Layer \"" + layerName + "\" learning rate policy power (lrPolicyPower) must be set to use " + learningRatePolicy)
          break //todo: break is not supported
        case Step =>
        case Sigmoid =>
          if (Double.isNaN(lrPolicySteps)) throw new IllegalStateException("Layer \"" + layerName + "\" learning rate policy steps (lrPolicySteps) must be set to use " + learningRatePolicy)
          break //todo: break is not supported
        case Schedule =>
          if (learningRateSchedule == null) throw new IllegalStateException("Layer \"" + layerName + "\" learning rate policy schedule (learningRateSchedule) must be set to use " + learningRatePolicy)
          break //todo: break is not supported
      }
      if (!Double.isNaN(lrPolicyPower) && (learningRatePolicy ne LearningRatePolicy.Inverse && learningRatePolicy ne LearningRatePolicy.Poly)) throw new IllegalStateException("Layer \"" + layerName + "\" power has been set but will not be applied unless the learning rate policy is set to Inverse or Poly.")
      if (!Double.isNaN(lrPolicySteps) && (learningRatePolicy ne LearningRatePolicy.Step && learningRatePolicy ne LearningRatePolicy.Sigmoid && learningRatePolicy ne LearningRatePolicy.TorchStep)) throw new IllegalStateException("Layer \"" + layerName + "\" steps have been set but will not be applied unless the learning rate policy is set to Step or Sigmoid.")
      if ((learningRateSchedule != null) && (learningRatePolicy ne LearningRatePolicy.Schedule)) throw new IllegalStateException("Layer \"" + layerName + "\" learning rate schedule has been set but will not be applied unless the learning rate policy is set to Schedule.")
    }

    private def generalValidation(layerName: String) {
      if (useDropConnect && (Double.isNaN(dropOut) && (Double.isNaN(layer.getDropOut)))) log.warn("Layer \"" + layerName + "\" dropConnect is set to true but dropout rate has not been added to configuration.")
      if (useDropConnect && dropOut == 0.0) log.warn("Layer \"" + layerName + " dropConnect is set to true but dropout rate is set to 0.0")
      if (useRegularization && (Double.isNaN(l1) && layer != null && Double.isNaN(layer.getL1) && Double.isNaN(l2) && Double.isNaN(layer.getL2) && (Double.isNaN(dropOut) || dropOut == 0.0) && (Double.isNaN(layer.getDropOut) || layer.getDropOut eq 0.0))) log.warn("Layer \"" + layerName + "\" regularization is set to true but l1, l2 or dropout has not been added to configuration.")
      if (layer != null) {
        if (useRegularization) {
          if (!Double.isNaN(l1) && Double.isNaN(layer.getL1)) layer.setL1(l1)
          if (!Double.isNaN(l2) && Double.isNaN(layer.getL2)) layer.setL2(l2)
        }
        else if (!useRegularization && ((!Double.isNaN(l1) && l1 > 0.0) || (!Double.isNaN(layer.getL1) && layer.getL1 > 0.0) || (!Double.isNaN(l2) && l2 > 0.0) || (!Double.isNaN(layer.getL2) && layer.getL2 > 0.0))) log.warn("Layer \"" + layerName + "\" l1 or l2 has been added to configuration but useRegularization is set to false.")
        if (Double.isNaN(l2) && Double.isNaN(layer.getL2)) layer.setL2(0.0)
        if (Double.isNaN(l1) && Double.isNaN(layer.getL1)) layer.setL1(0.0)
        if (layer.getWeightInit eq WeightInit.DISTRIBUTION) {
          if (dist != null && layer.getDist == null) layer.setDist(dist)
          else if (dist == null && layer.getDist == null) {
            layer.setDist(new Nothing(1e-3, 1))
            log.warn("Layer \"" + layerName + "\" distribution is automatically set to normalize distribution with mean 1e-3 and variance 1.")
          }
        }
        else if ((dist != null || layer.getDist != null)) log.warn("Layer \"" + layerName + "\" distribution is set but will not be applied unless weight init is set to WeighInit.DISTRIBUTION.")
      }
    }

    /**
     * Return a configuration based on this builder
     *
     * @return
     */
    def build: NeuralNetConfiguration = {
      val conf: NeuralNetConfiguration = new NeuralNetConfiguration
      conf.minimize = minimize
      conf.maxNumLineSearchIterations = maxNumLineSearchIterations
      conf.layer = layer
      conf.numIterations = numIterations
      conf.useRegularization = useRegularization
      conf.optimizationAlgo = optimizationAlgo
      conf.seed = seed
      conf.stepFunction = stepFunction
      conf.useDropConnect = useDropConnect
      conf.miniBatch = miniBatch
      conf.learningRatePolicy = learningRatePolicy
      conf.lrPolicyDecayRate = lrPolicyDecayRate
      conf.lrPolicySteps = lrPolicySteps
      conf.lrPolicyPower = lrPolicyPower
      var layerName: String = null
      if (layer == null || layer.getLayerName == null) layerName = "Layer not named"
      else layerName = "Layer " + layer.getLayerName
      learningRateValidation(layerName)
      if (layer != null) {
        if (Double.isNaN(layer.getLearningRate)) layer.setLearningRate(learningRate)
        if (Double.isNaN(layer.getBiasLearningRate)) layer.setBiasLearningRate(layer.getLearningRate)
        if (layer.getLearningRateSchedule == null) layer.setLearningRateSchedule(learningRateSchedule)
        if (Double.isNaN(layer.getL1)) layer.setL1(l1)
        if (Double.isNaN(layer.getL2)) layer.setL2(l2)
        if (layer.getActivationFunction == null) layer.setActivationFunction(activationFunction)
        if (layer.getWeightInit == null) layer.setWeightInit(weightInit)
        if (Double.isNaN(layer.getBiasInit)) layer.setBiasInit(biasInit)
        if (Double.isNaN(layer.getDropOut)) layer.setDropOut(dropOut)
        if (layer.getUpdater == null) layer.setUpdater(updater)
        updaterValidation(layerName)
        if (layer.getGradientNormalization == null) layer.setGradientNormalization(gradientNormalization)
        if (Double.isNaN(layer.getGradientNormalizationThreshold)) layer.setGradientNormalizationThreshold(gradientNormalizationThreshold)
      }
      generalValidation(layerName)
      return conf
    }
  }

}

@Data
@NoArgsConstructor class NeuralNetConfiguration extends Serializable with Cloneable {
  protected var layer: Nothing = null
  protected var leakyreluAlpha: Double = .0
  protected var miniBatch: Boolean = true
  protected var numIterations: Int = 0
  protected var maxNumLineSearchIterations: Int = 0
  protected var seed: Long = 0L
  protected var optimizationAlgo: Nothing = null
  protected var variables: List[String] = new ArrayList[String]
  protected var stepFunction: Nothing = null
  protected var useRegularization: Boolean = false
  protected var useDropConnect: Boolean = false
  protected var minimize: Boolean = true
  protected var learningRateByParam: Map[String, Double] = new HashMap[String, Double]
  protected var l1ByParam: Map[String, Double] = new HashMap[String, Double]
  protected var l2ByParam: Map[String, Double] = new HashMap[String, Double]
  protected var learningRatePolicy: Nothing = LearningRatePolicy.None
  protected var lrPolicyDecayRate: Double = .0
  protected var lrPolicySteps: Double = .0
  protected var lrPolicyPower: Double = .0

  /**
   * Creates and returns a deep copy of the configuration.
   */
  override def clone: NeuralNetConfiguration = {
    try {
      val clone: NeuralNetConfiguration = super.clone.asInstanceOf[NeuralNetConfiguration]
      if (clone.layer != null) clone.layer = clone.layer.clone
      if (clone.stepFunction != null) clone.stepFunction = clone.stepFunction.clone
      if (clone.variables != null) clone.variables = new ArrayList[String](clone.variables)
      if (clone.learningRateByParam != null) clone.learningRateByParam = new HashMap[String, Double](clone.learningRateByParam)
      if (clone.l1ByParam != null) clone.l1ByParam = new HashMap[String, Double](clone.l1ByParam)
      if (clone.l2ByParam != null) clone.l2ByParam = new HashMap[String, Double](clone.l2ByParam)
      return clone
    }
    catch {
      case e: CloneNotSupportedException => {
        throw new RuntimeException(e)
      }
    }
  }

  def variables: List[String] = {
    return new ArrayList[String](variables)
  }

  def addVariable(variable: String) {
    if (!variables.contains(variable)) {
      variables.add(variable)
      setLayerParamLR(variable)
    }
  }

  def clearVariables {
    variables.clear
  }

  def setLayerParamLR(variable: String) {
    val lr: Double = if (((variable.substring(0, 1) == DefaultParamInitializer.BIAS_KEY) && !Double.isNaN(layer.getBiasLearningRate))) layer.getBiasLearningRate else layer.getLearningRate
    val l1: Double = if ((variable.substring(0, 1) == DefaultParamInitializer.BIAS_KEY)) 0.0 else layer.getL1
    val l2: Double = if ((variable.substring(0, 1) == DefaultParamInitializer.BIAS_KEY)) 0.0 else layer.getL2
    learningRateByParam.put(variable, lr)
    l1ByParam.put(variable, l1)
    l2ByParam.put(variable, l2)
  }

  def getLearningRateByParam(variable: String): Double = {
    return learningRateByParam.get(variable)
  }

  def setLearningRateByParam(variable: String, rate: Double) {
    learningRateByParam.put(variable, rate)
  }

  def getL1ByParam(variable: String): Double = {
    return l1ByParam.get(variable)
  }

  def getL2ByParam(variable: String): Double = {
    return l2ByParam.get(variable)
  }

  /**
   * Return this configuration as json
   * @return this configuration represented as json
   */
  def toYaml: String = {
    val mapper: ObjectMapper = NeuralNetConfiguration.mapperYaml
    try {
      val ret: String = mapper.writeValueAsString(this)
      return ret
    }
    catch {
      case e: JsonProcessingException => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Return this configuration as json
   * @return this configuration represented as json
   */
  def toJson: String = {
    val mapper: ObjectMapper = NeuralNetConfiguration.mapper
    try {
      val ret: String = mapper.writeValueAsString(this)
      return ret
    }
    catch {
      case e: JsonProcessingException => {
        throw new RuntimeException(e)
      }
    }
  }

  def getExtraArgs: Array[AnyRef] = {
    if (layer == null || layer.getActivationFunction == null) return new Array[AnyRef](0)
    layer.getActivationFunction match {
      case "leakyrelu" =>
        return Array[AnyRef](leakyreluAlpha)
      case "relu" =>
        return Array[AnyRef](0)
      case _ =>
        return Array[AnyRef]
    }
  }
}