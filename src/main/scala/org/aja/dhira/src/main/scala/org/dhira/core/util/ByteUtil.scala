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

import java.io.DataInputStream
import java.io.IOException

object ByteUtil {
  /**
   *
   *
   * @param dis
   * @return
   * @throws IOException
   */
  @throws(classOf[IOException])
  def readString(dis: DataInputStream, maxSize: Int): String = {
    var bytes: Array[Byte] = new Array[Byte](maxSize)
    var b: Byte = dis.readByte
    var i: Int = -1
    val sb: StringBuilder = new StringBuilder
    while (b != 32 && b != 10) {
      i += 1
      bytes(i) = b
      b = dis.readByte
      if (i == 49) {
        sb.append(new String(bytes))
        i = -1
        bytes = new Array[Byte](maxSize)
      }
    }
    sb.append(new String(bytes, 0, i + 1))
    return sb.toString
  }
}

class ByteUtil {
  private def this() {
    this()
  }
}