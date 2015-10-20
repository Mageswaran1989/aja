initialCommands += """
  import org.apache.spark.SparkContext
  import org.apache.spark.SparkContext._
  import org.apache.spark.sql.SQLContext
  val sc = new SparkContext("local[*]", "Console")
  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._
                   """

cleanupCommands += """
  println("Closing the SparkContext:")
  sc.stop()
                   """.stripMargin

addCommandAlias("WordCountMain",  "test:runMain org.aja.tej.examples.usecases.wordcount.WordCountMain")

fork in run := true

unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "examples"
unmanagedSourceDirectories in Compile += baseDirectory.value / "examples"

/*
 * Plain old model. Currently using project/Build.scala for better approach
 */

/*name := "tej"

version := "0.1"

scalaVersion := "2.11.7"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.4.1",
  "org.apache.spark" %% "spark-mllib" % "1.4.1",
  "org.apache.spark" %% "spark-graphx" % "1.4.1",
  "org.apache.spark" %% "spark-sql" % "1.4.1",
  "org.apache.spark" %% "spark-hive" % "1.4.1",
  "org.apache.spark" %% "spark-streaming" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-flume" % "1.4.1",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.4.1",
  "org.twitter4j" % "twitter4j-core" % "3.0.3",
  "org.twitter4j" % "twitter4j-stream" % "3.0.3",
  "com.google.code.gson" %% "gson" % "2.3",
  "commons-cli" %% "commons-cli" % "1.2"
)



resolvers ++= Seq(
//  "Twitter4J Repository" at "http://twitter4j.org/maven2/"
//  "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
//  "Spray Repository" at "http://repo.spray.cc/",
//  " loudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
//  "Akka Repository" at "http://repo.akka.io/releases/"
//  "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
//  "Twitter Maven Repo" at "http://maven.twttr.com/",
//  "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
//  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
//  "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
//  "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
//  Resolver.sonatypeRepo("public")
) */



/**
  *  Experimental
  */

//(unmanagedSourceDirectories in Compile) += (baseDirectory.value /  "src/examples/scala")
//(unmanagedSourceDirectories in Compile) += (baseDirectory.value /  "spark_example")

//scalaSource in Compile += file(baseDirectory.value + "src/examples/scala")
//scalaSource in Compile += file(baseDirectory.value + "spark_examples")
//
//unmanagedSourceDirectories in Compile <<= (scalaSource in  Compile)( base => base / "spark_example" :: Nil)
//unmanagedSourceDirectories in Compile <<= (scalaSource in  Compile)( base => base /  "src/examples/scala" :: Nil)

//unmanagedSourceDirectories in Compile <++= baseDirectory { base => base / "src/examples"
////  Seq(
////    base / "source/a/b",
////    base / "source/a/c",
////    base / "source/d",
////    base / "source/e"
////  )
//}
