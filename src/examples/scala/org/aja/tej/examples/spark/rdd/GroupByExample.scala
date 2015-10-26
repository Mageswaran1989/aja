package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 groupByKey[Pair]
Very similar to groupBy, but instead of supplying a function, the key-component of each
pair will automatically be presented to the partitioner.
 * Avoid groupByKey when performing an associative reductive operation.
 * For example, rdd.groupByKey().mapValues(_.sum) will produce the same results as rdd.reduceByKey(_ + _).
 * However, the former will transfer the entire dataset across the network, while the latter will compute
 * local sums for each key in each partition and combine those local sums into larger sums after shuffling
 */
object GroupByExample  extends App {

  def useCases(sc: SparkContext) = {

    val a = sc . parallelize (1 to 9 , 3)
    a . groupBy ( x => { if ( x % 2 == 0) " even " else " odd " }) . collect

    val a1 = sc . parallelize (1 to 9 , 3)
    def myfunc ( a1 : Int ) : Int =
    {
      a1 % 2
    }
    a1 . groupBy ( myfunc ) . collect

    val a2 = sc . parallelize (1 to 9 , 3)
    def myfunc1 ( a : Int ) : Int =
    {
      a % 2
    }
    a2 . groupBy(x => myfunc1(x)).collect
    a2 . groupBy ( myfunc1( _ ) , 1) . collect


    import org . apache . spark . Partitioner
    class MyPartitioner extends Partitioner {
      def numPartitions : Int = 2
      def getPartition ( key : Any ) : Int =
      {
        key match
        {
          case null   => 0
          case key : Int => key % numPartitions
          case _   => key . hashCode % numPartitions
        }
      }
      override def equals ( other : Any ) : Boolean =
      {
        other match
        {
          case h : MyPartitioner => true
          case _       => false
        }
      }
    }
    val a3 = sc . parallelize (1 to 9 , 3)
    val p = new MyPartitioner ()
    val b3 = a3.groupBy (( x : Int ) => { x }, p)
    //use mapPartitionsWithIndex
    val c = b3.mapWith( i => i ) (( a , b ) => (b , a ) )
    c . collect


    val a4 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " , " spider " ,"eagle ") , 2)
    val b4 = a4 . keyBy ( _ . length )
    b4 . groupByKey . collect

  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}
