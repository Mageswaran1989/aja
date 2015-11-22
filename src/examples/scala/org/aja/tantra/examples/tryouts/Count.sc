

/**
 * Created by mageswaran on 8/11/15.
 */


def countTo(n: Int): Int = {
  def count(i: Int, acc: Int): Int = {
    if (i <= n) {
      println(i)
      count(i + 1, acc + i)
    }
    else
      acc
  }
  count(0, 0)
}

countTo(4)


