//this java
class Myclass {
  private int index;
  private String name;

  public MyClass(int index, String name) {
    this.index = index;
    this.name = name;
  }
}

//this is scala
class MyClass(index: Int, name: String) {
}

// this is Java
boolean nameHasUpperCase = false;
for (int i = 0; i < name.length(); ++i) {
  if (Character.isUpperCase(name.charAt(i))) {
    nameHasUpperCase = true;
    break;
  }
}

//this is scala
//The predicate (_.isUpperCase) is an example of a function literal in
//Scala. Called as predicate if it return boolean
val nameHasUpperCase = name.exists(_.isUpperCase) //uses predicates

//quite annonying thing
MyClass myClass = new MyClass;
val x: HashMap[Int, String] = new HashMap[Int, String]()

//instead
//it is enough to say the type once!
val x = new HashMap[Int, String]()
val x: Map[Int, String] = new HashMap()
