initialCommands += """
  import org.apache.spark.SparkContext
  import org.apache.spark.SparkContext._
  import org.apache.spark.sql.SQLContext
  val sc = new SparkContext("local[*]", "Console")
  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._
  println("To run examples under Tej: \n type "run-main org.aja.tej.examples." and TAB to select the example you are interested")
  """

cleanupCommands += """
  println("Closing the SparkContext:")
  sc.stop()
                   """.stripMargin

addCommandAlias("WordCountMain",  "test:runMain org.aja.tej.examples.usecases.wordcount.WordCountMain")

fork in run := true

