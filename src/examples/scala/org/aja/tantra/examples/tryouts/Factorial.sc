import scala.annotation.tailrec

//Some notes
//Tail recursion helps us to not consume the stack in case we use recursive functions.
//The "recursive" call will happen asynchronously, on some thread from the execution context. So it is very likely that
//this recursive call won't even reside on the same stack as the first call.
def factorial(x: Int) = {
  @tailrec
  def fact(acc: Int, x: Int): Int = {
    if (x <= 1)
      acc
    else
      fact(acc * x, x-1)
  }

  fact(1, x)

  /**
   * Pass 1 => 1 * 4, 3
   * Pass 2 => 4 * 3, 2
   * Pass 3 => 12 * 2, 1
   * Pass 4 => 24
   */
}

factorial(4)

(1 to 5).foldLeft(1){_ * _}