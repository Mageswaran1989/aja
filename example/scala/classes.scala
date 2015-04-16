class Complex(real: Double, imaginary: Double)
{
  /*
    It should be noted that the return type of these two methods is not given explicitly.
    It will be inferred automatically by the compiler, which looks at the right-hand side
    of these methods and deduces that both return a value of type Double.
  */
  def re() = real
  def im() = imaginary

  //functions without arguments. should we call this as object/function/variable?
  def re1 = real
  def im1 = imaginary
  //got it "def"  is used, so this is a function after all
  
  //By default all classes in scala are inherited from "scala.AnyRef".
  //Below is sample how to use override modifier
  override def toString() = "" + re() + (if(im1 < 0) "" else "+") + im() + "i"
}

object ComplexNumbers
{
  def main(args: Array[String])
  {
    val c = new Complex(1.2, 3.4)
    println("Real part :" + c.re()) //thank god calling function is still same!
    println("Imaginary part :" + c.im1)
    println("Complex Number is :" + c.toString())
  }
}
