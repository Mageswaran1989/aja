/**
 * Created by mdhandapani on 10/8/15.
 */

//Link; https://speakerdeck.com/boia01/monads-monoids-and-scala
//http://www.michael-noll.com/blog/2013/12/02/twitter-algebird-monoid-monad-for-large-scala-data-analytics/
/*
Associative:
(a * b) * c = a * (b * c)

Identity:
a * 1 = 1 -> identity = 1
1 + 0 = 1 -> identity = 0
 */
trait Monoid[T]
{
  def identity: T
  def combine(a: T, b: T): T
}

/*
Monads are structures that can be seen either as containers by programmers or as
a generalization of Monoids.
1.	 Create the collection.
2.	 Transform the elements of the collection.
3.	 Flatten nested collections.
 */
trait Monad[M[_]]
{
  def apply[T] (a: T): M[T]
  def flatMap[T, U] (m: M[T]) (f: T=>M[U]) : M[U]
}



val n = 5

for {
  i <- 1 to n
  j <- 1 to i -1
  if (i+j) % 2 == 0
} yield (i,j)
//Desugared
(1 to n) flatMap { i =>
  (1 to i - 1) filter { j =>
    (i+j) % 2 == 0
  } map { j =>
    (i,j)
  }
}

implicit  object StringMonoid extends Monoid[String] {
  def identity: String = ""
  def combine(a: String, b: String) = a.concat(b)
}

implicit object IntMonoid extends Monoid[Int] {
  override def identity: Int = 0

  override def combine(a: Int, b: Int): Int = a + b
}

def sum[T](xs: List[T])(implicit m: Monoid[T]) : T = {
  if (xs.isEmpty)
    m.identity
  else
    m.combine(xs.head, sum(xs.tail))
}

def functionalSum[T](xs: Seq[T])(implicit m: Monoid[T]) : T =
  xs.foldLeft(m.identity)(m.combine(_,_))


sum(List(1,2,3,4,5))
sum(List("a","b","c"))

functionalSum(List(1,2,3,4,5))
functionalSum(List("a","b","c"))