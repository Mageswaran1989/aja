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
package org.deeplearning4j.eval

import com.google.common.collect.HashMultiset
import com.google.common.collect.Multiset
import java.io.Serializable
import java.util.Arrays
import java.util.HashMap
import java.util.List
import java.util.Map

object ConfusionMatrix {
  def main(args: Array[String]) {
    val confusionMatrix: ConfusionMatrix[String] = new ConfusionMatrix[T](Arrays.asList("a", "b", "c"))
    confusionMatrix.add("a", "a", 88)
    confusionMatrix.add("a", "b", 10)
    confusionMatrix.add("b", "a", 14)
    confusionMatrix.add("b", "b", 40)
    confusionMatrix.add("b", "c", 6)
    confusionMatrix.add("c", "a", 18)
    confusionMatrix.add("c", "b", 10)
    confusionMatrix.add("c", "c", 12)
    val confusionMatrix2: ConfusionMatrix[String] = new ConfusionMatrix[_ <: Comparable[_ >: T]](confusionMatrix)
    confusionMatrix2.add(confusionMatrix)
    println(confusionMatrix2.toHTML)
    println(confusionMatrix2.toCSV)
  }
}

class ConfusionMatrix[T <: Comparable[(_$1) forSome {type _$1 >: T}]] extends Serializable {
  private var matrix: Map[T, Multiset[T]] = null
  private var classes: List[T] = null

  /**
   * Creates an empty confusion Matrix
   */
  def this(classes: List[T]) {
    this.matrix = new HashMap[T, Multiset[T]]
    this.classes = classes
  }

  /**
   * Creates a new ConfusionMatrix initialized with the contents of another ConfusionMatrix.
   */
  def this(other: ConfusionMatrix[T]) {
    `this`(other.getClasses)
    this.add(other)
  }

  /**
   * Increments the entry specified by actual and predicted by one.
   */
  def add(actual: T, predicted: T) {
    add(actual, predicted, 1)
  }

  /**
   * Increments the entry specified by actual and predicted by count.
   */
  def add(actual: T, predicted: T, count: Int) {
    if (matrix.containsKey(actual)) {
      matrix.get(actual).add(predicted, count)
    }
    else {
      val counts: Multiset[T] = HashMultiset.create
      counts.add(predicted, count)
      matrix.put(actual, counts)
    }
  }

  /**
   * Adds the entries from another confusion matrix to this one.
   */
  def add(other: ConfusionMatrix[T]) {
    import scala.collection.JavaConversions._
    for (actual <- other.matrix.keySet) {
      val counts: Multiset[T] = other.matrix.get(actual)
      import scala.collection.JavaConversions._
      for (predicted <- counts.elementSet) {
        val count: Int = counts.count(predicted)
        this.add(actual, predicted, count)
      }
    }
  }

  /**
   * Gives the applyTransformToDestination of all classes in the confusion matrix.
   */
  def getClasses: List[T] = {
    return classes
  }

  /**
   * Gives the count of the number of times the "predicted" class was predicted for the "actual"
   * class.
   */
  def getCount(actual: T, predicted: T): Int = {
    if (!matrix.containsKey(actual)) {
      return 0
    }
    else {
      return matrix.get(actual).count(predicted)
    }
  }

  /**
   * Computes the total number of times the class was predicted by the classifier.
   */
  def getPredictedTotal(predicted: T): Int = {
    var total: Int = 0
    import scala.collection.JavaConversions._
    for (actual <- classes) {
      total += getCount(actual, predicted)
    }
    return total
  }

  /**
   * Computes the total number of times the class actually appeared in the data.
   */
  def getActualTotal(actual: T): Int = {
    if (!matrix.containsKey(actual)) {
      return 0
    }
    else {
      var total: Int = 0
      import scala.collection.JavaConversions._
      for (elem <- matrix.get(actual).elementSet) {
        total += matrix.get(actual).count(elem)
      }
      return total
    }
  }

