#IntelliJ + Scala Plugin + Idea SBT Plug-in + Android plugin

##Download latest Intellij:  
Go to File -> Settings -> Plugin -> Browse Repositories  

Search and install:   
- Scala  
- SBT  
- Android Support   
- ADB Idea   

Visit following github for detailed information how to use Scala in Android:  
https://github.com/pfn/android-sdk-plugin  

###In summary:

In your ~/.bashrc, add paths to   
`ANDROID_HOME=   `
`ANDROID_NDK_HOME=`   

In your global SBT plugin file:  
~/.sbt/0.13/plugins/android.sbt:  
`addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.4.8")  `

Use Android Support plugin to create Android project:  
File -> Project -> Android and follow steps  

After the project is created:  
Create a file called build.sbt and paste the following lines  

`
import android.Keys._  
android.Plugin.androidBuild  
platformTarget in Android := "android-22"  
scalaVersion := "2.11.2"  
name := "your-app-name"  
javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6")  
`

To build Android from IntelliJ:  
Goto Run -> Edit Configuration -> Create SBT- Task called "Android-SBT" and add `"android:package-debug"` in Tasks text box.  
Also remove the "Make" from "Before Launch" section.  

If you see "Environmental Variables:" empty, your setup has some problem!  


