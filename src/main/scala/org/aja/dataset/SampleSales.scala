package org.aja.dataset

/**
 * Created by mageswaran on 14/5/16.
 */
object SampleSales {

  val csvData = Array[String](
    "111,1,1,100.0",
    "112,2,2,505.0",
    "113,3,3,510.0",
    "114,4,4,600.0",
    "115,1,2,500.0")


  case class SalesRecord(val transactionId: String,
                    val customerId: String,
                    val itemId: String,
                    val itemValue: Double)
}
