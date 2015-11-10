//Lets explore how parameters are handled in Scala class

class Person(name: String, age: Int) {

  val someData : String = ""
}
val p = new Person("Aja", 27)
//p.age //error
//p.name //error
p.someData

/////////////////////////////////////////////////////////////////////////////////
case class Person1(name: String, age: Int) {
   private val someData: String =""
}
val p1 = Person1("Aja", 27)
p1.name
p1.age
//p1.someData //not accesible

/////////////////////////////////////////////////////////////////////////////////
class Person2(var name: String, var age: Int) {
  protected val someData: String = ""

  def someMethod() = { println(name)}

  override def toString(): String   = this.getClass.getSimpleName + "(" + name + "," + age + ")"
}
val p2 = new Person2("Aja", 28)
//p2.someData //not accessible
p2.name
p2.age
p2
p2.name = "aja"
p2.age = 25
p2

/////////////////////////////////////////////////////////////////////////////////
class Person3(val name: String, val age: Int) {
  protected val someData: String = ""

  def someMethod() = { println(name)}

  override def toString(): String   = this.getClass.getSimpleName + "(" + name + "," + age + ")"
}
val p3 = new Person3("Aja", 27)
//p2.someData //not accessible
p3.name
p3.age //not accessible
p3
//p3.name = "aja"  //no setter created
//p3.age = 25 //no setter created
p3

/////////////////////////////////////////////////////////////////////////////////
//Here the constructor is made private
class Model private (x: Int, y: Int) {
  def this() = this(0,0)
  def this(y: Int) = this(0,y)
  def run = x + y
}
object Model {
  def train() = new Model().run
  def train(y: Int) = new Model(y).run
  def train(x: Int, y: Int) = new Model(x,y).run
}

var result = new Model().run
var addResult = Model.train()
addResult = Model.train(1)
addResult = Model.train(1,1)



//Cannnot override mutable variables


//////////////////////////////////////////////////////////////////////////////////////

val obj = new Object

