package org.aja.tej.test.usecases.stumbleupon.classification

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.configuration.Algo
import org.apache.spark.mllib.tree.impurity.Entropy
import StumbleuponUtils._

/**
 * Created by mageswaran on 7/8/15.
 */
object DecisionTreeTest {

  val dtModel = DecisionTree.train(getLabeledPoint, Algo.Classification, Entropy, maxTreeDepth)

  val dtTotalCorrect = getLabeledPoint.map { point =>
    val score = dtModel.predict(point.features)
    val predicted = if (score > 0.5) 1 else 0
    if (predicted == point.label) 1 else 0
  }.sum

  val dtAccuracy = dtTotalCorrect / getNumData

  println(s"DecisionTree Accuracy $dtAccuracy")

  val dtMetrics = Seq(dtModel).map{ model =>
    val scoreAndLabels = getLabeledPoint.map { point =>
      val score = model.predict(point.features)
      (if (score > 0.5) 1.0 else 0.0, point.label)
    }
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    (model.getClass.getSimpleName, metrics.areaUnderPR, metrics.areaUnderROC)
  }

  dtMetrics.foreach{ case (m, pr, roc) =>
    println(f"$m, Area under PR: ${pr * 100.0}%2.4f%%, Area under ROC: ${roc * 100.0}%2.4f%%")
  }

}
