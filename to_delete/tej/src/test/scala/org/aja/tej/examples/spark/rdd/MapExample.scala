package org.aja.tej.examples.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */
/*
 map
Applies a transformation function on each item of the RDD and returns the result as a
new RDD.

 mapPartitions
This is a specialized map that is called only once for each partition. The entire content
of the respective partitions is available as a sequential stream of values via the input
argument (Iterarator[T] ). The custom function must return yet another Iterator[U]. The
combined result iterators are automatically converted into a new RDD. Please note, that
the tuples (3,4) and (6,7) are missing from the following result due to the partitioning
we chose.

 mapPartitionsWithContext
Similar to mapPartitions, but allows accessing information about the processing state
within the mapper.

mapPartitionsWithIndex
Similar to mapPartitions, but takes two parameters. The first parameter is the index of
the partition and the second is an iterator through all the items within this partition. The
output is an iterator containing the list of items after applying whatever transformation
the function encodes.

 mapPartitionsWithSplit
This method has been marked as deprecated in the API. So, you should not use this
method anymore. Deprecated methods will not be covered in this document.

 mapValues[Pair]
Takes the values of a RDD that consists of two-component tuples, and applies the pro-
vided function to transform each value. Then, it forms new two-component tuples using
the key and the transformed value and stores them in a new RDD.

 mapWith
This is an extended version of map. It takes two function arguments. The first argument
must conform to Int ⇒ T and is executed once per partition. It will map the partition
index to some transformed partition index of type T . The second function must conform
to (U, T ) ⇒ U . T is the transformed partition index and U is a data item of the RDD.
Finally the function has to return a transformed data item of type U .


 */
object MapExample {
  def useCases(sc: SparkContext) = {

    val a = sc . parallelize ( List (" dog " , " salmon " , " salmon " , " rat " , " elephant ") , 3)
    val b = a . map ( _ . length )
    val c = a . zip ( b )
    c . collect

    val a1 = sc.parallelize (1 to 9 , 3)
    def myfunc[T](iter: Iterator[T]) : Iterator[(T,T)] = {
      var res = List [(T,T)]()
      var pre = iter.next
      while(iter.hasNext)
      {
        val cur = iter.next ;
        res.::=(pre, cur)
        pre = cur;
      }
      res.iterator
    }
    a1.mapPartitions(myfunc).collect
    // Array [( Int , Int ) ] = Array ((2 ,3) ,(1 ,2) , (5 ,6) ,(4 ,5) , (8 ,9) ,(7 ,8) )

    val x = sc . parallelize ( List ("1" , "2" , "3" , "4" , "5" , "6" , "7" , "8" ,
      "10") , 3)
    def myfunc1 ( iter : Iterator [ Int ]) : Iterator [ Int ] = {
      var res = List [ Int ]()
      while ( iter . hasNext ) {
        val cur = iter . next ;
        res = res ::: List . fill ( scala . util . Random . nextInt (10) ) ( cur )
      }
      res . iterator
    }
    //x . mapPartitions ( myfunc1 ) . collect
    // some of the number are not outputted at all . This is because the
    //random number generated for it is zero .
    // Array [ Int ] = Array (1 , 2 , 2 , 2 , 2 , 3 , 3 , 3 , 3 , 3 , 3 , 3 , 3 , 3 , 4 ,
     // 4 , 4 , 4 , 4 , 4 , 4 , 5 , 7 , 7 , 7 , 9 , 9 , 10)

    //The above program can also be written using flatMap as follows:
    val x1 = sc . parallelize (1 to 10 , 3)
    x1 . flatMap ( List . fill ( scala . util . Random . nextInt (10) ) ( _ ) ) . collect


    val a2 = sc.parallelize (1 to 9,3)
    import org.apache.spark.TaskContext
    def myfunc2 (tc:TaskContext, iter:Iterator[Int]): Iterator[Int] = {
      tc.addOnCompleteCallback (() => println (
        " Partition : "  + tc . partitionId +
        " , AttemptID : " + tc . attemptNumber() ))//  +
        //" , Interrupted : " + tc.interrupted ) )
      iter . toList . filter ( _ % 2 == 0) . iterator
    }

    //use TaskContext.get
    //a . mapPartitionsWithContext ( myfunc2 ) . collect


    val x2 = sc . parallelize ( List (1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9 ,10) , 3)
    def myfunc3 ( index : Int , iter : Iterator [ Int ]) : Iterator [ String ] = {
      iter . toList . map ( x => index + " ," + x ) . iterator
    }
    //x.mapPartitionsWithIndex ( myfunc3 ) . collect ()

    val a3 = sc . parallelize ( List (" dog " , " tiger " , " lion " , " cat " ," panther " , "eagle ") , 2)
    val b3 = a3 . map ( x => ( x . length , x ) )
    b3 . mapValues (" x " + _ + " x ") . collect


    val x3 = sc . parallelize ( List (1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9 ,10) , 3)
    x3 . mapWith ( a => a * 10) (( a , b ) => ( b + 2) ) . collect

    val x4 = sc . parallelize ( List (1 ,2 ,3 ,4 ,5 ,6 ,7 ,8 ,9 ,10) , 3)
    x4 . mapWith ( a => a * 10) (( a , b ) => ( a + 2) ) . collect

    val a4 = sc . parallelize (1 to 9 , 3)
    val b4 = a4 . mapWith (" Index :" + _ ) (( a , b ) => (" Value :" + a , b ) )
    b4 . collect


  }

}
