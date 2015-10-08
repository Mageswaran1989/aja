import android.Keys._
android.Plugin.androidBuild
platformTarget in Android := "android-22"
scalaVersion := "2.11.2"
name := "hello-world-scala"
javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6")
