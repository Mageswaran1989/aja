package org.aja.tantra.examples.concurrency.akka


import scala.concurrent.duration._
import scala.util.Random
import akka.actor._
import akka.util._

/**
 * Created by mageswaran on 31/3/16.
 */
//Url : http://www.ibm.com/developerworks/java/library/j-jvmc6/index.html


object Stars1 extends App {

  import Star._

  val starBaseLifetime = 5000 millis
  val starVariableLifetime = 2000 millis
  val starBaseSpawntime = 2000 millis
  val starVariableSpawntime = 1000 millis

  object Namer {
    case object GetName
    case class SetName(name: String)
    def props(names: Array[String]): Props = Props(new Namer(names))
  }

  class Namer(names: Array[String]) extends Actor {
    import context.dispatcher
    import Namer._
    var nextNameIndex = 0
    val nameIndexLimit = names.length * (names.length + 1)

    context.setReceiveTimeout(starBaseSpawntime + starVariableSpawntime)

    def receive = {
      case GetName => {
        val name =
          if (nextNameIndex < names.length) names(nextNameIndex)
          else {
            val first = nextNameIndex / names.length - 1
            val second = nextNameIndex % names.length
            names(first) + "-" + names(second)
          }
        sender ! SetName(name)
        nextNameIndex = (nextNameIndex + 1) % nameIndexLimit
      }
      case ReceiveTimeout => {
        println("Namer receive timeout, shutting down system")
        system terminate()
      }
    }
  }

  object Star {
    case class Greet(peer: ActorRef)
    case object AskName
    case class TellName(name: String)
    case object Spawn
    case object IntroduceMe
    case object SayGoodbye
    def props(greeting: String, gennum: Int, parent: String) = Props(new Star(greeting, gennum, parent))
  }

  class Star(greeting: String, gennum: Int, parent: String) extends Actor {
    import context.dispatcher

    var myName: String = ""
    var starsKnown = Map[String, ActorRef]()
    val random = Random
    val namer = context actorSelection namerPath
    namer ! Namer.GetName

    def scaledDuration(base: FiniteDuration, variable: FiniteDuration) =
      base + variable * random.nextInt(1000) / 1000

    val killtime = scaledDuration(starBaseLifetime, starVariableLifetime)
    scheduler.scheduleOnce(killtime, self, SayGoodbye)
    val spawntime = scaledDuration(starBaseSpawntime, starVariableSpawntime)
    val spawner = scheduler.schedule(spawntime, 1 second, self, Spawn)
    if (gennum > 1) scheduler.scheduleOnce(1 second, context.parent, IntroduceMe)

    def receive = {
      case Namer.SetName(name) => {
        myName = name
        println(s"$name is the ${gennum}th generation child of $parent")
        //Changing roles. Switch teh receive method
        //if second arg is false, then this receiver is kept in the stack and can reclaimed by calling unbecome
        context.become(named)
      }
    }

    //SetName message is sent to a dead-letter mailbox by default
    def named: Receive = {
      case Greet(peer) => peer ! AskName
      case AskName => sender ! TellName(myName)
      case TellName(name) => {
        println(s"$myName says: '$greeting, $name'")
        starsKnown += name -> sender
      }
      case Spawn => {
        println(s"$myName says: A star is born!")
        context.actorOf(props(greeting, gennum + 1, myName))
      }
      case IntroduceMe => starsKnown.foreach {
        case (name, ref) => ref ! Greet(sender)
      }
      case SayGoodbye => {
        println(s"$myName says: 'I’d like to thank the Academy...'")
        context stop self // takes all its descendants with it.
        // Without any Stars to spawn child Stars, no requests for names go to Namer,
        // so the Namer timeout is eventually fired and the system shuts down.
      }
    }
  }

  val namerPath = "/user/namer"
  val system = ActorSystem("actor-demo-scala")
  val scheduler = system.scheduler
  system.actorOf(Namer.props(Array("Bob", "Alice", "Rock", "Paper", "Scissors", "North", "South", "East",
    "West", "Up", "Down")), "namer")  //namer is the user actor name
  val star1 = system.actorOf(props("Howya doing", 1, "Nobody"))
  val star2 = system.actorOf(props("Happy to meet you", 1, "Nobody"))
  Thread sleep 500
  star1 ! Greet(star2)
  star2 ! Greet(star1)
}

/*
Alice is the 1th generation child of Nobody
Bob is the 1th generation child of Nobody
Alice says: 'Happy to meet you, Bob'
Bob says: 'Howya doing, Alice'
Bob says: A star is born!
Rock is the 2th generation child of Bob
Alice says: A star is born!
Paper is the 2th generation child of Alice
Bob says: A star is born!
Scissors is the 2th generation child of Bob
Alice says: 'Happy to meet you, Rock'
Alice says: A star is born!
North is the 2th generation child of Alice
Bob says: 'Howya doing, Paper'
Rock says: 'Howya doing, Paper'
Bob says: A star is born!
South is the 2th generation child of Bob
Alice says: 'Happy to meet you, Scissors'
Paper says: 'Happy to meet you, Scissors'
Alice says: A star is born!
East is the 2th generation child of Alice
Bob says: 'Howya doing, North'
Rock says: 'Howya doing, North'
Scissors says: 'Howya doing, North'
Rock says: A star is born!
West is the 3th generation child of Rock
Alice says: 'I’d like to thank the Academy...'
Bob says: A star is born!
Paper says: A star is born!
Up is the 2th generation child of Bob
Down is the 3th generation child of Paper
Rock says: A star is born!
Bob-Bob is the 3th generation child of Rock
Scissors says: A star is born!
Bob-Alice is the 3th generation child of Scissors
Bob says: 'I’d like to thank the Academy...'
Namer receive timeout, shutting down system

Process finished with exit code 0

*/