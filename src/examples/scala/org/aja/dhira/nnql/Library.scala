package org.aja.dhira.nnql

/**
 * Created by mageswaran on 29/5/16.
 */
class Library {

  val commands =
    """
      | CREATE n NEURONS
      | CREATE n NEURONS WITH INTERCONNECTION
      | CREATE NEURAL LAYER WITH n NEURONS
      | SET layer_name PROPERTY=VALUE
      | CONNECT layer_name1 layer_name2
      | PRINT layer_name1
    """.stripMargin
}
