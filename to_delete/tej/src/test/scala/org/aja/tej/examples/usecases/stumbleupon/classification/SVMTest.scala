package org.aja.tej.examples.usecases.stumbleupon.classification

import org.aja.tej.examples.mllib.classification.StumbleuponUtils
import StumbleuponUtils._
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics

/**
 * Created by mageswaran on 7/8/15.
 */
object SVMTest {

  def main(args: Array[String]) {
    val svmModel = SVMWithSGD.train(getLabeledPoint, numIterations)

    val svmTotalCorrect = getLabeledPoint.map { point =>
      if (svmModel.predict(point.features) == point.label) 1 else 0
    }.sum

    val svmAccuracy = svmTotalCorrect / getNumData

    println(s"SVM Accuracy $svmAccuracy")

    val svmMetrics = Seq(svmModel).map { model =>
      val scoreAndLabels = getLabeledPoint.map { point => (model.predict(point.features), point.label)
      }
      val metrics = new BinaryClassificationMetrics(scoreAndLabels)
      (model.getClass.getSimpleName, metrics.areaUnderPR, metrics.areaUnderROC)
    }

    svmMetrics.foreach{ case (m, pr, roc) =>
      println(f"$m, Area under PR: ${pr * 100.0}%2.4f%%, Area under ROC: ${roc * 100.0}%2.4f%%")
    }

  }

}
