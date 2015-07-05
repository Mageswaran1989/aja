package org.aja.spark

import org.apache.spark.SparkContext

case class SparkUtils(debug: Boolean = false) {

  def isHeader(columnName: String, line: String): Boolean = {
    line.contains(columnName)
  }
 
  def toFloat(s: String): Array[Double] = {
    if("?".equals(s)) Double.NaN else s.toDouble
  }

  def parse(line: String): List[Any] = {
    val items = line.split(",")
//    items.map(
  }
  def open_csv(fileName: String, sc: SparkContext) : Option(List[Any]) {
    val file = sc.textFile(fileName)
    val sampleLines = file.take(10)
    if(debug) file.first
    if(debug) sampleLines.filter(line => isHeader("id_1", line)).foreach(println)
    
    
  }
  
}
