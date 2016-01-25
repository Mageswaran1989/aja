package org.aja.tej.examples.sparksql.sql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types._
import org.apache.spark.sql.types.StructField

/**
 * Created by mageswaran on 24/1/16.
 */
object Types extends App{

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  val numericRows = Seq(
    Row(1.toByte, 2.toShort, 3, 4.toLong,BigDecimal(1), BigDecimal(2), 3.0f, 4.0)
  )
  val numericRowsRDD = sc.parallelize(numericRows, 4)

  val numericSchema = StructType(
    Seq(
      StructField("a", ByteType, true),
      StructField("b", ShortType, true),
      StructField("c", IntegerType, true),
      StructField("d", LongType, true),
      StructField("e", DecimalType(10, 5), true),
      StructField("f", DecimalType(20, 10), true),
      StructField("g", FloatType, true),
      StructField("h", DoubleType, true)
    )
  )

  val numericDF = sqlContext.createDataFrame(numericRowsRDD, numericSchema)

  numericDF.printSchema()

  numericDF.show()

  numericDF.registerTempTable("numeric")

  sqlContext.sql("SELECT * from numeric").show()

  val miscSchema = StructType(
    Seq(
      StructField("a", BooleanType, true),
      StructField("b", NullType, true),
      StructField("c", StringType, true),
      StructField("d", BinaryType, true)
    )
  )


  val complexScehma = StructType(
    Seq(
      StructField("a", StructType(
        Seq(
          StructField("u", StringType, true),
          StructField("v", StringType, true),
          StructField("w", StringType, true)
        )
      ), true),
      StructField("b", NullType, true),
      StructField("c", StringType, true),
      StructField("d", BinaryType, true)
    )
  )

}
