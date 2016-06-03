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

def getTemperoryDirectory(tempPath: Option[String]): java.io.File = {
  tempPath.map(name => new java.io.File(name))
         .filter(_.isDirectory)
         .getOrElse(new java.io.File(System.getProperty("java.io.tempDir")))
}

val t = getTemperoryDirectory(Some("/tmp/"))