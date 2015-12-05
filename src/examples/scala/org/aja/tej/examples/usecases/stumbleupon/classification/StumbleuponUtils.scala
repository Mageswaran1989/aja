package org.aja.tej.examples.usecases.stumbleupon.classification

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by mdhandapani on 5/8/15.
 */

/**
 * Description:
*==============================================
FieldName 	            Type	  Description
*==============================================
url	                    string	Url of the webpage to be classified
urlid	                  integer	StumbleUpon's unique identifier for each url
boilerplate	            json	  Boilerplate text
alchemy_category	      string	Alchemy category (per the publicly available Alchemy API found at www.alchemyapi.com)
alchemy_category_score	double	Alchemy category score (per the publicly available Alchemy API found at www.alchemyapi.com)
avglinksize	            double	    Average number of words in each link
commonLinkRatio_1	      double	# of links sharing at least 1 word with 1 other links / # of links
commonLinkRatio_2	      double	# of links sharing at least 1 word with 2 other links / # of links
commonLinkRatio_3	      double	# of links sharing at least 1 word with 3 other links / # of links
commonLinkRatio_4	      double	# of links sharing at least 1 word with 4 other links / # of links
compression_ratio	      double	Compression achieved on this page via gzip (measure of redundancy)
embed_ratio	            double	Count of number of <embed>  usage
frameBased	            integer (0 or 1)	A page is frame-based (1) if it has no body markup but have a frameset markup
frameTagRatio	          double	Ratio of iframe markups over total number of markups
hasDomainLink	          integer (0 or 1)	True (1) if it contains an <a>  with an url with domain
html_ratio	            double	Ratio of tags vs text in the page
image_ratio	            double	Ratio of <img> tags vs text in the page
is_news	                integer (0 or 1)	True (1) if StumbleUpon's news classifier determines that this webpage is news
lengthyLinkDomain	      integer (0 or 1)	True (1) if at least 3 <a> 's text contains more than 30 alphanumeric characters
linkwordscore	          double	Percentage of words on the page that are in hyperlink's text
news_front_page	        integer (0 or 1)	True (1) if StumbleUpon's news classifier determines that this webpage is front-page news
non_markup_alphanum_characters	integer	Page's text's number of alphanumeric characters
numberOfLinks	          integer	Number of <a>  markups
numwords_in_url	        double	Number of words in url
parametrizedLinkRatio	  double	A link is parametrized if it's url contains parameters  or has an attached onClick event
spelling_errors_ratio	  double	Ratio of words not found in wiki (considered to be a spelling mistake)
label	                  integer (0 or 1)	User-determined label. Either evergreen (1) or non-evergreen (0); available for train.tsv only
 */
object StumbleuponUtils {

  def getSparkContext = {
    val conf = new SparkConf().setAppName("Stumbleupon Dataset").setMaster("local[4]" /*"spark://myhost:7077"*/)
    new SparkContext(conf)
  }

  val numIterations = 10
  val maxTreeDepth = 5

  val getRawData = {
    val rawData = getSparkContext.textFile("data/stumbleupon/train_noheader.tsv")
    rawData.map(line => line.split("\t"))
    //records.first()s
  } . cache

  val getLabeledPoint = {
    val records = getRawData
    records.map {r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val features = trimmed.slice(4, r.size - 1).map(d => if (d =="?") 0.0 else d.toDouble)
      LabeledPoint(label, Vectors.dense(features))
    } . cache
  }

  def getNumData = {
   getRawData.count
  }

  val getLabeledDataForNb = {
    //For Naive Bayes with -1 => 0
    getRawData.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val features = trimmed.slice(4, r.size - 1).map(d => if (d == "?") 0.0 else d.toDouble).map(d => if (d < 0) 0.0 else d)
      LabeledPoint(label, Vectors.dense(features))
    } . cache
  }

  val featureVectors = getLabeledPoint.map(lp => lp.features)
  val featureMatrix = new RowMatrix(featureVectors)

  def printMatrixSummary = {
    val matrixSummary = featureMatrix.computeColumnSummaryStatistics()

    println(matrixSummary.mean)
    println(matrixSummary.min)
    println(matrixSummary.max)
    println(matrixSummary.variance)
    println(matrixSummary.numNonzeros)
    matrixSummary.mean.toArray.foreach(println)
    matrixSummary.variance.toArray.foreach(println)
  }

  val getFirstData = {
    getLabeledPoint.first
  }

  val getFirstDataLabel = {
    getFirstData.label
  }

}
