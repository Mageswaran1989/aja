package org.aja.dhira.nnql.internal

/**
  * Created by mdhandapani on 10/6/16.
  */
import scala.util.Random
import scala.math

/**
  *
  * @param N
  * @param numInputNeurons
  * @param hiddenLayerSize
  * @param numOutputNeurons
  * @param numLayers
  * @param rng
  */
class DBN(val N: Int, val numInputNeurons: Int, hiddenLayerSize: Array[Int], val numOutputNeurons: Int,
          val numLayers: Int, var rng: Random=null) {

  def sigmoid(x: Double): Double = {
    return 1.0 / (1.0 + math.pow(math.E, -x))
  }


  var inputSize: Int = 0

  val sigmoidLayers: Array[HiddenLayer] = new Array[HiddenLayer](numLayers)
  val rbmLayers: Array[RBM] = new Array[RBM](numLayers)

  if(rng == null) rng = new Random(1234)

  var i: Int = 0
  // construct multi-layer
  for(i <- 0 until numLayers) {
    if(i == 0) {
      inputSize = numInputNeurons
    } else {
      inputSize = hiddenLayerSize(i-1)
    }

    // construct sigmoid_layer
    sigmoidLayers(i) = new HiddenLayer(N, inputSize, hiddenLayerSize(i), null, null, rng)

    // construct rbm_layer
    rbmLayers(i) = new RBM(N, inputSize, hiddenLayerSize(i), sigmoidLayers(i).W, sigmoidLayers(i).b, null, rng)

  }

  // layer for output using LogisticRegression
  val log_layer: LogisticRegression = new LogisticRegression(N, hiddenLayerSize(numLayers-1), numOutputNeurons)


  def pretrain(train_X: Array[Array[Int]], lr: Double, k: Int, epochs: Int) {
    var layer_input: Array[Int] = new Array[Int](0)
    var prev_layer_inputSize: Int = 0
    var prev_layer_input: Array[Int] = new Array[Int](0)

    var i: Int = 0
    var j: Int = 0
    var epoch: Int = 0
    var n: Int = 0
    var l: Int = 0

    for(i <- 0 until numLayers) {  // layer-wise
      for(epoch <- 0 until epochs) {  // training epochs
        for(n <- 0 until N) {  // input x1...xN
          // layer input
          for(l <- 0 to i) {
            if(l == 0) {
              layer_input = new Array[Int](numInputNeurons)
              for(j <- 0 until numInputNeurons) layer_input(j) = train_X(n)(j)

            } else {
              if(l == 1) prev_layer_inputSize = numInputNeurons
              else prev_layer_inputSize = hiddenLayerSize(l-2)

              prev_layer_input = new Array[Int](prev_layer_inputSize)
              for(j <- 0 until prev_layer_inputSize) prev_layer_input(j) = layer_input(j)

              layer_input = new Array[Int](hiddenLayerSize(l-1))
              sigmoidLayers(l-1).sample_h_given_v(prev_layer_input, layer_input)
            }
          }

          rbmLayers(i).contrastive_divergence(layer_input, lr, k)
        }
      }
    }
  }


  def finetune(train_X: Array[Array[Int]], train_Y: Array[Array[Int]], lr: Double, epochs: Int) {
    var layer_input: Array[Int] = new Array[Int](0)
    var prev_layer_input: Array[Int] = new Array[Int](0)

    var epoch: Int = 0
    var n: Int = 0
    var i: Int = 0
    var j: Int = 0

    for(epoch <- 0 until epochs) {
      for(n <- 0 until N) {

        // layer input
        for(i <- 0 until numLayers) {
          if(i == 0) {
            prev_layer_input = new Array[Int](numInputNeurons)
            for(j <- 0 until numInputNeurons) prev_layer_input(j) = train_X(n)(j)
          } else {
            prev_layer_input = new Array[Int](hiddenLayerSize(i-1))
            for(j <- 0 until hiddenLayerSize(i-1)) prev_layer_input(j) = layer_input(j)
          }

          layer_input = new Array[Int](hiddenLayerSize(i))
          sigmoidLayers(i).sample_h_given_v(prev_layer_input, layer_input)
        }

        log_layer.train(layer_input, train_Y(n), lr)
      }
      // lr *= 0.95
    }
  }

  def predict(x: Array[Int], y: Array[Double]) {
    var layer_input: Array[Double] = new Array[Double](0)
    var prev_layer_input: Array[Double] = new Array[Double](numInputNeurons)

    var i: Int = 0
    var j: Int = 0
    var k: Int = 0

    for(j <- 0 until numInputNeurons) prev_layer_input(j) = x(j)

    var linear_outoput: Double = 0

    // layer activation
    for(i <- 0 until numLayers) {
      layer_input = new Array[Double](sigmoidLayers(i).numOutputNeurons)

      for(k <- 0 until sigmoidLayers(i).numOutputNeurons) {
        linear_outoput = 0.0

        for(j <- 0 until sigmoidLayers(i).numInputNeurons) {
          linear_outoput += sigmoidLayers(i).W(k)(j) * prev_layer_input(j)
        }
        linear_outoput += sigmoidLayers(i).b(k)
        layer_input(k) = sigmoid(linear_outoput)
      }

      if(i < numLayers-1) {
        prev_layer_input = new Array[Double](sigmoidLayers(i).numOutputNeurons)
        for(j <- 0 until sigmoidLayers(i).numOutputNeurons) prev_layer_input(j) = layer_input(j)
      }
    }

    for(i <- 0 until log_layer.n_out) {
      y(i) = 0
      for(j <- 0 until log_layer.n_in) {
        y(i) += log_layer.W(i)(j) * layer_input(j)
      }
      y(i) += log_layer.b(i)
    }

    log_layer.softmax(y)
  }

}


