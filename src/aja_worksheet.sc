import breeze.linalg._

//Aja worsheet to test scala snippets in IDE
//Eg:
val l = List(1,2,3,4).map(_+2)

val a = new DenseMatrix(4,2, Array(2.0,1.0,0.0,0.0,4.0,3.0,0.0,0.0))
// 4 X 2 => 4 x 4 . 1 X 2 . 2 X 2
//          4 X 2 . (2 X 2). 2 X 2
val svd.SVD(u,s,v) = svd(a)

println("u => \n" + u)
println("s => \n" + diag(s))
println("v => \n" + v)


val vale =  "123".toInt
