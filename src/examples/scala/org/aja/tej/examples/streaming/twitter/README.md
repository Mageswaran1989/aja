Source: https://github.com/databricks/reference-apps

Streaming Types:
1. Receive each record and process it ASAP
2. Combine the records based on time or number of records caled mini-batches
* Spark uses mini-batches with time base(batch interval) slicing called discretized stream or DStream



Input Sources -> Transformation -> 


##Input Sources:
1. Folder based
2. TCP Socket based
3. Twitter
4. AKKA actors
5. Flume
6. Kafta
7. Amazon Kinesis

##Transformation
Spark Streaming also provides operators such as reduce and count. These operators
return a DStream made up of a single element (for example, the count value for each
batch). Unlike the equivalent operators on RDDs, these do not trigger computation
on DStreams directly. That is, they are not actions, but they are still transformations,
as they return another DStream.

updateStateByKey ~ Broadcast variables + Accumulators

##Windowing
A window is defined by the length of the window and the sliding interval. For
example, with a 10-second window and a 5-second sliding interval, we will compute
results every 5 seconds, based on the latest 10 seconds of data in the DStream. For
example, we might wish to calculate the top websites by page view numbers over the
last 10 seconds and recompute this metric every 5 seconds using a sliding window.

Here are 5 typical stages for creating a production-ready classifier. 
Often, each stage is done with a different set of tools and even by different engineering teams:

1. Scrape/collect a dataset.
2. Clean and explore the data, doing feature extraction.
3. Build a model on the data and iterate/improve it.
4. Improve the model using more and more data, perhaps upgrading your infrastructure to support building larger models. (Such as migrating over to Hadoop.)
5. Apply the model in real time.

Spark can be used for all of the above and simple to use for all these purposes. We've chosen to break up the language classifier into 3 parts with one simple Spark program to accomplish each part:

1. Collect a Dataset of Tweets - 
Spark Streaming is used to collect a dataset of tweets and write them out to files.
2. Examine the Tweets and Train a Model - 
Spark SQL is used to examine the dataset of Tweets. Then Spark MLLib is used to apply the K-Means algorithm to train a model on the data.
3. Apply the Model in Real-time - 
Spark Streaming and Spark MLLib are used to filter a live stream of Tweets for those that match the specified cluster.


##Twitter API credentials:
- https://apps.twitter.com/  
- https://databricks-training.s3.amazonaws.com/realtime-processing-with-spark-streaming.html#twitter-credential-setup  

##Schema
- https://github.com/episod/twitter-api-fields-as-crowdsourced/wiki  
- http://support.gnip.com/sources/twitter/data_format.html
  
  
#Package to hold examples related to Spark Streaming Library

#Examples
----------
  
|Program   | Description  |Run Command|
|:---------|:------------:| |
|TwitterWithNeo4j | Creates some nodes and relationship to play with Cypher|run-main org.aja.tej.examples.streaming.twitter.TwitterWithNeo4 20 --consumerKey yM4CdwtCfDcs6OtEfrPUFLnPw  --consumerSecret k1QEczYNMKXZYFOhPB18Jtyde6uK9dKrB7PAOmM3oouhWlmRZ3  --accessToken 68559516-eoQTbOt4sOpJCHiGnKll8DGW4ihXpmPf0u2xwXLwE --accessTokenSecret GOWRqKf1EDjxjPSoOAuazefweKdJgidvNQBvTpri7TEd5|