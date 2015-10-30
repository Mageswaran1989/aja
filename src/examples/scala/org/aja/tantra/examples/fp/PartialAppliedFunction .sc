
def someName(a: Int, b: Int, c:Int) = {
  a + b + c
}

val sum  = someName _

val partialTotal = sum(1,_: Int,3)

val total = partialTotal(2)
