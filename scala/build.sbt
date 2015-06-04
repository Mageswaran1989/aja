organization := "Aja"

name := "Artificial Neural Network"

version := 0.01

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
   "org.apache.commons" % "commons-math3" % "3.3",
   "org.jfree" % "jfreechart" % "1.0.17",
   "com.typesafe.akka" %% "akka-actor" % "2.2.3",
   "org.apache.spark" %% "spark-core" % "1.0.2",
   "org.apache.spark" %% "spark-mllib" % "1.0.2",
   "org.scalatest" %% "scalatest" % "2.1.6"
)

// Resolver for Apache Spark framework
resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

