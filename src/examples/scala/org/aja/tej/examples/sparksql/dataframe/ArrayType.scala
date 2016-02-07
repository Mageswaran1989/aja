package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql._
import org.apache.spark.sql.types._

/**
 * Created by mageswaran on 6/2/16.
 */
object ArrayTypeExample extends App {


  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  val schema = StructType(
    Array(
      StructField("users", ArrayType(StringType, false), false )
    )
  )

  val df = sqlContext.createDataFrame(sqlContext.emptyDataFrame.rdd, schema)

  //df.select($"users.element")
//  Exception in thread "main" org.apache.spark.sql.AnalysisException: cannot resolve 'users[element]'
//  due to data type mismatch: argument 2 requires integral type, however, 'element' is of string type.;

  case class User(name: String)
  df.explode($"users"){ case Row(arr: Array[String]) =>  arr.map(User(_)) }
}
