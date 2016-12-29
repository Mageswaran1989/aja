/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */
package org.dhira.core.util

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io._
import java.util.zip.GZIPInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author Mageswaran Dhandapani
 * @since 10-Sep-2016
 */
object ArchiveUtils {
  private val log: Logger = LoggerFactory.getLogger(classOf[ArchiveUtils])

  /**
   * Extracts files to the specified destination
   * @param file the file to extract to
   * @param dest the destination directory
   * @throws IOException
   */
  @throws(classOf[IOException])
  def unzipFileTo(file: String, dest: String) {
    val target: File = new File(file)
    if (!target.exists) throw new IllegalArgumentException("Archive doesnt exist")
    val fin: FileInputStream = new FileInputStream(target)
    val BUFFER: Int = 2048
    val data:  Array[Byte] = new Array[Byte](BUFFER)
    if (file.endsWith(".zip")) {
      val zis: ZipInputStream = new ZipInputStream(fin)
      var ze: ZipEntry = zis.getNextEntry
      while (ze != null) {
        val fileName: String = ze.getName
        val newFile: File = new File(dest + File.separator + fileName)
        log.info("file unzip : " + newFile.getAbsoluteFile)
        new File(newFile.getParent).mkdirs
        val fos: FileOutputStream = new FileOutputStream(newFile)
        var len: Int = 0
        while ((({
          len = zis.read(data); len
        })) > 0) {
          fos.write(data, 0, len)
        }
        fos.close
        ze = zis.getNextEntry
      }
      zis.closeEntry
      zis.close
    }
    else if (file.endsWith(".tar.gz") || file.endsWith(".tgz")) {
      val in: BufferedInputStream = new BufferedInputStream(fin)
      val gzIn: GzipCompressorInputStream = new GzipCompressorInputStream(in)
      val tarIn: TarArchiveInputStream = new TarArchiveInputStream(gzIn)
      var entry: TarArchiveEntry = null
      while ((({
        entry = tarIn.getNextEntry.asInstanceOf[TarArchiveEntry]; entry
      })) != null) {
        log.info("Extracting: " + entry.getName)
        if (entry.isDirectory) {
          val f: File = new File(dest + File.separator + entry.getName)
          f.mkdirs
        }
        else {
          var count: Int = 0
          val fos: FileOutputStream = new FileOutputStream(dest + File.separator + entry.getName)
          val destStream: BufferedOutputStream = new BufferedOutputStream(fos, BUFFER)
          while ((({
            count = tarIn.read(data, 0, BUFFER); count
          })) != -1) {
            destStream.write(data, 0, count)
          }
          destStream.flush
          IOUtils.closeQuietly(destStream)
        }
      }
      tarIn.close
    }
    else if (file.endsWith(".gz")) {
      val is2: GZIPInputStream = new GZIPInputStream(fin)
      val extracted: File = new File(target.getParent, target.getName.replace(".gz", ""))
      if (extracted.exists) extracted.delete
      extracted.createNewFile
      val fos: OutputStream = FileUtils.openOutputStream(extracted)
      IOUtils.copyLarge(is2, fos)
      is2.close
      fos.flush
      fos.close
    }
    target.delete
  }
}

class ArchiveUtils {
  private def this() {
    this()
  }
}