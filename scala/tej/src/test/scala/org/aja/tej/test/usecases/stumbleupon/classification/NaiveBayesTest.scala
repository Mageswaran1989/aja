package org.aja.tej.tej.test.usecases.stumbleupon.classification

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

// Calculate PrecisionRecall and ROC curve (receiver operating characteristic )
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import StumbleuponUtils._

/**
 * Created by mdhandapani on 6/8/15.
 */
object NaiveBayesTest {

  def main(args: Array[String]) {

    lazy val nbModel = NaiveBayes.train(StumbleuponUtils.getLabeledDataForNb)

    val nbTotalCorrect = StumbleuponUtils.getLabeledDataForNb.map { point =>
      if (nbModel.predict(point.features) == point.label) 1 else 0
    }.sum

    val nbAccuracy = nbTotalCorrect / StumbleuponUtils.getNumData

    println(s"Naive Bayes Accuracy: $nbAccuracy")

    val nbMetrics = Seq(nbModel).map{ model =>
      val scoreAndLabels = StumbleuponUtils.getLabeledDataForNb.map { point =>
        val score = model.predict(point.features)
        (if (score > 0.5) 1.0 else 0.0, point.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      (model.getClass.getSimpleName, metrics.areaUnderPR,metrics.areaUnderROC)
    }

    nbMetrics.foreach{ case (m, pr, roc) =>
      println(f"$m, Area under PR: ${pr * 100.0}%2.4f%%, Area under ROC: ${roc * 100.0}%2.4f%%")
    }

    //Adding new Feature to improve prediction accuracy
    val categories = getRawData.map(r => r(3)).distinct.collect.zipWithIndex.toMap
    val numCategories = categories.size
    println(categories)


    val dataNB = getRawData.map { r =>
      val trimmed = r.map(_.replaceAll("\"", ""))
      val label = trimmed(r.size - 1).toInt
      val categoryIdx = categories(r(3))
      val categoryFeatures = Array.ofDim[Double](numCategories)
      categoryFeatures(categoryIdx) = 1.0
      LabeledPoint(label, Vectors.dense(categoryFeatures))
    }

    val nbModelCats = NaiveBayes.train(dataNB)
    val nbTotalCorrectCats = dataNB.map { point =>
      if (nbModelCats.predict(point.features) == point.label) 1 else 0
    }.sum

    val nbAccuracyCats = nbTotalCorrectCats / getNumData
    val nbPredictionsVsTrueCats = dataNB.map { point =>
      (nbModelCats.predict(point.features), point.label)
    }

    val nbMetricsCats = new BinaryClassificationMetrics(nbPredictionsVsTrueCats)
    val nbPrCats = nbMetricsCats.areaUnderPR
    val nbRocCats = nbMetricsCats.areaUnderROC

    println(f"${nbModelCats.getClass.getSimpleName}\nAccuracy:${nbAccuracyCats * 100}%2.4f%%\nArea under PR: ${nbPrCats * 100.0}%2.4f%%\nArea under ROC: ${nbRocCats * 100.0}%2.4f%%")



  }
}
