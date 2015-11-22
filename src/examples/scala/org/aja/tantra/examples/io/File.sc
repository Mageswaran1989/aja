import java.io._

import scala.io.Source

val bufferedSource = Source.fromFile("/opt/aja/README.md")
try {
  for (line <- bufferedSource.getLines()) {
    println(line)
  }
} catch {
  case e: FileNotFoundException => println("FileNotFoundException")
  case e: IOException => println("IOException")
}
bufferedSource.close

val readmeFile = Source.fromFile("/opt/aja/README.md").getLines().toList

//////////////////////////////////////////////////////
def listDir(path: String): List[File] = {
  val dir = new File(path)
  if (dir.exists && dir.isDirectory) {
    dir.listFiles().filter(_.isFile).toList
  } else {
    List[File]()
  }
}
val dataFiles = listDir("/opt/aja/data")
for (file <- dataFiles) {
  println(file)
}

//Writer
// - PrintWriter
// - FileWriter
//new BufferedWriter(new FileWritter(new File("file.txt")))
val text = "Aja-dhira"
val testWrite = new BufferedWriter(new FileWriter(new File("Aja.txt")))
testWrite.write("Aja Aja Aja\n")
testWrite.write("Aja Aja Aja")
testWrite.close()
val ajaFile = Source.fromFile("Aja.txt")
ajaFile.getLines().foreach(println)
val ajaJavaFile = new File("Aja.txt")
ajaJavaFile.canExecute
ajaJavaFile.canWrite
ajaJavaFile.getCanonicalPath
val buffereReader = new BufferedReader(new FileReader(ajaJavaFile))
buffereReader.readLine()

println("//////////////////////////////////////////////////////////////////")

val fileBuffer = Source.fromFile("/opt/aja/data/testSet.txt")
fileBuffer.getLines().toList.map(_.split("\t")).foreach(x => println(x(0) + x(1) +" " + x(2)))

println("////////////////////////////////////////////////////////////////////////////")

val f = new java.io.File("/tmp/.Aja")
if (f.exists()) f.delete()

val configFilePath = new java.io.File("/tmp/.Aja")
if (configFilePath.exists()) {
  configFilePath.getAbsolutePath
} else {
  println("In else!")
  configFilePath.createNewFile()
  configFilePath.getAbsolutePath
}

