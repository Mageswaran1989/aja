package org.aja.tej.examples.sparksql.sql

import java.sql.{Timestamp, Date}

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.{SQLContext, Row}
import org.apache.spark.sql.types._
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by mageswaran on 24/1/16.
  */
object DateTime extends  App{

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  val schema = StructType(
    Seq(
      StructField("id", IntegerType, true),
      StructField("dt", DateType, true),
      StructField("ts", TimestampType, true)
    )
  )
  val rows = sc.parallelize(
    Seq(
      Row(
        1,
        Date.valueOf("2000-01-11"),
        Timestamp.valueOf("2011-10-02 09:48:05.123456")
      ),
      Row(
        1,
        Date.valueOf("2004-04-14"),
        Timestamp.valueOf("2011-10-02 12:30:00.123456")
      ),
      Row(
        1,
        Date.valueOf("2008-12-31"),
        Timestamp.valueOf("2011-10-02 15:00:00.123456")
      )
    ), 4)
  val tdf = sqlContext.createDataFrame(rows, schema)

  tdf.printSchema()

  tdf.registerTempTable("dates_times")

  println("*** Here's the whole table")
  sqlContext.sql("SELECT * FROM dates_times").show()
  //  +---+----------+--------------------+
  //  | id|        dt|                  ts|
  //  +---+----------+--------------------+
  //  |  1|2000-01-11|2011-10-02 09:48:...|
  //  |  1|2004-04-14|2011-10-02 12:30:...|
  //  |  1|2008-12-31|2011-10-02 15:00:...|
  //  +---+----------+--------------------+

  println("*** Query for a date range")
  sqlContext.sql(
    s"""
       |  SELECT * FROM dates_times
       |  WHERE dt > cast('2002-01-01' as date)
       |    AND dt < cast('2006-01-01' as date)
       """.stripMargin).show()

  println("*** Query to skip a timestamp range")
  sqlContext.sql(
    s"""
       |  SELECT * FROM dates_times
       |  WHERE ts < cast('2011-10-02 12:00:00' as timestamp)
       |     OR ts > cast('2011-10-02 13:00:00' as timestamp)
       """.stripMargin).show()
}
