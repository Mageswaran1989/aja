package org.aja.tej.examples.sparksql.datasets

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

/**
 * Created by mdhandapani on 26/10/15.
 */

/*
A Spark DataFrame is a distributed collection of data organized into named columns that provides operations to filter,
group, or compute aggregates, and can be used with Spark SQL.
DataFrames can be constructed from structured data files, existing RDDs, tables in Hive, or external databases.

Dataset : http://www.modelingonlineauctions.com/datasets
External plugin: https://github.com/databricks/spark-csv

http://jsonstudio.com/resources/ => World Bank dataset in json

https://catalog.data.gov/dataset?res_format=JSON
 */

//After few example, we will use below trait EbayEnv as it makes the things easy
//Below gives new user an experience of the scala class Linearization at work

trait EbaySparkContexts {
  println("Trait is used from : " + this.getClass.getSimpleName)
  val sc: SparkContext = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val sqlContext = new SQLContext(sc)

  val worldBank = "data/world_bank.json"

}
class EbayDatasetUtil extends EbaySparkContexts {

  val getRawEbayAuctionDataRDD = sc.textFile("data/ebay/ebay.csv", 10)

//  val getEbayAuctionDataDF = sqlContext.read //Error
//    .format("com.databricks.spark.csv")
//    .option("header", "true") // Use first line of all files as header
//    .option("inferSchema", "true") // Automatically infer data types
//    .load("cars.csv")

  val getWorldBankDataRawRDD = sc.textFile(worldBank, 10)

  val getWorldBankDF = sqlContext.read.json(worldBank).toDF()
}

object EbayDatasetUtil extends EbaySparkContexts {

  def getRawEbayAuctionDataRDDLazy = sc.textFile("data/ebay/ebay.csv", 10)

  def getEbayAuctionDataDFLazy = sqlContext.read.format("com.databricks.spark.csv").load("data/ebay/ebay.csv")

}

//All the above can be merged as one trait and be used

trait DataFrameEnv {
  println("Trait is used from : " + this.getClass.getSimpleName)
  val sc: SparkContext = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val sqlContext = new SQLContext(sc)
  val worldBank = "data/world_bank.json"

  val getRawEbayAuctionDataRDD = sc.textFile("data/ebay/ebay.csv", 10)

  //val getEbayAuctionDataDF = sqlContext.read.format("com.databricks.spark.csv").load("data/ebay/ebay.csv") //error

  def getRawEbayAuctionDataRDDLazy = sc.textFile("data/ebay/ebay.csv", 10)

  def getEbayAuctionDataDFLazy = sqlContext.read.format("com.databricks.spark.csv").load("data/ebay/ebay.csv")

  val getWorldBankDataRawRDD = sc.textFile(worldBank, 10)

  val getWorldBankDF = sqlContext.read.json(worldBank).toDF()

  //hive http://www.infoobjects.com/dataframes-with-apache-spark/
}