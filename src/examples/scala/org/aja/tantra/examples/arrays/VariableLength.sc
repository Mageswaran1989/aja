def printArgs(args: String*) = {
  var i : Int = 0
  if (args.length < 1)
    println("Please provide args for the program")

  for(arg <- args) {
    println("Passed arg num: [" + i + "] : " + arg)
    i = i + 1
  }
}

printArgs("Mageswaran", "1", "Aja")