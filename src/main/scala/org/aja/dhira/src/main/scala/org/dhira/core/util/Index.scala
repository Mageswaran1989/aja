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
package org.dhira.core.util

import java.io.Serializable
import java.util.Map
import java.util.concurrent.ConcurrentHashMap

/**
 * An index is a transform of objects augmented with a list and a reverse lookup table
 * for fast lookups.
 * Indices are used for vocabulary in many of the natural language processing
 * @author Adam Gibson
 *
 */
@SuppressWarnings(Array("rawtypes", "unchecked"))
@SerialVersionUID(1160629777026141078L)
class Index extends Serializable {
  private[util] var objects: Map[Integer, AnyRef] = new ConcurrentHashMap[Integer, AnyRef]
  private[util] var indexes: Map[AnyRef, Integer] = new ConcurrentHashMap[AnyRef, Integer]

  def add(o: AnyRef, idx: Int): Boolean = {
    if (o.isInstanceOf[String] && o.toString.isEmpty) {
      throw new IllegalArgumentException("Unable to add the empty string")
    }
    var index: Integer = indexes.get(o)
    if (index == null) {
      index = idx
      objects.put(idx, o)
      indexes.put(o, index)
      return true
    }
    return false
  }

  def add(o: AnyRef): Boolean = {
    if (o.isInstanceOf[String] && o.toString.isEmpty) {
      throw new IllegalArgumentException("Unable to add the empty string")
    }
    var index: Integer = indexes.get(o)
    if (index == null) {
      index = objects.size
      objects.put(index, o)
      indexes.put(o, index)
      return true
    }
    return false
  }

  def indexOf(o: AnyRef): Int = {
    val index: Integer = indexes.get(o)
    if (index == null) {
      return -1
    }
    else {
      return index
    }
  }

  def get(i: Int): AnyRef = {
    return objects.get(i)
  }

  def size: Int = {
    return objects.size
  }

  override def toString: String = {
    val buff: StringBuilder = new StringBuilder("[")
    val sz: Int = objects.size
    var i: Int = 0

    {
      i = 0
      while (i < sz) {
        {
          val e: AnyRef = objects.get(i)
          buff.append(e)
          if (i < (sz - 1)) buff.append(" , ")
        }
        ({
          i += 1; i - 1
        })
      }
    }
    buff.append("]")
    return buff.toString
  }

  override def equals(o: AnyRef): Boolean = {
    if (this eq o) return true
    if (o == null || getClass ne o.getClass) return false
    val index: Index = o.asInstanceOf[Index]
    if (if (objects != null) !(objects == index.objects) else index.objects != null) return false
    return !(if (indexes != null) !(indexes == index.indexes) else index.indexes != null)
  }

  override def hashCode: Int = {
    var result: Int = if (objects != null) objects.hashCode else 0
    result = 31 * result + (if (indexes != null) indexes.hashCode else 0)
    return result
  }
}