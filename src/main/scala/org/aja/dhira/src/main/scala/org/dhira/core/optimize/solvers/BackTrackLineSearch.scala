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

import org.apache.commons.math3.util.FastMath
import org.dhira.core.exception.InvalidStepException
import org.dhira.core.nnet.api.Model
import org.dhira.core.nnet.conf.stepfunctions.NegativeGradientStepFunction
import org.dhira.core.optimize.api.ConvexOptimizer
import org.dhira.core.optimize.api.LineOptimizer
import org.dhira.core.optimize.api.StepFunction
import org.dhira.core.optimize.stepfunctions.NegativeDefaultStepFunction
import org.nd4j.linalg.api.blas.Level1
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.scalar.comparison.ScalarSetValue
import org.nd4j.linalg.api.ops.impl.transforms.comparison.Eps
import org.nd4j.linalg.api.shape.Shape
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.BooleanIndexing
import org.nd4j.linalg.indexing.conditions.Conditions
import org.nd4j.linalg.indexing.functions.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.nd4j.linalg.ops.transforms.Transforms.abs

//"Line Searches and Backtracking", p385, "Numeric Recipes in C"
/**
@author Aron Culotta <a href="mailto:culotta@cs.umass.edu">culotta@cs.umass.edu</a>

 Adapted from mallet with original authors above.
 Modified to be a vectorized version that uses jblas matrices
 for computation rather than the mallet ops.


 Numerical Recipes in C: p.385. lnsrch. A simple backtracking line
 search. No attempt at accurately finding the true minimum is
 made. The goal is only to ensure that BackTrackLineSearch will
 return a position of higher value.

@author Adam Gibson
  */
object BackTrackLineSearch {
  private val log: Logger = LoggerFactory.getLogger(classOf[BackTrackLineSearch])
}

class BackTrackLineSearch extends LineOptimizer {
  private var layer: Model = null
  private var stepFunction: StepFunction = null
  private var optimizer: ConvexOptimizer = null
  private var maxIterations: Int = 0
  private[solvers] var stepMax: Double = 100
  private var minObjectiveFunction: Boolean = true
  private var relTolx: Double = 1e-7f
  private var absTolx: Double = 1e-4f
  protected final val ALF: Double = 1e-4f

  /**
   * @param layer
   * @param stepFunction
   * @param optimizer
   */
  def this(layer: Model, stepFunction: StepFunction, optimizer: ConvexOptimizer) {
    this()
    this.layer = layer
    this.stepFunction = stepFunction
    this.optimizer = optimizer
    this.maxIterations = layer.conf.getMaxNumLineSearchIterations
  }

  /**
   * @param optimizable
   * @param optimizer
   */
  def this(optimizable: Model, optimizer: ConvexOptimizer) {
    this(optimizable, new NegativeDefaultStepFunction, optimizer)
  }

  def setStepMax(stepMax: Double) {
    this.stepMax = stepMax
  }

  def getStepMax: Double = {
    return stepMax
  }

  /**
   * Sets the tolerance of relative diff in function value.
   * Line search converges if abs(delta x / x) < tolx
   * for all coordinates.
   */
  def setRelTolx(tolx: Double) {
    relTolx = tolx
  }

  /**
   * Sets the tolerance of absolute diff in function value.
   * Line search converges if abs(delta x) < tolx
   * for all coordinates.
   */
  def setAbsTolx(tolx: Double) {
    absTolx = tolx
  }

  def getMaxIterations: Int = {
    return maxIterations
  }

  def setMaxIterations(maxIterations: Int) {
    this.maxIterations = maxIterations
  }

  def setScoreFor(parameters: INDArray): Double = {
    if (Nd4j.ENFORCE_NUMERICAL_STABILITY) {
      BooleanIndexing.applyWhere(parameters, Conditions.isNan, new Value(Nd4j.EPS_THRESHOLD))
    }
    layer.setParams(parameters)
    layer.computeGradientAndScore
    return layer.score
  }

