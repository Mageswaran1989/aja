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
package org.dhira.core.optimize.solvers

import org.dhira.core.containers.Pair
import org.dhira.core.exception.InvalidStepException
import org.dhira.core.nnet.api.Layer
import org.dhira.core.nnet.api.Model
import org.dhira.core.nnet.api.Updater
import org.dhira.core.nnet.conf.LearningRatePolicy
import org.dhira.core.nnet.conf.NeuralNetConfiguration
import org.dhira.core.nnet.gradient.Gradient
import org.dhira.core.nnet.graph.ComputationGraph
import org.dhira.core.nnet.updater.UpdaterCreator
import org.dhira.core.nnet.updater.graph.ComputationGraphUpdater
import org.dhira.core.optimize.api.ConvexOptimizer
import org.dhira.core.optimize.api.IterationListener
import org.dhira.core.optimize.api.StepFunction
import org.dhira.core.optimize.api.TerminationCondition
import org.dhira.core.optimize.stepfunctions.NegativeDefaultStepFunction
import org.dhira.core.optimize.stepfunctions.NegativeGradientStepFunction
import org.dhira.core.optimize.terminations.EpsTermination
import org.dhira.core.optimize.terminations.ZeroDirection
import org.nd4j.linalg.api.ndarray.INDArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util._
import java.util.concurrent.ConcurrentHashMap

/**
 * Base optimizer
 * @author Adam Gibson
 */
object BaseOptimizer {
  protected val log: Logger = LoggerFactory.getLogger(classOf[BaseOptimizer])
  val GRADIENT_KEY: String = "g"
  val SCORE_KEY: String = "score"
  val PARAMS_KEY: String = "params"
  val SEARCH_DIR: String = "searchDirection"

  def getDefaultStepFunctionForOptimizer(optimizerClass: Class[_ <: ConvexOptimizer]): StepFunction = {
    if (optimizerClass eq classOf[Nothing]) {
      return new NegativeGradientStepFunction
    }
    else {
      return new NegativeDefaultStepFunction
    }
  }
}

abstract class BaseOptimizer extends ConvexOptimizer {
  protected var conf: NeuralNetConfiguration = null
  protected var iteration: Int = 0
  protected var stepFunction: StepFunction = null
  protected var iterationListeners: Iterable[IterationListener] = _ // new List[IterationListener]
  protected var terminationConditions: Iterable[TerminationCondition] = _//new ArrayList[E]
  protected var model: Model = null
  protected var lineMaximizer: BackTrackLineSearch = null
  protected var updater: Updater = null
  protected var computationGraphUpdater: ComputationGraphUpdater = null
  protected var step: Double = .0
  private var batchSizeValue: Int = 0
  protected var scoreValue: Double = .0
  protected var oldScore: Double = .0
  protected var stepMax: Double = Double.MaxValue
  protected var searchState: Map[String, AnyRef] = new ConcurrentHashMap[String, AnyRef]

  /**
   *
   * @param conf
   * @param stepFunction
   * @param iterationListeners
   * @param model
   */
  def this(conf: NeuralNetConfiguration, stepFunction: StepFunction, iterationListeners: Iterable[IterationListener], model: Model) {
    this(conf, stepFunction, iterationListeners, Arrays.asList(new ZeroDirection, new EpsTermination), model)
  }

  /**
   *
   * @param conf
   * @param stepFunction
   * @param iterationListeners
   * @param terminationConditions
   * @param model
   */
  def this(conf: NeuralNetConfiguration, stepFunction: StepFunction, iterationListeners: Iterable[IterationListener],
           terminationConditions: Iterable[TerminationCondition], model: Model) {
    this()
    this.conf = conf
    this.stepFunction = (if (stepFunction != null) stepFunction else BaseOptimizer.getDefaultStepFunctionForOptimizer(this.getClass))
    this.iterationListeners = if (iterationListeners != null) iterationListeners else List[IterationListener]
    this.terminationConditions = terminationConditions
    this.model = model
    lineMaximizer = new Nothing(model, this.stepFunction, this)
    lineMaximizer.setStepMax(stepMax)
    lineMaximizer.setMaxIterations(conf.getMaxNumLineSearchIterations)
  }

  def score(): Double = {
    model.computeGradientAndScore
    return model.score
  }

  def getUpdater: Updater = {
    if (updater == null) {
      updater = UpdaterCreator.getUpdater(model)
    }
    return updater
  }

  def setUpdater(updater: Nothing) {
    this.updater = updater
  }

  def getComputationGraphUpdater: ComputationGraphUpdater = {
    if (computationGraphUpdater == null && model.isInstanceOf[ComputationGraph]) {
      computationGraphUpdater = new ComputationGraphUpdater(model.asInstanceOf[ComputationGraph])
    }
    return computationGraphUpdater
  }

  def setUpdaterComputationGraph(updater: ComputationGraphUpdater) {
    this.computationGraphUpdater = updater
  }

