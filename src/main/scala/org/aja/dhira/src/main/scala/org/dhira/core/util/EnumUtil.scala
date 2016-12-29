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

/**
 * Created by agibsonccc on 9/3/14.
 */
object EnumUtil {
  def parse[E <: Enum](value: String, clazz: Class[E]): E = {
    val i: Int = value.toInt
    val constants: Array[Enum[_ <: Enum[E]]] = clazz.getEnumConstants
    for (constant <- constants) {
      if (constant.ordinal == i) return constant.asInstanceOf[E]
    }
    return null
  }
}

class EnumUtil {
  private def this() {
    this()
  }
}