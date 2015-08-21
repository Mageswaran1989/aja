//def binarySearch(ds: Array[Double], key: Double): Int = {
//  @annotation.tailrec
//  def go(low: Int, mid: Int, high: Int): Int = {
//    if (low > high)
//      -mid - 1
//    else
//    {
//      val mid2 = (low + high) / 2
//      val d = ds(mid2)
//      if (d == key)
//        mid2
//      else if (d > key)
//        go(low, mid2, mid2-1)
//      else
//        go(mid2 + 1, mid2, high)
//    }
//  }
//  go(0, 0, ds.length - 1)
//}
//
//// parametric polymorphism
//// A: Type Variable
//def binarySearch[A](as: Array[A], key: A, gt: (A,A) => Boolean): Int = {
//  @annotation.tailrec
//  def go(low: Int, mid: Int, high: Int): Int = {
//    if (low > high)
//      -mid - 1
//    else
//    {
//      val mid2 = (low + high) / 2
//      val a = as(mid2)
//      val greater = gt(a, key)
//      if (!greater && !gt(key,a))
//        mid2
//      else if (greater)
//        go(low, mid2, mid2-1)
//      else
//        go(mid2 + 1, mid2, high)
//    }
//  }
//  go(0, 0, as.length - 1)
//}
//
//val array = Array[Int](2,3,7,9,14)
//val key = 7
//println("Position of 7 in (2,3,7,9,14) is %d".format(binarySearch[Int](array, key,
//  (a: Int, b: Int) => a <= b)))
