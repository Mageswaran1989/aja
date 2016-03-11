package org.aja.tej.examples.dataset

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 7/3/16.
 */

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

case class Badges(Id: Int, UserId: Int, Name: String, Date: Long)

case class Comments(Id: Int, PostId: Int, Score: Int, Text: String, CreationDate: Long, UserId: Int)

abstract class StackTable[T] {

  val file: File

  protected def getDate(n: scala.xml.NodeSeq): Long = n.text match {
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

  protected def getInt(n: scala.xml.NodeSeq): Int = n.text match {
    case "" => 0
    case x => x.toInt
  }

  protected def parseXml(x: scala.xml.Elem): T

  def parse(s: String): Option[T] =
    if (s.startsWith("  <row ")) Some(parseXml(scala.xml.XML.loadString(s)))
    else None

}

object Post extends StackTable[Post] {

  val file = new File("data/datascience.stackexchange.com/Posts.xml")
  assert(file.exists)
  //Update the get methods with repect to the readme.txt of stackoverflow dataset
  override protected def parseXml(x: scala.xml.Elem): Post = Post(
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

  protected def getTags(x: scala.xml.NodeSeq): Array[String] = x.text match {
    case "" => Array()
    case s => s.drop(1).dropRight(1).split("><")
  }
}

object Badges extends StackTable[Badges] {

  val file =  new File("data/datascience.stackexchange.com/Badges.xml")
  assert(file.exists)
  //Update the get methods with repect to the readme.txt of stackoverflow dataset
  override protected def parseXml(x: scala.xml.Elem): Badges = Badges(
    getInt(x \ "@Id"),
    getInt(x \ "@UserId"),
    (x \ "@Name").text,
    getDate(x \ "@Date")
  )
}

object Comments extends StackTable[Comments] {

  val file =  new File("data/datascience.stackexchange.com/Comments.xml")
  assert(file.exists)
  //Update the get methods with repect to the readme.txt of stackoverflow dataset
  override protected def parseXml(x: scala.xml.Elem): Comments = Comments(
    getInt(x \ "@Id"),
    getInt(x \ "@PostId"),
    getInt(x \ "@Score"),
    (x \ "@Text").text,
    getDate(x \ "@CreationDate"),
    getInt(x \ "@UserId")
  )
}

class StackOverFlowUtility(sc: SparkContext) {

  private val xmlBadgeFile = sc.textFile(Badges.file.getAbsolutePath)
  val rddBadges = xmlBadgeFile.flatMap(Badges.parse)

  val xmlPostFile = sc.textFile(Post.file.getAbsolutePath)
  val rddPost = xmlPostFile.flatMap(Post.parse)

  val xmlCommentFile = sc.textFile(Comments.file.getAbsolutePath)
  val rddComments = xmlCommentFile.flatMap(Comments.parse)

}

object StackOverFlowUtility {
  def apply(sc: SparkContext) = new StackOverFlowUtility(sc)

  val dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

  def getHour(date: Long) = {
    val calender = Calendar.getInstance()
    calender.setTimeInMillis(date)
    calender.get(Calendar.HOUR_OF_DAY)
  }


}
