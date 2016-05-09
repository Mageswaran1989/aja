//package org.aja.tantra.examples.concurrency.akka
//
//import java.net.InetSocketAddress
//import akka.actor.Actor
//import akka.io.{IO, Tcp}
//import akka.util.ByteString
//import scala.concurrent.Promise
//import Tcp._
//
//import akka.actor.{Props, ActorSystem}
//import play.api.libs.concurrent.Execution.Implicits._
//import play.api.mvc._
//
///**
// * Created by mageswaran on 4/5/16.
// * https://mobiarch.wordpress.com/2016/01/19/reactive-tcp-client-using-akka-framework/
// */
//object PlayExample {
//
//  class TcpClient(remote: InetSocketAddress,
//                  requestData: String,
//                  thePromise: Promise[String]) extends Actor {
//    import context.system
//
//    println("Connecting")
//    IO(Tcp) ! Connect(remote)
//
//    def receive = {
//      case CommandFailed(_: Connect) =>
//        println ("Connect failed")
//        context stop self
//
//      case c @ Connected(remote, local) =>
//        println ("Connect succeeded")
//        val connection = sender()
//        connection ! Register(self)
//        println("Sending request early")
//        connection ! Write(ByteString(requestData))
//
//        context become {
//          case CommandFailed(w: Write) =>
//            println("Failed to write request.")
//          case Received(data) =>
//            println("Received response.")
//            //Fulfill the promise
//            thePromise.success(
//              data.decodeString("UTF-8"))
//          case "close" =>
//            println("Closing connection")
//            connection ! Close
//          case _: ConnectionClosed =>
//            println("Connection closed by server.")
//            context stop self
//        }
//      case _ => println("Something else is up.")
//    }
//  }
//
//  class Application extends Controller {
//
//    def index = Action.async {
//      val host = "example.com"
//      val promise = Promise[String]()
//      val props = Props(classOf[TcpClient],
//        new InetSocketAddress(host, 80),
//        s"GET / HTTP/1.1\r\nHost: ${host}\r\nAccept: */*\r\n\r\n", promise)
//
//      //Discover the actor
//      val sys = ActorSystem.create("MyActorSystem")
//      val tcpActor = sys.actorOf(props)
//
//      //Convert the promise to Future[Result]
//      promise.future map { data =>
//        tcpActor ! "close"
//        Ok(data)
//      }
//    }
//
//  }
//
//  def main(args: Array[String]) {
//
//  }
//}
