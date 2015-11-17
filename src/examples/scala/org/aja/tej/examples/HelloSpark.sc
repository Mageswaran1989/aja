import java.io.File
import org.aja.tej.utils.TejUtils
import org.apache.spark.{SparkContext, SparkConf}
object HelloWorldSpark {
    val outputPath = "output/hw_wordcount_output/"
    val logFile = "data/datascience.stackexchange.com/Posts.xml" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]" /*"spark://myhost:7077"*/)
  val sc = new SparkContext("local", "test")
    try {
      val logData = sc.textFile(logFile, 2).cache()
      val numAs = logData.filter(line => line.contains("a")).count()
      val numBs = logData.filter(line => line.contains("b")).count()
      println("$$$$$$$$$Lines with a: %s, Lines with b: %s".format(numAs, numBs))
      val dir = new File(outputPath)
      if (dir.exists)
        dir.delete()
      sc.textFile(logFile)
        .flatMap(_.split(" "))
        .map((_, 1))
        .reduceByKey(_ + _)
      //    .saveAsTextFile(outputPath)
      //////////////////////////////////////////////////////////////////////////////////////////////////
      //20090505-000000 af.b Tuisblad 1 36236
      val pagecount = sc.textFile("data/pagecounts/")
      sc.setLogLevel("INFO")
      pagecount.take(10).foreach(println)
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + pagecount.count())
      //Lets count the
      val engPages = pagecount.filter(_.split(" ")(1) == "en").count
      println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + engPages)
    }
    finally {
      TejUtils.waitForSparkUI(sc)
    }
}