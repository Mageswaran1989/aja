
def someName(a: Int, b: Int, c:Int) = {
  a + b + c
}


//partially applied function
val sum  = someName _ //no need of type info

val partialTotal = sum(1,_: Int,3) //needs type info

val total = partialTotal(2)

println("////////////////////////////////////////////////////////////////")

val truthier: PartialFunction[Boolean, String] = { case true => "truthful" }
val fallback: PartialFunction[Boolean, String] = { case x => "sketchy" }

val tester = truthier orElse fallback

println(tester(1 == 1))
println(tester(2 + 2 == 5))

//In this example, tester is a partial function composed of two other partial functions,
//truthier and fallback. In the first println statement, truthier is executed because the
//partial function’s internal case matches. In the second, fallback is executed because
//  the value of the expression is outside of the domain of truthier.

val pantsTest: PartialFunction[String, String] = {
  case "pants" => "yes, we have pants!"
}
println(pantsTest.isDefinedAt("pants"))
println(pantsTest.isDefinedAt("skort"))
