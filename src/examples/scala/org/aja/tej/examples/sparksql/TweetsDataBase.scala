package org.aja.tej.examples.sparksql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
 * Created by mageswaran on 18/10/15.
 */
object TweetsDataBase {
  def main(args: Array[String]) {

    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

    val sqlContext = new SQLContext(sc)

    val tweetsDF = sqlContext.read.json("data/tweets/tweets*/part-*")

    tweetsDF.show()
    tweetsDF.printSchema()

    println("Number of tweets: " + tweetsDF.count)
  }
}
