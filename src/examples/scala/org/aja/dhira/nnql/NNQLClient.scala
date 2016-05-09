package org.aja.dhira.nnql

import akka.actor.Actor.Receive
import akka.actor.{ Actor, ActorRef, Props }
import akka.io.Tcp.{Register, Connected, Write, Received}
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

/**
 * Created by mageswaran on 1/5/16.
 */
object NNQLClient {
  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[NNQLClient], remote, replies)
}

class NNQLClient(remote: InetSocketAddress, listener: ActorRef) extends Actor {

  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote) //Tcp.Bind(self, remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context stop self

    case c @ Connected(remote, local) =>
      listener ! c
      val connection = sender()
      connection ! Register(self)

      context become {
        case data: ByteString =>
          connection ! Write(data)
        case Received(data) =>
          listener ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          listener ! "connection closed"
        case string: String => println(string)
        case CommandFailed(w: Write) =>
          // O/S buffer was full
          listener ! "write failed"
          context stop self
      }
  }
}


class ClientUser extends Actor {
  override def receive: Receive = {
    case data1:String => println(data1)
    case data: ByteString => println("received data from server: " + data.utf8String);
    case Received(data) => println("Received")
    case c @ Connected(remote, local) =>
      val connection = sender()
      //connection ! ByteString("hello world")
      connection ! ByteString("create")
    case x => println("Got Some data -> " + x)
  }
}