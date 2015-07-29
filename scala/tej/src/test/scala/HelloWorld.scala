
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by mageswaran on 26/7/15.
 */
object HelloWorld extends App {
  //def main(args: Array[String]) {
  val logFile = "/opt/spark/README.md" // Should be some file on your system
  val conf = new SparkConf().setAppName("Simple Application").setMaster("local[2]" /*"spark://myhost:7077"*/)
  val sc = new SparkContext(conf)
  val logData = sc.textFile(logFile, 2).cache()
  val numAs = logData.filter(line => line.contains("a")).count()
  val numBs = logData.filter(line => line.contains("b")).count()
  println("$$$$$$$$$Lines with a: %s, Lines with b: %s".format(numAs, numBs))

  sc.textFile(logFile)
    .flatMap(_.split(" "))
    .map((_, 1))
    .reduceByKey(_ + _)
    .saveAsTextFile("data/wordcount.txt")

  //}
}
