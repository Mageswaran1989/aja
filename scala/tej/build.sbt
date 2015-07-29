name := "tej"

version := "0.1"

scalaVersion := "2.11.7"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.4.1",
  "org.apache.spark" %% "spark-mllib" % "1.4.1",
  "org.apache.spark" %% "spark-sql" % "1.4.1",
  "org.apache.spark" %% "spark-hive" % "1.4.1",
  "org.apache.spark" %% "spark-streaming" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-flume" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.4.1"
)

libraryDependencies += "org.twitter4j" %% "twitter4j-core" % "3.0.3"

libraryDependencies += "org.twitter4j" %% "twitter4j-stream" % "3.0.3"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"