package org.aja.tej.examples.mllib.textprocessing

import org.aja.tej.utils.{TejUtils, TejTwitterUtils}

/**
 * Created by mdhandapani on 17/12/15.
 */
object Explore20NewsGroup extends App {
  val path = "data/20_newsgroups"

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val rdd = sc.wholeTextFiles(path)
  val text = rdd.map { case (file, text) => text }
  println(text.count)

}
