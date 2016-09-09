package org.aja.tej.examples.graphx.neo4j


import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}
import org.neo4j.spark._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

/**
  * Created by mdhandapani on 13/7/16.
  */
object Neo4jSparkConnector extends App {

  val conf = new SparkConf().
    setMaster("local[6]").
    setAppName(this.getClass.getSimpleName).
    set("spark.neo4j.bolt.password", "aja")

  val sc = new SparkContext(conf)

  val sqlContext = new SQLContext(sc)

  val tuppleCount = Neo4jTupleRDD(sc, "MATCH (n) return id(n)", Seq.empty).count
  val rowCount = Neo4jRowRDD(sc,"MATCH (n) where id(n) < {maxId} return id(n)", Seq("maxId" -> 100000)).count

  println("Count : " + tuppleCount)
  println("Count : " + rowCount)

  //--------------------------------------------------------

  val neo4jDataFrame1 = Neo4jDataFrame.withDataType(sqlContext, "MATCH (n) return id(n) as id",
    Seq.empty, "id" -> LongType)
  println("Count : " + neo4jDataFrame1.count)


  val query1 = "MATCH (n:Person) return n.age as age"
  val neo4jDataFrame2 = Neo4jDataFrame.withDataType(sqlContext, query1, Seq.empty, "age" -> LongType)
  val summedAge = neo4jDataFrame2.agg(sum(neo4jDataFrame2.col("age"))).collect()
  println("summedAge :" + summedAge.head)

  val query2 = "MATCH (n:Person) return n.age as age"
  val query3 = "MATCH (n:Person) where n.id = {x} return n.age as age"

  val rdd = sc.makeRDD(1.to(1000000))

  val ages = rdd.map( i => {
    val df = Neo4jDataFrame.withDataType(sqlContext,query3, Seq("x"-> i.asInstanceOf[AnyRef]), "age" -> LongType)
    df.agg(sum(df("age"))).first().getLong(0)
  })

  val sumOfAges = ages.reduce( _ + _ )

  val neo4jDataFrame3 = Neo4jDataFrame(sqlContext, "MATCH (n) WHERE id(n) < {maxId} return n.name as name",
    Seq("maxId" -> 100000),"name" -> "string")
  println("neo4jDataFrame2: " + neo4jDataFrame2.count)

  //--------------------------------------------------------

  val neo4jGraph = Neo4jGraph.loadGraph(sc, "Person", Seq("KNOWS"), "Person")

  val verticesCount = neo4jGraph.vertices.count
  println("verticesCount :" + verticesCount)

  val edgesCount = neo4jGraph.edges.count
  println("edgesCount : " + edgesCount)

  import org.apache.spark.graphx.lib._

  val graph2 = PageRank.run(neo4jGraph, 5)

  val vertices = graph2.vertices.take(5)

  println("vertices : " + vertices)

  Neo4jGraph.saveGraph(sc, neo4jGraph, "rank")


  //------------------------------------------------------------

//  val neo4jGraphFrame1 = Neo4jGraphFrame(sqlContext,"Person" -> "name",("KNOWS"),"Person" -> "name")

  val neo4jGraphFrame2 = Neo4jGraphFrame.fromGraphX(sc,"Person",Seq("KNOWS"),"Person")

  println(neo4jGraphFrame2.vertices.count)

  println(neo4jGraphFrame2.edges.count)

  val pageRankResults = neo4jGraphFrame2.pageRank.resetProbability(0.15).maxIter(5).run

  pageRankResults.vertices.take(5)

  // pattern matching
  val results1 = pageRankResults.find("(A)-[]->(B)").select("A","B").take(3)


  neo4jGraphFrame2.find("(A)-[]->(B);(B)-[]->(C); !(A)-[]->(C)")


  neo4jGraphFrame2.find("(A)-[]->(B);(B)-[]->(C); !(A)-[]->(C)").take(3)

  // doesn't work yet ... complains about different table widths
  val results2 = neo4jGraphFrame2.find("(A)-[]->(B); (B)-[]->(C); !(A)-[]->(C)").filter("A.id != C.id")
  // Select recommendations for A to follow C
  val results3 = results2.select("A", "C").take(3)

  println(neo4jGraphFrame2.labelPropagation.maxIter(3).run().take(3))
}
