package org.aja.tantra.examples.concurrency.akka

import scala.annotation.tailrec
import scala.collection.mutable.{ Buffer, Set }
import scala.concurrent.duration._
import scala.util.Random
import akka.actor._
import akka.event.LoggingReceive
import akka.util._
/**
 * Created by mageswaran on 31/3/16.
 */


object Stars2 extends App {

  object Scout {
    case object FindTalent

    val starBaseLifetime = 7 seconds
    val starVariableLifetime = 3 seconds
    val findBaseTime = 1 seconds
    val findVariableTime = 3 seconds

    def props(): Props = Props(new Scout())
  }

  class Scout extends Actor {
    import Scout._
    import Academy._
    import context.dispatcher

    val random = Random
    scheduleFind

    def scheduleFind = {
      val nextTime = scaledDuration(findBaseTime, findVariableTime)
      scheduler.scheduleOnce(nextTime, self, FindTalent)
    }

    def scaledDuration(base: FiniteDuration, variable: FiniteDuration) =
      base + variable * random.nextInt(1000) / 1000

    def receive = {
      case FindTalent => academy ! GetName
      case GiveName(name) => {
        system.actorOf(Star.props(name, scaledDuration(starBaseLifetime, starVariableLifetime)), name)
        println(s"$name has been discovered")
        scheduleFind
      }
    }
  }

  object Academy {
    case object GetName
    case class GiveName(name: String)
    case class Register(name: String)
    case class PickStars(count: Int)
    case object PickFailure
    case class StarsPicked(ref: List[(ActorRef, String)])

    def props(names: Array[String]): Props = Props(new Academy(names))
  }

  class Academy(names: Array[String]) extends Actor {
    import Academy._

    var nextNameIndex = 0
    val nameIndexLimit = names.length * (names.length + 1)
    val liveStars = Buffer[(ActorRef, String)]()
    val random = Random

    def pickN(n: Int) = {
      @tailrec
      def pickr(picks: Set[Int]): Set[Int] =
        if (picks.size == n) picks
        else pickr(picks + random.nextInt(liveStars.size))

      pickr(Set[Int]())
    }

    def pickStars(n: Int): Seq[(ActorRef, String)] = {
      if (n <= liveStars.size / 2) pickN(n).toSeq map { i => liveStars(i) }
      else {
        val picks = pickN(liveStars.size - n)
        (0 until liveStars.size).filter { i => !picks.contains(i) }.map { i => liveStars(i) }
      }
    }

    def receive = {
      case GetName => {
        val name =
          if (nextNameIndex < names.length) names(nextNameIndex)
          else {
            val first = nextNameIndex / names.length - 1
            val second = nextNameIndex % names.length
            names(first) + "-" + names(second)
          }
        sender ! GiveName(name)
        nextNameIndex = (nextNameIndex + 1) % nameIndexLimit
      }
      case Register(name) => {
        liveStars += ((sender, name))
        context.watch(sender)
        println(s"Academy now tracking ${liveStars.size} stars")
      }
      case Terminated(ref) => {
        val star = (liveStars.find(_._1 == ref)).get
        liveStars -= star
        println(s"${star._2} has left the business\nAcademy now tracking ${liveStars.size} Stars")
      }
      case PickStars(n) => {
        if (liveStars.size < n) sender ! PickFailure
        else sender ! StarsPicked(pickStars(n).toList)
      }
    }
  }

  object Star {
    case object OfferRole
    case object AcceptRole
    case object RejectRole
    case object CancelOffer
    case object RoleComplete

    def props(name: String, lifespan: FiniteDuration) = Props(new Star(name, lifespan))
  }

  class Star(name: String, lifespan: FiniteDuration) extends Actor {
    import Star._
    import context.dispatcher

    var acceptedOffer: ActorRef = null

    academy ! Academy.Register(name)

    scheduler.scheduleOnce(lifespan, self, PoisonPill)

    def receive = {
      case OfferRole => {
        sender ! AcceptRole
        acceptedOffer = sender
        context become booked
      }
    }

    def booked: Receive = {
      case OfferRole => sender ! RejectRole
      case CancelOffer => if (sender == acceptedOffer) context become receive
      case RoleComplete => context become receive
    }
  }

