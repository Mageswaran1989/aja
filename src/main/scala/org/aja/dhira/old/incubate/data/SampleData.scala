package org.aja.dhira.incubate.data

import org.aja.dhira.incubate.math.AnnMath._


/**
 * Created by mageswaran on 10/10/15.
 */
object SampleData {

def XOR2d(numRecords: Int): Vector[LabeledVector] = {
  val randomGen = scala.util.Random

  Vector.tabulate(numRecords)(x=>  {
    val point1 = Math.abs(2 * randomGen.nextInt(100)) / 100
    val point2 = Math.abs(2 * randomGen.nextInt(100)) / 100
    val output = point1 ^ point2
    new LabeledVector(output, new ColVector(Array(point1.toLong, point2.toLong)))
  })
}

  def XOR3d(numRecords: Int): Vector[LabeledVector] = {
    val randomGen = scala.util.Random

    Vector.tabulate(numRecords)(x=>  {
      val point1 = Math.abs(2 * randomGen.nextInt(100)) / 100
      val point2 = Math.abs(2 * randomGen.nextInt(100)) / 100
      val point3 = Math.abs(2 * randomGen.nextInt(100)) / 100
      val output = point1 ^ point2 ^ point3
      new LabeledVector(output, new ColVector(Array(point1.toLong, point2.toLong, point3.toLong)))
    })
  }

}
