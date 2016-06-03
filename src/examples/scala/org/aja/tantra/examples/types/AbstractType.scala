package org.aja.tantra.examples.types

import java.io._

/**
 * Created by mageswaran on 10/11/15.
 */

//Abstract Type
abstract class BulkReader {
  type In
  val source: In
  def read: String
}

class StringBulkReader(val source: String) extends BulkReader {
  type In = String
  def read = source
}

class FileBulkReader(val source: File) extends BulkReader {
  type In = File
  def read = {
    val b = new BufferedInputStream(new FileInputStream(source))
    val size = b.available()
    val bytes = new Array[Byte](size)
    b.read(bytes, 0, size)
    new String(bytes)
  }
}

//Paramerterised type
///////////////////////////////////////////////////////////////////////////

abstract class BulkReader1[In] {
  val source: In
  def read: String
}

class StringBulkReader1(val source: String) extends BulkReader1[String] {
  def read = source
}

class FileBulkReader1(val source: File) extends BulkReader1[File] {
  def read = {
    val b = new BufferedInputStream(new FileInputStream(source))
    val size = b.available()
    val bytes = new Array[Byte](size)
    b.read(bytes, 0, size)
    new String(bytes)
  }
}

/////////////////////////////////////////////////////////////////////////

object AbstractType extends App{
  println( new StringBulkReader("Hello Scala!").read )
  //println( new FileBulkReader(new File("/bin/cp")).read )

  println( new StringBulkReader1("Hello Scala!").read )
  //println( new FileBulkReader1(new File("/bin/cp")).read )

}
