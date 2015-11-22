
val sc =
val data = List(("1", "example1", Array(0,2,5)), ("2", "example2", Array(1,20,5)))
val distData = sc.parallelize(data)
val distTable = distData.toDF("userId", "someString", "varA")
distTable.registerTempTable("distTable_tmp")
val temp1 = sqlContext.sql("select userId, someString, varA from distTable_tmp")
val temp2 = sqlContext.sql("select userId, someString, explode(varA) as varA from distTable_tmp")


