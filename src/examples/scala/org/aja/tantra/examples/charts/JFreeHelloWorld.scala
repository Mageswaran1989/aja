package org.aja.tantra.examples.charts


import org.jfree.chart._
import org.jfree.data.xy._


/**
 * Created by mageswaran on 26/1/16.
 */
object JFreeHelloWorld extends App{

  val xy1 = Array(Array(1.0,1.1), Array(1.0, 1.0), Array(0.0, 0.0), Array(0.0,0.01))
  val x = Array[Double](1,2,3,4,5,6,7,8,9,10)
  val y = x.map(_*2)
  val dataset = new DefaultXYDataset
  dataset.addSeries("Series 1",Array(x,y))

  val frame = new ChartFrame(
    "Title",
    ChartFactory.createScatterPlot(
      "Plot",
      "X Label",
      "Y Label",
      dataset,
      org.jfree.chart.plot.PlotOrientation.HORIZONTAL,
      false,false,false
    )
  )
  frame.pack()
  frame.setVisible(true)


  import scalax.chart._
  import scalax.chart.api._

  val xy = for (x <- 1 to 10) yield (x,x*2)

  val chart = XYLineChart(xy)

  chart.show()

  val series = new XYSeries("f(x) = sin(x)")
  val chart1 = XYLineChart(series)
  chart1.show()
  for (x <- -4.0 to 4 by 0.1) {
    swing.Swing onEDT {
      series.add(x,math.sin(x))
    }
    Thread.sleep(50)
  }





}
