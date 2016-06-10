#Package to hold examples related to Spark Streaming Library

##Data Stream Sources
- **Basic data stream sources** include TCP sockets, Akka Actors, and files. Spark
Streaming includes the libraries required to process data from these sources. A Spark
Streaming application that wants to process data streams from a basic source needs
to link only against the Spark Streaming library.
- **Advanced data stream sources** include Kafka, Flume, Kinesis, MQTT, ZeroMQ, and
Twitter. The libraries required for processing data streams from these sources are
not included with Spark Streaming, but are available as external libraries. A Spark
Streaming application that wants to process stream data from an advanced source
must link against not only the Spark Streaming library, but also the external library
for that source.

##Receiver
A Receiver receives data from a streaming data source and stores it in memory. Spark Streaming creates and
runs a Receiver on a worker node for each data stream. An application can connect to multiple data streams
to process data streams in parallel.
##Destinations
The results obtained from processing a data stream can be used in a few different ways
- File
- Database
- UI
- Application

##Streaming batch interval
- Spark splits the stream into micro batches. The batch interval defines the size of the batch in seconds. For example, a batch interval of 5 seconds will cause Spark to collect 5 seconds worth of data to process.
- Spark Streaming creates a new RDD from a streaming source every 5 seconds.
- The batch size can be as small as **500 milliseconds**. The upper bound for the batch size is determined
by the latency requirements of your application and the available memory. The executors created for a
Spark Streaming application must have sufficient memory to store the received data in memory for good
performance.

##Processing time
This is the time it takes Spark to process one batch of data within the streaming batch interval. In our case, it’s the time we need to aggregate the records and write them to Aerospike.

##Median streaming rate
This defines the number of records Spark pulls from the stream per second and stream receiver. For example, if the streaming batch interval is 5 seconds, we have 3 stream receivers and a median streaming rate of 4,000 records, Spark would pull 4,000 x 3 x 5 = 60,000 records per batch. So we would have to be able to process these 60,000 records within 5 seconds. Otherwise we run behind and our streaming application becomes unstable.

##Checkpointing
- When application does statefull transformation on never ending stream, it can checkpoint(store) the streaming data for dataloss due a crash
- If the machine running the driver program crashes after some data has been received but before it has been processed, there is a potential for data loss. This dataloss is recoverd from the checkpoint directory.

##Workflow
SparkConf -> Spark Context -> Streaming Contexxt(ssc)  
ssc.checkpoint("path-to-checkpoint-directory")  
ssc.start()  ~ blocking call  

**{processing stream data}**  

ssc.stop()  

##Waiting for Stream Computation to Finish
The awaitTermination method in the StreamingContext class makes an application thread wait for stream
computation to stop. It’s syntax is  
**ssc.awaitTermination()**   
The awaitTermination method is required if a driver application is multi-threaded and the start
method was called not from the main application thread but by another thread. The start method in the
StreamingContext class is blocking method; it does not return until stream computation is finished or
stopped. In a single-threaded driver, the main thread will wait until the start method returns. However,
if the start method was called from another thread, you can prevent your main thread from exiting
prematurely by calling awaitTermination.

#Examples
----------

|Program   | Description  |
|:---------|:------------:|
|helloWorld|Basic Intro   |
|twitter   |Streaming + Twitter + Neo4j |


###Misc
Number of Partitions =  (Batch Interval / spark.streaming.blockInterval) * number of receivers
