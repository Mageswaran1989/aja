package org.aja.tej.examples.mllib

import org.aja.tej.utils.TejUtils
import org.apache.spark.mllib.linalg.{DenseVector, Vectors}
import org.apache.spark.mllib.regression.{LinearRegressionWithSGD, LabeledPoint}
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.rdd.RDD

/**
 * Created by mdhandapani on 19/10/15.
 */
object HelloMLLib {

  //Convert each line of dataset to Array[Double] values
  //18.9,350,165,260,8,2.56,4,3,200.3,69.9,3910,1
  def parseData(line : String) : Array[Double] = {
    val values = line.split(",")

    val mpg = values(0).toDouble
    val displacement = values(1).toDouble
    val hp = values(2).toInt
    val torque = values(3).toInt
    val CRatio = values(4).toDouble
    val RARatio = values(5).toDouble
    val CarbBarrells = values(6).toInt
    val NoOfSpeed = values(7).toInt
    val length = values(8).toDouble
    val width = values(9).toDouble
    val weight = values(10).toDouble
    val automatic = values(11).toInt

    return Array(mpg,displacement,hp,  // return keyword explicitly
                 torque,CRatio,RARatio,CarbBarrells,
                 NoOfSpeed,length,width,weight,automatic)
  }

  def rddArrayToLP(arr: Array[Double]) = {
    new LabeledPoint(arr(0), Vectors.dense(arr(1), arr(2), arr(3), arr(4), arr(5),
                                           arr(6), arr(7), arr(8), arr(9), arr(10), arr(11)))
  }

  //https://www.kaggle.com/c/titanic/data
  //  survival        Survival    (0 = No; 1 = Yes)
  //  pclass          Passenger Class  (1 = 1st; 2 = 2nd; 3 = 3rd)
  //  name            Name
  //    sex             Sex
  //    age             Age
  //    sibsp           Number of Siblings/Spouses Aboard
  //  parch           Number of Parents/Children Aboard
  //  ticket          Ticket Number
  //  fare            Passenger Fare
  //  cabin           Cabin
  //    embarked        Port of Embarkation (C = Cherbourg; Q = Queenstown; S = Southampton)
  //PassengerId,Survived,Pclass,Name                     ,Sex ,Age,SibSp,Parch,Ticket   ,Fare,Cabin,Embarked
  //1,                 0,     3,"Braund, Mr. Owen Harris",male,22 ,    1,    0,A/5 21171,7.25,     ,       S
  //0,                 1,     2,      3,                4,    5, 6,    7,    8,        9,  10,   11,       12  //Index
  def titanicDatasetToLP(line: String) = {
    val values = line.split(",")

    //skip passenger id values(0)
    val survival = values(1).toDouble
    val pclass = values(2).toDouble
    //skip name values(3) values(4)
    val sex = values(5).trim.equalsIgnoreCase("male")
    val age = values(6).toDouble
    val sibsp = values(7).toDouble
    val parch = values(8).toDouble
    //skip ticket number values(9)
    val fare = values(10).toDouble
    //val cabin = if (values(11).isEmpty) //TODO: handle missing data

  }
  def main(args: Array[String]) {
    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    println("Spark version : " + sc.version)

    val carRDD = sc.textFile("data/car-milage-no-hdr.csv").map(parseData(_))

    //Summary Statictics
    println("\n**********************************************************************\n")
    val carDataVector = carRDD.map(v => Vectors.dense(v))
    val summary = Statistics.colStats(carDataVector)


    carRDD.foreach(rdd => {
                            rdd.foreach(value => print("%6.2f | ".format(value)))
                            println()
                          })
    print("Max: ");summary.max.toArray.foreach(value => print("%5.1f | ".format(value)));println()
    print("Min: ");summary.min.toArray.foreach(value => print("%5.1f | ".format(value)));println()
    print("Mean: ");summary.mean.toArray.foreach(value => print("%5.1f | ".format(value)));println()

    println("\n**********************************************************************\n")
    println("Correlations")
    val hp = carDataVector.map(_(2))
    val weights = carDataVector.map(_(10))
    var corPearson = Statistics.corr(hp, weights, "pearson")
    println(" hp to weights: Pearson correlation =  %2.4f".format(corPearson))
    var corSpearman = Statistics.corr(hp, weights, "spearman")
    println(" hp to weights: Spearman correlation =  %2.4f".format(corSpearman))

    println("\n**********************************************************************\n")
    println("Linear Regression")

    val carRDDLP = carRDD.map(rddArrayToLP(_))
    val carRDDLPTrainingData = carRDDLP.filter(lp => lp.features(9) <= 4000)
    val carRDDLPTestData = carRDDLP.filter(lp => lp.features(9) > 4000)

    println("Number of trainingData: %3d".format(carRDDLPTrainingData.count))
    println("Number of TestData: %3d".format(carRDDLPTestData.count))

    //
    // Train a Linear Regression Model
    // numIterations = 100, stepsize = 0.000000001
    // without such a small step size the algorithm will diverge
    //
    val lrModel = LinearRegressionWithSGD.train(carRDDLPTrainingData, 100, 0.0000001)

    println("Linear Regression: intercept -> " + lrModel.intercept + " weights -> " + lrModel.weights)

    val valuesAndPredictions = carRDDLPTestData.map(lp => (lp.label, lrModel.predict(lp.features)))

    val mse = valuesAndPredictions.map(vp => math.pow((vp._1 - vp._2), 2))
                                  //.sum()
                                  .reduce( _ + _)

    println("Mean square error for the LRModle predictions: " + mse)
    println("Root Mean square error for the LRModle predictions: " + math.sqrt(mse))

    println(" Values \t Predictions")
    valuesAndPredictions.take(20).foreach(vp => println("%5.1f \t %5.1f".format(vp._1, vp._2)))

    println("\n**********************************************************************\n")
    println("Classification")


  }
}
