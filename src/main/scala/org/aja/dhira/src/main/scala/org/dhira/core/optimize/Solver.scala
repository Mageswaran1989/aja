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
package org.dhira.core.optimize

import org.deeplearning4j.nn.api.Model
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.optimize.api.ConvexOptimizer
import org.deeplearning4j.optimize.api.IterationListener
import org.deeplearning4j.optimize.api.StepFunction
import org.deeplearning4j.optimize.solvers.ConjugateGradient
import org.deeplearning4j.optimize.solvers.LBFGS
import org.deeplearning4j.optimize.solvers.LineGradientDescent
import org.deeplearning4j.optimize.solvers.StochasticGradientDescent
import org.deeplearning4j.optimize.stepfunctions.StepFunctions
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.List

/**
 * Generic purpose solver
 * @author Adam Gibson
 */
object Solver {

  class Builder {
    private var conf: Nothing = null
    private var model: Nothing = null
    private var listeners: List[IterationListener] = new ArrayList[IterationListener]

    def configure(conf: Nothing): Solver.Builder = {
      this.conf = conf
      return this
    }

    def listener(listeners: IterationListener*): Solver.Builder = {
      this.listeners.addAll(Arrays.asList(listeners))
      return this
    }

    def listeners(listeners: Collection[IterationListener]): Solver.Builder = {
      this.listeners.addAll(listeners)
      return this
    }

    def model(model: Nothing): Solver.Builder = {
      this.model = model
      return this
    }

    def build: Solver = {
      val solver: Solver = new Solver
      solver.conf = conf
      solver.stepFunction = StepFunctions.createStepFunction(conf.getStepFunction)
      solver.model = model
      solver.listeners = listeners
      return solver
    }
  }

}

class Solver {
  private var conf: Nothing = null
  private var listeners: Collection[IterationListener] = null
  private var model: Nothing = null
  private var optimizer: Nothing = null
  private var stepFunction: StepFunction = null

  def optimize {
    if (optimizer == null) optimizer = getOptimizer
    optimizer.optimize
  }

  def getOptimizer: Nothing = {
    if (optimizer != null) return optimizer
    conf.getOptimizationAlgo match {
      case LBFGS =>
        optimizer = new LBFGS(conf, stepFunction, listeners, model)
        break //todo: break is not supported
      case LINE_GRADIENT_DESCENT =>
        optimizer = new LineGradientDescent(conf, stepFunction, listeners, model)
        break //todo: break is not supported
      case CONJUGATE_GRADIENT =>
        optimizer = new ConjugateGradient(conf, stepFunction, listeners, model)
        break //todo: break is not supported
      case STOCHASTIC_GRADIENT_DESCENT =>
        optimizer = new StochasticGradientDescent(conf, stepFunction, listeners, model)
        break //todo: break is not supported
      case _ =>
        throw new IllegalStateException("No optimizer found")
    }
    return optimizer
  }

  def setListeners(listeners: Collection[IterationListener]) {
    this.listeners = listeners
    if (optimizer != null) optimizer.setListeners(listeners)
  }
}