val m = Map("TamilNadu" -> "Chennai",
             "Karnatake" -> "Bangalore",
              "Kerala" -> "Cochin")

m.get("TamilNadu")
m.get("Kerala").get
m.get("India")
m.get("Sri Lanka").getOrElse("Neighbour Country")

m.get("Nepal").get