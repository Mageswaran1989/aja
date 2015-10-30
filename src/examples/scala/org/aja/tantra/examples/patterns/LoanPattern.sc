import scala.io.Source

//Loan pattern makes sure that the resource being used is automatically get closed

object Control {
  //A can be anything with with a method close()
  //Uing curring, first provide the resources, second the function literal that operated on the object
  def using[A <: {def close(): Unit},B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }
}

val ajaReadme = Control.using(Source.fromFile("/opt/aja/README.md")){ bufferedSource => {
  bufferedSource.getLines().foreach(println)
}
}
def readTextFile(file: String): Option[List[String]] = {
  try {
    Control.using(Source.fromFile(file)) { bufferedSource =>
      Some(bufferedSource.getLines().toList)
    }
  }catch {
    case e: Exception => None
  }
}
val rd = readTextFile("/opt/aja/README.md") match {
  case Some(lines) => lines.foreach(println)
  case None => println("File Not found")
}
val rd1 = readTextFile("/opt/aja/README.mdd") match {
  case Some(lines) => lines.foreach(println)
  case None => println("File Not found")
}