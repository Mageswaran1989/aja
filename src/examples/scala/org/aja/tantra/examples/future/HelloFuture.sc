object Main extends App {
  import scala.concurrent.{Await, Future}
  import scala.concurrent.duration._

  implicit val ec = scala.concurrent.ExecutionContext.global

  def factorialAcc(acc: Int, n: Int): Future[Int] = {
//
//    if (n == 97)
//      throw new Exception("n is 97")

    if (n <= 1) {
      Future.successful(acc)

    } else {
      val fNum = getFutureNumber(n)
      fNum.flatMap(num => factorialAcc(num * acc, num - 1))
    }
  }


  def factorial(n: Int): Future[Int] = {
    factorialAcc(1, n)
  }

  protected def getFutureNumber(n: Int) : Future[Int] = Future.successful(n)

  val r = Await.result(factorial(100), 5.seconds)
  println(r)

}

Main.main(Array(""))