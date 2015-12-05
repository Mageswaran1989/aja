//Objects are instantiated automatically and lazily by the runtime system (see Section 5.4
//  of [ScalaSpec2009]). Just as for classes and traits, the body of the object is the con-
//  structor, but since the system instantiates the object, there is no way for the user to
//specify a parameter list for the constructor, so they aren’t supported. Any data defined
//in the object has to be initialized with default values. For the same reasons, auxiliary
//constructors can’t be used and are not supported.

//Student is Singleton class or class with static members
object Student {
  val numberOfStudents = 500
  val numberOfClasses = 10
}

//if a class (or a type referring to a class) and an object are declared in
//  the same file, in the same package, and with the same name, they are called a companion
//class (or companion type) and a companion object, respectively.
//
//  There is no namespace collision when the name is reused in this way, because Scala stores
//the class name in the type namespace, while it stores the object name in the term
//namespace

class Person(var name: String, var age: Int) {
  override def toString = "Person(" + name + "," + age + ")"

  val canThisAccessedFromCO = 7
}

//object -> Not Singleton, rather it is Companion object
//Since you can't instantiate a singleton object,
// you can't pass parameters to the primary constructor.
object Person {
  //Factory pattern
  def apply(name: String, age: Int): Person = new Person(name, age)

//  If there are several alternative constructors for a class and it also has a
//  companion object, consider defining fewer constructors on the class and
//  defining several overloaded apply methods on the companion object to
//  handle the variations.

  def unapply(p: Person) = Some(p.name, p.age) //Extractors
//Don't forget the "Some" part!

  //println(canThisAccessedFromCO) //can access directly
  println("canThisAccessedFromCO :" + new Person("Aja", 1).canThisAccessedFromCO)
}

val p = Person("Mageswaran", 26)
p match { //works with the help of "unapply"
  case Person(name, age) => println("Person is " + name + " of age " + age)
  case _ => println("No match")
}

/////////////////////////////////////////////////////////////////////////

trait Clickable {
  def click()
}

abstract class Widget {
  def draw(): Unit
  override def toString() = "(widget)"
}

class Button(val label: String) extends  Widget with Clickable {
  def draw() = println("Button")
  def click() = println("Button clicked")
  override def toString() = "(button: label=" + label + ", " + super.toString() + ")"
}

class TextField(var text: String) extends Widget with Clickable {
  def draw() = println("TextField")
  def click() = println("TextField clicked")
  override def toString() = "(textfield: text="+text+", "+super.toString()+")"
}

object TextField {
  def unapply(textField: TextField) = Some(textField.text)
}

object Widget {
  val ButtonExtractorRE = """\(button: label=([^,]+),\s+\(Widget\)\)""".r
  val TextFieldExtractorRE = """\(textfield: text=([^,]+),\s+\(Widget\)\)""".r

  def apply(specification: String): Option[Widget] = specification match {
    case ButtonExtractorRE(label)   => new Some(new Button(label))
    case TextFieldExtractorRE(text) => new Some(new TextField(text))
    case _ => None
  }
}
//>>>>>>>>>>>>>>> Lets test the code

Widget("(button: label=click me, (Widget))") match {
  case Some(w) => w match {
    case b: Button => println("Button with label : " + b.label)
    case t: TextField => println("TextField with text : " + t.text)
  }
  case None => println("No widget created")
}

Widget("(textfield: text=This is text, (Widget))") match {
  case Some(w) => w match {
    case b: Button => println("Button with label : " + b.label)
    case t: TextField => println("TextField with text : " + t.text)
  }
  case None => println("No widget created")
}



//Do not define main or any other method in a companion object that needs
//to be visible to Java code as a static method. Define it in a singleton
//object, instead.
