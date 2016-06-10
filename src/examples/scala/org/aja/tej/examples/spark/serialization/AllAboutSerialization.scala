package org.aja.tej.examples.spark.serialization

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 30/10/15.
 */

/*
Spark is a distributed computing engine and its main abstraction is a resilient distributed dataset (RDD), which can be
viewed as a distributed collection. Basically, RDD's elements are partitioned across the nodes of the cluster, but Spark
abstracts this away from the user, letting the user interact with the RDD (collection) as if it were a local one.

Not to get into too many details, but when you run different transformations on a RDD (map, flatMap, filter and others),
your transformation code (closure) is:

1. Serialized on the driver node,
2. Shipped to the appropriate nodes in the cluster,
3. Deserialized, and
4. Finally executed on the nodes

You can of course run this locally, but all those phases (apart from shipping over network) still
occur. [This lets you catch any bugs even before deploying to production]

Links: http://stackoverflow.com/questions/22592811/task-not-serializable-java-io-notserializableexception-when-calling-function-ou
 */

//You know I am going to take your application down :)
class ClassRDDManupulation_Ver1 {
  def listIncrement(sc : SparkContext) = {

    val listRDD = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10), 2)
    def increment(x: Int) = x + 1
    println("ClassRDDManupulation_Ver1")
    val incrementedListRDD = listRDD.map(increment(_))
    incrementedListRDD.foreach(println)
  }
}

class ClassRDDManupulation_Ver2 extends java.io.Serializable { //Someone packed me! huh I am gonna travel
def listIncrement(sc : SparkContext) = {

  val listRDD = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10), 2)
  def increment(x: Int) = x + 1
  println("ClassRDDManupulation_Ver2")
  val incrementedListRDD = listRDD.map(increment(_))
  incrementedListRDD.foreach(println)
}
}

//Only the increment anonymous function is serialized by default by Spark
class ClassRDDManupulation_Ver3 {
  def listIncrement(sc : SparkContext) = {

    val listRDD = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10), 2)
    val increment = (x: Int) => x + 1 //Hey there... now I am a first class citizen!
    println("ClassRDDManupulation_Ver3")
    val incrementedListRDD = listRDD.map(increment(_))
    incrementedListRDD.foreach(println)
  }
}

//Oh god I cant be keep incarnating!
//By default whole obejct is serialized
object ClassRDDManupulation_Ver4 {
  def listIncrement(sc : SparkContext) = {

    val listRDD = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10), 2)
    def increment(x: Int) = x + 1
    println("ClassRDDManupulation_Ver4")
    val incrementedListRDD = listRDD.map(increment(_))
    incrementedListRDD.foreach(println)
  }
}

object AllAboutSerialization extends App {

  def simpleRDDManipulation(sc : SparkContext) = {

    val listRDD = sc.parallelize(List(1,2,3,4,5,6,7,8,9,10), 2)
    def increment(x: Int) = x + 1
    println("simpleRDDManipulation")
    val incrementedListRDD = listRDD.map(increment(_))
    incrementedListRDD.foreach(println)
  }

  val sc = TejUtils.getSparkContext("AllAboutSerialization")

  simpleRDDManipulation(sc)

  val classRDDManupulation_Ver2 = new ClassRDDManupulation_Ver2
  classRDDManupulation_Ver2.listIncrement(sc)

  val classRDDManupulation_Ver3 = new ClassRDDManupulation_Ver3
  classRDDManupulation_Ver3.listIncrement(sc)

  ClassRDDManupulation_Ver4.listIncrement(sc)

  //So why did this made the crash? Neither the class or the function is serializable
  val classRDDManupulation_Ver1 = new ClassRDDManupulation_Ver1
  classRDDManupulation_Ver1.listIncrement(sc)
}
