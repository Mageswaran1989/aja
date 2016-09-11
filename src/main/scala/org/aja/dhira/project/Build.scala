import sbt.Keys._
import sbt._

object BuildSettings {

  val Name = "dhira"
  val Version = "0.0.1"
  // You can use either version of Scala. We default to 2.11.7:
  val ScalaVersion = "2.11.7"
  val ScalaVersions = Seq("2.11.7", "2.10.5")


  lazy val buildSettings = Defaults.coreDefaultSettings ++ Seq (
    name          := Name,
    version       := Version,
    scalaVersion  := ScalaVersion,
    crossScalaVersions := ScalaVersions,
    organization  := "Aja",
    description   := "Accomplish Joyfull Adventures",
    scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8", "-Xlint")
  )
}

object Resolvers {
  val typesafe      = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  val sonatype      = "Sonatype Release" at "https://oss.sonatype.org/content/repositories/releases"
  val mvnrepository = "MVN Repo" at "http://mvnrepository.com/artifact"
  val allResolvers  = Seq(typesafe, sonatype, mvnrepository)

}

//Dont use %% for third party libraries for which appending Scala version might not help in fetching
object Dependency {
  object Version {
    val ScalaTest    = "2.2.4"
    val ScalaCheck   = "1.12.2"
  }


  val scalaTest             = "org.scalatest"         %% "scalatest"        % Version.ScalaTest  % "test"
  val scalaCheck            = "org.scalacheck"        %% "scalacheck"       % Version.ScalaCheck % "test"

  val cli                   = "commons-cli"           % "commons-cli"       % "1.2" withSources()


  val dataFormat            = "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-yaml" % "2.5.1" withSources()
  val apacheCompress        = "org.apache.commons"    % "commons-compress" % "1.8" withSources()
  val apacheCommonIO        = "commons-io"            % "commons-io" % "2.4"
  val nd4s                  = "org.nd4j"              % "nd4s_2.11" % "0.5.0" withSources()
  val sl4j                  = "org.slf4j"             % "slf4j-log4j12" % "1.2" withSources()
  val sl4j2                 = "org.grlea.log.adapters" % "simple-log-sl4j" % "1.7" withSources()

}

object Dependencies {
  import Dependency._

  val tej =
    Seq(scalaTest, scalaCheck, cli, dataFormat, apacheCompress, apacheCommonIO, nd4s, sl4j)
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
      resolvers := Resolvers.allResolvers,
      exportJars := true,
      // For the Hadoop variants to work, we must rebuild the package before
      // running, so we make it a dependency of run.
      (run in Compile) <<= (run in Compile) dependsOn (packageBin in Compile),
      libraryDependencies ++= Dependencies.tej,
      libraryDependencies ~= { _.map(_.exclude("ch.qos.logback", "logback-classic")) },
      dependencyOverrides ++= Set(
        "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
      ),
      excludeFilter in unmanagedSources := (HiddenFileFilter || "*-script.scala"),
      // unmanagedResourceDirectories in Compile += baseDirectory.value / "conf",
      mainClass := Some("run"),
      // Must run the examples and tests in separate JVMs to avoid mysterious
      // scala.reflect.internal.MissingRequirementError errors.
      // fork := true,
      // Must run Spark tests sequentially because they compete for port 4040!
      parallelExecution in Test := false))

}

