val m = Map("TamilNadu" -> "Chennai",
             "Karnatake" -> "Bangalore",
              "Kerala" -> "Cochin")

m.get("TamilNadu")
m.get("Kerala").get
m.get("India")
m.get("Sri Lanka").getOrElse("Neighbour Country")

//m.get("Nepal").get //java.util.NoSuchElementException: None.get
val someNumber = Some(5)
val noneNumber = None

for (option <- List(noneNumber, someNumber)) {
  option.map(n => println(n * 5))
}

//since noneNumber is empty, None is returned