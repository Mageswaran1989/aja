val line = Console.readLine

class Upper {
  //Strings* -> Variable length ~ Array[String]
  def convert(strings: String*): Seq[String] = {
     //                   annonumous function
    //strings.map((string: String) => string.toUpperCase())
    strings.map(_.toUpperCase())
    //The value of the last expression is the default return value of a function.
    //No return is required.

  }
}

val upper = new Upper
Console.println(upper.convert("FP", "and", "Obj", "meets", "Scala"))