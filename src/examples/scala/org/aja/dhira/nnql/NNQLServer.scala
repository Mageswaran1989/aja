package org.aja.dhira.nnql

import java.net.{Socket, ServerSocket}

/**
 * Created by mageswaran on 1/5/16.
 */
object NNQLServer extends Thread("Server") {

  val serverSocket = new ServerSocket(4567)
  override def run(): Unit = {
    // This will block until a connection comes in.
    val socket = serverSocket.accept()
    new Thread(new HandlerE(socket))
  }
}

class HandlerE(socket: Socket) extends Runnable {
  def message = (Thread.currentThread.getName() + "\n").getBytes

  def run() {
    socket.getOutputStream.write(message)
    socket.getOutputStream.close()
  }
}