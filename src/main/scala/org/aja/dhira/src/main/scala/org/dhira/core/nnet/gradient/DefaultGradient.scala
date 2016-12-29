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
package org.dhira.core.nnet.gradient

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.List
import java.util.Map

/**
 * Default gradient implementation. Basically lookup table
 * for ndarrays
 *
 * @author Adam Gibson
 */
object DefaultGradient {
  val DEFAULT_FLATTENING_ORDER: Char = 'f'
}

class DefaultGradient extends Gradient {
  private var gradients: Map[String, INDArray] = new LinkedHashMap[String, INDArray]
  private var flatteningOrders: Map[String, Character] = null
  private var flattenedGradient: INDArray = null

  def this(flattenedGradient: INDArray) {
    this()
    this.flattenedGradient = flattenedGradient
  }

  def gradientForVariable: Map[String, INDArray] = {
    return gradients
  }

  def gradient(order: List[String]): INDArray = {
    val toFlatten: List[INDArray] = new ArrayList[INDArray]
    if (flatteningOrders == null) {
      import scala.collection.JavaConversions._
      for (s <- order) {
        if (!gradients.containsKey(s)) continue //todo: continue is not supported
        toFlatten.add(gradients.get(s))
      }
    }
    else {
      import scala.collection.JavaConversions._
      for (s <- order) {
        if (!gradients.containsKey(s)) continue //todo: continue is not supported
        if (flatteningOrders.containsKey(s) && flatteningOrders.get(s) ne DefaultGradient.DEFAULT_FLATTENING_ORDER) {
          toFlatten.add(Nd4j.toFlattened(flatteningOrders.get(s), gradients.get(s)))
        }
        else {
          toFlatten.add(gradients.get(s))
        }
      }
    }
    return Nd4j.toFlattened(DefaultGradient.DEFAULT_FLATTENING_ORDER, toFlatten)
  }

  private def flattenGradient {
    if (flatteningOrders != null) {
      val toFlatten: List[INDArray] = new ArrayList[INDArray]
      import scala.collection.JavaConversions._
      for (entry <- gradients.entrySet) {
        if (flatteningOrders.containsKey(entry.getKey) && flatteningOrders.get(entry.getKey) ne DefaultGradient.DEFAULT_FLATTENING_ORDER) {
          toFlatten.add(Nd4j.toFlattened(flatteningOrders.get(entry.getKey), entry.getValue))
        }
        else {
          toFlatten.add(entry.getValue)
        }
      }
      flattenedGradient = Nd4j.toFlattened(DefaultGradient.DEFAULT_FLATTENING_ORDER, toFlatten)
    }
    else {
      flattenedGradient = Nd4j.toFlattened(DefaultGradient.DEFAULT_FLATTENING_ORDER, gradients.values)
    }
  }

  def gradient: INDArray = {
    if (flattenedGradient != null) return flattenedGradient
    flattenGradient
    return flattenedGradient
  }

  def clear {
    gradients.clear
  }

  def getGradientFor(variable: String): INDArray = {
    return gradients.get(variable)
  }

  def setGradientFor(variable: String, newGradient: INDArray): INDArray = {
    val last: INDArray = gradients.put(variable, newGradient)
    return last
  }

  def setGradientFor(variable: String, gradient: INDArray, flatteningOrder: Character): INDArray = {
    val last: INDArray = setGradientFor(variable, gradient)
    if (flatteningOrder != null) {
      if (flatteningOrders == null) flatteningOrders = new LinkedHashMap[String, Character]
      flatteningOrders.put(variable, flatteningOrder)
    }
    return last
  }

  def flatteningOrderForVariable(variable: String): Character = {
    if (flatteningOrders == null) return null
    return flatteningOrders.get(variable)
  }

  override def toString: String = {
    return "DefaultGradient{" + "gradients=" + gradients + (if (flatteningOrders != null) flatteningOrders else "") + '}'
  }
}