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
import java.util._

/**
 * Clusters strings based on fingerprint: for example
 * Two words and TWO words or WORDS TWO would be put together
 * @author Adam Gibson
 *
 */
@SerialVersionUID(-4120559428585520276L)
object StringCluster {

  @SerialVersionUID(-1390696157208674054L)
  class SizeComparator extends Comparator[Map[String, Integer]] with Serializable {
    def compare(o1: Map[String, Integer], o2: Map[String, Integer]): Int = {
      val s1: Int = o1.size
      val s2: Int = o2.size
      if (s1 == s2) {
        var total1: Int = 0
        import scala.collection.JavaConversions._
        for (i <- o1.values) {
          total1 += i
        }
        var total2: Int = 0
        import scala.collection.JavaConversions._
        for (i <- o2.values) {
          total2 += i
        }
        if (total2 < total1) return -1
        if (total2 > total1) return 1
        return 0
      }
      else if (s2 < s1) {
        return -1
      }
      else {
        return 1
      }
    }
  }

}

@SerialVersionUID(-4120559428585520276L)
class StringCluster extends HashMap[String, Map[String, Integer]] {
  def this(list: List[String]) {
    this()
    {
      var i: Int = 0
      while (i < list.size) {
        {
          val s: String = list.get(i)
          val keyer: FingerPrintKeyer = new FingerPrintKeyer
          val key: String = keyer.key(s)
          if (containsKey(key)) {
            val m: Map[String, Integer] = get(key)
            if (m.containsKey(s)) {
              m.put(s, m.get(s) + 1)
            }
            else {
              m.put(s, 1)
            }
          }
          else {
            val m: Map[String, Integer] = new TreeMap[String, Integer]
            m.put(s, 1)
            put(key, m)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def getClusters: List[Map[String, Integer]] = {
    val _clusters: List[Map[String, Integer]] = new ArrayList[Map[String, Integer]](values)
    Collections.sort(_clusters, new StringCluster.SizeComparator)
    return _clusters
  }

  def sort {
    Collections.sort(new ArrayList[Map[String, Integer]](values), new StringCluster.SizeComparator)
  }
}