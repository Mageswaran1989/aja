Contains example usage of RDD APIs

Spark is a distributed computing engine and its main abstraction is a resilient distributed dataset (RDD), which can be 
viewed as a distributed collection. Basically, RDD's elements are partitioned across the nodes of the cluster, but Spark 
abstracts this away from the user, letting the user interact with the RDD (collection) as if it were a local one.

Not to get into too many details, but when you run different transformations on a RDD (map, flatMap, filter and others), 
your transformation code (closure) is:

1. Serialized on the driver node,
2. Shipped to the appropriate nodes in the cluster,
3. Deserialized, and 
4. Finally executed on the nodes

You can of course run this locally, but all those phases (apart from shipping over network) still 
occur. [This lets you catch any bugs even before deploying to production]
