//http://gabrielsw.blogspot.in/2009/06/boolean-algebra-internal-dsl-in-scala.html
val Π = Math.Pi
def √(x:Double)=Math.sqrt(x)
val x= √(9*Π)

def ∑(r:Range)(f:Int =>Int)=r.reduceLeft(_+ f(_))
def ∏(r:Range)(f:Int =>Int)=r.reduceLeft(_* f(_))

val s= ∑(1 to 100)(x=>x^2)
val p= ∑(1 to 100 by 2)(x=>x^2)
val y= ∏(1 to 30 by 3)(_)

class LogicBoolean(val value:Boolean) {
  import LogicBoolean.¬
  override def toString = value.toString

  def V(b:LogicBoolean) = new LogicBoolean(this.value || b.value)
  def Λ(b:LogicBoolean) = new LogicBoolean(this.value && b.value)
  def →(b:LogicBoolean) = (¬(this)) V (this Λ b)
}

object LogicBoolean {
  def ¬(a:LogicBoolean) = new LogicBoolean(!a.value)
  implicit def boolToLogicBool(x:Boolean) = new LogicBoolean(x)
}

println("¬A :"+ ¬(A))
println("¬B :"+ ¬(B))
println("A Λ B :"+ (A Λ B))
println("A V B :"+ (A V B))
println("A → B :" + (A → B))
println("B → A :" + (B → A))


import LogicBoolean._

val r = List(true,false)

def expr(a: Boolean, b: Boolean, c: Boolean, d: Boolean) =
( (a Λ b) V ¬(a Λ c) ) Λ ((c V d) Λ ¬(b) )

//let's print the truth table
println ("\na\tb\tc\td\t(a Λ b) V ¬(a Λ c) ) Λ ((c V d) Λ ¬(b)")

for (a <- r; b <- r; c <- r; d <- r ) 
  println (a+"\t"+b+"\t"+c+"\t"+d+"\t"+expr(a,b,c,d)) 


