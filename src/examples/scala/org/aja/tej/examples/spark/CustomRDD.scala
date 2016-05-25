package org.aja.tej.examples.spark

import org.aja.tej.utils.TejUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, TaskContext}

/**
 * Created by mageswaran on 14/5/16.
 */

class SalesRecord(val transactionId: String,
                  val customerId: String,
                  val itemId: String,
                  val itemValue: Double) extends Comparable[SalesRecord]
with Serializable {

  override def compareTo(o: SalesRecord): Int = {
    return this.transactionId.compareTo(o.transactionId)
  }

  override def toString: String = {
    transactionId+","+customerId+","+itemId+","+itemValue
  }
}

//-----------------------------------------------------------------------

class DiscountRDD(prev:RDD[SalesRecord],discountPercentage:Double) extends RDD[SalesRecord](prev) {

  //This method is the one which computes value for each partition of RDD.
  // In our code, we take input sales record and output it by applying discount as
  // specified by discountPercentage.
  override def compute(split: Partition, context: TaskContext): Iterator[SalesRecord] =  {
    firstParent[SalesRecord].iterator(split, context).map(salesRecord => {
      val discount = salesRecord.itemValue * discountPercentage
      new SalesRecord(salesRecord.transactionId,salesRecord.customerId,salesRecord.itemId,discount)
    }
    )
  }

  //getPartitions method allows developer to specify the new partitions for the RDD.
  //As we don’t change the partitions in our example, we can just reuse the partitions of parent RDD
  override protected def getPartitions: Array[Partition] = firstParent[SalesRecord].partitions
}

//-----------------------------------------------------------------------
//Adding custom operators to RDD

class CustomFunctions(rdd:RDD[SalesRecord]) {

  //Lets create a custom function which takes the RDD[SalesRecord] and finds the sales total
  def totalSales = rdd.map(_.itemValue).sum

  def discount(discountPercentage:Double) = new DiscountRDD(rdd,discountPercentage)

}

object CustomFunctions {
  //When imported like "import CustomFunctions._", "totalSales" will be an implicit method detected by Scala
  //for the type RDD[SalesRecord]
  implicit def addCustomFunctions(rdd: RDD[SalesRecord]) = new CustomFunctions(rdd)
}

//-----------------------------------------------------------------------

object CustomRDD {

  def main(args: Array[String]) {

    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

    val dataRDD = sc.parallelize(org.aja.dataset.SampleSales.csvData)

    val salesRecordRDD = dataRDD.map(row => {
      val colValues = row.split(",")
      new SalesRecord(colValues(0),colValues(1),
        colValues(2),colValues(3).toDouble)
    })


    // total amount of sales. don't you think this lengthy?
    salesRecordRDD.map(_.itemValue).sum

    //!!!Error!!!
    //salesRecordRDD.totalSales

    import CustomFunctions._
    println("Total sales: " , salesRecordRDD.totalSales)

    //Using the newly added SalesRecordRDD method
    val discountRDD = salesRecordRDD.discount(0.1)
    println(discountRDD.collect().toList)


  }
}
