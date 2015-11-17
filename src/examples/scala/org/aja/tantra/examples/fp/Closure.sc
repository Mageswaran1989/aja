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

///////////////////////////////////////////////////////////////////////

var factor = 3
val multiplier = (i:Int) => i * factor

val l1 = List(1, 2, 3, 4, 5) map multiplier

//<============Change the factor and closure does the unexpected magic!
factor = 5
val l2 = List(1, 2, 3, 4, 5) map multiplier

/*
Two free variables: i and factor
i -> Formal parameter
factor ->  a reference to a variable in the enclosing scope,
           reads its current value each time
 */
