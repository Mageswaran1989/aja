package org.aja.tej.examples.spark.rdd.summarizationPatterns

import org.aja.dataset.StackOverFlowDS
import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 10/3/16.
 */

object SummaryPatternInAction  extends App {

  def useCases(sc: SparkContext) = {

    //Problem1: Given a list of user’s comments, determine the first and last time a user com‐
    //mented and the total number of comments from that user.

    val userIdCreationDateMap = StackOverFlowDS(sc).rddComments.map(comments => (comments.UserId, comments.CreationDate))
    val groupedByUser = userIdCreationDateMap.groupByKey()
    //( (userId1, (time1, time2) ...), (userId2, (time1, time2) ...) )
    //So what we want? UserId,Minimum,Maximum,Count
    val solution1 = groupedByUser.map{case(userId, groupedDates) => (userId, groupedDates.min, groupedDates.max, groupedDates.size)}


    //Problem2: Given a list of user’s comments, determine the average comment length per
    //hour of day.
    val commentsHoursTuple = StackOverFlowDS(sc).rddComments
      .map(comments => (StackOverFlowDS.getHour(comments.CreationDate), comments.Text.length))
      .groupByKey()

    val solution2 = commentsHoursTuple.map{case (hour, commentsLength) => (hour,commentsLength.sum/commentsLength.size)}.take(10)

    //Problem3: Given a list of user’s comments, determine the median and standard deviation
    // of comment lengths per hour of day.
    val solution3 = commentsHoursTuple.mapValues(iter => iter.toList.sorted)
      .map{case (hour, commentsLengthSeq) =>
        val evenOrOdd = commentsLengthSeq.size % 2
        val median = if(evenOrOdd == 0)
          commentsLengthSeq.toIndexedSeq(commentsLengthSeq.size/2)
        else
          commentsLengthSeq.toIndexedSeq(commentsLengthSeq.size/2) + commentsLengthSeq.toIndexedSeq(commentsLengthSeq.size/2+1) /2
        val mean = commentsLengthSeq.sum / commentsLengthSeq.size
        val variance = commentsLengthSeq.map(_ - mean).map(v =>v * v).sum/commentsLengthSeq.size
        val stdDev = Math.sqrt(variance)
        (hour,median,stdDev)
      }


    //Problem4: Inverted index of links in posts
    //    Suppose we want to add StackOverflow links to each Wikipedia page that is referenced
    //      in a StackOverflow comment. The following example analyzes each comment in Stack‐
    //    Overflow to find hyperlinks to Wikipedia. If there is one, the link is output with the
    //    comment ID to generate the inverted index. When it comes to the reduce phase, all the
    //      comment IDs that reference the same hyperlink will be grouped together. These groups
    //      are then concatenated together into a white space delimited String and directly output
    //    to the file system. From here, this data file can be used to update the Wikipedia page
    //    with all the comments that reference it.



    //    val urlPattern = """/^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/""".r.findAllIn(text)
    val commentIdUrlTuple = StackOverFlowDS(sc).rddComments
      .map{comments => {
        val urlPattern = """(https?):\/\/(www\.)?[a-z0-9\.:].*?(?=\s)""".r.findAllIn(comments.Text)
        (comments.PostId,  urlPattern.toList)
      }}

    commentIdUrlTuple.take(4)


    //    val text = "Fair enough regarding what constitutes a &quot;valid&quot; question, although on other SE sites this " +
    //      "question would **not** be immediately closed as you've stated: e.g., " +
    //      "[2495 votes](http://stackoverflow.com/questions/194812/list-of-freely-available-programming-books), " +
    //      "[1440 votes](http://stackoverflow.com/questions/1711/what-is-the-single-most-influential-book-every-programmer-should-read), " +
    //      "[168 votes](http://tex.stackexchange.com/questions/11/what-is-the-best-book-to-start-learning-latex), and so on. " +
    //      "There's great interest for these kinds of questions, even if this isn't deemed the right place."
    //    val urlPattern = """(https?):\/\/(www\.)?[a-z0-9\.:].*?(?=\s)""".r.findAllIn(text).foreach(println)

    TejUtils.waitForSparkUI(sc)

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}

