package org.aja.tej.examples.dataset.hetrogenity_activity_recognition

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassifier, LogisticRegression}
import org.apache.spark.ml.feature.{StringIndexer, IndexToString, VectorIndexer}
import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.mllib.linalg.{Vectors,Vector}

/**
  * Created by mdhandapani on 5/2/16.
  */
object MLPipeLine {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  val toDouble = udf[Double, String](_.toDouble)

  val toVector = udf((x:Double, y:Double, z:Double) => Vectors.dense(x,y,z))

  val watchSchema = StructType(Seq(
    StructField("index",        IntegerType, true),
    StructField("arrival_time", DoubleType, true),
    StructField("creation_time",DoubleType, true),
    StructField("x",            DoubleType, true),
    StructField("y",            DoubleType, true),
    StructField("z",            DoubleType, true),
    StructField("user",         StringType, true),
    StructField("model",        StringType, true),
    StructField("device",       StringType, true),
    StructField("ground_truth", StringType, true)))

  val labelMap = Map("sit" -> "0","stairsdown" -> "1", "walk" -> "2", "stand" -> "3", "bike" -> "4", "stairsup" -> "6", "null" -> null)

  val csvToDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(watchSchema)
    .load("data/hetrogenity_activity_recognition/Watch_accelerometer.csv")
    .na.replace("ground_truth", labelMap)
    .na.drop() //Drop Rows with null



  def main(args: Array[String]) {

    val rawData = csvToDF.withColumn("ground_truth", toDouble(csvToDF("ground_truth")))

    val data = rawData.select(rawData("ground_truth").as("label"), toVector($"x",$"y", $"z").as("features"))
    data.registerTempTable("trainingSet")
    data.show(10)

    //!!!!! Not suitable for MultiClass !!!!!
    //    val lr = new LogisticRegression()
    //
    //    println("LogisticsRegression parameters: \n" + lr.explainParams())
    //
    //    lr.setMaxIter(10).setRegParam(0.01)
    //
    //    val model = lr.fit(trainingSet)

    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedlabel")
      .fit(data)

    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedfeatures")
      .setMaxCategories(6)
      .fit(data)

    val Array(trainingData, testData) = data.randomSplit(Array(0.7, 0.3))

    val dt = new DecisionTreeClassifier()
    .setLabelCol("indexedlabel")
    .setFeaturesCol("indexedfeatures")

    val labelConverter = new IndexToString()
    .setInputCol("prediction")
    .setOutputCol("predictedLabel")
    .setLabels(labelIndexer.labels)

    val pipeline = new Pipeline().setStages(Array(labelIndexer, featureIndexer, dt, labelConverter))

    val model = pipeline.fit(trainingData)

    val predictions = model.transform(testData)

    predictions.select("predictedLabel", "label", "features").show(5)


  }

}
