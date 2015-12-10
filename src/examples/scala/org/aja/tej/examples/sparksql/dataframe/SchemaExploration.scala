package org.aja.tej.examples.sparksql.dataframe

/**
 * Created by mageswaran on 26/11/15.
 */
object SchemaExploration extends EbayDatasetUtil with EbaySparkContexts with App {

  println("DataFrame SELECT")

  val worldBankData = getWorldBankDF

  worldBankData.printSchema()

  worldBankData.take(1).foreach(println)
}
