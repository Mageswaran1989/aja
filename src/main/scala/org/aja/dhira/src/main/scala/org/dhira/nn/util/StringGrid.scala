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
import org.apache.commons.io.IOUtils
import org.deeplearning4j.berkeley.Counter
import org.deeplearning4j.berkeley.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util._
import org.deeplearning4j.berkeley.StringUtils.splitOnCharWithQuoting

/**
 * String matrix
 * @author Adam Gibson
 *
 */
@SerialVersionUID(4702427632483221813L)
object StringGrid {
  private val log: Logger = LoggerFactory.getLogger(classOf[StringGrid])
  val NONE: String = "NONE"

  @throws(classOf[IOException])
  def fromFile(file: String, sep: String): StringGrid = {
    val read: List[String] = FileUtils.readLines(new File(file))
    if (read.isEmpty) throw new IllegalStateException("Nothing to read; file is empty")
    return new StringGrid(sep, read)
  }

  @throws(classOf[IOException])
  def fromInput(from: InputStream, sep: String): StringGrid = {
    val read: List[String] = IOUtils.readLines(from)
    if (read.isEmpty) throw new IllegalStateException("Nothing to read; file is empty")
    return new StringGrid(sep, read)
  }
}

@SerialVersionUID(4702427632483221813L)
class StringGrid extends ArrayList[List[String]] {
  private var sep: String = null
  private var numColumns: Int = -1

  def this(grid: StringGrid) {
    this()
    this.sep = grid.sep
    this.numColumns = grid.numColumns
    addAll(grid)
    fillOut
  }

  def this(sep: String, numColumns: Int) {
    this()
    `this`(sep, new ArrayList[String])
    this.numColumns = numColumns
    fillOut
  }

  def getNumColumns: Int = {
    return numColumns
  }

  private def fillOut {
    import scala.collection.JavaConversions._
    for (list <- this) {
      if (list.size < numColumns) {
        val diff: Int = numColumns - list.size
        {
          var i: Int = 0
          while (i < diff) {
            {
              list.add(StringGrid.NONE)
            }
            ({
              i += 1; i - 1
            })
          }
        }
      }
    }
  }

