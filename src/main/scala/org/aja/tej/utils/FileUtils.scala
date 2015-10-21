package org.aja.tej.utils
import java.io._

/**
 * Created by mageswaran on 30/8/15.
 */
object FileUtils {
  //Used to throw RuntimeException when file not found
  case class FileOperationError(msg: String) extends RuntimeException(msg)

  //Overloaded function that converts the given path string as a folder
  def rmrf(root: String): Unit = rmrf(new File(root))

  //Recursively delete the files and folders
  def rmrf(root: File): Unit = {
    if (root.isFile)
      root.delete()
    else if (root.exists) {
      root.listFiles foreach rmrf
      root.delete()
    }
  }

  //Overloaded function that converts given path string as a file
  def rm(file: String): Unit = rm(new File(file))

  //Deletes the file
  def rm(file: File): Unit =
    if (file.delete == false) throw FileOperationError(s"Deleting $file failed!")


}
