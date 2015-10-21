package org.aja.tej.examples.usecases.map.reduce

/**
 * Created by mageswaran on 17/10/15.
 */

/**
Sorting
=======

Problem Statement:
------------------
  There is a set of records and it is required to sort these records by some rule or process these
records in a certain order.

Solution:
---------
  Simple sorting is absolutely straightforward – Mappers just emit all items as values associated with the
sorting keys that are assembled as function of items. Nevertheless, in practice sorting is often used in a quite tricky way,
that’s why it is said to be a heart of MapReduce (and Hadoop). In particular, it is very common to use composite keys to
achieve secondary sorting and grouping.
  Sorting in MapReduce is originally intended for sorting of the emitted key‐value pairs by key, but there exist techniques
that leverage Hadoop implementation specifics to achieve sorting by values. See this blog
(http://www.riccomini.name/Topics/DistributedComputing/Hadoop/SortByValue/) for more details.
  It is worth noting that if MapReduce is used for sorting of the original (not intermediate) data, it is often a good idea
to continuously maintain data in sorted state using BigTable concepts. In other words, it can be more efficient to sort
data once during insertion than sort them for each MapReduce query.

Applications:
------------
  ETL, Data Analysis
*/

object Sorting {

}
