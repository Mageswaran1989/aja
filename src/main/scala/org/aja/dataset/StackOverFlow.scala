package org.aja.dataset

/**
 * Created by mdhandapani on 29/7/15.
 */
import java.io.File

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.aja.dataset.StackOverFlowDS._


object StackOverFlow extends App {
  //LogManager.getRootLogger().setLevel(Level.WARN)

  val sc = new SparkContext("local", "Main")
  val minSplits = 1
  val xmlData = sc.textFile(Post.file.getAbsolutePath, minSplits)
  val objData = xmlData.flatMap(Post.parse)
  objData.cache

  var query: RDD[Post] = objData

  query.take(20).foreach(post => if(post.tags.length > 0) println(post.tags(0)))

  println("Enter new command:")
  do {
  } while (readCommand)
  println("Exit")

  def readCommand: Boolean = {
    val command = readLine
    if (command.isEmpty)
      true //false
    else {
      //...match commands
      command match {
        case c if c.startsWith("t:") => {
          //filter for posts that contain any of the comma separated list of tags.
          val tags = c.drop(2).split(",").toSet
          println("Tags are: " + tags)
          query = query.filter(_.tags.exists(tags.contains))
          true
        }
        case c if c.startsWith("d:") => {
          //filter for posts that are within the date range
          val d = c.drop(2).split(",").map(i => Post.dateFormat.parse(i + "T00:00:00.000").getTime)
          query = query.filter(n => n.creationDate >= d(0) && n.creationDate < d(1))
          true
        }
        case "!" => time("Count") {
          println("######The count is :" + query.count)
          true
        }
        case "!t" => time("Tags") {
          val tags = query.flatMap(_.tags).countByValue
          println("$$$$$Tags: " + tags.toSeq.sortBy(_._2 * -1).take(10).mkString(","))
          true
        }
        case "~" => {
          println("Reset all filters applied to query")
          query = objData
          true
        }
        case "exit" => false
        case default => {
          println("Command invalid!")
          true
        }
      }
    }
  }
  def time[T](name: String)(block: => T): T = {
    val startTime = System.currentTimeMillis
    val result = block // call-by-name
    println(s"$name: ${System.currentTimeMillis - startTime}ms")
    result
  }
}