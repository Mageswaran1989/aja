package org.aja.tej.examples.dataset.hetrogenity_activity_recognition

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext}

/**
  * Created by mdhandapani on 5/2/16.
  */
object MLPipeLine {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  val watchSchema = StructType(Seq(
    StructField("index", IntegerType, true),
    StructField("arrival_time", DoubleType, true),
    StructField("creation_time", DoubleType, true),
    StructField("x", DoubleType, true),
    StructField("y", DoubleType, true),
    StructField("z", DoubleType, true),
    StructField("user", StringType, true),
    StructField("model", StringType, true),
    StructField("device", StringType, true),
    StructField("grounf_truth", StringType, true)))

  val csvToDF = sqlContext.read
    .format("com.databricks.spark.csv")
    .option("header", "true")
    .schema(watchSchema)
    .load("data/hetrogenity_activity_recognition/Watch_accelerometer.csv")

  def main(args: Array[String]) {
    csvToDF.printSchema()

    csvToDF.show(10)

    //    val arrayOfRows = sc.textFile("data/hetrogenity_activity_recognition/Watch_accelerometer.csv")
    //    .map(_.split(","))
    //    .map(features => Row(features(0).toInt, features(1).toDouble, features(2).toDouble, features(3).toDouble, features(4).toDouble, features(5).toDouble, features(9)))
    //
    //    arrayOfRows.take(10).foreach(println)
  }

}
