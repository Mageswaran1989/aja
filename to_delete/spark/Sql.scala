
https://github.com/spirom/LearningSpark/tree/master/src/main/scala/sql


Json:

{"cty":"United Kingdom","gzip":"H4sIAAAAAAAAAKtWystVslJQcs4rLVHSUUouqQTxQvMyS1JTFLwz89JT8nOB4hnFqSBxj/zS4lSF/DQFl9S83MSibKBMZVExSMbQwNBM19DA2FSpFgDvJUGVUwAAAA==","nm":"Edmund lronside","yrs":"1016"}

Output:
{"cty":"United Kingdom","hse":{"nm": "Cnut","cty": "United Kingdom","hse": "House of Denmark","yrs": "1016-1035"},"nm":"Edmund lronside","yrs":"1016"}


val jsonData = sqlContext.read.json(sourceFilesPath)
//loop through the DataFrame and manipulate the gzip Filed
val jsonUnGzip = jsonData.map(r => Row(r.getString(0), GZipHelper.unCompress(r.getString(1)).get, r.getString(2), r.getString(3)))

//I get a row with 4 columns (String,String,String,String)
// org.apache.spark.sql.Row = [United Kingdom,{"nm": "Cnut","cty": "United Kingdom","hse": "House of Denmark","yrs": //"1016-1035"},Edmund lronside,1016]

//Answer
val jsonNested = sqlContext.read.json(jsonUnGzip.map{case Row(cty:String, json:String,nm:String,yrs:String) => s"""{"cty": \"$cty\", "extractedJson": $json , "nm": \"$nm\" , "yrs": \"$yrs\"}"""})

http://stackoverflow.com/questions/34069282/how-to-query-json-data-column-using-spark-dataframes


//Ignore case
DF.groupBy(upper(col("a"))).agg(sum(col("b"))).
//DataFrame provide function "upper" to update column to uppercase.


https://bzhangusc.wordpress.com/2015/06/10/why-your-join-is-so-slow/
//https://bzhangusc.wordpress.com/


df.write().format("json").mode(SaveMode.Overwrite).save("/tmp/path");

https://pythagoreanscript.wordpress.com/2015/05/30/spark-sql-udf-user-defined-functions/
https://forums.databricks.com/questions/79/how-can-i-register-custom-udfs.html

http://snowplowanalytics.com/blog/2015/05/21/first-experiments-with-apache-spark/

