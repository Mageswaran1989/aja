import scala.annotation.StaticAnnotation

/**
 * Created by mageswaran on 10/1/16.
 */

class Persist(tableName: String, params: Map[String,Any])
  extends StaticAnnotation
// Doesn't compile:
//@Persist("ACCOUNTS", Map("dbms" -> "MySql", "writeAutomatically" -> true))
@Persist("ACCOUNTS", Map(("dbms", "MySql"), ("writeAutomatically", true)))
class Account(val balance: Double)

//Relax case matches in "match"
//@unchecked

// the compiler will generate the separate implementations for each primitive corresponding to an AnyVal.
//@specialized


import java.io._
class FilePrinter(val file: File) {
  @throws(classOf[IOException])
  def print() = {
    var reader: LineNumberReader = null
    try {
      reader = new LineNumberReader(new FileReader(file))
      loop(reader)
    } finally {
      if (reader != null)
        reader.close
    }
  }
  private def loop(reader: LineNumberReader): Unit = {
    val line = reader.readLine()
    if (line != null) {
      //println("" + String.format("%3d: %s\n", reader.getLineNumber, line))
      loop(reader)
    }
  }
}

val fileName = new File("data/lenses.txt")
try {
  val file = new FilePrinter(fileName).print()
} catch {
  case io: IOException  =>
  System.err.println("IOException for file " + fileName);
  System.err.println(io.getMessage());
}

