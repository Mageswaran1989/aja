Unit ~ Void
Null ~ Option[x] which can be Some(x) or None  ~ Either[+T1, +T2] - Left for exception
Nil ~ End element in the List

AnyRef ~ java.Object ~ java.* ref types / scala.* ref types
Nothing - Subclass of all types

AnyRef <- Iteratable[T] <- Collection[+T] <- Seq[+T]

#Collections:
- List
- Map
- Set
- Queue
- Stack

Predef defines a number of implicit conversion methods for the value types (excluding Unit).

There are implicit conversions to the corresponding scala.runtime.RichX types.

#Annonymous Function
A FunctionN trait, where N is 0 to 22, is instantiated for an anonymous function with
N arguments. So, consider the following anonymous function:
          (t1: T1, ..., tN: TN) => new R(...)
It is syntactic sugar for the following creation of an anonymous class:
new FunctionN {
  def apply(t1: T1, ..., tN: TN): R = new R(...)
  // other methods
}

#Scala Symbols
+/- : Operators for adding/removing elements
++/-- : Operators for adding/removing elements defined in the iterators (which could be other sets, lists, etc.).

