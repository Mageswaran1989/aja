package org.aja.tej.examples.sparksql.dataframe

/**
 * Created by mdhandapani on 8/12/15.
 */
class ReadWrite {

  //hiveContext.read.format(“orc”).load(“bypath/*”) //load multiple directory using dataframe load

  //df.write.partitionedBy("partCol").insertInto("tableName

  //is to append new records to the existing dataset using:  originalDF.unionAll(stageDF).write.avro(masterNew)

  //By default Spark will create one file per partition. Spark SQL defaults to using 200 partitions. If you want to
  // reduce the number of files written out, repartition your dataframe using repartition and give it the desired number of partitions.
}
