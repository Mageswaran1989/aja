/opt/spark/bin/spark-submit \
  --class "Regression" \
  --master local[4] \
  target/scala-2.10/mllib-linearregression_2.10-1.0.jar/
