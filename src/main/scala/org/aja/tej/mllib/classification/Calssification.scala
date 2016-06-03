package org.aja.tej.mllib.classification

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors


/**
 * Created by mdhandapani on 5/8/15.
 *
 * In MlLib three common classification models are available:
 * 1. Linear Models
 *    * Loss functions
 *      > Logistic Loss ~ Logistic Regression
 *      > Hinge Loss ~ Support Vector Machine
 *      > Zero-one Loss
 * 2. Decision Trees
 * 3. Naive Bayes
 */

/**
 * Common Steps:
 * 1. Setup the SparkContext
 * 2. Prepare the data
 */
class Calssification {

  /**
   * Linear Models
   *
   * Mathematics:
   *  y = f(w^T x)
   *  Here, y is the target variable, w is the vector of parameters (known as the weightvector),
   *  and x is the vector of input features.
   *  wTx is the linear predictor (or vector dot product) of the weight vector w and feature vector x.
   *  To this linear predictor, we applied a function f (called the link function).
   */

  /**
   * Logistic regression
   * Model Nature: A probabilistic model
   * Output: Predictions bounded between o and 1
   * Mathematics:
   * Link function : 1 / (1 + exp(-w^Tx)
   * Loss function : 1 / (1 + exp(-yw^Tx)
   */

  /**
   * Linear support vector machines -Powerful technique for regression and classification
   * Model Nature: Non probabilistic
   * Output: Positive or negative
   *
   * Mathematics:
   * Link function : y = w^Tx
   * Loss function : max(0,1-yw^Tx)
   */



  /**
   * Decision trees
   * Model Nature: Non probabilistic and based on information gain
   * Output: Yes/No or Left/Right or 0/1
   */




}
