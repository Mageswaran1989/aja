initialCommands += """
  import breeze.linalg._ //Contains all LA operations for Vector and Matrix
  import breeze.numerics._ //Universal functions to operate on Vector and Matrix
  import breeze.stats.distributions._ //Required LA Statistics
  import org.apache.spark.SparkContext
  import org.apache.spark.SparkContext._
  import org.apache.spark.sql.SQLContext
  val sc = new SparkContext("local[*]", "Console")
  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._
                   """

cleanupCommands += """
  println("Closing the SparkContext:")
  sc.stop()
                   """.stripMargin

fork in run := true

