/**
 * Created by mdhandapani on 10/8/15.
 */

  /*
  // 0 1 1 2 3 5
  // F_n = F_(n-2) + F_(n-1)
  int fibonacci(int n)
  {
    static int sum = 0;
    int i, p_1 = 0, p_2 = 1;
    for (i = 0; i < n; i++)
    {
      printf("%d ", sum);
      sum = p_2 + p_1;
      p_2 = p_1;
      p_1 = sum;
    }
    return sum;
  }

  int recursive_fibonacci(int n)
  {
     static int sum = 0, p_2 = 0, p_1 = 1;
     if (0 == n)
       return 0;
     else if (1 == n)
       return 1;
     else
       return recursive_fibonacci(n-2) + recursive_fibonacci(n-1);
  }
  */

//0 1 1 2 3 5 8
  //@annotation.tailrec
  def fibonacci(n: Int, flag: Boolean): Int = {
  println(n + " " + flag)
    if (0 == n || 1 == n)
      n
    else
      fibonacci(n - 1, true) + fibonacci(n - 2, false)
  }

val fibonacciOf5 = fibonacci(5, true)

//5 true
//4 true
//3 true
//2 true
//1 true

//0 false
//1 false
//2 false
//1 true

//0 false
//3 false
//2 true
//1 true
//0 false
//1 false
//fibonacciOf5: Int = 5

  //with TCO - Tail Call Optimization
  //@anotation.tailrec
  def fibonacciTco(n: Int): Int = {
    //@annotation.tailrec
    def go(n: Int, sum: Int): Int = {
      if (0 == n || 1 == n)
        n
      else
        go(n - 1, go(n - 2, sum))
    }
    go(n, 1)
  }





