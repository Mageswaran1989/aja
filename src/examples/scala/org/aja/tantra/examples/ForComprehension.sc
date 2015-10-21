//for (sequence and/or filter) yield expression
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