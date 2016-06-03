object Breed extends Enumeration {
  val doberman = Value("Doberman Pinscher")
  val yorkie = Value("Yorkshire Terrier")
  val scottie = Value("Scottish Terrier")
  val dane = Value("Great Dane")
  val portie = Value("Portuguese Water Dog")
}

Breed.dane.id
// print a list of breeds and their IDs
println("ID\tBreed")
for (breed <- Breed) println(breed + "\t" + breed)
// print a list of Terrier breeds
//println("\nJust Terriers:")
//Breed.
//  filter(_.toString.endsWith("Terrier")).foreach(println)



object WeekDay extends Enumeration {
  type WeekDay = Value
  val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
}

//import WeekDay._
//def isWorkingDay(d: WeekDay) = ! (d == Sat || d == Sun)
//WeekDay filter isWorkingDay foreach println


