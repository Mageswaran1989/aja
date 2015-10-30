import java.io._

@SerialVersionUID(123L)
class Person(name: String, age: Int) extends Serializable{
  override def toString = s"Person($name,$age)"
}


val aja = new Person("Mageswaran", 27)
val oos = new ObjectOutputStream(new FileOutputStream("/tmp/testSerialization"))
oos.writeObject(aja)
oos.close()

val ois = new ObjectInputStream(new FileInputStream("/tmp/testSerialization"))
val ajaReadObj = ois.readObject().asInstanceOf[Person]
ois.close()

println(ajaReadObj)