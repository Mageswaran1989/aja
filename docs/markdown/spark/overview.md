# Overview of Spark


## What happens when you run a Spark application?  
### What are Spark Cluster components?  
![deploy](../../img/spark/deploy.png)

Spark Cluster components:  
1. *Master node*  
   - Runs Master daemon and manages all Worker Nodes  
   
2. *Worker Nodes*  
   - Runs Worker daemon which communicates with Master node and manages local Executors. 
    
3. *Driver*    
   - A application with SparkContext and main() that runs on a node becomes a Driver. This can be a locally launched one or started from a Master node or that runs on a scheduled Worker node by a Yarn cluster.    
   - It is always better to start the Driver on the same network as the Worker node.  
     
 
Each Worker manages one or multiple ExecutorBackend processes. Each ExecutorBackend launches and manages an Executor instance. Each Executor maintains a thread pool, in which each task runs as a thread.
> Worker -> ExecutorBackend -> Execuotr -> Thread pool -> Tasks as a thread  

Each application has one Driver and multiple Executors. The tasks within the same Executor belong to the same application.

`Standalone Mode:`  
  - In Standalone deployment mode, ExecutorBackend is instantiated as CoarseGrainedExecutorBackend.  
  - Worker manages each CoarseGrainedExecutorBackend process thourgh an ExecutorRunner instance (Object).
   

#### Sample code:  
[GroupByTest.scala](../../../scala/tej/src/test/scala/org/aja/tej/test/spark/GroupByTest.scala)  
 