  /**
   * @param parameters      the parameters to optimize
   * @param gradients       the line/rate of change
   * @param searchDirection the point for the line search to go in
   * @return the next step size
   * @throws InvalidStepException
   */
  @throws(classOf[InvalidStepException])
  def optimize(parameters: INDArray, gradients: INDArray, searchDirection: INDArray): Double = {
    var test: Double = .0
    var stepMin: Double = .0
    var step: Double = .0
    var step2: Double = .0
    var oldStep: Double = .0
    var tmpStep: Double = .0
    var rhs1: Double = .0
    var rhs2: Double = .0
    var a: Double = .0
    var b: Double = .0
    var disc: Double = .0
    var score: Double = .0
    val scoreAtStart: Double = .0
    var score2: Double = .0
    minObjectiveFunction = (stepFunction.isInstanceOf[NegativeDefaultStepFunction] || stepFunction.isInstanceOf[NegativeGradientStepFunction])
    val l1Blas: Level1 = Nd4j.getBlasWrapper.level1
    val sum: Double = l1Blas.nrm2(searchDirection)
    val slope: Double = -1f * Nd4j.getBlasWrapper.dot(searchDirection, gradients)
    BackTrackLineSearch.log.debug("slope = {}", slope)
    val maxOldParams: INDArray = abs(parameters)
    Nd4j.getExecutioner.exec(new ScalarSetValue(maxOldParams, 1))
    val testMatrix: INDArray = abs(gradients).divi(maxOldParams)
    test = testMatrix.max(Integer.MAX_VALUE).getDouble(0)
    step = 1.0
    stepMin = relTolx / test
    oldStep = 0.0
    step2 = 0.0
    score = ({
      score2 = ({
        scoreAtStart = layer.score; scoreAtStart
      }); score2
    })
    var bestScore: Double = score
    var bestStepSize: Double = 1.0
    if (BackTrackLineSearch.log.isTraceEnabled) {
      val norm1: Double = l1Blas.asum(searchDirection)
      val infNormIdx: Int = l1Blas.iamax(searchDirection)
      val infNorm: Double = FastMath.max(Float.NegativeInfinity, searchDirection.getDouble(infNormIdx))
      BackTrackLineSearch.log.trace("ENTERING BACKTRACK\n")
      BackTrackLineSearch.log.trace("Entering BackTrackLineSearch, value = " + scoreAtStart + ",\ndirection.oneNorm:" + norm1 + "  direction.infNorm:" + infNorm)
    }
    if (sum > stepMax) {
      BackTrackLineSearch.log.warn("Attempted step too big. scaling: sum= {}, stepMax= {}", sum, stepMax)
      searchDirection.muli(stepMax / sum)
    }
    var candidateParameters: INDArray = null
    {
      var iteration: Int = 0
      while (iteration < maxIterations) {
        {
          if (BackTrackLineSearch.log.isTraceEnabled) {
            BackTrackLineSearch.log.trace("BackTrack loop iteration {} : step={}, oldStep={}", iteration, step, oldStep)
            BackTrackLineSearch.log.trace("before step, x.1norm: {} \nstep: {} \noldStep: {}", parameters.norm1(Integer.MAX_VALUE), step, oldStep)
          }
          if (step == oldStep) throw new IllegalArgumentException("Current step == oldStep")
          candidateParameters = parameters.dup('f')
          stepFunction.step(candidateParameters, searchDirection, step)
          oldStep = step
          if (BackTrackLineSearch.log.isTraceEnabled) {
            val norm1: Double = l1Blas.asum(candidateParameters)
            BackTrackLineSearch.log.trace("after step, x.1norm: " + norm1)
          }
          if ((step < stepMin) || Nd4j.getExecutioner.execAndReturn(new Eps(parameters, candidateParameters, Shape.toOffsetZeroCopy(candidateParameters, 'f'), candidateParameters.length)).sum(Integer.MAX_VALUE).getDouble(0) == candidateParameters.length) {
            score = setScoreFor(parameters)
            BackTrackLineSearch.log.debug("EXITING BACKTRACK: Jump too small (stepMin = {}). Exiting and using original params. Score = {}", stepMin, score)
            return 0.0
          }
          score = setScoreFor(candidateParameters)
          BackTrackLineSearch.log.debug("Model score after step = {}", score)
          if ((minObjectiveFunction && score < bestScore) || (!minObjectiveFunction && score > bestScore)) {
            bestScore = score
            bestStepSize = step
          }
          if (minObjectiveFunction && score <= scoreAtStart + ALF * step * slope) {
            BackTrackLineSearch.log.debug("Sufficient decrease (Wolfe cond.), exiting backtrack on iter {}: score={}, scoreAtStart={}", iteration, score, scoreAtStart)
            if (score > scoreAtStart) throw new IllegalStateException("Function did not decrease: score = " + score + " > " + scoreAtStart + " = oldScore")
            return step
          }
          if (!minObjectiveFunction && score >= scoreAtStart + ALF * step * slope) {
            BackTrackLineSearch.log.debug("Sufficient increase (Wolfe cond.), exiting backtrack on iter {}: score={}, bestScore={}", iteration, score, scoreAtStart)
            if (score < scoreAtStart) throw new IllegalStateException("Function did not increase: score = " + score + " < " + scoreAtStart + " = scoreAtStart")
            return step
          }
          else if (Double.isInfinite(score) || Double.isInfinite(score2) || Double.isNaN(score) || Double.isNaN(score2)) {
            BackTrackLineSearch.log.warn("Value is infinite after jump. oldStep={}. score={}, score2={}. Scaling back step size...", oldStep, score, score2)
            tmpStep =.2 * step
            if (step < stepMin) {
              score = setScoreFor(parameters)
              BackTrackLineSearch.log.warn("EXITING BACKTRACK: Jump too small (step={} < stepMin={}). Exiting and using previous parameters. Value={}", step, stepMin, score)
              return 0.0
            }
          }
          else if (minObjectiveFunction) {
            if (step == 1.0) tmpStep = -slope / (2.0 * (score - scoreAtStart - slope))
            else {
              rhs1 = score - scoreAtStart - step * slope
              rhs2 = score2 - scoreAtStart - step2 * slope
              if (step == step2) throw new IllegalStateException("FAILURE: dividing by step-step2 which equals 0. step=" + step)
              val stepSquared: Double = step * step
              val step2Squared: Double = step2 * step2
              a = (rhs1 / stepSquared - rhs2 / step2Squared) / (step - step2)
              b = (-step2 * rhs1 / stepSquared + step * rhs2 / step2Squared) / (step - step2)
              if (a == 0.0) tmpStep = -slope / (2.0 * b)
              else {
                disc = b * b - 3.0 * a * slope
                if (disc < 0.0) {
                  tmpStep = 0.5 * step
                }
                else if (b <= 0.0) tmpStep = (-b + FastMath.sqrt(disc)) / (3.0 * a)
                else tmpStep = -slope / (b + FastMath.sqrt(disc))
              }
              if (tmpStep > 0.5 * step) tmpStep = 0.5 * step
            }
          }
          else {
            if (step == 1.0) tmpStep = -slope / (2.0 * (scoreAtStart - score - slope))
            else {
              rhs1 = scoreAtStart - score - step * slope
              rhs2 = scoreAtStart - score2 - step2 * slope
              if (step == step2) throw new IllegalStateException("FAILURE: dividing by step-step2 which equals 0. step=" + step)
              val stepSquared: Double = step * step
              val step2Squared: Double = step2 * step2
              a = (rhs1 / stepSquared - rhs2 / step2Squared) / (step - step2)
              b = (-step2 * rhs1 / stepSquared + step * rhs2 / step2Squared) / (step - step2)
              if (a == 0.0) tmpStep = -slope / (2.0 * b)
              else {
                disc = b * b - 3.0 * a * slope
                if (disc < 0.0) {
                  tmpStep = 0.5 * step
                }
                else if (b <= 0.0) tmpStep = (-b + FastMath.sqrt(disc)) / (3.0 * a)
                else tmpStep = -slope / (b + FastMath.sqrt(disc))
              }
              if (tmpStep > 0.5 * step) tmpStep = 0.5 * step
            }
          }
          step2 = step
          score2 = score
          BackTrackLineSearch.log.debug("tmpStep: {}", tmpStep)
          step = Math.max(tmpStep,.1f * step)
        }
        ({
          iteration += 1; iteration - 1
        })
      }
    }
    if (minObjectiveFunction && bestScore < scoreAtStart) {
      BackTrackLineSearch.log.debug("Exited line search after maxIterations termination condition; bestStepSize={}, bestScore={}, scoreAtStart={}", bestStepSize, bestScore, scoreAtStart)
      return bestStepSize
    }
    else if (!minObjectiveFunction && bestScore > scoreAtStart) {
      BackTrackLineSearch.log.debug("Exited line search after maxIterations termination condition; bestStepSize={}, bestScore={}, scoreAtStart={}", bestStepSize, bestScore, scoreAtStart)
      return bestStepSize
    }
    else {
      BackTrackLineSearch.log.debug("Exited line search after maxIterations termination condition; score did not improve (bestScore={}, scoreAtStart={}). Resetting parameters", bestScore, scoreAtStart)
      setScoreFor(parameters)
      return 0.0
    }
  }
}