import akka.actor.{ActorSystem, ActorLogging, Actor, Props}

case object Pint

class Person extends Actor with ActorLogging {
  def receive = {
    case Pint => log.info("Thanks for the pint")
  }
}

object HowdyAkka extends App {
  val system = ActorSystem("howdy-akka")

  val alice = system.actorOf(Props(new Person), "alice")

  alice ! Pint

  system.terminate()
}

//class ActorDemo extends Actor {
//  def act() {
//    println("MMageswaran")
//  }
//}
//
//object HelloWorldActor {
//
//  val act = new ActorDemo
//  act.start()
//
//  import scala.actors.Actor._
//
//  val actor1 = actor(
//    println("Mageswaran in second actor")
//  )
//
//  actor1.start()
//
//  val lizza = actor{
//    var count: Int = 0
//    loop {
//      receive {
//        case "Hi" => {
//          count = count + 1
//          println("Hi there...")
//        }
//        case "Greetings..."=> {
//          count = count + 1
//          println(s"Greetings! with mailboxsize of $mailboxSize")
//        }
//        case "count" => println(s"Values of count is $count")
//        case _=> println("Hello...")
//      }
//    }
//  }
//
//  def main(args: Array[String]) {
//    lizza ! "Hi"
//    lizza ! "Greetings..."
//    lizza ! "Hey you there?"
//    lizza ! "count"
//  }

//}
