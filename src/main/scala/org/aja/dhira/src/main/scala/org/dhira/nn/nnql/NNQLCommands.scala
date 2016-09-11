package org.aja.dhira.nnql

/**
  * Created by mdhandapani on 18/5/16.
  */
object NNQLCommands {

  trait RunTimeActions
  trait NNQlExpr
  case class CreateNeurons(n: Long, interconnections: Boolean) extends NNQlExpr
  case class CreateLayer(n: Long) extends NNQlExpr

  case class LoadData(csvPath: String, referenceName: Option[String]) extends NNQlExpr {
    import scala.io.Source
    val lines = Source.fromFile(csvPath).getLines().toArray
    val numCols = lines.take(1)(0).split(",").size

    val data = lines.map(line =>
    line.split(",").map(_.toDouble))

    data.foreach(println)
  }
  case class StartTraining() extends NNQlExpr with RunTimeActions
  case class StopTraining() extends NNQlExpr with RunTimeActions

}
