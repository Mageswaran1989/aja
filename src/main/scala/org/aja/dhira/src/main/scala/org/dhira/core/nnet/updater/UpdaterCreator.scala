package org.dhira.core.nnet.updater

import org.deeplearning4j.nn.api.Model
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork

/**
 *
 *
 * @author Adam Gibson
 */
object UpdaterCreator {
  /**
   * Create an updater based on the configuration
   * @param conf the configuration to get the updater for
   * @return the updater for the configuration
   */
  private def getUpdater(conf: Nothing): Nothing = {
    val updater: Updater = conf.getLayer.getUpdater
    updater match {
      case ADADELTA =>
        return new Nothing
      case ADAGRAD =>
        return new Nothing
      case ADAM =>
        return new Nothing
      case NESTEROVS =>
        return new Nothing
      case RMSPROP =>
        return new Nothing
      case SGD =>
        return new Nothing
      case NONE =>
        return new Nothing
      case CUSTOM =>
        throw new UnsupportedOperationException("Not implemented yet.")
    }
    return null
  }

  def getUpdater(layer: Model): Nothing = {
    if (layer.isInstanceOf[Nothing]) {
      return new Nothing(layer.asInstanceOf[Nothing])
    }
    else {
      return getUpdater(layer.conf)
    }
  }
}

class UpdaterCreator {
  private def this() {
    this()
  }
}