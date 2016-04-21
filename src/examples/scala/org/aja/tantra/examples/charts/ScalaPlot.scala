package org.aja.tantra.examples.charts

/**
 * Created by mageswaran on 1/2/16.
 */
object ScalaPlot extends App {

  import org.sameersingh.scalaplot.Implicits._

  val x = 0.0 until 2.0 * math.Pi by 0.1
  //output(PNG("/opt/aja/data/", "test"), xyChart(x ->(math.sin(_), math.cos(_))))
  output(GUI, xyChart(x ->(math.sin(_), math.cos(_))))
}
