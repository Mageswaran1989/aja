#Hallmarks of functional programming
 - Mapping,
 - Filtering, 
 - Folding, and 
 - Reducing.
 
 All collections are based on Iterable and Traversable traits
###Traversable
- A Traversable has one abstract method: `foreach`. When you call foreach, the collection will feed the passed function 
all the elements it keeps, one after the other.
- When you have call a Traversables foreach, or its derived methods, it will blow its values into your function one at 
a time - so it has control over the iteration.
- Traversable requires implementing the foreach method, which is used by everything else.

###Iterable
- On the other hand, an Iterable has as abstract method iterator, which returns an Iterator. You can call next on an 
Iterator to get the next element at the time of your choosing. Until you do, it has to keep track of where it was in 
the collection, and what's next.
- With the Iterator returned by an Iterable though, you suck the values out of it, controlling when to move to the next 
one yourself.
- Iterable requires implementing the iterator method, which is used by everything else.

```scala
sealed abstract class Tree extends Traversable[Int]
case class Branch(left: Tree, right: Tree) extends Tree {
  def foreach[U](f: Int => U) = left foreach f; right foreach f
}
case class Node(elem: Int) extends Tree {
  def foreach[U](f: Int => U) = f(elem)
}

sealed abstract class Tree extends Iterable[Int]
case class Branch(left: Tree, right: Tree) extends Tree {
  def iterator: Iterator[Int] = left.iterator ++ right.iterator
}
case class Node(elem: Int) extends Tree {
  def iterator: Iterator[Int] = Iterator.single(elem)
}
```

- Iterable doesn't define length.  Iterable abstracts across finite sequences and potentially infinite streams. Most of 
the time one is dealing with finite sequences so you "have a length," and Seq reflects that. Frequently you won't 
actually make use of length. But it's needed often enough, and is easy to provide, so use Seq while returing the type 
in APIs.

#when do you choose to type a given funciton's return type as seq vs iterable vs traversable?

try to use only a small set of collections: Set, Map, Seq, IndexedSeq
I often violate this previous rule, though, using List in favour of Seq. It allows the caller to do pattern matching with the cons extractors
use immutable types only (e.g. collection.immutable.Set, collection.immutable.IndexedSeq)
do not use concrete implementations (Vector), but the general type (IndexedSeq) which gives the same API
if you are encapsulating a mutable structure, only return Iterator instances, the caller can then easily generate a strict structure, e.g. by calling toList on it
if your API is small and clearly tuned towards "big data throughput", use IndexedSeq