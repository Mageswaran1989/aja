Array(1, 2) == Array(1, 2)


Array(1, 2).sameElements(Array(1, 2))



val l = "Hello" → 3.14

class Point2(var x: Int, var y: Int) extends Equals {
  def move(mx: Int, my: Int) : Unit = {
    x = x + mx
    y = y + my
  }
  override def hashCode(): Int = y + (31*x)

  def canEqual(that: Any): Boolean = that match {
    case p: Point2 => true
    case _ => false
  }

  override def equals(that: Any): Boolean = {
    def strictEquals(other: Point2) =
      this.x == other.x && this.y == other.y
    that match {
      case a: AnyRef if this eq a => true
      case p: Point2 => (p canEqual this) && strictEquals(p)
      case _ => false
    }
  }
}

////////////////////////////////////////////////////////


trait InstantaneousTime extends Equals {
  val repr: Int
  override def canEqual(other: Any) =
    other.isInstanceOf[InstantaneousTime] //Allow any subclass

  override def equals(other: Any) : Boolean =
    other match {
      case that: InstantaneousTime =>
        if(this eq that) true else {
          (that.## == this.##) &&
            (that canEqual this) &&
            (repr == that.repr)
        }
      case _ => false
    }
  override def hashCode(): Int = repr.hashCode
}
trait Event extends InstantaneousTime {
  val name: String
  override def canEqual(other: Any) =
    other.isInstanceOf[Event] //Subclass opt out of equality canEqual
  override def equals(other: Any): Boolean = other match {
    case that: Event =>
      if(this eq that) {
        true
      } else {
        (that canEqual this) &&
          (repr == that.repr) &&
          (name == that.name)
      }
    case _ => false
  }
}
//
//WHEN OVERRIDING EQUALITY OF A PARENT CLASS, ALSO OVERRIDES CANEQUAL
//The canEqual method is a lever, allowing subclasses to opt out of their parent
//class’s equality implementation. This allows a subclass to do so without the
//usual dangers associated with a parent class equals method returning true
//


val x = new InstantaneousTime {
val repr = 2
}

val y = new Event {
 val name = "TestEvent"
 val repr = 2
}

y == x
x == y