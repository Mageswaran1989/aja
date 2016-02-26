import sbt.Keys._
import sbt._

object BuildSettings {

  val Name = "aja"
  val Version = "0.0.1"
  // You can use either version of Scala. We default to 2.11.7:
  val ScalaVersion = "2.11.7"
  val ScalaVersions = Seq("2.11.7", "2.10.5")


  lazy val buildSettings = Defaults.coreDefaultSettings ++ Seq (
    name          := Name,
    version       := Version,
    scalaVersion  := ScalaVersion,
    crossScalaVersions := ScalaVersions,
    organization  := "com.aja",
    description   := "Accomplish Joyfull Adventures",
    scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8", "-Xlint")
  )
}

object Resolvers {
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
  val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  val sonatype = "Sonatype Release" at "https://oss.sonatype.org/content/repositories/releases"
  val mvnrepository = "MVN Repo" at "http://mvnrepository.com/artifact"
  val cloudera = "Cloudera Repos" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
  val neo4Cyper = "anormcypher" at "http://repo.anormcypher.org/"
  val allResolvers = Seq(neo4Cyper, typesafe, sonatype, mvnrepository, cloudera)

}

//Dont use %% for thir party libraries for which appending Scala version might not help in fetching
object Dependency {
  object Version {
    val Spark        = "1.6.0"
    val ScalaTest    = "2.2.4"
    val ScalaCheck   = "1.12.2"
    val Twitter      = "3.0.3"
    val Breeze       = "0.11.2"
    val Akka         = "2.4.0"
    val BreezeViz    = "0.12"
    //    val scalaActors  = "2.11"
    val sparkCSV = "1.3.0"
    val sparkts  = "0.1.0"
    val neo4JScalaCypher = "0.7.1"
  }

  //                                       %% means use scala version
  val sparkCore             = "org.apache.spark"      %% "spark-core"       % Version.Spark  withSources()
  val sparkMLLib            = "org.apache.spark"      %% "spark-mllib"      % Version.Spark  withSources()
  val sparkStreaming        = "org.apache.spark"      %% "spark-streaming"  % Version.Spark  withSources()
  val sparkStreamingKafta   = "org.apache.spark"      %% "spark-streaming-kafka" % Version.Spark  withSources()
  val sparkStreamingflume   = "org.apache.spark"      %% "spark-streaming-flume" % Version.Spark  withSources()
  val sparkStreamingTwitter = "org.apache.spark"      %% "spark-streaming-twitter" % Version.Spark  withSources()
  val sparkSQL              = "org.apache.spark"      %% "spark-sql"        % Version.Spark  withSources()
  val sparkGrapx            = "org.apache.spark"      %% "spark-graphx"     % Version.Spark  withSources()
  val sparkHive             = "org.apache.spark"      %% "spark-hive"       % Version.Spark  withSources()
  val sparkRepl             = "org.apache.spark"      %% "spark-repl"       % Version.Spark  withSources()
  val scalaTest             = "org.scalatest"         %% "scalatest"        % Version.ScalaTest  % "test"
  val scalaCheck            = "org.scalacheck"        %% "scalacheck"       % Version.ScalaCheck % "test"
  val twitterCoreAddon      = "org.twitter4j"         % "twitter4j-core"    % Version.Spark  withSources()
  val twitterStreamAddon    = "org.twitter4j"         % "twitter4j-stream"  % Version.Spark  withSources()
  val gsonLib               = "com.google.code.gson"  % "gson"              % "2.3" withSources()
  val cli                   = "commons-cli"           % "commons-cli"       % "1.2" withSources()
  val breeze                = "org.scalanlp"          %% "breeze"           % Version.Breeze withSources()
  //Breeze native uses OpenBlass and ATLAS
  val breezeNatives         = "org.scalanlp"          %% "breeze-natives"   % Version.Breeze withSources()
  val akka                  = "com.typesafe.akka"     %% "akka-actor"       % Version.Akka withSources()
  val breeezeViz            =  "org.scalanlp"         %% "breeze-viz"       % Version.BreezeViz withSources()
  //  val scalaActor     =  "org.scala-lang"       %% "scala-actors"     % Version.scalaActors withSources()
  val sparkCSV              = "com.databricks"        % "spark-csv_2.11"    % Version.sparkCSV withSources()
  val sparkTS               = "com.cloudera.sparkts"  %% "sparkts" % Version.sparkts withSources()
  //val neo4jScalaCypher = "org.anormcypher" % "anormcypher_2.11" % Version.neo4JScalaCypher withSources()
  val scalaChart            = "com.github.wookietreiber" %% "scala-chart"   % "latest.integration" withSources()
  val scalaPlot             = "org.sameersingh.scalaplot" % "scalaplot"     % "0.0.4" withSources()
}

object Dependencies {
  import Dependency._

  val tej =
    Seq(sparkCore, sparkMLLib, sparkStreaming, sparkStreamingKafta, sparkStreamingflume,
      sparkStreamingTwitter, sparkSQL, sparkGrapx, sparkHive, sparkRepl,
      scalaTest, scalaCheck, twitterCoreAddon, twitterStreamAddon, gsonLib, cli, breeze,
      breezeNatives, breeezeViz, akka, sparkCSV, scalaChart, scalaPlot)
}

object TejSparkBuild extends Build {
  import BuildSettings._



  val excludeSigFilesRE = """META-INF/.*\.(SF|DSA|RSA)""".r
  lazy val activatorspark = Project(
    id = "aja-workspace",
    base = file("."),
    settings = buildSettings ++ Seq(
      shellPrompt := { state => "(%s)> ".format(Project.extract(state).currentProject.id) },
      unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "examples" / "scala",
      unmanagedSourceDirectories in Compile += baseDirectory.value / "src" / "examples" / "java",
      maxErrors          := 5,
      triggeredMessage   := Watched.clearWhenTriggered,
      // runScriptSetting,
      //resolvers := Resolvers.allResolvers,
      exportJars := true,
      // For the Hadoop variants to work, we must rebuild the package before
      // running, so we make it a dependency of run.
      (run in Compile) <<= (run in Compile) dependsOn (packageBin in Compile),
      libraryDependencies ++= Dependencies.tej,
      dependencyOverrides ++= Set(
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
      ),
      excludeFilter in unmanagedSources := (HiddenFileFilter || "*-script.scala"),
      // unmanagedResourceDirectories in Compile += baseDirectory.value / "conf",
      mainClass := Some("run"),
      // Must run the examples and tests in separate JVMs to avoid mysterious
      // scala.reflect.internal.MissingRequirementError errors. (TODO)
      // fork := true,
      // Must run Spark tests sequentially because they compete for port 4040!
      parallelExecution in Test := false))

}

