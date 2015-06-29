package org.aja.ml.core

import scala.language.implicitConversions
import scala.util.Try

/**
* <p> Packages that contains all Aja defined encapsulated types
*  @author Mageswaran
*  @since  June 2015
**/

object Types {

  object Aja {
    type XY = (Double,Double)
    type XYTSeries = Array[(Double, Double)]
    
    type Matrix[T] = Array[Arrray[T]]
    type Vector[T] = Array[T]
    
    type DblMatrix = Matrix[Double]
    type DblVector = Vector[Double]


  }

}
