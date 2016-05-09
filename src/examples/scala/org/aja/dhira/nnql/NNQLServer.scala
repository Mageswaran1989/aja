package org.aja.dhira.nnql

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

class SimplisticHandler extends Actor {
  import Tcp._
  def receive = {
    case Received(data) =>  data.utf8String match {
      case "create" | "CREATE" => sender() ! Write(ByteString("New thread started"))
      case _ => print(data.utf8String); sender() ! Write(data)
    }
    case PeerClosed     => context stop self
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







//import java.net.{Socket, ServerSocket}

/**
 * Created by mageswaran on 1/5/16.
 */
//object NNQLServer extends Thread("Server") {
//
//  val serverSocket = new ServerSocket(4567)
//  override def run(): Unit = {
//    // This will block until a connection comes in.
//    val socket = serverSocket.accept()
//    new Thread(new HandlerE(socket))
//  }
//}
//
//class HandlerE(socket: Socket) extends Runnable {
//  def message = (Thread.currentThread.getName() + "\n").getBytes
//
//  def run() {
//    socket.getOutputStream.write(message)
//    socket.getOutputStream.close()
//  }
//}