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

for {
  word <- Array("Hello", "Scala")
  char <- word
} yield char.toUpper

for {
  word <- Seq("Hello", "Scala")
  char <- word if char.isUpper
} yield char

//TODO : Retreive XML elements using for comprehension