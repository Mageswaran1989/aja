package org.aja.tej.examples.sparksql.json

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

import scala.collection.mutable

/**
 * Created by mdhandapani on 12/1/16.
 */
object HelloWorldJson {

  def main(args: Array[String]) {
    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    println("*** easy enough to query flat JSON")
    val people = sqlContext.read.json("data/flat.json")
    people.printSchema()
    people.registerTempTable("people")
    val young = sqlContext.sql("SELECT firstName, lastName FROM people WHERE age < 30")
    young.foreach(println)

    println("*** nested JSON results in fields that have compound names, like address.state")
    val peopleAddr = sqlContext.read.json("data/notFlat.json")
    peopleAddr.printSchema()
    peopleAddr.foreach(println)
    peopleAddr.registerTempTable("peopleAddr")
    val inPA = sqlContext.sql("SELECT firstName, lastName FROM peopleAddr WHERE address.state = 'PA'")
    inPA.foreach(println)

    println("*** interesting characters in field names lead to problems with querying, " +
      "as Spark SQL has no quoting mechanism for identifiers")
    val peopleAddrBad = sqlContext.read.json("data/notFlatBadFieldName.json")
    peopleAddrBad.printSchema()

    println("*** instead read the JSON in as an RDD[String], do necessary string" +
      " manipulations (example below is simplistic) and then turn it into a Schema RDD")
    val lines = sc.textFile("data/notFlatBadFieldName.json")
    val linesFixed = lines.map(s => s.replaceAllLiterally("$", ""))
    val peopleAddrFixed = sqlContext.read.json(linesFixed)
    peopleAddrFixed.printSchema()
    peopleAddrFixed.registerTempTable("peopleAddrFixed")
    val inPAFixed = sqlContext.sql("SELECT firstName, lastName FROM peopleAddrFixed WHERE address.state = 'PA'")
    inPAFixed.foreach(println)


    println("*** easy case -- one record")
    val ex1 = sqlContext.read.json("data/inference1.json")
    ex1.schema.printTreeString()
    ex1.registerTempTable("table1")
    println("simple query")
    sqlContext.sql("select b from table1").foreach(println)

    println(" two records, overlapping fields")
    val ex2 = sqlContext.read.json("data/inference2.json")
    ex2.schema.printTreeString()
    ex2.registerTempTable("table2")
    println("it's OK to reference a sometimes missing field")
    sqlContext.sql("select b from table2").foreach(println)
    println("it's OK to reach into a sometimes-missing record")
    sqlContext.sql("select g.h from table2").foreach(println)

    println(" two records, scalar and structural conflicts")
    val ex3 = sqlContext.read.json("data/inference3.json")
    ex3.schema.printTreeString()
    ex3.registerTempTable("table3")
    println("it's ok to query conflicting types but not reach inside them")
    // don't try to query g.h or g[1]
    sqlContext.sql("select g from table3").foreach(println)


    //Mixed json schema data
    val transactions = sqlContext.read.json("data/mixed.json")
    transactions.printSchema()
    transactions.registerTempTable("transactions")


    val all = sqlContext.sql("SELECT id FROM transactions")
    all.foreach(println)

    val more = sqlContext.sql("SELECT id, since FROM transactions")
    more.foreach(println)

    val deeper = sqlContext.sql("SELECT id, address.zip FROM transactions")
    deeper.foreach(println)

    println("selecting an array valued column")
    val array1 = sqlContext.sql("SELECT id, orders FROM transactions")
    array1.foreach(println)

    println("selecting a specific array element")
    val array2 = sqlContext.sql("SELECT id, orders[0] FROM transactions")
    array2.foreach(println)


    //---------------------------------------------------------

    val peopleJsonReference = """
    {"name":"Michael", "cities":["palo alto", "menlo park"], "schools":[{"sname":"stanford", "year":2010}, {"sname":"berkeley", "year":2012}]}
    {"name":"Andy",    "cities":["santa cruz"],              "schools":[{"sname":"ucsb", "year":2011}]}
    {"name":"Justin",  "cities":["portland"],                "schools":[{"sname":"berkeley", "year":2014}]}"""

    //val people = sqlContext.read.json("/opt/aja/data/people.json")
    //people: org.apache.spark.sql.DataFrame = [age: bigint, cities: array<string>, name: string, schools: array<struct<sname:string,year:bigint>>]

    val names = people.select('name).collect()
    //names: Array[org.apache.spark.sql.Row] = Array([Michael], [Andy], [Justin])

    names.map(row => row.getString(0))
    //res88: Array[String] = Array(Michael, Andy, Justin)

    val flattened = people.explode("cities", "city"){case c: mutable.WrappedArray[String] => c}
    //flattened: org.apache.spark.sql.DataFrame = [age: bigint, cities: array<string>, name: string, schools: array<struct<sname:string,year:bigint>>, city: string]

    val allCities = flattened.select('city).collect()
  }
}
