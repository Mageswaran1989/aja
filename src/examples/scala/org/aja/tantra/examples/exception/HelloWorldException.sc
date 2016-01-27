

import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Created by mdhandapani on 10/8/15.
 */

//Scala doesn't support checked exceptions

try {
  val f = new FileReader("input.txt")
} catch {
  case ex: FileNotFoundException => {
    println("Missing file exception")
  }
  case ex: IOException => {
    println("IO Exception")
  }
  case unknown => println("Unknown exception " + unknown); System.exit(-1)
} finally {
  println("Exiting finally...")
}




