package org.aja.tantra.examples.concurrency.akka

/**
 * Created by mageswaran on 6/10/16.
 */
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, Sink, Source}
import akka.util.ByteString
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import java.nio.file.Paths
import scala.concurrent.duration._

object TestMain extends App {
  implicit val actorSystem = ActorSystem("test")
  implicit val materializer = ActorMaterializer()
  implicit def ec = actorSystem.dispatcher

  val sources = Vector("build.sbt", ".gitignore")
    .map(Paths.get(_))
    .map(p =>
      FileIO.fromPath(p)
        .viaMat(Framing.delimiter(ByteString(System.lineSeparator()), Int.MaxValue, allowTruncation = true))(Keep.left)
        .mapMaterializedValue { f =>
          f.onComplete {
            case Success(r) if r.wasSuccessful => println(s"Read ${r.count} bytes from $p")
            case Success(r) => println(s"Something went wrong when reading $p: ${r.getError}")
            case Failure(NonFatal(e)) => println(s"Something went wrong when reading $p: $e")
          }
          NotUsed
        }
    )
  val finalSource = Source(sources).flatMapConcat(identity)

  val result = finalSource.map(_ => 1).runWith(Sink.reduce[Int](_ + _))
  result.onComplete {
    case Success(n) => println(s"Read $n lines total")
    case Failure(e) => println(s"Reading failed: $e")
  }
  Await.ready(result, 10.seconds)

  actorSystem.terminate()
}
