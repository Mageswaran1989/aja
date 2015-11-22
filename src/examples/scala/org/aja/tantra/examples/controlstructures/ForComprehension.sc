//Title: for (sequence and/or filter) yield expression
/*
Syntax:
=======
for (
variableName <- someCollections
) yield variableName

for {
variableName <- someCollections
variableName1 <- someCollections someConditions
someConditions
}

for clause yield body

 */

//10 included
0 to 10
//10 excluded
0 until 10

for (
  i <- 0 to 9
) yield i

for {
  i <- 0 to 9
  j <- 0 to 9
} yield (i,j)
//What you see? nested loop!

for {
  i <- 0 to 9
  j <- 0 to 9
  if (i == j)
} yield (i,j)

for (
  i <- 0 to 9 if((i % 2) == 0)
) yield i


for {
  element <- List(1,2,3,4,5)
} yield "T"+element

val c = for {
  word <- Array("Hello", "Scala") //temp variable
  char <- word //new varaible declaration
} yield char.toUpper

for {
  word <- Seq("Hello", "Scala")
  char <- word if char.isUpper
} yield char

val list = List(1,2,3,4,5,6,7,8,9,10)

for(element <- list if element%2 == 0; if element > 5)  yield element

//TODO : Retreive XML elements using for comprehension