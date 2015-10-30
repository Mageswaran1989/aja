var outSideVariable = 0


def someFunction(value: Int) = {
  //closure action
  outSideVariable += value
}

//update the outsideVariable through closure
someFunction(10)
//print
outSideVariable

// update directly
outSideVariable = outSideVariable + 9

//Lets see whct happens inside the closure?
someFunction(1)
//print
outSideVariable