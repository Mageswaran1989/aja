package org.aja.tej.examples.usecases.map.reduce

import org.aja.tej.utils.TejUtils

/**
 * Created by mageswaran on 15/10/15.
 */

/**
  Collating
  =========
    Problem Statement: There is a set of items and some function of one item. It is required to save all items that have
    the same value of function into one file or perform some other computation that requires all such items to be
    processed as a group. The most typical example is building of inverted indexes.

   Solution:
   --------
      The solution is straightforward. Mapper computes a given function for each item and emits value of the function as a
   key and item itself as a value. Reducer obtains all items grouped by function value and process or save them. In case of
   inverted indexes, items are terms (words) and function is a document ID where the term was found.

   Applications:
   --------------
    Inverted Indexes, ETL

   Document 1                                        ID  Term     Doc_ID
   Document 2   ===> Stoping Word List Filter ===>   1   obtains    1
   Document 3                                        ...

*/
object Collating extends App{

  val sc = TejUtils.getSparkContext("MapReducePattern - Collating")
  val idAccumulator = sc.accumulator(0, "Local Term ID")

  val invertedIndexTable = sc.wholeTextFiles("data/email/ham").map(rec => {
    idAccumulator += 1
    val localTerm = rec._2.split(" ").map(_.trim)
    val localDocId = rec._1.split("/").last

    (idAccumulator, localTerm, localDocId)
  }
  )

  println("ID \t Term \t DocId")
  invertedIndexTable.foreach(println)

  println()

  val oneRec = invertedIndexTable.take(1).map(x => println(s"Id: ${x._1} \n " +
    s"Terms: ${x._2.foreach(println)} \n " +
    s"DocId: ${x._3}"))


}
