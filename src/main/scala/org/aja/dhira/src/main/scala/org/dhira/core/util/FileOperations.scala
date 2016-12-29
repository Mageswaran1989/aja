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

import java.io._

object FileOperations {
  def createAppendingOutputStream(to: File): OutputStream = {
    try {
      return new BufferedOutputStream(new FileOutputStream(to, true))
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

  def appendTo(data: String, append: File) {
    try {
      val bos: BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(append, true))
      bos.write(data.getBytes)
      bos.flush
      bos.close
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }
}

class FileOperations {
  private def this() {
    this()
  }
}