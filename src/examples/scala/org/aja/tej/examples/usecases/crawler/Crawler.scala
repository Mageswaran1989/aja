package org.aja.tej.examples.usecases.crawler

import java.io.{File, FilenameFilter}
import org.aja.tej.utils.CommandLineOptions

import scala.io.Source

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

/**
 * Created by mdhandapani on 7/10/15.
 */

/**
 * Simulate a web crawl to prep. data for InvertedIndex5b.
 * Crawl uses <code>SparkContext.wholeTextFiles</code> to read the files
 * in a directory hierarchy and return a single RDD with records of the form:
 *    <code>(file_name, file_contents)</code>
 * After loading with <code>SparkContext.wholeTextFiles</code>, we post process
 * the data in two ways. First, we the <code>file_name</code> will be an
 * absolute path, which is normally what you would want. However, to make it
 * easier to support running the corresponding unit test <code>Crawl5aSpec</code>
 * anywhere, we strip all leading path elements. Second, the <code>file_contents</code>
 * still contains linefeeds. We remove those, so that <code>InvertedIndex5b</code>
 * can treat each line as a complete record.
 */

object Crawler {
  def main(args: Array[String]) = {

    val options = CommandLineOptions(
      this.getClass.getSimpleName,
      CommandLineOptions.inputPath("data/enron-spam-ham/*"), // Note the "*"
      CommandLineOptions.outputPath("output/crawl"),
      CommandLineOptions.master("local"),
      CommandLineOptions.quiet)

    val argz    = options(args.toList)
    val master  = argz("master").toString
    val quiet   = argz("quiet").toBoolean
    val out     = argz("output-path").toString
    val in      = argz("input-path").toString

    val separator = java.io.File.separator

    val sc = new SparkContext(master, "Crawl")

    try {
      val files_contents = sc.wholeTextFiles(argz("input-path"))
      if (!quiet)
        println(s"Writing ${files_contents.count} lines to: $out")
      // See class notes above.
      files_contents.map{
        case (id, text) =>
          val lastSep = id.lastIndexOf(separator)
          val id2 = if (lastSep < 0) id.trim else id.substring(lastSep+1, id.length).trim
          val text2 = text.trim.replaceAll("""\s*\n\s*""", " ")
          (id2, text2)
      }.saveAsTextFile(out)
    } finally {
      sc.stop()
    }
  }
}
