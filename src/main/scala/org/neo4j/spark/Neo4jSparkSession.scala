package org.neo4j.spark

import org.apache.spark.graphx.Graph
import org.apache.spark.sql.{types, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.reflect.ClassTag

/**
  * @author Mageswaran Dhandapani
  * @since 09.07.16
  */
class Neo4jSparkSession private (sc: SparkContext,
                                 sqlContext: SQLContext) {

  object RDD {
    def toTuple(cquery: String, parameters: Seq[(String, AnyRef)]) = {
      Neo4jTupleRDD(sc, cquery, parameters)
    }

    def toRow(cquery: String, parameters: Seq[(String, AnyRef)]) = {
      Neo4jRowRDD(sc, cquery, parameters)
    }
  }


  object DF {
    def withType(cquery: String, parameters: Seq[(String, Any)], schema: (String, types.DataType)) = {
      Neo4jDataFrame.withDataType(sqlContext, cquery, parameters, schema)
    }

    def query(cquery: String, parameters: Seq[(String, Any)], schema: (String, String)) = {
      Neo4jDataFrame(sqlContext, cquery, parameters, schema)
    }

  }

  //  def loadGraph[VD:ClassTag,ED:ClassTag](statement: String, parameters: Seq[(String, AnyRef)], defaultValue : VD = Nil) = {
  //    Neo4jGraph.loadGraph(sc, statement, parameters, defaultValue)
  //  }

  def toGraphFrame(src:(String,String), edge : (String,String), dst:(String,String)) = {
    Neo4jGraphFrame(sqlContext:SQLContext, src:(String,String), edge : (String,String), dst:(String,String))
  }

  def fromGraphX(label1:String, rels:Seq[String], label2:String) = {
    Neo4jGraphFrame.fromGraphX(sc, label1, rels, label2)
  }

//  def fromEdges()
//
//  def loadGraph()
//  //  def loadGraph()
//
//  def saveGraph()
//  //  def saveGraph()
}


object Neo4jSparkSession {

  class Builder {

    val session: Option[Neo4jSparkSession] = None

    private[this] var userSpecifiedContext: Option[SparkContext] = None
    private[this] var userSpecifiedSqlContext: Option[SQLContext] = None
    private[this] var userSpecifiedSparkConf: Option[SparkConf] = None
    //private[this] var userSpecifiedSession: Option[] //TODO SparkSession

    def sparkContext(sc: SparkContext) = synchronized {
      userSpecifiedContext = Option(sc)
      this
    }

    val sparkContext = userSpecifiedContext.getOrElse {
      val sparkConf =userSpecifiedSparkConf.getOrElse {
        new SparkConf().set("spark.neo4j.bolt.password", "neo4j")
      }
      SparkContext.getOrCreate(sparkConf)
    }

    def sqlContext(sqlContext: SQLContext) = synchronized {
      userSpecifiedSqlContext = Option(sqlContext)
      this
    }

    val sqlContext = userSpecifiedSqlContext.getOrElse {
      new SQLContext(sparkContext)
    }

    def sparkConfig(config: SparkConf) = {
      userSpecifiedSparkConf = Option(config)
      this
    }


    def getOrCreate = synchronized {
      session.getOrElse(new Neo4jSparkSession(sparkContext, sqlContext))
    }

  }

  val builder: Builder = new Builder
}
