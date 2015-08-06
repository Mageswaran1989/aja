package org.aja.tej.test.usecases.stumbleupon.classification

import org.apache.spark.mllib.classification.NaiveBayes
// Calculate PrecisionRecall and ROC curve (receiver operating characteristic )
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics

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

  }
}
