Source: https://github.com/databricks/reference-apps

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


Twitter API credentials: https://apps.twitter.com/
https://databricks-training.s3.amazonaws.com/realtime-processing-with-spark-streaming.html#twitter-credential-setup