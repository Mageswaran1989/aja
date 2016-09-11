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

import org.apache.commons.io.FileUtils
import java.io._

/**
 * Serialization utils for saving and reading serializable objects
 *
 * @author Adam Gibson
 */
object SerializationUtils {
  @SuppressWarnings(Array("unchecked")) def readObject[T](file: File): T = {
    var ois: ObjectInputStream = null
    try {
      ois = new ObjectInputStream(FileUtils.openInputStream(file))
      return ois.readObject.asInstanceOf[T]
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    } finally {
      if (ois != null) try {
        ois.close
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
  }

  /**
   * Reads an object from the given input stream
   * @param is the input stream to read from
   * @return the read object
   */
  @SuppressWarnings(Array("unchecked")) def readObject[T](is: InputStream): T = {
    try {
      val ois: ObjectInputStream = new ObjectInputStream(is)
      return ois.readObject.asInstanceOf[T]
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Converts the given object to a byte array
   * @param toSave the object to save
   */
  def toByteArray(toSave: Serializable): Array[Byte] = {
    try {
      val bos: ByteArrayOutputStream = new ByteArrayOutputStream
      val os: ObjectOutputStream = new ObjectOutputStream(bos)
      os.writeObject(toSave)
      val ret: Array[Byte] = bos.toByteArray
      os.close
      return ret
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Writes the object to the output stream
   * THIS DOES NOT FLUSH THE STREAMMultiLayerNetwork
   * @param toSave the object to save
   * @param writeTo the output stream to write to
   */
  def writeObject(toSave: Serializable, writeTo: OutputStream) {
    try {
      val os: ObjectOutputStream = new ObjectOutputStream(writeTo)
      os.writeObject(toSave)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  def saveObject(toSave: AnyRef, saveTo: File) {
    try {
      val os1: OutputStream = FileUtils.openOutputStream(saveTo)
      val os: ObjectOutputStream = new ObjectOutputStream(os1)
      os.writeObject(toSave)
      os.flush
      os.close
      os1.close
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }
}

class SerializationUtils {
  private def this() {
    this()
  }
}