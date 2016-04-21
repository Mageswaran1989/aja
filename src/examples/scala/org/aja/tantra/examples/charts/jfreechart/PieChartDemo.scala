package org.aja.tantra.examples.charts.jfreechart

import java.awt.{BasicStroke, Stroke}

import org.jfree.chart._
import org.jfree.chart.plot.PiePlot3D
import org.jfree.chart.renderer.category.LineAndShapeRenderer
import org.jfree.data.general.DefaultPieDataset
import org.jfree.util.Rotation

/**
  * Created by mdhandapani on 2/2/16.
  */
object PieChartDemo extends App {

  val data: DefaultPieDataset = new DefaultPieDataset();
  data.setValue("Linux", 29)
  data.setValue("Mac", 20)
  data.setValue("Windows", 51)

  val chart1: JFreeChart = ChartFactory.createPieChart3D("Testing",
  data,
  true,
  true,
  false)

//  val renderer = new LineAndShapeRenderer{
//  override def getItemStroke(row: Int, col: Int): Stroke = new BasicStroke(1.0f, CAP_BUTT, JOIN_BEVEL, 2.0f, null, 0.0f)
//}
  val plot: PiePlot3D  = chart1.getPlot().asInstanceOf[PiePlot3D]

  plot.setStartAngle(290)
  plot.setDirection(Rotation.CLOCKWISE)
  plot.setForegroundAlpha(0.5f)
//  plot.setRenderer(renderer)
//
//  ChartPanel chartpanel = new ChartPanel(chart)

  val frame = new ChartFrame(
    "PieChart",
    chart1
  )
  frame.pack()
  frame.setVisible(true)


}
