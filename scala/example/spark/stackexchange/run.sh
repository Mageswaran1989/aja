/opt/spark/bin/spark-submit \
  --class "org.aja.StackOverflowMain" \
  --master local[4] \
  target/scala-2.10/stackoverflowdataset_2.10-1.0.jar
