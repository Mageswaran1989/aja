package org.aja.dhira.incubate.data

import org.aja.dhira.incubate.math.AnnMath.ColVector

/**
 * Created by mageswaran on 10/10/15.
 */

/**
 * Class to hold each supervised data record
 * @param label Expected or Observed output
 * @param vector Input Features
 */
class LabeledVector(val label: Long, val vector: ColVector) {

  override def toString(): String = label.toString + "=>" + vector.toString

  //todo hashCode
}
