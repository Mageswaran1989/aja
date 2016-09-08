package org.neo4j.spark

import org.apache.spark.Partition

/**
  * @author mh
  * @since 02.03.16
  */
class DummyPartition extends Partition {
  override def index: Int = 0
}
