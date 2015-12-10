package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
 * Created by mageswaran on 23/10/15.
 * Link: https://www.mapr.com/blog/using-apache-spark-dataframes-processing-tabular-data
 * Dataset links: http://www.modelingonlineauctions.com/datasets
 *                https://data.sfgov.org/Public-Safety/SFPD-Incidents-from-1-January-2003/tmnf-yvry
 */

/*
ebay.csv
auctionId   bid bidTime    bidder   bidderRate, openBid  price  item   daysToLive
8213034705, 95, 2.927373, jake7870, 0,         95,       117.5, xbox, 3

Data Field Description
auctionid - unique identifier of an auction
bid - the proxy bid placed by a bidder
bidtime - the time (in days) that the bid was placed, from the start of the auction
bidder - eBay username of the bidder
bidderrate - eBay feedback rating of the bidder
openbid - the opening bid set by the seller
price - the closing price that the item sold for (equivalent to the second highest bid + an increment)

 */
object HelloEbayDataset {

  //define the schema using a case class
  case class Auction(auctionid: String, bid: Float, bidtime: Float, bidder: String, bidderrate: Integer, openbid: Float,
                     price: Float, item: String, daystolive: Integer)

  def main(args: Array[String]) {

    //Initialize the SparkContext and SQL context
    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._ //To convert RDD -> DF

    val ebayDataSetRDD = sc.textFile("data/ebay/ebay.csv")

    println(ebayDataSetRDD.first())

    //==================================================================================================================
    val ebayDF = ebayDataSetRDD.map(line => line.split(",")) //RDD[Array(Array((),(),()...),Array((),(),()...), ....)]
                               .map(p => Auction(p(0), p(1).toFloat, p(2).toFloat, p(3), p(4).toInt, p(5).toFloat,
                                         p(6).toFloat, p(7), p(8).toInt)) //RDD[Auction] objects
                               .toDF //Implicit conversion to DataFrames inferring case class as the schema

    ebayDF.printSchema()

    ebayDF.show() //print top 20 rows

    println("How many auctions were held?  \n" + ebayDF.select("auctionid").distinct.count) //"auctionid" from the case class variable name

    println("How many bids were made per item?")
    ebayDF.groupBy("auctionid", "item").count.sort("auctionid").show
    ebayDF.groupBy("item", "auctionid").count.sort("auctionid").show

    ebayDF.groupBy("item", "auctionid").count.sort("auctionid").explain() //To print Catalyst physical plan

//      What's the minimum, maximum, and average number of bids per item?
    //ebayDF.groupBy("item", "auctionid").count.agg(min($"count"), max($"count"), avg($"count"))

    println("Showing the bids with price > 100")
    ebayDF.filter("price > 200").show()

    //==================================================================================================================

    ebayDF.registerTempTable("EbayAuctionTable")
    println("How many bids were made per item?")
    var results = sqlContext.sql("SELECT auctionid, item,  count(bid) FROM EbayAuctionTable GROUP BY auctionid, item").sort("auctionid")
    results.show()

    println("What's the minimum, maximum, and average number of bids per item?")
    results =sqlContext.sql("SELECT auctionid, MAX(price) FROM EbayAuctionTable  GROUP BY item,auctionid")
    results.show()

    //From: sfpd dataset
    //csv lib : https://github.com/databricks/spark-csv
    //    groupId: com.databricks
   //    artifactId: spark-csv_2.11
   //    version: 1.2.0
    //  Return the dataset specified by data source as a DataFrame, use the header for column names
    //val df = sqlContext.load("com.databricks.spark.csv", Map("path" -> "sfpd.csv", "header" -> "true"))
  }

}
