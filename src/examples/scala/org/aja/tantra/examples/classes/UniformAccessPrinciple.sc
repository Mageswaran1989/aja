class Counter {
  var count = 5
}

//Clients read and write
//field values as if they are publicly accessible,
//even though in some cases they are actually
//calling methods.
class Counter1 {
  private var cnt = 5
  //Method and field lives in the same
  //name space
  def count = cnt
  def count_=(newCount: Int) = cnt = newCount
}

val c = new Counter
c.count
c.count = 6
c.count

val c1 = new Counter1
c1.count
c1.count = 6
c1.count
c1.count_=(7)
c1.count

//val/var vs def
//def is lazy evaluation
