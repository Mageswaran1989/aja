package org.aja.tej.examples.spark.rdd.summarizationPatterns

import java.io.File
import java.text.SimpleDateFormat

import org.aja.tej.utils.TejUtils

/**
 * Created by mageswaran on 7/3/16.
 */

case class Badges(Id: Int, UserId: Int, Name: String, Date: Long)

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

object Badges extends StackTable[Badges] {

  val file =  new File("data/datascience.stackexchange.com/Badges.xml")
  assert(file.exists)
  //Update the get methods with repect to the readme.txt of stackoverflow dataset
  override def parseXml(x: scala.xml.Elem): Badges = Badges(
    getInt(x \ "@Id"),
    getInt(x \ "@UserId"),
    (x \ "@Name").text,
    getDate(x \ "@Date")
  )
}

object PatternInAction {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val xmlFile = sc.textFile(Badges.file.getAbsolutePath)
  val rddBadges = xmlFile.flatMap(Badges.parse)


}
