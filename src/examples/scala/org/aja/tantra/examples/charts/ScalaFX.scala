package org.aja.tantra.examples.charts

/**
  * Created by mdhandapani on 2/2/16.
  */
object ScalaFX {

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
