package org.dhira.core.nnet.conf

/**
 * Learning Rate Policy
 *
 * How to decay learning rate during training.
 *
 * <p><b>None</b> = do not apply decay policy aka fixed in Caffe <br>
 * <p><b>Exponential</b> = applies decay rate to the power of the # batches  <br>
 * <p><b>Inverse</b> = divide learning rate by negative (1 + decay rate * # batches)^power <br>
 * <p><b>Poly</b> = polynomial decay that hits 0 when iterations are complete <br>
 * <p><b>Sigmoid</b> = sigmoid decay rate <br>
 * <p><b>Step</b> = decay rate to the power of the floor (nearest integer) of # of batches by # of steps <br>
 * <p><b>Schedule</b> = rate to use at a specific iteration <br>
 * <p><b>Score</b> = apply decay when score stops improving <br>
 */
object LearningRatePolicy extends Enumeration {
  type LearningRatePolicy = Value
  val None, Exponential, Inverse, Poly, Sigmoid, Step, TorchStep, Schedule, Score = Value
}