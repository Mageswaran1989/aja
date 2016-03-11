package org.aja.tej.examples.spark.rdd.counterPattern

import org.aja.tej.examples.dataset.StackOverFlowUtility
import org.aja.tej.utils.TejUtils

/**
 * Created by mageswaran on 10/3/16.
 */
object InAction extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
  //Problem 1: Given a list of user’s comments, count number of comments per hour
  val commentsPerHoursMap = StackOverFlowUtility(sc).rddComments.
    map(comments => (StackOverFlowUtility.getHour(comments.CreationDate), comments.Text.length)).
    countByKey().take(5)

  //Problem2 : Same as above but if dataset is very huge
  val commentsPerHoursMap1 = StackOverFlowUtility(sc).rddComments.
    map(comments => (StackOverFlowUtility.getHour(comments.CreationDate), comments.Text.length)).
    mapValues(_ => 1).reduceByKey(_ + _). //Map all values to be 1 and reduce it ;p
    take(50)
}
