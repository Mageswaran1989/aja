package org.aja.dhira.nnql

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

class SimplisticHandler extends Actor {
  import Tcp._
  var i = 0d
  var exit = false
  var manipulate: Thread = _
  def receive = {
    case Received(data) =>  data.utf8String match {
//      case "create" | "CREATE" => sender() ! Write(ByteString("Handler started"))
//      case "start" | "START" => {
      case "create" | "CREATE" => {
        println("New Start started")
        sender() ! Write(ByteString("Handler started"))
        manipulate = new Thread(new Runnable {
          override def run(): Unit = {

            exit = false
            while(!exit) {
              i = i + 1
              Thread.sleep(1000)
            }
          }
        })
        manipulate.start()
      }
      case "print" | "PRINT" => println("Value of i is: " + i)
      case "stop" | "STOP" => exit = true; context stop self
      case _ => print(data.utf8String); sender() ! Write(data)
    }
    case PeerClosed => println("peer closed"); context stop self
  }
}

class NNQLServer(hostAddress: String, port: Int) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress(hostAddress, port))

  def receive = {
    case b @ Bound(hostAddress) => {}
    // do some logging or setup ...

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)
  }

}