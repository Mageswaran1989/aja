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

import java.io.PrintWriter
import java.lang.management.ManagementFactory
import java.lang.management.ThreadInfo
import java.lang.management.ThreadMXBean
import java.lang.reflect.Constructor
import java.util.Map
import java.util.concurrent.ConcurrentHashMap

/**
 * General reflection utils
 */
object ReflectionUtils {
  private val EMPTY_ARRAY: Array[Class[_]] = Array[Class[_]]
  /**
   * Cache of constructors for each class. Pins the classes so they
   * can't be garbage collected until ReflectionUtils can be collected.
   */
  private val CONSTRUCTOR_CACHE: Map[Class[_], Constructor[_]] = new ConcurrentHashMap[Class[_], Constructor[_]]
  private var threadBean: ThreadMXBean = ManagementFactory.getThreadMXBean

  def setContentionTracing(`val`: Boolean) {
    threadBean.setThreadContentionMonitoringEnabled(`val`)
  }

  private def getTaskName(id: Long, name: String): String = {
    if (name == null) {
      return Long.toString(id)
    }
    return id + " (" + name + ")"
  }

  /**
   * Print all of the thread's information and stack traces.
   *
   * @param stream the stream to
   * @param title a string title for the stack trace
   */
  def printThreadInfo(stream: PrintWriter, title: String) {
    val STACK_DEPTH: Int = 20
    val contention: Boolean = threadBean.isThreadContentionMonitoringEnabled
    val threadIds: Array[Long] = threadBean.getAllThreadIds
    stream.println("Process Thread Dump: " + title)
    stream.println(threadIds.length + " active threads")
    for (tid <- threadIds) {
      val info: ThreadInfo = threadBean.getThreadInfo(tid, STACK_DEPTH)
      if (info == null) {
        stream.println("  Inactive")
        continue //todo: continue is not supported
      }
      stream.println("Thread " + getTaskName(info.getThreadId, info.getThreadName) + ":")
      val state: Thread.State = info.getThreadState
      stream.println("  State: " + state)
      stream.println("  Blocked count: " + info.getBlockedCount)
      stream.println("  Waited count: " + info.getWaitedCount)
      if (contention) {
        stream.println("  Blocked time: " + info.getBlockedTime)
        stream.println("  Waited time: " + info.getWaitedTime)
      }
      if (state eq Thread.State.WAITING) {
        stream.println("  Waiting on " + info.getLockName)
      }
      else if (state eq Thread.State.BLOCKED) {
        stream.println("  Blocked on " + info.getLockName)
        stream.println("  Blocked by " + getTaskName(info.getLockOwnerId, info.getLockOwnerName))
      }
      stream.println("  Stack:")
      for (frame <- info.getStackTrace) {
        stream.println("    " + frame.toString)
      }
    }
    stream.flush
  }

  private var previousLogTime: Long = 0

  /**
   * Return the correctly-typed {@link Class} of the given object.
   *
   * @param o object whose correctly-typed <code>Class</code> is to be obtained
   * @return the correctly typed <code>Class</code> of the given object.
   */
  @SuppressWarnings(Array("unchecked")) def getClass[T](o: T): Class[T] = {
    return o.getClass.asInstanceOf[Class[T]]
  }

  private[util] def clearCache {
    CONSTRUCTOR_CACHE.clear
  }

  private[util] def getCacheSize: Int = {
    return CONSTRUCTOR_CACHE.size
  }
}

class ReflectionUtils {
  private def this() {
    this()
  }
}