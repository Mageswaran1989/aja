package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 reduce
This function provides the well-known reduce functionality in Spark. Please note that
any function f you provide, should be commutative(2 + 3 = 3 + 2) in order to generate reproducible
results.

 reduceByKey[Pair] , reduceByKeyLocally[Pair] ,
reduceByKeyToDriver[Pair]
Very similar to reduce, but performs the reduction separately for each key of the RDD.
This function is only available if the RDD consists of two-component tuples.

Avoid reduceByKey When the input and output value types are different
For example, consider writing a transformation that finds all the unique strings corresponding to each key.
One way would be to use map to transform each element into a Set and then combine the Sets with reduceByKey

  rdd.map(kv => (kv._1, new Set[String]() + kv._2))
      .reduceByKey(_ ++ _)
  This code results in tons of unnecessary object creation because a new set must be allocated for each record.
  It’s better to use aggregateByKey, which performs the map-side aggregation more efficiently:

  val zero = new collection.mutable.Set[String]()
     rdd.aggregateByKey(zero)(
    (set, v) => set += v,
    (set1, set2) => set1 ++= set2)
 */
object ReduceExample  extends App {

  def useCases(sc: SparkContext) = {
    val a = sc.parallelize (1 to 100 , 3)
    a.reduce ( _ + _ )

    val a1 = sc . parallelize ( List (" dog " , " cat " , " owl " , " gnu " , " ant ") ,2)
    val b1 = a1 . map ( x => ( x . length , x ) )
    b1 . reduceByKey ( _ + _ ) . collect

    val a2 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " , " panther " , "eagle ") , 2)
    val b2 = a2 . map ( x => ( x . length , x ) )
    b2 . reduceByKey ( _ + _ ) . collect

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}


/*
val reparitioned = rdd.repartition(16)
val filtered: RDD[(MyKey, myData)] = MyUtils.filter(reparitioned, startDate, endDate)
val mapped: RDD[(DateTime, myData)] = filtered.map(kv=(kv._1.processingTime, kv._2))
val reduced: RDD[(DateTime, myData)] = mapped.reduceByKey(_+_)

When I run this with some logging this is what I see:

reparitioned ======> [List(2536, 2529, 2526, 2520, 2519, 2514, 2512, 2508, 2504, 2501, 2496, 2490, 2551, 2547, 2543, 2537)]
filtered ======> [List(2081, 2063, 2043, 2040, 2063, 2050, 2081, 2076, 2042, 2066, 2032, 2001, 2031, 2101, 2050, 2068)]
mapped ======> [List(2081, 2063, 2043, 2040, 2063, 2050, 2081, 2076, 2042, 2066, 2032, 2001, 2031, 2101, 2050, 2068)]
reduced ======> [List(0, 0, 0, 0, 0, 0, 922, 0, 0, 0, 0, 0, 0, 0, 0, 0)]

My logging is done using these two lines:

val sizes: RDD[Int] = rdd.mapPartitions(iter => Array(iter.size).iterator, true)
log.info(s"rdd ======> [${sizes.collect.toList}]")

My question is why does my data end up in one partition after the reduceByKey? After the filter it can be seen that the data is evenly distributed, but the reduceByKey results in data in only one partition.

Ans:


My DateTimes were all without seconds and milliseconds since I wanted to group data belonging to the same minute. The hashCode() for Joda DateTimes which are one minute apart is a constant:

scala> val now = DateTime.now
now: org.joda.time.DateTime = 2015-11-23T11:14:17.088Z

scala> now.withSecondOfMinute(0).withMillisOfSecond(0).hashCode - now.minusMinutes(1).withSecondOfMinute(0).withMillisOfSecond(0).hashCode
res42: Int = 60000
As can be seen by this example, if the hashCode values are similarly spaced, they can end up in the same partition:
scala> val nums = for(i <- 0 to 1000000) yield ((i*20 % 1000), i)
nums: scala.collection.immutable.IndexedSeq[(Int, Int)] = Vector((0,0), (20,1), (40,2), (60,3), (80,4), (100,5), (120,6), (140,7), (160,8), (180,9), (200,10), (220,11), (240,12), (260,13), (280,14), (300,15), (320,16), (340,17), (360,18), (380,19), (400,20), (420,21), (440,22), (460,23), (480,24), (500,25), (520,26), (540,27), (560,28), (580,29), (600,30), (620,31), (640,32), (660,33), (680,34), (700,35), (720,36), (740,37), (760,38), (780,39), (800,40), (820,41), (840,42), (860,43), (880,44), (900,45), (920,46), (940,47), (960,48), (980,49), (0,50), (20,51), (40,52), (60,53), (80,54), (100,55), (120,56), (140,57), (160,58), (180,59), (200,60), (220,61), (240,62), (260,63), (280,64), (300,65), (320,66), (340,67), (360,68), (380,69), (400,70), (420,71), (440,72), (460,73), (480,74), (500...

scala> val rddNum = sc.parallelize(nums)
rddNum: org.apache.spark.rdd.RDD[(Int, Int)] = ParallelCollectionRDD[0] at parallelize at <console>:23

scala> val reducedNum = rddNum.reduceByKey(_+_)
reducedNum: org.apache.spark.rdd.RDD[(Int, Int)] = ShuffledRDD[1] at reduceByKey at <console>:25

scala> reducedNum.mapPartitions(iter => Array(iter.size).iterator, true).collect.toList

res2: List[Int] = List(50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
To distribute my data more evenly across the partitions I created my own custom Partitoiner:
class JodaPartitioner(rddNumPartitions: Int) extends Partitioner {
  def numPartitions: Int = rddNumPartitions
  def getPartition(key: Any): Int = {
    key match {
      case dateTime: DateTime =>
        val sum = dateTime.getYear + dateTime.getMonthOfYear +  dateTime.getDayOfMonth + dateTime.getMinuteOfDay  + dateTime.getSecondOfDay
        sum % numPartitions
      case _ => 0
    }
  }
}





 */
