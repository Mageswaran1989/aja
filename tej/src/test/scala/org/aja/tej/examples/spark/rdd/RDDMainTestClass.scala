package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils._
/**
 * Created by mageswaran on 12/8/15.
 */
object RDDMainTestClass {

  def main(args: Array[String]) {
    val sc = getSparkContext("RDDMainTestClass")
    val rddRawData = getRawData(sc, "data/datascience.stackexchange.com/Posts.xml")

    val aggregateExample = new AggregateExample(sc)
    val cartesianExample = new CartesianExample(sc)
    CheckPointExample.useCases(sc)
    CoalesceRepartitionExample.useCases(sc)
    CogroupGroupwithExample.useCases(sc)
    CollectToArrayExample.useCases(sc)
    CollectAsMapExample.useCases(sc)
    CombineByKeyExample.useCases(sc)
    SparkContextExample.useCases(sc)
    Count.useCases(sc)
    Dependencies.useCases(sc)
    Distinct.useCases(sc)
    First.useCases(sc)
    Filter.useCases(sc)
    FlatMapExample.useCases(sc)
    Fold.useCases(sc)
    ForEachExample.useCases(sc)
    GetStorageLevelExample.useCases(sc)
    GlomExample.useCases(sc)
    GroupByExample.useCases(sc)
    HistogramExample.useCases(sc)
    JoinExample.useCases(sc)
    LeftOuterJoinExample.useCases(sc)
    MapExample.useCases(sc)
    MeanExample.useCases(sc)
    NameExample.useCases(sc)
    PersistCacheExample.useCases(sc)
    PipeExample.useCases(sc)
    ReduceExample.useCases(sc)
    SampleExample.useCases(sc)
    SaveAsExample.useCases(sc)
    StatExample.useCases(sc)
    SortExample.useCases(sc)
    SubractSumExample.useCases(sc)
    UnionExample.useCases(sc)
    TakeTopExample.useCases(sc)
  }
}
