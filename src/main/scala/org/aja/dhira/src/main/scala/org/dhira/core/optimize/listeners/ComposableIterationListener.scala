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
package org.dhira.core.optimize.listeners

import org.dhira.core.nnet.api.Model
import org.dhira.core.optimize.api.IterationListener
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection

/**
 * A group of listeners
 * @author Adam Gibson
 */
class ComposableIterationListener extends IterationListener {
  private var listeners: Iterable[IterationListener] = _ //TODO
  private var isInvoked: Boolean = false

  def this(iterationListener: IterationListener*) {
    this()
    listeners = listeners ++ iterationListener //addAll(Arrays.asList(iterationListener))
  }

  def this(listeners:  Iterable[IterationListener] ) {
    this()
    this.listeners = listeners
  }

  def invoked(): Boolean = {
    return isInvoked
  }

  def invoke() {
    this.isInvoked = true
  }

  def iterationDone(model: Model, iteration: Int) {
    import scala.collection.JavaConversions._
    for (listener <- listeners) listener.iterationDone(model, iteration)
  }
}