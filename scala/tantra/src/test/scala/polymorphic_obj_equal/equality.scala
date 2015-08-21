////Book: Scala in Action chapter 2
////How to take of things while implementing obj equality
//trait InstantaneousTime {
//  val repr: Int
//  override def equals(other: Any) : Boolean = other match {
//    case that: InstantaneousTime =>
//      if(this eq that) {
//        true
//      } else {
//        (that.## == this.##) && (repr == that.repr)
//      }
//    case _ => false
//  }
//  override def hashCode() : Int = repr.##
//}
//
///**
//* Note:
//* ## == hashCode
//* == == equals
//*/
//
//trait Event extends InstantaneousTime {
//  val name: String
//  override def equals(other: Any): Boolean = other match {
//    case that: Event =>
//      if(this eq that) {
//        true
//      } else {
//      (repr == that.repr) && (name == that.name)
//      }
//    case _ => false
//  }
//}
//
//val x = new InstantaneousTime {
//  val repr = 2
//}
//
//val y = new Event {
//  val name = "TestEvent"
//  val repr = 2
//  }
//
///**
//scala> y == x  false
//scala> x == y  true
//*/
//
//trait InstantaneousTime extends Equals {
//  val repr: Int
//  override def canEqual(other: Any) = other.isInstanceOf[InstantaneousTime]
//  override def equals(other: Any) : Boolean = other match {
//    case that: InstantaneousTime =>
//      if(this eq that)
//        true
//      else {
//        (that.## == this.##) && (that canEqual this) && (repr == that.repr)
//      }
//    case _ => false
//  }
//  override def hashCode(): Int = repr.hashCode
//}
//
//trait Event extends InstantaneousTime {
//  val name: String
//  override def canEqual(other: Any) = other.isInstanceOf[Event]
//  override def equals(other: Any): Boolean = other match {
//    case that: Event =>
//      if(this eq that) {
//        true
//      }
//      else {
//        (that canEqual this) && (repr == that.repr) && (name == that.name)
//    }
//    case _ => false
//  }
//}
//
///**
//WHEN OVERRIDING EQUALITY OF A PARENT CLASS, ALSO OVERRIDES CANEQUAL
//The canEqual method is a lever, allowing subclasses to opt out of their parent
//class’s equality implementation. This allows a subclass to do so without the
//usual dangers associated with a parent class equals method returning true
//while a subclass would return false for the same two objects.
//*/
//
///*
//scala> y == x false
//scala> x == y false
//*/
//
//
