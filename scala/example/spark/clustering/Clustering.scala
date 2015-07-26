
val DATASET_PATH = "/opt/aja/data"

val movies = sc.textFile(DATASET_PATH + "/ml-100k/u.item")
println(movies.first)
