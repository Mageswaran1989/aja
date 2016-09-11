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
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.util.Collection
import java.util.Iterator
import java.util.Queue
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Naive disk based queue for storing items on disk.
 * Only meant for poll and adding items.
 * @author Adam Gibson
 */
class DiskBasedQueue[E] extends Queue[E] with Serializable {
  private var dir: File = null
  private var paths: Queue[String] = new ConcurrentLinkedDeque[String]
  private var executorService: ScheduledExecutorService = null
  private var running: AtomicBoolean = new AtomicBoolean(true)
  private var save: Queue[E] = new ConcurrentLinkedDeque[E]

  def this(dir: File) {
    this()
    this.dir = dir
    if (!dir.exists && dir.isDirectory) {
      throw new IllegalArgumentException("Illegal queue: must be a directory")
    }
    if (!dir.exists) dir.mkdirs
    if (dir.listFiles != null && dir.listFiles.length > 1) try {
      FileUtils.deleteDirectory(dir)
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }
    dir.mkdir
    executorService = Executors.newSingleThreadScheduledExecutor
    executorService.execute(new Runnable() {
      def run {
        while (running.get) {
          while (!save.isEmpty) addAndSave(save.poll)
          try {
            Thread.sleep(1000)
          }
          catch {
            case e: InterruptedException => {
              Thread.currentThread.interrupt
            }
          }
        }
      }
    })
  }

  def this(path: String = ".queue") {
    this(new File(path))
  }

  def size: Int = {
    return paths.size
  }

  def isEmpty: Boolean = {
    return paths.isEmpty
  }

  def contains(o: AnyRef): Boolean = {
    throw new UnsupportedOperationException
  }

  def iterator: Iterator[E] = {
    throw new UnsupportedOperationException
  }

  def toArray: Array[AnyRef] = {
    throw new UnsupportedOperationException
  }

  def toArray[T](a: Array[T]): Array[T] = {
    throw new UnsupportedOperationException
  }

  def add(e: E): Boolean = {
    save.add(e)
    return true
  }

  def remove(o: AnyRef): Boolean = {
    throw new UnsupportedOperationException
  }

  def containsAll(c: Collection[_]): Boolean = {
    throw new UnsupportedOperationException
  }

  def addAll(c: Collection[_ <: E]): Boolean = {
    import scala.collection.JavaConversions._
    for (e <- c) addAndSave(e)
    return true
  }

  def removeAll(c: Collection[_]): Boolean = {
    throw new UnsupportedOperationException
  }

  def retainAll(c: Collection[_]): Boolean = {
    throw new UnsupportedOperationException
  }

  def clear {
    throw new UnsupportedOperationException
  }

  def offer(e: E): Boolean = {
    throw new UnsupportedOperationException
  }

  def remove: E = {
    throw new UnsupportedOperationException
  }

  def poll: E = {
    val path: String = paths.poll
    val ret: E = SerializationUtils.readObject(new File(path))
    val item: File = new File(path)
    item.delete
    return ret
  }

  def element: E = {
    throw new UnsupportedOperationException
  }

  def peek: E = {
    throw new UnsupportedOperationException
  }

  private def addAndSave(e: E) {
    val path: File = new File(dir, UUID.randomUUID.toString)
    SerializationUtils.saveObject(e, path)
    paths.add(path.getAbsolutePath)
  }
}