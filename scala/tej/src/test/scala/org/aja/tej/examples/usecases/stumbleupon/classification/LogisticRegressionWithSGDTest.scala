package org.aja.tej.examples.usecases.stumbleupon.classification

import StumbleuponUtils._
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * Created by mageswaran on 7/8/15.
 */
object LogisticRegressionWithSGDTest {

  def main(args: Array[String]) {

    val lrModel = LogisticRegressionWithSGD.train(getLabeledPoint, numIterations)

    val lrTotalCorrect = getLabeledPoint.map { point =>
      if (lrModel.predict(point.features) == point.label) 1 else 0}.sum

    val lrAccuracy = lrTotalCorrect / getNumData

    println(s"LogisticRegressionWithSGD Accuracy $lrAccuracy")

    val lrMetrics = Seq(lrModel).map { model =>
      val scoreAndLabels = getLabeledPoint.map { point => (model.predict(point.features), point.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      (model.getClass.getSimpleName, metrics.areaUnderPR, metrics.areaUnderROC)
    }

    lrMetrics.foreach{ case (m, pr, roc) =>
      println(f"$m, Area under PR: ${pr * 100.0}%2.4f%%, Area under ROC: ${roc * 100.0}%2.4f%%")
    }

    val scaler = new StandardScaler(withMean = true, withStd = true).fit(featureVectors)
    val scaledData = getLabeledPoint.map(lp => LabeledPoint(lp.label, scaler.transform(lp.features)))
    println(getLabeledPoint.first.features)
    println(scaledData.first.features)
    //test the first value of scaledData
    println((0.789131 - 0.41225805299526636) / math.sqrt(0.1097424416755897))

    val lrModelScaled = LogisticRegressionWithSGD.train(scaledData,numIterations)
    val lrTotalCorrectScaled = scaledData.map { point =>
      if (lrModelScaled.predict(point.features) == point.label) 1 else 0
    }.sum

    val lrAccuracyScaled = lrTotalCorrectScaled / getNumData
    val lrPredictionsVsTrue = scaledData.map { point =>
      (lrModelScaled.predict(point.features), point.label)
    }

    val lrMetricsScaled = new BinaryClassificationMetrics(lrPredictionsVsTrue)

    val lrPr = lrMetricsScaled.areaUnderPR
    val lrRoc = lrMetricsScaled.areaUnderROC

    println(f"${lrModelScaled.getClass.getSimpleName}\nAccuracy:${lrAccuracyScaled * 100}%2.4f%%\nArea under PR: ${lrPr *
      100.0}%2.4f%%\nArea under ROC: ${lrRoc * 100.0}%2.4f%%")

    //Adding new Feature to improve prediction accuracy
    val categories = getRawData.map(r => r(3)).distinct.collect.zipWithIndex.toMap
    val numCategories = categories.size
    println(categories)

    println(numCategories)

    val dataCategories = getRawData.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val categoryIdx = categories(r(3))
      val categoryFeatures = Array.ofDim[Double](numCategories)
      categoryFeatures(categoryIdx) = 1.0
      val otherFeatures = trimmed.slice(4, r.size - 1).map(d => if(d == "?") 0.0 else d.toDouble)
      val features = categoryFeatures ++ otherFeatures
      LabeledPoint(label, Vectors.dense(features))
    }

    println(dataCategories.first)

    val scalerCats = new StandardScaler(withMean = true, withStd = true).fit(dataCategories.map(lp => lp.features))
    val scaledDataCats = dataCategories.map(lp =>
      LabeledPoint(lp.label, scalerCats.transform(lp.features)))

    println(dataCategories.first.features)


    println(scaledDataCats.first.features)

    val lrModelScaledCats = LogisticRegressionWithSGD.train(scaledDataCats, numIterations)
    val lrTotalCorrectScaledCats = scaledDataCats.map { point =>
      if (lrModelScaledCats.predict(point.features) == point.label) 1 else 0
    }.sum

    val lrAccuracyScaledCats = lrTotalCorrectScaledCats / getNumData
    val lrPredictionsVsTrueCats = scaledDataCats.map { point =>
      (lrModelScaledCats.predict(point.features), point.label)
    }

    val lrMetricsScaledCats = new BinaryClassificationMetrics(lrPredictionsVsTrueCats)
    val lrPrCats = lrMetricsScaledCats.areaUnderPR
    val lrRocCats = lrMetricsScaledCats.areaUnderROC

    println(f"${lrModelScaledCats.getClass.getSimpleName}\nAccuracy:${lrAccuracyScaledCats * 100}%2.4f%%\nArea under PR: ${lrPrCats * 100.0}%2.4f%%\nArea under ROC: ${lrRocCats * 100.0}%2.4f%%")


    ////////////////////////////////////////////
    //Tuning
    ///////////////////////////////////////////




  }
}
