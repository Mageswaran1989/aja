package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 saveAsHadoopFile[Pair] , saveAsHadoopDataset[Pair] ,
saveAsNewAPIHadoopFile[Pair]
Saves the RDD in a Hadoop compatible format using any Hadoop outputFormat class
the user specifies.

 saveAsObjectFile
Saves the RDD in binary format.

 saveAsSequenceFile[SeqFile]
Saves the RDD as a Hadoop sequence file.

 saveAsTextFile
Saves the RDD as text files. One line at a time.

 */
object SaveAsExample extends App  {
  def useCases(sc: SparkContext) = {
    val x = sc.parallelize (1 to 100 , 3)
    x.saveAsObjectFile(" objFile ")
    val y = sc.objectFile[Array[Int]](" objFile ")
    y.collect

    val v = sc . parallelize ( Array ((" owl " ,3) , (" gnu " ,4) , (" dog " ,1) , (" cat " ,2)
      , (" ant " ,5) ) , 2)
    v . saveAsSequenceFile (" hd_seq_file ")

    val a = sc . parallelize (1 to 10000 , 3)
    a . saveAsTextFile (" mydata_a ")
// head -n 5 ~/ Documents / spark -0.9.0 - incubating -    bin - cdh4 / bin / mydata_a / part -00000
    // ll ~/ Documents / spark -0.9.0 - incubating - bin - cdh4 / bin / mydata_a /

    import org.apache.hadoop.io.compress.GzipCodec
    a.saveAsTextFile (" mydata_b " , classOf[GzipCodec])
// ll ~/ Documents / spark -0.9.0 - incubating - bin - cdh4 /bin / mydata_b /

    val x1 = sc . textFile (" mydata_b ")
    x1 . count

    val x2 = sc . parallelize ( List (1 ,2 ,3 ,4 ,5 ,6 ,6 ,7 ,9 ,8 ,10 ,21) , 3)
    x2 . saveAsTextFile ("hdfs://localhost:8020/user/cloudera/test") ;

    val sp = sc . textFile ("hdfs://localhost:8020/user/cloudera/sp_data")
    sp . flatMap ( _.split(" ")).saveAsTextFile("hdfs://localhost:8020/user/cloudera/sp_x")

  }
  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