object DBN {
  def test_dbn() {
    val rng: Random = new Random(123)

    val pretrain_lr: Double = 0.1
    val pretraining_epochs: Int = 1000
    val k: Int = 1
    val finetune_lr: Double = 0.1
    val finetune_epochs: Int = 500

    val train_N: Int = 6
    val test_N: Int = 4
    val numInputNeurons: Int = 6
    val numOutputNeurons: Int = 2
    val hiddenLayerSize: Array[Int] = Array(3, 3)
    val numLayers = hiddenLayerSize.length


    // training data
    val train_X: Array[Array[Int]] = Array(
      Array(1, 1, 1, 0, 0, 0),
      Array(1, 0, 1, 0, 0, 0),
      Array(1, 1, 1, 0, 0, 0),
      Array(0, 0, 1, 1, 1, 0),
      Array(0, 0, 1, 1, 0, 0),
      Array(0, 0, 1, 1, 1, 0)
    )

    val train_Y: Array[Array[Int]] = Array(
      Array(1, 0),
      Array(1, 0),
      Array(1, 0),
      Array(0, 1),
      Array(0, 1),
      Array(0, 1)
    )

    // construct DBN
    val dbn: DBN = new DBN(train_N, numInputNeurons, hiddenLayerSize, numOutputNeurons, numLayers, rng)

    // pretrain
    dbn.pretrain(train_X, pretrain_lr, k, pretraining_epochs);

    // finetune
    dbn.finetune(train_X, train_Y, finetune_lr, finetune_epochs);


    // test data
    val test_X: Array[Array[Int]] = Array(
      Array(1, 1, 0, 0, 0, 0),
      Array(1, 1, 1, 1, 0, 0),
      Array(0, 0, 0, 1, 1, 0),
      Array(0, 0, 1, 1, 1, 0)
    )

    val test_Y: Array[Array[Double]] = Array.ofDim[Double](test_N, numOutputNeurons)

    var i: Int = 0
    var j: Int = 0

    // test
    for(i <- 0 until test_N) {
      dbn.predict(test_X(i), test_Y(i))
      for(j <- 0 until numOutputNeurons) {
        print(test_Y(i)(j) + " ")
      }
      println()
    }

  }
}
