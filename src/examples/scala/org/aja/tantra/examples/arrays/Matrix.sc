//Reference :    @ http://effprog.blogspot.com/2011/01/spiral-printing-of-two-dimensional.html */

val dim = 3
val mat = Array.ofDim[Int](dim,dim)

var value: Int = 1
for {i:Int  <- 0 to dim - 1
     j: Int <- 0 to dim - 1
} {
  mat(i)(j) = value
  value = value + 1
  }


for {i <- 0 to dim - 1
     j <- 0 to dim - 1
} {
  print(mat(i)(j) + "\t")
  if (j == dim -1 ) println()
}


for {i <- 0 to dim -1
     j <- 0 to dim -1
} {
  print(mat(i)(j) + " ")
}

///////////////////////////////////////
//Matrix spiral print

for {i <- 0 to dim - 1
     j <- 0 to dim - 1
} {
  print(mat(i)(j) + "\t")
  if (j == dim -1 ) println()
}

var startingRowIndex = 0 //k
var endRowIndex = dim - 1 //m
var startingColIndex = 0 //l
var endColIndex = dim -1  //n
while ( startingRowIndex <= endRowIndex &&
  startingColIndex <= endColIndex)
{
  println( "\nPrint the first row from the remaining rows ")
  for (i <- startingColIndex to endColIndex) {
    print(mat(startingRowIndex)(i) + " ")
  }
  startingRowIndex += 1
  println( "\n Print the last column from the remaining columns ")
  for ( i <- startingRowIndex to endRowIndex) {
    print(mat(i)(endColIndex) + " ")
  }
  endColIndex -= 1
  println( " \nPrint the last row from the remaining rows ")
  for (i <- endColIndex to startingColIndex by -1 ) {
    print(mat(endRowIndex)(i) + " ")
  }
  endRowIndex -= 1
  println( " \nPrint the first column from the remaining columns ")
  for (i <- endRowIndex to startingRowIndex by -1) {
    print(mat(i)(startingColIndex) + " ")
  }
  startingColIndex += 1

  println()
  println()
  println()
  println(startingRowIndex)
  println(endRowIndex)

  println(startingColIndex)
  println(endColIndex)
}
