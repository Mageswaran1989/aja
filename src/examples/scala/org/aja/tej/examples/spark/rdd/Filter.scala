package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
Evaluates a boolean function for each data item of the RDD and puts the items for
which the function returned true into the resulting RDD.

When you provide a filter function, it must be able to handle all data items contained
in the RDD. Scala provides so-called partial functions to deal with mixed data-types.
(Tip: Partial functions are very useful if you have some data which may be bad and
you do not want to handle but for the good data (matching data) you want to apply
some kind of map function. The following article is good. It teaches you about partial
functions in a very nice way and explains why case has to be used for partial functions:
http://blog.bruchez.name/2011/10/scala-partial-functions-without-phd.html)

 filterWith : Deprecated "use mapPartitionsWithIndex and filter"
This is an extended version of filter. It takes two function arguments. The first argument
must conform to Int ⇒ T and is executed once per partition. It will transform the
partition index to type T . The second function looks like (U, T ) ⇒ Boolean. T is
the transformed partition index and U are the data items from the RDD. Finally the
function has to return either true or false (i.e. Apply the filter).


 */
object Filter extends App{

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize (1 to 10 , 3)
    a . filter ( _ % 2 == 0)
    a . collect

    val b = sc . parallelize (1 to 8)
    b . filter ( _ < 4) . collect

    //Error
    // val a1 = sc . parallelize ( List (" cat " , " horse " , 4.0 , 3.5 , 2 , " dog ") )
   //a1 . filter ( _ < 4) . collect
    /*This fails because some components of a are not implicitly comparable against integers.
      Collect uses the isDefinedAt property of a function-object to determine whether the test-
      function is compatible with each data item. Only data items that pass this test (=filter)
    are then mapped using the function-object. */

    val a2 = sc . parallelize ( List (" cat " , " horse " , 4.0 , 3.5 , 2 , " dog ") )
    a2 . collect ({ case a : Int => " is integer "
                    case b : String => " is string " }) . collect

    val myfunc : PartialFunction [ Any , Any ] = {
      case a : Int  => " is integer "
      case b : String => " is string " }

    myfunc . isDefinedAt ("")
    myfunc . isDefinedAt (1)
    myfunc . isDefinedAt (1.5)

//    Be careful! The above code works because it only checks the type itself! If you use
//    operations on this type, you have to explicitly declare what type you want instead of any.
//    Otherwise the compiler does (apparently) not know what bytecode it should produce:

    //val myfunc2 : PartialFunction [ Any , Any ] = { case x if ( x < 4) => " x "} //error
    val myfunc3 : PartialFunction [ Int , Any ] = { case x if ( x < 4) => " x "}
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))
}
