package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
generator, setGenerator
Allows setting a string that is attached to the end of the RDD’s name when printing the
dependency graph.

 */
object GeneratorExample  extends App {

  def useCases(sc: SparkContext) = {
//    @transient var generator
//    def setGenerator ( _generator : String )

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
