trait T[A] {
  val valueT: A //abstract member
  def getValueT= valueT
}

class C extends T[String] {
  val valueT = "string" //no need of override for abstract memeber
  val valueC = "Aja"
  def getValueC = valueC

  class C2
  trait T2
}

val c = new C

val clazz = c.getClass
println("------------------------------------------------")
val clazz2 = classOf[C]
println("------------------------------------------------")
val methods = clazz.getMethods.foreach(println)
println("------------------------------------------------")
val ctors = clazz.getConstructors
println("------------------------------------------------")
val fields = clazz.getFields
println("------------------------------------------------")
val annos = clazz.getAnnotations
println("------------------------------------------------")
val name = clazz.getName
println("------------------------------------------------")
val parentInterfaces = clazz.getInterfaces
println("------------------------------------------------")
val superClass = clazz.getSuperclass
println("------------------------------------------------")
val typeParams = clazz.getTypeParameters
println("------------------------------------------------")

List(3.14159).isInstanceOf[List[String]]

List(3.14159).asInstanceOf[List[String]]

//Note that these two operations are methods and not keywords in the language, and
//their names are deliberately somewhat verbose. Normally, type checks and casts like
//these should be avoided. For type checks, use pattern matching instead. For casts,
//consider why a cast is necessary and determine if a refactoring of the design can eliminate
//the requirement for a cast.


//class StringList[String] extends List[String] {} != StringList extends List[String] {}