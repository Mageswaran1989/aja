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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.List
import java.util.concurrent._

object MultiThreadUtils {
  private var log: Logger = LoggerFactory.getLogger(classOf[MultiThreadUtils])
  private var instance: ExecutorService = null

  def newExecutorService: ExecutorService = {
    val nThreads: Int = Runtime.getRuntime.availableProcessors
    return new ThreadPoolExecutor(nThreads, nThreads, 60L, TimeUnit.SECONDS, new LinkedTransferQueue[Runnable], new ThreadFactory() {
      def newThread(r: Runnable): Thread = {
        val t: Thread = Executors.defaultThreadFactory.newThread(r)
        t.setDaemon(true)
        return t
      }
    })
  }

  def parallelTasks(tasks: List[Runnable], executorService: ExecutorService) {
    val tasksCount: Int = tasks.size
    val latch: CountDownLatch = new CountDownLatch(tasksCount)
    {
      var i: Int = 0
      while (i < tasksCount) {
        {
          val taskIdx: Int = i
          executorService.execute(new Runnable() {
            def run {
              try {
                tasks.get(taskIdx).run
              }
              catch {
                case e: Throwable => {
                  log.info("Unchecked exception thrown by task", e)
                }
              } finally {
                latch.countDown
              }
            }
          })
        }
        ({
          i += 1; i - 1
        })
      }
    }
    try {
      latch.await
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }
}

class MultiThreadUtils {
  private def this() {
    this()
  }
}