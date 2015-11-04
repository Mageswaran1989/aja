

import breeze.linalg.{DenseVector, DenseMatrix}
val data = ((1.0,2.0,3.0),(4.0,5.0,6.0))
val dataArray = Array[Double](1.0,2.0,3.0,4.0,5.0,6.0)
data.getClass
val m0 = new DenseMatrix[Double](2,3,dataArray)
val size =m0.activeSize
val rows = m0.rows
val cols = m0.cols
m0.valueAt(0)
m0.valueAt(1)
m0.valueAt(0,0)
m0

println("/////////////////////////////////////////////////////////////////")

val list=Seq(2.3,3.4,2.0,1.0)
val listOfList=Seq(list,list,list)

val bbb=DenseVector(list:_*)

val bbm= DenseMatrix(listOfList:_*)
bbm.rows
bbm.cols

bbm.map(_+1)

println("/////////////////////////////////////////////////////////////////")
val m = DenseMatrix.zeros[Double](5,5)
val n = DenseMatrix.tabulate(5,5)((x,y) => x * 1 + y * 2 )
println(m)
println()
println(n)
println()
println("Size of n: ("+ n.rows + "," + n.cols +")")
//Slicing
println("Column 1 of n: ")
println(n(::, 1))
println("Row 4 of n: ")
println(n(4, ::))
//Boxing
println("n(1 to 3, 1 to 3): ")
println(n(1 to 3, 1 to 3))
//val array = Array[(Double,Double,Double)]((1,2,3),(1,2,3),(1,2,3))
//val arrayToMat = new DenseMatrix(3,3, array)
//arrayToMat.toString()

m.map(_+1)

