import scala.actors.Actor
class ActorDemo extends Actor {
  def act() {
    println("MMageswaran")
  }
}

val act = new ActorDemo
act.start()

import scala.actors.Actor._

val actor1 = actor(
  println("Mageswaran in second actor")
)

actor1.start()

val lizza = actor{
  var count: Int = 0
  loop {
    receive {
      case "Hi" => {
        count = count + 1
        println("Hi there...")
      }
      case "Greetings..."=> {
        count = count + 1
        println(s"Greetings! with mailboxsize of $mailboxSize")
      }
      case "count" => println(s"Values of count is $count")
      case _=> println("Hello...")
    }
  }
}

lizza ! "Hi"
lizza ! "Greetings..."
lizza ! "Hey you there?"
lizza ! "count"
