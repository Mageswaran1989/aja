import scala.annotation.tailrec

def pascal(c: Int, r: Int) = {

  def go(c: Int, r: Int): Int = {
    if (c == 0 || r == 0 || c == r)
      1
    else
      go(c-1, r-1) + go(c, r-1)
  }

  go(c,r)
}

pascal(0,0)
pascal(4,4)
pascal(0,2)
pascal(1,2)
pascal(1,3)
pascal(2,5)
pascal(4,5)


def pascal1(c: Int, r: Int) = {

  @tailrec
  def go(c: Int, r: Int): Int = {
    if (c == 0 || r == 0 || c == r)
      1
    else {

    }
  }

  go(c,r)
}

pascal1(0,0)
pascal1(4,4)
pascal1(0,2)
pascal1(1,2)
pascal1(1,3)
pascal1(2,5)
pascal1(4,5)