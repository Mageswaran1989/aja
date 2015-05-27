trait Monoid[T]
{
  def zero: T
  def op(a: T, b: T): c
}

trait Monas[M[_]]
{
  def apply[T] (a: T): M[T]
  def flatMap[T, U] (m: M[T]) (f: T=>M[U]) : M[U]
}


