package org.aja.ml.core

import scala.annotation.implicitNotFound
import scala.collection._
import scala.util.{Try, Success, Failure}
import scala.reflect.ClassTag
import scala.language.implicitConversions

import org.apache.log4j.Logger

import org.aja.ml.core.Types.Aja.{DblMatrix, DblVector, Vector}

class XTimeSeries[T](val label: String, array: Array[T]) {
  import XTimeSeries._
  require(!array.isEmpty, "Cannot instantiate XTimeSeries with no data")

  def head: T = array.head
  def tail: T = array.tail
 
  final val size: Int = array.size

  def toArray: Array[T] = array
  def toList: List[T] = array.toList
 
  def == (that: XTimeSeries[T]): Boolean {
    require(!that.isEmpty, "Cannot compare with an empty series")
    size == that.size && array.equals(that.toArray)
  }
   
}
