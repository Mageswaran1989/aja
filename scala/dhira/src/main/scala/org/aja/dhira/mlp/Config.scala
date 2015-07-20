package org.aja.dhira.mlp

/**
 * <p>Class that defines the configuration for the Multi-layer Perceptron. The validation
 * of the configuration/tuning parameters for the MLP is defined in this class.</p>
 * @constructor Creates a configuration object for the neural network.
 * @param alpha  Momentum parameter used to adjust the value of the gradient of the weights
 * with previous value (smoothing)
 * @param eta   Learning rate [0, 1] used in the computation of the gradient of the weights
 * during training
 * @param hidLayers  Sequence of number of neurons for the hidden layers
 * @param numEpochs  Number of epochs or iterations allowed to train the weights/model
 * @param eps  Convergence criteria used as exit condition of the convergence toward optimum
 * weights that minimize the sum of squared error
 * @param activation Activation function (sigmoid or tanh) that computes the output of hidden
 * layers during forward propagation
 *
 * @throws IllegalArgumentException if one of the class parameters is either out of bounds or
 * undefined *
 * @author Mageswaran
 * @since MaJuly 20, 2015
 * @note Scala for Machine Learning Chapter 9 Artificial Neural Network/Multilayer perceptron
 */
final class Config( val alpha: Double,
                       val eta: Double,
                       val hidLayers: Array[Int],
                       val numEpochs: Int,
                       val eps: Double,
                       val activation: Double => Double)
{
  import Config._

  check(alpha, eta, numEpochs)

  /**
   * <p>Return the id of the output layer.</p>
   * @return 1 if there is no hidden layers, the id of the last hidden layer + 1, otherwise
   */
  final def outLayerId: Int = if(hidLayers.isEmpty) 1 else hidLayers.size+1

  /**
   * <p>Return the number of hidden layers in this Neural network.</p>
   * @return 0 if there is no hidden layer, the size of the hidLayer array or sequence otherwise
   */
  final def nHiddens: Int = if(hidLayers.isEmpty) 0 else hidLayers.size
}



/**
 * <p>Companion object for the MLConfig class. This singleton defines the boundary
 * values for the parameters of the class and the different variation of constructors.</p
 *
 * @author Mageswaran
 * @since May 4, 2014
 * @note Scala for Machine Learning Chapter 9 Artificial Neural Network/Multilayer perceptron
 */
object Config {
  private val EPS: Double = 1e-17
  private val ALPHA_LIMITS = (0.0, 1.0)
  private val ETA_LIMITS = (1e-5, 1.0)
  private val NUM_EPOCHS_LIMITS = (2, 5000)

  /**
   * Default constructor for the MLP class
   * @param alpha  Momentum parameter used to adjust the value of the gradient of the weights
   * with previous value (smoothing)
   * @param eta   Learning rate ]0, 1] used in the computation of the gradient of the weights
   * during training
   * @param hiddenLayers  Sequence of number of neurons for the hidden layers
   * @param numEpochs  Number of epochs or iterations allowed to train the weights/model
   * @param eps  Convergence criteria used as exit condition of the convergence toward optimum
   * weights that minimize the sum of squared error
   * @param activation Activation function (sigmoid or tanh) that computes the output of hidden
   * layers during forward propagation
   */
  def apply(
             alpha: Double,
             eta: Double,
             hiddenLayers: Array[Int],
             numEpochs: Int,
             eps: Double,
             activation: Double => Double): Config =
    new Config(alpha, eta, hiddenLayers, numEpochs, eps, activation)

  /**
   * Constructor for the MLP class with a single hidden layer
   * @param alpha  Momentum parameter used to adjust the value of the gradient of the weights
   * with previous value (smoothing)
   * @param eta   Learning rate ]0, 1] used in the computation of the gradient of the weights
   * during training
   * @param numHiddenNeurons Number of neuron or unit in the hidden layer
   * @param numEpochs  Number of epochs or iterations allowed to train the weights/model
   * @param eps  Convergence criteria used as exit condition of the convergence toward optimum
   * weights that minimize the sum of squared error
   * @param activation Activation function (sigmoid or tanh) that computes the output of hidden
   * layers during forward propagation
   */
  def apply(
             alpha: Double,
             eta: Double,
             numHiddenNeurons: Int,
             numEpochs: Int,
             eps: Double,
             activation: Double => Double): Config =
    new Config(alpha, eta, Array[Int](numHiddenNeurons), numEpochs, eps, activation)

  /**
   * Constructor for the MLP class with a logistic function as activation
   * @param alpha  Momentum parameter used to adjust the value of the gradient of the weights
   * with previous value (smoothing)
   * @param eta   Learning rate ]0, 1] used in the computation of the gradient of the weights
   * during training
   * @param hiddenLayers  Sequence of number of neurons for the hidden layers
   * @param numEpochs  Number of epochs or iterations allowed to train the weights/model
   * @param eps  Convergence criteria used as exit condition of the convergence toward optimum
   * weights that minimize the sum of squared error
   */
  def apply(
             alpha: Double,
             eta: Double,
             hiddenLayers: Array[Int],
             numEpochs: Int,
             eps: Double): Config =
    new Config(alpha, eta, hiddenLayers, numEpochs, eps, (x:Double) =>{1.0/(1.0 + Math.exp(-x))})

  /**
   * Constructor for the MLP class with a logistic function as activation and
   * a predefined number of epochs.
   * @param alpha  Momentum parameter used to adjust the value of the gradient of the weights
   * with previous value (smoothing)
   * @param eta   Learning rate ]0, 1] used in the computation of the gradient of the weights
   * during training
   * @param hiddenLayers  Sequence of number of neurons for the hidden layers
   * @param eps  Number of epochs or iterations allowed to train the weights/model
   */
  def apply(alpha: Double, eta: Double, hiddenLayers: Array[Int], eps: Double): Config =
    new Config(alpha, eta, hiddenLayers, NUM_EPOCHS_LIMITS._2, eps,
      (x: Double) => { 1.0/(1.0 + Math.exp(-x))})

  /**
   * Constructor for the MLP class with a single hidden layer , a predefined
   * number of epochs for training and the logistic function as activation function
   * @param alpha  Momentum parameter used to adjust the value of the gradient of the weights
   * with previous value (smoothing)
   * @param eta   Learning rate ]0, 1] used in the computation of the gradient of the weights
   * during training
   * @param numHiddenNeurons Number of neuron or unit in the hidden layer
   * @param eps  Convergence criteria used as exit condition of the convergence toward optimum
   * weights that minimize the sum of squared error
   */
  def apply(alpha: Double, eta: Double, numHiddenNeurons: Int, eps: Double): Config =
    new Config(alpha, eta, Array[Int](numHiddenNeurons), NUM_EPOCHS_LIMITS._2, eps,
      (x: Double) => {1.0/(1.0 + Math.exp(-x))})


  private def check(alpha: Double, eta: Double, numEpochs: Int): Unit ={
    require(alpha >= ALPHA_LIMITS._1 && alpha <= ALPHA_LIMITS._2,
      s"Momentum factor, alpha $alpha is out of bounds")
    require(eta >= ETA_LIMITS._1 && eta <= ETA_LIMITS._2,
      s"Learning rate eta for the Neural Network $eta is out of range")
    require(numEpochs > 1, s"Number of epoch $numEpochs for the Neural Network should be > 1")
  }
}