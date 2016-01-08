package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.GroupedData
import org.apache.spark.sql.functions._

/**
  * Created by mdhandapani on 6/1/16.
  */
object Aggregation  extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._ //To convert RDD -> DF

  val sessionDF = Seq(
    ("day1", "user1", "session1", 100.0),
    ("day1", "user1", "session2", 200.0),
    ("day1", "user1", "session3", 300.0),
    ("day1", "user1", "session4", 400.0),
    ("day2", "user1", "session4", 90.0)
  ).toDF("day", "userId", "sessionId", "purchaseTotal")

  val groupedSessions = sessionDF.groupBy("day", "userId")

  val groupedSessionsDF = groupedSessions.agg(countDistinct("sessionId"), sum("purchaseTotal"))

  //val groupedSessionsDF1 = groupedSessions.agg($"day", $"userId", countDistinct("sessionId") -> "count", sum("purchaseTotal") -> "sum")

  groupedSessionsDF.foreach(println)

}
