/*
Iterators are the imperative version of streams. Like streams, iterators describe potentially
infinite lists. However, there is no data-structure which contains the elements
of an iterator. Instead, iterators allow one to step through the sequence,
using two abstract methods next and hasNext.

trait Iterator[+A] {
def hasNext: Boolean
def next: A
}
 */

val it : Iterator[Int] = Iterator.range(0,10)

while(it.hasNext) {
  val x = it.next()
  println(x * x)
}

