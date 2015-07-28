name := "dhira"

version := "1.0"

//Scala version is downgraded from 2.11.7 to match scalatest versions
scalaVersion := "2.11.7"

libraryDependencies +=
  "log4j" % "log4j" % "1.2.15" excludeAll(
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jms")
    )

//libraryDependencies  ++= Seq(
//"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
//"org.scalanlp" %% "breeze" % "0.11.2",
//"org.scalanlp" %% "breeze-natives" % "0.11.2",
//"org.scalanlp" %% "breeze-viz" % "0.11.2",
//  "log4j" %% "log4j" % "2.3"
//)

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)
