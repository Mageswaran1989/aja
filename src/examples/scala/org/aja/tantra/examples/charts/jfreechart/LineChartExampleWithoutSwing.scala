package org.jfree.chart.demo;

import java.awt.Color;

import org.jfree.chart.{ChartFrame, JFreeChart, ChartFactory, ChartPanel}
;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
  * A simple demonstration application showing how to create a line chart using data from an
  * {@link XYDataset}.
  *
  */
object LineChartWithoutSwing {




  //        final ChartPanel chartPanel = new ChartPanel(chart);
  //        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
  //        setContentPane(chartPanel);

  /**
    * Creates a sample dataset.
    *
    * @return a sample dataset.
    */
  def  createDataset(): XYDataset  = {

    val  series1: XYSeries = new XYSeries("First");
    series1.add(1.0, 1.0);
    series1.add(2.0, 4.0);
    series1.add(3.0, 3.0);
    series1.add(4.0, 5.0);
    series1.add(5.0, 5.0);
    series1.add(6.0, 7.0);
    series1.add(7.0, 7.0);
    series1.add(8.0, 8.0);

    val  series2: XYSeries = new XYSeries("Second");
    series2.add(1.0, 5.0);
    series2.add(2.0, 7.0);
    series2.add(3.0, 6.0);
    series2.add(4.0, 8.0);
    series2.add(5.0, 4.0);
    series2.add(6.0, 4.0);
    series2.add(7.0, 2.0);
    series2.add(8.0, 1.0);

    val  series3: XYSeries = new XYSeries("Third");
    series3.add(3.0, 4.0);
    series3.add(4.0, 3.0);
    series3.add(5.0, 2.0);
    series3.add(6.0, 3.0);
    series3.add(7.0, 6.0);
    series3.add(8.0, 3.0);
    series3.add(9.0, 4.0);
    series3.add(10.0, 3.0);

    val  dataset: XYSeriesCollection = new XYSeriesCollection();
    dataset.addSeries(series1);
    dataset.addSeries(series2);
    dataset.addSeries(series3);

    return dataset;

  }

  /**
    * Creates a chart.
    *
    * @param dataset  the data for the chart.
    *
    * @return a chart.
    */
  def  createChart(dataset: XYDataset ): JFreeChart = {

    // create the chart...
    val  chart1: JFreeChart = ChartFactory.createXYLineChart(
      "Line Chart Demo 6",      // chart title
      "X",                      // x axis label
      "Y",                      // y axis label
      dataset,                  // data
      PlotOrientation.VERTICAL,
      true,                     // include legend
      true,                     // tooltips
      false                     // urls
    );

    // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
    chart1.setBackgroundPaint(Color.white);

    //        final StandardLegend legend = (StandardLegend) chart.getLegend();
    //      legend.setDisplaySeriesShapes(true);

    // get a reference to the plot for further customisation...
    val  plot: XYPlot = chart1.getXYPlot();
    plot.setBackgroundPaint(Color.lightGray);
    //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);

    val  renderer: XYLineAndShapeRenderer = new XYLineAndShapeRenderer();
    renderer.setSeriesLinesVisible(0, false);
    renderer.setSeriesShapesVisible(1, false);
    plot.setRenderer(renderer);

    // change the auto tick unit selection to integer units only...
    val  rangeAxis: NumberAxis =  plot.getRangeAxis().asInstanceOf[NumberAxis]
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    // OPTIONAL CUSTOMISATION COMPLETED.

    return chart1;

  }


  def main(args: Array[String]) {
    val  dataset: XYDataset = createDataset();
    val  chart1: JFreeChart = createChart(dataset);

    val frame = new ChartFrame(
      "Title",
      chart1
    )
    frame.pack()
    frame.setVisible(true)

    val scaterChart = ChartFactory.createScatterPlot(
    "Scatter Plot",
    "X",
    "Y",
    dataset,
    PlotOrientation.VERTICAL,
    true, //include legent
    true, //tooltips
    false //urls
    )

    val scatterFrame = new ChartFrame(
      "Scatter Frame",
      scaterChart
    )
    scatterFrame.pack()
    scatterFrame.setVisible(true)
  }
}