  def setListeners(listeners: Collection[Nothing]) {
    if (listeners == null) this.iterationListeners = Collections.emptyList
    else this.iterationListeners = listeners
  }

  def getConf: NeuralNetConfiguration = {
    return conf
  }

  def gradientAndScore: Pair[Gradient, Double] = {
    oldScore = score
    model.computeGradientAndScore
    val pair: Pair[Gradient, Double] = model.gradientAndScore
    this.scoreValue = pair.getSecond
    updateGradientAccordingToParams(pair.getFirst, model, model.batchSize)
    return pair
  }

  /**
   * Optimize call. This runs the optimizer.
   * @return whether it converged or not
   */
  def optimize: Boolean = {
    var gradient: INDArray = null
    var searchDirection: INDArray = null
    var parameters: INDArray = null
    model.validateInput
    var pair: Pair[Gradient, Double] = gradientAndScore
    if (searchState.isEmpty) {
      searchState.put(BaseOptimizer.GRADIENT_KEY, pair.getFirst.gradient)
      setupSearchState(pair)
    }
    else {
      searchState.put(BaseOptimizer.GRADIENT_KEY, pair.getFirst.gradient)
    }
    import scala.collection.JavaConversions._
    for (condition <- terminationConditions) {
      if (condition.terminate(0.0, 0.0, Array[AnyRef](pair.getFirst.gradient))) {
        BaseOptimizer.log.info("Hit termination condition " + condition.getClass.getName)
        return true
      }
    }
    preProcessLine
    {
      var i: Int = 0
      while (i < conf.getNumIterations) {
        {
          gradient = searchState.get(BaseOptimizer.GRADIENT_KEY).asInstanceOf[INDArray]
          searchDirection = searchState.get(BaseOptimizer.SEARCH_DIR).asInstanceOf[INDArray]
          parameters = searchState.get(BaseOptimizer.PARAMS_KEY).asInstanceOf[INDArray]
          try {
            step = lineMaximizer.optimize(parameters, gradient, searchDirection)
          }
          catch {
            case e: InvalidStepException => {
              BaseOptimizer.log.warn("Invalid step...continuing another iteration: {}", e.getMessage)
              step = 0.0
            }
          }
          if (step != 0.0) {
            stepFunction.step(parameters, searchDirection, step)
            model.setParams(parameters)
          }
          else {
            BaseOptimizer.log.debug("Step size returned by line search is 0.0.")
          }
          pair = gradientAndScore
          postStep(pair.getFirst.gradient)
          import scala.collection.JavaConversions._
          for (listener <- iterationListeners) listener.iterationDone(model, i)
          checkTerminalConditions(pair.getFirst.gradient, oldScore, score, i)
          this.iteration += 1
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return true
  }

  protected def postFirstStep(gradient: INDArray) {
  }

  def checkTerminalConditions(gradient: INDArray, oldScore: Double, score: Double, i: Int): Boolean = {
    import scala.collection.JavaConversions._
    for (condition <- terminationConditions) {
      if (condition.terminate(score, oldScore, Array[AnyRef](gradient))) {
        BaseOptimizer.log.debug("Hit termination condition on iteration {}: score={}, oldScore={}, condition={}", i, score, oldScore, condition)
        if (condition.isInstanceOf[EpsTermination] && conf.getLayer != null && conf.getLearningRatePolicy eq LearningRatePolicy.Score) {
          model.applyLearningRateScoreDecay
        }
        return true
      }
    }
    return false
  }

  def batchSize: Int = {
    batchSizeValue
  }

  def setBatchSize(batchSize: Int) {
    this.batchSizeValue = batchSize
  }

  /**
   * Pre preProcess to setup initial searchDirection approximation
   */
  def preProcessLine {
  }

  /**
   * Post step to update searchDirection with new gradient and parameter information
   */
  def postStep(gradient: INDArray) {
  }

  def updateGradientAccordingToParams(gradient: Gradient, model: Nothing, batchSize: Int) {
    if (model.isInstanceOf[ComputationGraph]) {
      val graph: ComputationGraph = model.asInstanceOf[ComputationGraph]
      if (computationGraphUpdater == null) {
        computationGraphUpdater = new ComputationGraphUpdater(graph)
      }
      computationGraphUpdater.update(graph, gradient, iteration, batchSize)
    }
    else {
      if (updater == null) updater = UpdaterCreator.getUpdater(model)
      val layer: Nothing = model.asInstanceOf[Nothing]
      updater.update(layer, gradient, iteration, batchSize)
    }
  }

  /**
   * Setup the initial search state
   * @param pair
   */
  def setupSearchState(pair: Pair[Gradient, Double]) {
    val gradient: INDArray = pair.getFirst.gradient(conf.variables)
    val params: INDArray = model.params.dup
    searchState.put(BaseOptimizer.GRADIENT_KEY, gradient)
    searchState.put(BaseOptimizer.SCORE_KEY, pair.getSecond)
    searchState.put(BaseOptimizer.PARAMS_KEY, params)
  }
}