  def this(sep: String, data: Collection[String]) {
    this()
    `super`
    this.sep = sep
    val list: List[String] = new ArrayList[String](data)
    {
      var i: Int = 0
      while (i < list.size) {
        {
          val line: String = list.get(i).trim
          if (line.indexOf('\"') > 0) {
            val counter: Nothing = new Nothing
            {
              var j: Int = 0
              while (j < line.length) {
                {
                  counter.incrementCount(line.charAt(j), 1.0)
                }
                ({
                  j += 1; j - 1
                })
              }
            }
            if (counter.getCount('"') > 1) {
              val split: Array[String] = splitOnCharWithQuoting(line, sep.charAt(0), '"', '\\')
              add(new ArrayList[String](Arrays.asList(split)))
            }
            else {
              val row: List[String] = new ArrayList[String](Arrays.asList(splitOnCharWithQuoting(line, sep.charAt(0), '"', '\\')))
              if (numColumns < 0) numColumns = row.size
              else if (row.size != numColumns) StringGrid.log.warn("Row " + i + " had invalid number of columns  line was " + line)
              add(row)
            }
          }
          else {
            val row: List[String] = new ArrayList[String](Arrays.asList(splitOnCharWithQuoting(line, sep.charAt(0), '"', '\\')))
            if (numColumns < 0) numColumns = row.size
            else if (row.size != numColumns) {
              StringGrid.log.warn("Could not add " + line)
            }
            add(row)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    fillOut
  }

  /**
   * Removes all rows with a column of NONE
   * @param column the column to remove by
   */
  def removeRowsWithEmptyColumn(column: Int) {
    val remove: List[List[String]] = new ArrayList[List[String]]
    import scala.collection.JavaConversions._
    for (list <- this) {
      if (list.get(column) == StringGrid.NONE) remove.add(list)
    }
    removeAll(remove)
  }

  def head(num: Int) {
    if (num >= size) num = size
    val builder: StringBuilder = new StringBuilder
    {
      var i: Int = 0
      while (i < num) {
        {
          builder.append(get(i) + "\n")
        }
        ({
          i += 1; i - 1
        })
      }
    }
    StringGrid.log.info(builder.toString)
  }

  /**
   * Removes the specified columns from the grid
   * @param columns the columns to remove
   */
  def removeColumns(columns: Integer*) {
    if (columns.length < 1) throw new IllegalArgumentException("Columns must contain at least one column")
    val removeOrder: List[Integer] = Arrays.asList(columns)
    Collections.sort(removeOrder)
    import scala.collection.JavaConversions._
    for (list <- this) {
      val remove: List[String] = new ArrayList[String]
      {
        var i: Int = 0
        while (i < columns.length) {
          {
            remove.add(list.get(columns(i)))
          }
          ({
            i += 1; i - 1
          })
        }
      }
      list.removeAll(remove)
    }
  }

  /**
   * Removes all rows with a column of missingValue
   * @param column he column to remove by
   * @param missingValue the missingValue sentinel value
   */
  def removeRowsWithEmptyColumn(column: Int, missingValue: String) {
    val remove: List[List[String]] = new ArrayList[List[String]]
    import scala.collection.JavaConversions._
    for (list <- this) {
      if (list.get(column) == missingValue) remove.add(list)
    }
    removeAll(remove)
  }

  def getRowsWithColumnValues(values: Collection[String], column: Int): List[List[String]] = {
    val ret: List[List[String]] = new ArrayList[List[String]]
    import scala.collection.JavaConversions._
    for (`val` <- this) {
      if (values.contains(`val`.get(column))) ret.add(`val`)
    }
    return ret
  }

  def sortColumnsByWordLikelihoodIncluded(column: Int) {
    val counter: Nothing = new Nothing
    val col: List[String] = getColumn(column)
    import scala.collection.JavaConversions._
    for (s <- col) {
      val tokenizer: StringTokenizer = new StringTokenizer(s)
      while (tokenizer.hasMoreTokens) {
        counter.incrementCount(tokenizer.nextToken, 1.0)
      }
    }
    if (counter.totalCount <= 0.0) {
      StringGrid.log.warn("Unable to calculate probability; nothing found")
      return
    }
    counter.incrementAll(counter.keySet, 1.0)
    val remove: Set[String] = new HashSet[String]
    import scala.collection.JavaConversions._
    for (key <- counter.keySet) if (key.length < 2 || key.matches("[a-z]+")) remove.add(key)
    import scala.collection.JavaConversions._
    for (key <- remove) counter.removeKey(key)
    counter.pruneKeysBelowThreshold(4.0)
    val totalCount: Double = counter.totalCount
    Collections.sort(this, new Comparator[List[String]]() {
      def compare(o1: List[String], o2: List[String]): Int = {
        val c1: Double = sumOverTokens(counter, o1.get(column), totalCount)
        val c2: Double = sumOverTokens(counter, o2.get(column), totalCount)
        return Double.compare(c1, c2)
      }
    })
  }

  private def sumOverTokens(counter: Nothing, column: String, totalCount: Double): Double = {
    val tokenizer: StringTokenizer = new StringTokenizer(column)
    var count: Double = 0
    while (tokenizer.hasMoreTokens) count += Math.log(counter.getCount(column) / totalCount)
    return count
  }

  def clusterColumn(column: Int): Nothing = {
    return new Nothing(getColumn(column))
  }

  def dedupeByClusterAll {
    {
      var i: Int = 0
      while (i < size) {
        dedupeByCluster(i)
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /**
   * Deduplicate based on the column clustering signature
   * @param column
   */
  def dedupeByCluster(column: Int) {
    val cluster: Nothing = clusterColumn(column)
    System.out.println(cluster.get("family mcdonalds restaurant"))
    System.out.println(cluster.get("family mcdonalds restaurants"))
    val list2: List[Map[String, Integer]] = cluster.getClusters
    {
      var i: Int = 0
      while (i < list2.size) {
        {
          if (list2.get(i).size > 1) {
            System.out.println(list2.get(i))
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val keyer: Nothing = new Nothing
    val alreadyDeDupped: Set[Integer] = new HashSet[Integer]
    {
      var i: Int = 0
      while (i < size) {
        {
          val key: String = keyer.key(get(i).get(column))
          val map: Map[String, Integer] = cluster.get(key)
          if (map != null && map.size > 1) {
            val list: List[Integer] = filterRowsByColumn(column, map.keySet)
            if (list.size > 1) modifyRows(alreadyDeDupped, column, list, map)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /**
   * Cleans up the rows specified that haven't already been deduplified
   * @param alreadyDeDupped the already dedupped rows
   * @param column the column to homogenize
   * @param rows the rows to preProcess
   * @param cluster the cluster of values
   */
  private def modifyRows(alreadyDeDupped: Set[Integer], column: Integer, rows: List[Integer], cluster: Map[String, Integer]) {
    var chosenKey: String = null
    var max: Integer = null
    import scala.collection.JavaConversions._
    for (entry <- cluster.entrySet) {
      val key: String = entry.getKey
      val value: Int = entry.getValue
      val `val`: StringTokenizer = new StringTokenizer(key)
      val list: List[String] = new ArrayList[String]
      var allLower: Boolean = true
      while (`val`.hasMoreTokens) {
        val token: String = `val`.nextToken
        if (token.length >= 3 && token.matches("[A-Z]+")) {
          continue //todo: continue is not supported
        }
        list.add(token)
      } //todo: labels is not supported
      import scala.collection.JavaConversions._
      for (s <- list) {
        allLower = allLower && s.matches("[a-z]+")
      }
      if (allLower) {
        continue //todo: continue is not supported
      }
      if (list.get(list.size - 1).toLowerCase == "the") {
        continue //todo: continue is not supported
      }
      if (max == null || (!allLower && value > max)) {
        max = value
        chosenKey = key
      }
    }
    if (chosenKey == null) {
      var max2: String = maximalValue(cluster)
      val `val`: StringTokenizer = new StringTokenizer(max2)
      val list: List[String] = new ArrayList[String]
      while (`val`.hasMoreTokens) {
        var token: String = `val`.nextToken
        if (token.length >= 3 && token.matches("[A-Z]+")) {
          token = token.charAt(0) + token.substring(1).toLowerCase
        }
        list.add(token)
      }
      var allLower: Boolean = true
      import scala.collection.JavaConversions._
      for (s <- list) allLower = allLower && s.matches("[a-z]+")
      if (list.get(list.size - 1).toLowerCase == "the") {
        max2 = max2.replaceAll("^[Tt]he", "")
      }
      if (allLower) max2 = StringUtils.capitalize(max2)
      chosenKey = max2
    }
    import scala.collection.JavaConversions._
    for (i2 <- rows) {
      if (!alreadyDeDupped.contains(i2)) {
        disambiguateRow(i2, column, chosenKey)
      }
    }
  }

  private def maximalValue(map: Map[String, Integer]): String = {
    val counter: Nothing = new Nothing
    import scala.collection.JavaConversions._
    for (entry <- map.entrySet) {
      counter.incrementCount(entry.getKey, entry.getValue)
    }
    return counter.argMax
  }

  private def disambiguateRow(row: Integer, column: Integer, chosenValue: String) {
    System.out.println("SETTING " + row + " column " + column + " to " + chosenValue)
    get(row).set(column, chosenValue)
  }

  def filterRowsByColumn(column: Int, values: Collection[String]): List[Integer] = {
    val list: List[Integer] = new ArrayList[Integer]
    {
      var i: Int = 0
      while (i < size) {
        {
          if (values.contains(get(i).get(column))) list.add(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return list
  }

  def sortBy(column: Int) {
    Collections.sort(this, new Comparator[List[String]]() {
      def compare(o1: List[String], o2: List[String]): Int = {
        return o1.get(column).compareTo(o2.get(column))
      }
    })
  }

  def toLines: List[String] = {
    val lines: List[String] = new ArrayList[String]
    import scala.collection.JavaConversions._
    for (list <- this) {
      val sb: StringBuilder = new StringBuilder
      import scala.collection.JavaConversions._
      for (s <- list) {
        sb.append(s.replaceAll(sep, " "))
        sb.append(sep)
      }
      lines.add(sb.toString.substring(0, sb.lastIndexOf(sep)))
    }
    return lines
  }

  def swap(column1: Int, column2: Int) {
    val col1: List[String] = getColumn(column1)
    val col2: List[String] = getColumn(column2)
    {
      var i: Int = 0
      while (i < size) {
        {
          get(i).set(column1, col2.get(i))
          get(i).set(column2, col1.get(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def merge(column1: Int, column2: Int) {
    checkInvalidColumn(column1)
    checkInvalidColumn(column2)
    if (column1 != column2)
    import scala.collection.JavaConversions._
    for (list <- this) {
      val sb: StringBuilder = new StringBuilder
      sb.append(list.get(column1))
      sb.append(list.get(column2))
      list.set(Math.min(column1, column2), sb.toString.replaceAll("\"", "").replace(sep, " "))
      list.remove(Math.max(column1, column2))
    }
    numColumns -= 1
  }

  def getAllWithSimilarity(threshold: Double, firstColumn: Int, secondColumn: Int): StringGrid = {
    for (column <- Array[Int](firstColumn, secondColumn)) checkInvalidColumn(column)
    val grid: StringGrid = new StringGrid(sep, numColumns)
    import scala.collection.JavaConversions._
    for (list <- this) {
      val sim: Double = MathUtils.stringSimilarity(list.get(firstColumn), list.get(secondColumn))
      if (sim >= threshold) grid.addRow(list)
    }
    return grid
  }

  @throws(classOf[IOException])
  def writeLinesTo(path: String) {
    FileUtils.writeLines(new File(path), toLines)
  }

  def fillDown(value: String, column: Int) {
    checkInvalidColumn(column)
    import scala.collection.JavaConversions._
    for (list <- this) list.set(column, value)
  }

  def select(column: Int, value: String): StringGrid = {
    val grid: StringGrid = new StringGrid(sep, numColumns)
    {
      var i: Int = 0
      while (i < size) {
        {
          val row: List[String] = get(i)
          if (row.get(column) == value) {
            grid.addRow(row)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return grid
  }

  def split(column: Int, sepBy: String) {
    val col: List[String] = getColumn(column)
    var validate: Int = -1
    val remove: Set[String] = new HashSet[String]
    {
      var i: Int = 0
      while (i < col.size) {
        {
          val s: String = col.get(i)
          val split2: Array[String] = StringUtils.splitOnCharWithQuoting(s, sepBy.charAt(0), '"', '\\')
          if (validate < 0) validate = split2.length
          else if (validate != split2.length) {
            StringGrid.log.warn("Row " + get(i) + " will be invalid after split; removing")
            remove.add(s)
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    import scala.collection.JavaConversions._
    for (s <- remove) {
      val grid: StringGrid = select(column, s)
      removeAll(grid)
    }
    val replace: Map[Integer, List[String]] = new HashMap[Integer, List[String]]
    {
      var i: Int = 0
      while (i < size) {
        {
          val list: List[String] = get(i)
          val newList: List[String] = new ArrayList[String]
          val split: String = list.get(column)
          val split2: Array[String] = StringUtils.splitOnCharWithQuoting(split, sepBy.charAt(0), '"', '\\')
          {
            var j: Int = 0
            while (j < list.size) {
              {
                if (j == column) for (s <- split2) newList.add(s)
                else newList.add(list.get(j))
              }
              ({
                j += 1; j - 1
              })
            }
          }
          replace.put(i, newList)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    import scala.collection.JavaConversions._
    for (entry <- replace.entrySet) {
      set(entry.getKey, entry.getValue)
    }
  }

  def filterBySimilarity(threshold: Double, firstColumn: Int, secondColumn: Int) {
    for (column <- Array[Int](firstColumn, secondColumn)) checkInvalidColumn(column)
    val remove: List[List[String]] = new ArrayList[List[String]]
    import scala.collection.JavaConversions._
    for (list <- this) {
      val sim: Double = MathUtils.stringSimilarity(list.get(firstColumn), list.get(secondColumn))
      if (sim < threshold) remove.add(list)
    }
    removeAll(remove)
  }

  def prependToEach(prepend: String, toColumn: Int) {
    import scala.collection.JavaConversions._
    for (row <- this) {
      val currVal: String = row.get(toColumn)
      row.set(toColumn, prepend + currVal)
    }
  }

  def appendToEach(append: String, toColumn: Int) {
    import scala.collection.JavaConversions._
    for (row <- this) {
      val currVal: String = row.get(toColumn)
      row.set(toColumn, currVal + append)
    }
  }

  def addColumn(column: List[String]) {
    if (column.size != this.size) throw new IllegalArgumentException("Unable to add column; not enough rows")
    {
      var i: Int = 0
      while (i < size) {
        {
          get(i).add(column.get(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  /**
   * Combine the column based on a template and a number of template variable
   * columns. Note that this will also collapse the columns specified (removing them)
   *
   * @param templateColumn the column with the template ( uses printf style templating)
   * @param paramColumns the columns with template variables
   */
  def combineColumns(templateColumn: Int, paramColumns: Array[Integer]) {
    import scala.collection.JavaConversions._
    for (list <- this) {
      val format: List[String] = new ArrayList[String]
      for (j <- paramColumns) format.add(list.get(j))
      list.set(templateColumn, String.format(list.get(templateColumn), format.toArray(Array[String]).asInstanceOf[Array[AnyRef]]))
      list.removeAll(format)
    }
  }

  /**
   * Combine the column based on a template and a number of template variable
   * columns. Note that this will also collapse the columns specified (removing them)
   *
   * @param templateColumn the column with the template ( uses printf style templating)
   * @param paramColumns the columns with template variables
   */
  def combineColumns(templateColumn: Int, paramColumns: Array[Int]) {
    import scala.collection.JavaConversions._
    for (list <- this) {
      val format: List[String] = new ArrayList[String]
      for (j <- paramColumns) format.add(list.get(j))
      list.set(templateColumn, String.format(list.get(templateColumn), format.toArray(Array[String]).asInstanceOf[Array[AnyRef]]))
      list.removeAll(format)
    }
  }

  def addRow(row: List[String]) {
    if (row.isEmpty) {
      StringGrid.log.warn("Unable to add empty row")
    }
    else if (!isEmpty && row.size != get(0).size) {
      StringGrid.log.warn("Unable to add row; not the same number of columns")
    }
    else add(row)
  }

  def mapByPrimaryKey(columnKey: Int): Map[String, List[List[String]]] = {
    val map: Map[String, List[List[String]]] = new HashMap[String, List[List[String]]]
    import scala.collection.JavaConversions._
    for (line <- this) {
      val `val`: String = line.get(columnKey)
      var get: List[List[String]] = map.get(`val`)
      if (get == null) {
        get = new ArrayList[List[String]]
        map.put(`val`, get)
      }
      get.add(new ArrayList[String](Arrays.asList(sep)))
    }
    return map
  }

  def getRow(row: Int): List[String] = {
    checkInvalidRow(row)
    return new ArrayList[String](get(row))
  }

  def getColumn(column: Int): List[String] = {
    checkInvalidColumn(column)
    val ret: List[String] = new ArrayList[String]
    import scala.collection.JavaConversions._
    for (list <- this) {
      ret.add(list.get(column))
    }
    return ret
  }

  private def checkInvalidRow(row: Int) {
    if (row < 0 || row >= size) throw new IllegalArgumentException("Row does not exist")
  }

  private def checkInvalidColumn(column: Int) {
    if (column < 0 || column >= numColumns) throw new IllegalArgumentException("Invalid column " + column)
  }

  def getRowsWithDuplicateValuesInColumn(column: Int): StringGrid = {
    checkInvalidColumn(column)
    val grid: StringGrid = new StringGrid(sep, numColumns)
    val columns: List[String] = getColumn(column)
    val counter: Nothing = new Nothing
    import scala.collection.JavaConversions._
    for (`val` <- columns) counter.incrementCount(`val`, 1.0)
    counter.pruneKeysBelowThreshold(2.0)
    val keys: Set[String] = counter.keySet
    import scala.collection.JavaConversions._
    for (row <- this) {
      import scala.collection.JavaConversions._
      for (key <- keys) if (row.get(column) == key) grid.addRow(row)
    }
    return grid
  }

  def getRowWithOnlyOneOccurrence(column: Int): StringGrid = {
    checkInvalidColumn(column)
    val grid: StringGrid = new StringGrid(sep, numColumns)
    val columns: List[String] = getColumn(column)
    val counter: Nothing = new Nothing
    import scala.collection.JavaConversions._
    for (`val` <- columns) counter.incrementCount(`val`, 1.0)
    val keys: Set[String] = new HashSet[_](counter.keySet)
    import scala.collection.JavaConversions._
    for (key <- keys) {
      if (counter.getCount(key) > 1) {
        counter.removeKey(key)
      }
    }
    import scala.collection.JavaConversions._
    for (row <- this) {
      import scala.collection.JavaConversions._
      for (key <- keys) if (row.get(column) == key) grid.addRow(row)
    }
    return grid
  }

  def getUniqueRows: StringGrid = {
    val ret: StringGrid = new StringGrid(this)
    ret.stripDuplicateRows
    return ret
  }

  def stripDuplicateRows {
    val set: Set[List[String]] = new HashSet[List[String]](this)
    clear
    addAll(set)
  }
}