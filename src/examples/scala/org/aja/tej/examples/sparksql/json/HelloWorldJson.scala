package org.aja.tej.examples.sparksql.json

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
  * Created by mdhandapani on 12/1/16.
  */
object HelloWorldJson extends App{

//(employeeID: Int, Name: String, ProjectDetails: JsonObject{[{ProjectName, Description, Duriation, Role}]})
//Eg:


  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)
  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._

  val empDF = sqlContext.read.json("data/employee.json")

  case class ProjectInfo(ProjectName: String, Description: String, Duration: String, Role: String)
  case class Project(eplyeeID: Int, Name: String, ProjectDetails: Seq[ProjectInfo])

  

  empDF.show()

}
