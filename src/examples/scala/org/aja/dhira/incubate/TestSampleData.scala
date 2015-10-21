package org.aja.dhira.incubate

import org.aja.dhira.incubate.data.SampleData._

/**
 * Created by mageswaran on 11/10/15.
 */
object TestSampleData {

  def main(args: Array[String]) {
    val xorSampleData = XOR3d(100)

    xorSampleData.foreach(println)
  }
}
