package org.aja.tantra.examples.charts.jfreechart

import org.jfree.chart._
import org.jfree.data.xy._


/**
 * Created by mageswaran on 26/1/16.
 */
object JFreeHelloWorld extends App{

  val x = Array[Double](1,2,3,4,5,6,7,8,9,10)
  val y = x.map(_*2)
  val dataset = new DefaultXYDataset
  dataset.addSeries("Series 1",Array(x,y))

  val chart = ChartFactory.createScatterPlot(
    "Plot",
    "X Label",
    "Y Label",
    dataset,
    org.jfree.chart.plot.PlotOrientation.HORIZONTAL,
    false,false,false
  )

  val frame = new ChartFrame(
    "Title",
    chart
  )
  frame.pack()
  frame.setVisible(true)
}
