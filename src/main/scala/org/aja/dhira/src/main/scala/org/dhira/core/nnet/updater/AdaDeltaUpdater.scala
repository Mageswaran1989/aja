package org.dhira.core.nnet.updater

import org.deeplearning4j.nn.api.Layer
import org.nd4j.linalg.learning.AdaDelta
import org.nd4j.linalg.learning.GradientUpdater

/**
 * @author Adam Gibson
 */
class AdaDeltaUpdater extends BaseUpdater {
  def init {
  }

  def init(variable: String, layer: Nothing): GradientUpdater = {
    var updater: GradientUpdater = updaterForVariable.get(variable)
    if (updater == null) {
      updater = new AdaDelta(layer.conf.getLayer.getRho, layer.conf.getLayer.getEpsilon)
      updaterForVariable.put(variable, updater)
    }
    return updater
  }
}