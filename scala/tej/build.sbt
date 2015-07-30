name := "tej"

version := "0.1"

scalaVersion := "2.11.7"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.4"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.4"
libraryDependencies += "com.google.code.gson" % "gson" % "2.3"
libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

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



resolvers ++= Seq(
//  "Twitter4J Repository" at "http://twitter4j.org/maven2/",
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
)