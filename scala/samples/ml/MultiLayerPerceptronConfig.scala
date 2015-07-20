package org.aja.ml.samples

import org.aja.ml.supervised.annet.{MultiLayerPreceptron, MultiLayerPreceptronConfig}
import org.aja.ml.core.XTimeSeries
import org.aja.ml.core.Aja

object ConfigSample extends App {

  private val ALPHA = 0.9
  private val ETA = 0.1
  private val SIZE_HIDDEN_LAYER = 5
  private val TEST_SIZE = 30
  private val NUM_EPOCHS = 250
  private val NOISE_RATIO = 0.7
  private val EPS = 1e-4

  val noise = () => NOISE_RATIO*Random.nextDouble
  val f1 = (x: Double) => x*(1.0 + noise())
  val f2 = (x: Double) => x*x*(1.0 + noise())
	
  def vec1(x: Double): DblVector = Array[Double](f1(x), noise(), f2(x), noise())
  def vec2(x: Double): DblVector = Array[Double](noise(), noise())
	 	 
  val x = XTSeries[DblVector](Array.tabulate(TEST_SIZE)(vec1(_)))
  val y = XTSeries[DblVector](Array.tabulate(TEST_SIZE)(vec2(_) ))
	     
   // Normalization._
   val features: XTSeries[DblVector] = XTSeries.normalize(x).get
   val labels = XTSeries.normalize(y).get.toArray
	      
   if( !args.isEmpty ) {
     args(0) match {
       case "alpha" => eval(-1.0, ETA, features, labels)
       case "eta" =>  eval(ALPHA, -1, features, labels)
       case _ => eval(-1.0, -1.0, features, labels)
				}
			}
			else 
				eval(-1.0, -1.0, features, labels)

  private def eval(
                    alpha: Double,
                    eta: Double,
                    features: XTSeries[DblVector],
                    labels: DblMatrix): Int =
    _eval(alpha, eta, features, labels)

  private def _eval(alpha: Double, eta: Double, features: DblMatrix, labels: DblMatrix): Int = {
    implicit val mlpObjective = new MLP.MLPBinClassifier

    Try {
      (0.001 until 0.01 by 0.002).foreach( x =>  {
        val _alpha = if(alpha < 0.0)  x else ALPHA
        val _eta = if(eta < 0.0) x else ETA
        val config = MLPConfig(_alpha, _eta, Array[Int](SIZE_HIDDEN_LAYER), NUM_EPOCHS, EPS)

        val mlp = MLP[Double](config, features, labels)
        assert( mlp.model != None,
          s"$name run failed for eta = $eta and alpha = $alpha")
        DisplayUtils.show(s"$name run for eta = $eta and alpha = $alpha ${mlp.model.get.toString}",
          logger)
      })
      1
    } match {
      case Success(n) => n
      case Failure(e) => DisplayUtils.error(s"$name run", logger, e)
    }
  }
}
