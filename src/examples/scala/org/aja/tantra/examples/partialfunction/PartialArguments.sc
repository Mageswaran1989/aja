
def adder(m: Int, n: Int, p: Int) = m + n + p
val add2 = adder(2, _: Int, _: Int)
println(s"Calling a function with partial arguments applied: ${add2(3, 5)}")
