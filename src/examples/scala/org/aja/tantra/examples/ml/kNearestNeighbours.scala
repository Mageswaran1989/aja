package org.aja.tantra.examples.ml

/**
  * Created by mdhandapani on 28/1/16.
  */

/*
   Pros: High Accuracy, Insensitive to outliers, No assumption about the data
   Cons: Computationally expensive, REquires lot of memory
   Dataset: Numeric values, Nominal values

   Calculate the distance between all existing data with the given data. Consider top k similar pieces of data in the
   calculated list and find their labels.
 */
object kNearestNeighbours {

  def createSampleDataSet() = {
    val group = Array.ofDim[Double](4,2)
    group(0) = Array(1,1.1)
    group(1) = Array(1,1.0)
    group(2) = Array(0,0)
    group(3) = Array(1,1.1)
    val labels = Array('A', 'A', 'B', 'B')

    (group, labels)
  }


  def main (args: Array[String]) {

    val dataSet = createSampleDataSet()

    dataSet._2.foreach(println)
  }
}
