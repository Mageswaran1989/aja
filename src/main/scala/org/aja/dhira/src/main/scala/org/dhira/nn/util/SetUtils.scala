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
package org.deeplearning4j.util

import java.util.Collection
import java.util.HashSet
import java.util.Set

object SetUtils {
  def intersection[T](parentCollection: Collection[T], removeFromCollection: Collection[T]): Set[T] = {
    val results: Set[T] = new HashSet[T](parentCollection)
    results.retainAll(removeFromCollection)
    return results
  }

  def intersectionP[T](s1: Set[_ <: T], s2: Set[_ <: T]): Boolean = {
    import scala.collection.JavaConversions._
    for (elt <- s1) {
      if (s2.contains(elt)) return true
    }
    return false
  }

  def union[T](s1: Set[_ <: T], s2: Set[_ <: T]): Set[T] = {
    val s3: Set[T] = new HashSet[T](s1)
    s3.addAll(s2)
    return s3
  }

  /** Return is s1 \ s2 */
  def difference[T](s1: Collection[_ <: T], s2: Collection[_ <: T]): Set[T] = {
    val s3: Set[T] = new HashSet[T](s1)
    s3.removeAll(s2)
    return s3
  }
}

class SetUtils {
  private def this() {
    this()
  }
}