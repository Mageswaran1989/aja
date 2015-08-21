package org.aja.tej.examples.usecases

/**
 * Created by mdhandapani on 29/7/15.
 */
import scala.xml.{ NodeSeq, MetaData }
import java.io.File
import scala.io.{ BufferedSource, Source }

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.log4j.{ LogManager, Level }

//Update the case class with repect to the readme.txt of stackoverflow dataset
case class Post(
                 id: Int,
                 postTypeId: Int,
                 parentId: Int,
                 acceptedAnswerId: Int,
                 creationDate: Long,
                 score: Int,
                 viewCount: Int,
                 body: String,
                 ownerUserId: Int,
                 lastEditorUserId: Int,
                 lastEditorDisplayName: String,
                 lastEditDate: Long,
                 lastActivityDate: Long,
                 communityOwnedDate: Long,
                 closedDate: Long,
                 title: String,
                 tags: Array[String],
                 answerCount: Int,
                 commentCount: Int,
                 favoriteCount: Int)

abstract class StackTable[T] {

  val file: File

  def getDate(n: scala.xml.NodeSeq): Long = n.text match {
    case "" => 0
    case s => dateFormat.parse(s).getTime
  }

  def dateFormat = {
    import java.text.SimpleDateFormat
    import java.util.TimeZone
    val f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    f.setTimeZone(TimeZone.getTimeZone("GMT"))
    f
  }

  def getInt(n: scala.xml.NodeSeq): Int = n.text match {
    case "" => 0
    case x => x.toInt
  }

  def parseXml(x: scala.xml.Elem): T

  def parse(s: String): Option[T] =
    if (s.startsWith("  <row ")) Some(parseXml(scala.xml.XML.loadString(s)))
    else None

}

object Post extends StackTable[Post] {

  //  val file = new File("data/Posts.xml")
  val file = new File("data/datascience.stackexchange.com/Posts.xml")
  assert(file.exists)
  //Update the get methods with repect to the readme.txt of stackoverflow dataset
  override def parseXml(x: scala.xml.Elem): Post = Post(
    getInt(x \ "@Id"),
    getInt(x \ "@PostTypeId"),
    getInt(x \ "@ParentID"),
    getInt(x \ "@AcceptedAnswerId"),
    getDate(x \ "@CreationDate"),
    getInt(x \ "@Score"),
    getInt(x \ "@ViewCount"),
    (x \ "@Body").text,
    getInt(x \ "@OwnerUserId"),
    getInt(x \ "@LastEditorUserId"),
    (x \ "@LastEditorDisplayName").text,
    getDate(x \ "@LastEditDate"),
    getDate(x \ "@LastActivityDate"),
    getDate(x \ "@CommunityOwnedDate"),
    getDate(x \ "@ClosedDate"),
    (x \ "@Title").text,
    getTags(x \ "@Tags"),
    getInt(x \ "@AnswerCount"),
    getInt(x \ "@CommentCount"),
    getInt(x \ "@FavoriteCount"))

  def getTags(x: scala.xml.NodeSeq): Array[String] = x.text match {
    case "" => Array()
    case s => s.drop(1).dropRight(1).split("><")
  }
}

object StackOverFlow extends App {
  //LogManager.getRootLogger().setLevel(Level.WARN)

  val sc = new SparkContext("local", "Main")
  val minSplits = 1
  val jsonData = sc.textFile(Post.file.getAbsolutePath, minSplits)
  val objData = jsonData.flatMap(Post.parse)
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