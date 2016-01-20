//import org.apache.spark.sql._
//import org.apache.spark.sql.types._
//
//  val sqlContext = new org.apache.spark.sql.SQLContext(sc)
//
//  val schema =
//    StructType(
//      StructField("name", StringType, false) ::
//      StructField("age", IntegerType, true) :: Nil)
//
//  val people =
//    sc.textFile("examples/src/main/resources/people.txt").map(
//      _.split(",")).map(p => Row(p(0), p(1).trim.toInt))
//  val dataFrame = sqlContext.createDataFrame(people, schema)
//  dataFrame.printSchema
//  // root
//  // |-- name: string (nullable = false)
//  // |-- age: integer (nullable = true)
