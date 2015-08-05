package org.aja.tej.test.usecases.stumbleupon.classification

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by mdhandapani on 5/8/15.
 */
object StumbleuponUtils {

  def getSparkContext = {
    val conf = new SparkConf().setAppName("MLLib Application").setMaster("local[2]" /*"spark://myhost:7077"*/)
    new SparkContext(conf)
  }

  def getRawData = {
    val rawData = getSparkContext.textFile("data/stumbleupon/train_noheader.tsv")
    rawData.map(line => line.split("\t"))
    //records.first()s
  }

  def getLabeledPoint {
    val records = getRawData
    records.map {r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val features = trimmed.slice(4, r.size - 1).map(d => if (d =="?") 0.0 else d.toDouble)
      LabeledPoint(label, Vectors.dense(features))
    } . cache
  }

  //data.cache
  //val numData = data.count

  def getLabeledDataForNb = {
    val records = getRawData
    //For Naive Bayes with -1 => 0
    records.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val features = trimmed.slice(4, r.size - 1).map(d => if (d == "?") 0.0 else d.toDouble).map(d => if (d < 0) 0.0 else d)
      LabeledPoint(label, Vectors.dense(features))
    } . cache
  }

  def main(args: Array[String]) {
    val nbModel = NaiveBayes.train(getLabeledDataForNb)
  }


}
