trait AbstractT2 {
  println("In AbstractT2:")
  val value: Int
  lazy val inverse = { println("initializing inverse:"); 1.0/value }
  //println("AbstractT2: value = "+value+", inverse = "+inverse)
  //Enable above print to get error!
  //Why?  This println forces inverse to be evaluated inside the body of AbstractT2
}
val c2d = new AbstractT2 {
  println("In c2d:")
  val value = 10
}
println("Using c2d:")
println("c2d.value = "+c2d.value+", inverse = "+c2d.inverse)

//So, how is a lazy val different from a method call? In a method call, the body is executed
//  every time the method is invoked. For a lazy val, the initialization “body” is evaluated
//  only once, when the variable is used for the first time. This one-time evaluation makes
//little sense for a mutable field. Therefore, the lazy keyword is not allowed on vars.
//(They can’t really make use of it anyway.)
//
//If a val is lazy, make sure all uses of the val are also lazy!



//////////////////////////////////////////////////////////////////////


object Db {
  val table = Map(1 -> (1, "Haruki Murakami", -1),
    2 -> (2, "Milan Kundera", 1),
    3 -> (3, "Jeffrey Eugenides", 1),
    4 -> (4, "Mario Vargas Llosa", 1),
    5 -> (5, "Julian Barnes", 2))
  def team(id: Int) = {
    for (rec <- table.values.toList; if rec._3 == id)
      yield recToEmployee(rec)
  }
  def get(id: Int) = recToEmployee(table(id))
  private def recToEmployee(rec: (Int, String, Int)) = {
    println("[db] fetching " + rec._1)
    Employee(rec._1, rec._2, rec._3)
  }
}

case class Employee(id: Int,
                    name: String,
                    managerId: Int) {
  lazy val manager: Employee = Db.get(managerId)
  lazy val team: List[Employee] = Db.team(id)
}

Db.get(2)