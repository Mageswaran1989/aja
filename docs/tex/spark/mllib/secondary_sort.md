Secondary Sort

Another important capability to be aware of is the repartitionAndSortWithinPartitions transformation. 
It’s a transformation that sounds arcane, but seems to come up in all sorts of strange situations. 
This transformation pushes sorting down into the shuffle machinery, where large amounts of data can be spilled 
efficiently and sorting can be combined with other operations.

For example, Apache Hive on Spark uses this transformation inside its join implementation. It also acts as a vital 
building block in the secondary sort pattern, in which you want to both group records by key and then, when iterating 
over the values that correspond to a key, have them show up in a particular order. This issue comes up in algorithms 
that need to group events by user and then analyze the events for each user based on the order they occurred in time. 
Taking advantage of repartitionAndSortWithinPartitions to do secondary sort currently requires a bit of legwork on the 
part of the user, but SPARK-3655 will simplify things vastly.