  object Director {
    case object MakeMovie

    val starCountBase = 2
    val starCountVariable = 4
    val productionTime = 3 seconds
    val recoveryTime = 3 seconds

    def props(name: String) = Props(new Director(name))
  }

  class Director(name: String) extends Actor {
    import Academy._
    import Director._
    import ProductionAssistant._
    import context.dispatcher

    val random = Random
    var starsCast: List[ActorRef] = Nil

    def makeMovie = {
      val numstars = random.nextInt(starCountVariable) + starCountBase
      academy ! PickStars(numstars)
    }

    def retryMovie = scheduler.scheduleOnce(recoveryTime, self, MakeMovie)
    makeMovie

    def receive = {
      case MakeMovie => makeMovie
      case PickFailure => retryMovie
      case StarsPicked(stars) => {
        println(s"$name wants to make a movie with ${stars.length} actors")
        starsCast = stars.map(_._1)
        context.actorOf(CastingAssistant.props(name, stars.map(_._1)), name + ":Casting")
        context become casting
      }
    }

    def casting: Receive = {
      case CastingAssistant.AllSigned(stars) => {
        println(s"$name cast ${stars.length} actors for movie, starting production")
        context.actorOf(ProductionAssistant.props(productionTime, stars), name + ":Production")
        context become making
      }
      case CastingAssistant.CastingFailure => {
        println(s"$name failed casting a movie")
        retryMovie
        context become receive
      }
    }

    def making: Receive = {
      case m: ProductionAssistant.ProductionEnd => {
        m match {
          case ProductionComplete => println(s"$name made a movie!")
          case ProductionFailed => println(s"$name failed making a movie")
        }
        makeMovie
        context become receive
      }
    }
  }

  object CastingAssistant {
    case class AllSigned(stars: List[ActorRef])
    case object CastingFailure

    val retryTime = 1 second

    def props(dirname: String, stars: List[ActorRef]) = Props(new CastingAssistant(dirname, stars))
  }

  class CastingAssistant(dirname: String, stars: List[ActorRef]) extends Actor {
    import CastingAssistant._
    import Star._
    import context.dispatcher

    var signed = Set[ActorRef]()
    stars.foreach { star =>
    {
      star ! OfferRole
      context.watch(star)
    }
    }

    def receive = {
      case AcceptRole => {
        signed += sender
        println(s"Signed star ${signed.size} of ${stars.size} for director $dirname")
        if (signed.size == stars.size) {
          context.parent ! AllSigned(stars)
          context.stop(self)
        }
      }
      case RejectRole => scheduler.scheduleOnce(retryTime, sender, OfferRole)
      case Terminated(ref) => {
        context.parent ! CastingFailure
        stars.foreach { _ ! Star.CancelOffer }
        context.stop(self)
      }
    }
  }

  object ProductionAssistant {
    sealed trait ProductionEnd
    case object ProductionComplete extends ProductionEnd
    case object ProductionFailed extends ProductionEnd

    def props(time: FiniteDuration, stars: List[ActorRef]) = Props(new ProductionAssistant(time, stars))
  }

  class ProductionAssistant(time: FiniteDuration, stars: List[ActorRef]) extends Actor {
    import ProductionAssistant._
    import context.dispatcher

    stars.foreach { star => context.watch(star) }
    scheduler.scheduleOnce(time, self, ProductionComplete)

    def endProduction(end: ProductionEnd) = {
      context.parent ! end
      stars.foreach { star => star ! Star.RoleComplete }
      context.stop(self)
    }

    def receive = {
      case ProductionComplete => endProduction(ProductionComplete)
      case Terminated(ref) => endProduction(ProductionFailed)
    }
  }

  val system = ActorSystem("actor-demo-scala")
  val scheduler = system.scheduler
  val academy = system.actorOf(Academy.props(Array("Bob", "Alice", "Rock", "Paper", "Scissors", "North", "South", "East",
    "West", "Up", "Down")), "Academy")
  system.actorOf(Scout.props(), "Sam")
  system.actorOf(Scout.props(), "Dean")
  system.actorOf(Director.props("Astro"), "Astro")
  system.actorOf(Director.props("Cosmo"), "Cosmo")
  Thread sleep 15000
  system.terminate()
}