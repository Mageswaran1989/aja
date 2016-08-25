/**
 * Created by mdhandapani on 10/8/15.
 *
 * Simply put passing function around the program. Giving the "first class citizen" recognition
 * to the functions just like objects.
 */

//Function "f" takes a integer and converts that to string
//This gives flexibility of defining the pattern of how the string can be
def apply(f: Int => String, v: Int) = f(v)
def layout[A](x: A) = "[" + x.toString() + "]"
println(apply(layout, 10))

println("/////////////////////////////////////////////////////////////////////////")

def formatResult(str: String, n: Int, f: Int => Int) : Unit = {
  println("The %s of %d is %d".format(str, n, f(n)))
}

formatResult("Increment", 7, (x: Int) => {val r = x + 1; r})
formatResult("Increment", 7, (x) => x + 1)
formatResult("Increment", 7, x => x + 1)
formatResult("Increment", 7, (x: Int) => x + 1)
/*In this last form _ + 1, sometimes called underscore
syntax for a function literal, we are not even bothering to name the argument to the
function, using _ represent the sole argument. When using this notation, we can
only reference the function parameter once in the body of the function */
formatResult("Increment", 7, _ + 1)
formatResult("Increment", 7, x => {val r = x + 1; r})
//If you really have patience to create a local variable
//for a function
val f = (x: Int) => {val r = x + 1; r}
formatResult("Increment", 8, f)


println("/////////////////////////////////////////////////////////////////////////")


