#Apache Spark
-------------

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

#Spark Jargons

**Application:-** User program built on Spark. Consists of a driver program and executors on the cluster.

**Application jar:-**  A jar containing the user's Spark application. In some cases users will want to create an "uber jar"
containing their application along with its dependencies. The user's jar should never include Hadoop
or Spark libraries, however, these will be added at runtime.

**Driver Program:-** The process running the main() function of the application and creating the SparkContext or
The program/process responsible for running the Job over the Spark Engine

**Cluster manager:-**  An external service for acquiring resources on the cluster (e.g. standalone manager, Mesos, YARN)

**Deploy mode:-**  Distinguishes where the driver process runs. In "cluster" mode, the framework launches the driver inside
of the cluster. In "client" mode, the submitter launches the driver outside of the cluster.

**Worker Node:-** Any node that can run application code in the cluster

**Executor:-** The process responsible for executing a task or A process launched for an application on a worker node,
that runs tasks and keeps data in memory or disk storage across them. Each application has its own executors.

**Tasks:-** Each stage has some tasks, one task per partition. One task is executed on one partition of data on one executor(machine).

**Job:-**  A piece of code which reads some input  from HDFS or local, performs some computation on the data and writes some
output data or A parallel computation consisting of multiple tasks that gets spawned in response to a Spark action
(e.g. save, collect); you'll see this term used in the driver's logs.

**Stages:-** Jobs are divided into stages. Stages are classified as a Map or reduce stages(Its easier to understand if you
have worked on Hadoop and want to correlate). Stages are divided based on computational boundaries, all
computations(operators) cannot be Updated in a single Stage, especially if there is shuffle operation involved.
It happens over many stages.

**DAG:-** DAG stands for Directed Acyclic Graph, in the present context its a DAG of operators.

**Master:-** The machine on which the Driver program runs

**Slave:-** The machine on which the Executor program runs

##Spark Components
- RDDs - a low level API for expressing DAGs that will be executed in parallel by Spark workers
- Catalyst - an internal library for expressing trees that we use to build relational algebra and expression evaluation.  There's also an optimizer and query planner than turns these into logical concepts into RDD actions.
- Tungsten - an internal optimized execution engine that can compile catalyst expressions into efficient java bytecode that operates directly on serialized binary data.  It also has nice low level data structures / algorithms like hash tables and sorting that operate directly on serialized data.  These are used by the physical nodes that are produced by the query planner (and run inside of RDD operation on workers).
- DataFrames - a user facing API that is similar to SQL/LINQ for constructing dataflows that are backed by catalyst logical plans
- Datasets - a user facing API that is similar to the RDD API for constructing dataflows that are backed by catalyst logical plans


##The Spark Programming Model

Dataset (HDFS/local) -> Transformation -> Actions (distributed computation) -> Get results to application running machine


##Resilient Distributed Datasets
- An RDD is laid out across the cluster of machines as a collections of partitions, each including a subset of the data.
- Partitions define the unit of parallelism in Spark
- Partitions are processed sequentially in each machine  across the cluster in parallel

##RDD Transformation and Actions
- Transformation are lazily evaluated, converting the dataset of type U -> dataset of type V
- Actions triggers the distributed computation to take place on the cluster
- See examples [here!](rdd)



##Serlization - How Spark machines talks with each other in a Cluster
See [this example](serlization/AllAboutSerialization.scala) for more details.

- KyroSerializer (https://github.com/EsotericSoftware/kryo)
..- The KyroSerializer is much faster and generally uses about one tenth of the memory as the default serializer. You can
switch the serializer by setting spark.serializer to spark.KryoSerializer. If you want to use KyroSerializer, you need
to make sure that the classes are serializable by KyroSerializer. Spark provides a trait KryoRegistrator, which you can
extend to register your classes with Kyro, as shown in the following code:

```scala
class Reigstrer extends spark.KyroRegistrator {  
  override def registerClasses(kyro: Kyro) {  
  kyro.register(classOf[MyClass])  
  }  
}```

##Caching
- Contetns of RDD after first action can be stored in RAM for fast re-use/computation on iterative algorithms
- If the RDD contents fits the memory it will be stored in RAM, if not it will be recomputed next time an action uses. 
Spark spills partitions that will not fit in the memoey to disk.
- It is beast practise to cache RDD when they are likely to be referenced by multiple actions and are expensive(both money and time) to regenerate.
- `rdd.cahce()` or `rdd.persist(StorageLevel.MEMORY)`
- Differenct StorageLevel
..- MEMORY_SER
..- MEMORY_AND_DISK
..- MEMORY_AND_DISK_SER
