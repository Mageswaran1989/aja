package org.aja.dhira.nn.containers

import java.util

import scala.collection.mutable
import scala.collection.mutable.{AbstractMap, HashMap}
import scala.collection.immutable.TreeMap
import scala.collection.JavaConverters._

/**
 * Created by mageswaran on 10/9/16.
 * The MapFactory is a mechanism for specifying what kind of map is to be used
 * by some object.  For example, if you want a Counter which is backed by an
 * IdentityHashMap instead of the defaul HashMap, you can pass in an
 * IdentityHashMapFactory.
 *
 * @author Mageswaran Dhandapani
 * @since  0.1 09-Sep-2016
 */

abstract class MapFactory[K, V] extends Serializable {
  def buildMap(): mutable.Map[K,V]
}

object MapFactory {
  @SerialVersionUID(1L)
  class HashMapFactory[K, V] extends MapFactory[K, V] {
    def buildMap() = new HashMap[K, V]()

  }

  @SerialVersionUID(1L)
  class IdentityHashMapFactory[K, V] extends MapFactory[K, V] {
    def buildMap() = {
      new util.IdentityHashMap[K, V].asScala //TODO does this help?
    }
  }

  @SerialVersionUID(1L)
  class TreeMapFactory[K, V] extends MapFactory[K, V] {
    def buildMap() = {
      new util.TreeMap[K, V].asScala //TODO does this help?
    }
  }

  @SerialVersionUID(1L)
  class WeakHashMapFactory[K, V] extends MapFactory[K, V] {
    def buildMap() = {
      new util.WeakHashMap[K, V].asScala //TODO does this help?
    }
  }

}
