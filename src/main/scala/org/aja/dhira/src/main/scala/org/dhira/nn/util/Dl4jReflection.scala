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

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.ArrayList
import java.util.Arrays
import java.util.List
import java.util.Properties

/**
 * @author Adam Gibson
 */
object Dl4jReflection {
  /**
   * Gets the empty constructor from a class
   * @param clazz the class to getFromOrigin the constructor from
   * @return the empty constructor for the class
   */
  def getEmptyConstructor(clazz: Class[_]): Constructor[_] = {
    var c: Constructor[_] = clazz.getDeclaredConstructors(0)
    {
      var i: Int = 0
      while (i < clazz.getDeclaredConstructors.length) {
        {
          if (clazz.getDeclaredConstructors(i).getParameterTypes.length < 1) {
            c = clazz.getDeclaredConstructors(i)
            break //todo: break is not supported
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return c
  }

  def getAllFields(clazz: Class[_]): Array[Field] = {
    var targetClass: Class[_] = clazz
    val fields: List[Field] = new ArrayList[Field]
    do {
      fields.addAll(Arrays.asList(targetClass.getDeclaredFields))
      targetClass = targetClass.getSuperclass
    } while (targetClass != null && targetClass ne classOf[AnyRef])
    return fields.toArray(new Array[Field](fields.size))
  }

  /**
   * Sets the properties of the given object
   * @param obj the object o set
   * @param props the properties to set
   */
  @throws(classOf[Exception])
  def setProperties(obj: AnyRef, props: Properties) {
    for (field <- obj.getClass.getDeclaredFields) {
      field.setAccessible(true)
      if (props.containsKey(field.getName)) {
        set(field, obj, props.getProperty(field.getName))
      }
    }
  }

  @throws(classOf[Exception])
  private def set(field: Field, obj: AnyRef, value: String) {
    val clazz: Class[_] = field.getType
    field.setAccessible(true)
    if ((clazz == classOf[Double]) || (clazz == classOf[Double])) {
      val `val`: Double = Double.valueOf(value)
      field.set(obj, `val`)
    }
    else if (clazz == classOf[String]) {
      field.set(obj, value)
    }
    else if ((clazz == classOf[Integer]) || (clazz == classOf[Int])) {
      val `val`: Int = value.toInt
      field.set(obj, `val`)
    }
    else if ((clazz == classOf[Float]) || (clazz == classOf[Float])) {
      val f: Float = Float.parseFloat(value)
      field.set(obj, f)
    }
  }

  /**
   * Get fields as properties
   * @param obj the object to get fields for
   * @param clazzes the classes to use for reflection and properties.
   *                T
   * @return the fields as properties
   */
  @throws(classOf[Exception])
  def getFieldsAsProperties(obj: AnyRef, clazzes: Array[Class[_]]): Properties = {
    val props: Properties = new Properties
    for (field <- obj.getClass.getDeclaredFields) {
      if (Modifier.isStatic(field.getModifiers)) continue //todo: continue is not supported
      field.setAccessible(true)
      val `type`: Class[_] = field.getType
      if (clazzes == null || contains(`type`, clazzes)) {
        val `val`: AnyRef = field.get(obj)
        if (`val` != null) props.put(field.getName, `val`.toString)
      }
    }
    return props
  }

  private def contains(test: Class[_], arr: Array[Class[_]]): Boolean = {
    for (c <- arr) if (c == test) return true
    return false
  }
}

class Dl4jReflection {
  private def this() {
    this()
  }
}