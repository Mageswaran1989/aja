package org.aja.tej.test.sparksql

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by mdhandapani on 29/7/15.
 */
object HelloWorld {

  val conf = new SparkConf().setAppName("Simple Application").setMaster("local[2]" /*"spark://myhost:7077"*/)
  val sc = new SparkContext(conf)

  // sc is an existing SparkContext.
  val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)

  sqlContext.sql("CREATE TABLE IF NOT EXISTS src (key INT, value STRING)")
  sqlContext.sql("LOAD DATA LOCAL INPATH 'examples/src/main/resources/kv1.txt' INTO TABLE src")

  // Queries are expressed in HiveQL
  sqlContext.sql("FROM src SELECT key, value").collect().foreach(println)

}