  override def toString: String = {
    return matrix.toString
  }

  /**
   * Outputs the ConfusionMatrix as comma-separated values for easy import into spreadsheets
   */
  def toCSV: String = {
    val builder: StringBuilder = new StringBuilder
    builder.append(",,Predicted Class,\n")
    builder.append(",,")
    import scala.collection.JavaConversions._
    for (predicted <- classes) {
      builder.append(String.format("%s,", predicted))
    }
    builder.append("Total\n")
    var firstColumnLabel: String = "Actual Class,"
    import scala.collection.JavaConversions._
    for (actual <- classes) {
      builder.append(firstColumnLabel)
      firstColumnLabel = ","
      builder.append(String.format("%s,", actual))
      import scala.collection.JavaConversions._
      for (predicted <- classes) {
        builder.append(getCount(actual, predicted))
        builder.append(",")
      }
      builder.append(getActualTotal(actual))
      builder.append("\n")
    }
    builder.append(",Total,")
    import scala.collection.JavaConversions._
    for (predicted <- classes) {
      builder.append(getPredictedTotal(predicted))
      builder.append(",")
    }
    builder.append("\n")
    return builder.toString
  }

  /**
   * Outputs Confusion Matrix in an HTML table. Cascading Style Sheets (CSS) can control the table's
   * appearance by defining the empty-space, actual-count-header, predicted-class-header, and
   * count-element classes. For example
   *
   * @return html string
   */
  def toHTML: String = {
    val builder: StringBuilder = new StringBuilder
    val numClasses: Int = classes.size
    builder.append("<table>\n")
    builder.append("<tr><th class=\"empty-space\" colspan=\"2\" rowspan=\"2\">")
    builder.append(String.format("<th class=\"predicted-class-header\" colspan=\"%d\">Predicted Class</th></tr>%n", numClasses + 1))
    builder.append("<tr>")
    import scala.collection.JavaConversions._
    for (predicted <- classes) {
      builder.append("<th class=\"predicted-class-header\">")
      builder.append(predicted)
      builder.append("</th>")
    }
    builder.append("<th class=\"predicted-class-header\">Total</th>")
    builder.append("</tr>\n")
    var firstColumnLabel: String = String.format("<tr><th class=\"actual-class-header\" rowspan=\"%d\">Actual Class</th>", numClasses + 1)
    import scala.collection.JavaConversions._
    for (actual <- classes) {
      builder.append(firstColumnLabel)
      firstColumnLabel = "<tr>"
      builder.append(String.format("<th class=\"actual-class-header\" >%s</th>", actual))
      import scala.collection.JavaConversions._
      for (predicted <- classes) {
        builder.append("<td class=\"count-element\">")
        builder.append(getCount(actual, predicted))
        builder.append("</td>")
      }
      builder.append("<td class=\"count-element\">")
      builder.append(getActualTotal(actual))
      builder.append("</td>")
      builder.append("</tr>\n")
    }
    builder.append("<tr><th class=\"actual-class-header\">Total</th>")
    import scala.collection.JavaConversions._
    for (predicted <- classes) {
      builder.append("<td class=\"count-element\">")
      builder.append(getPredictedTotal(predicted))
      builder.append("</td>")
    }
    builder.append("<td class=\"empty-space\"></td>\n")
    builder.append("</tr>\n")
    builder.append("</table>\n")
    return builder.toString
  }

  override def equals(o: AnyRef): Boolean = {
    if (!(o.isInstanceOf[ConfusionMatrix[_ <: Comparable[_ >: T]]])) return false
    val c: ConfusionMatrix[_] = o.asInstanceOf[ConfusionMatrix[_]]
    return (matrix == c.matrix) && (classes == c.classes)
  }

  override def hashCode: Int = {
    var result: Int = 17
    result = 31 * result + (if (matrix == null) 0 else matrix.hashCode)
    result = 31 * result + (if (classes == null) 0 else classes.hashCode)
    return result
  }
}