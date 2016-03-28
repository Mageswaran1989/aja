package org.aja.tantra.examples.concurrency.threads

import java.net.{Socket, ServerSocket}
import java.util.concurrent.{Executors, ExecutorService}
import java.util.Date

/**
 * Created by mageswaran on 27/3/16.
 */

class NetworkServiceE(port: Int, poolSize: Int) extends Runnable {
  val serverSocket = new ServerSocket(port)

  def run() {
    while (true) {
      // This will block until a connection comes in.
      val socket = serverSocket.accept()
      (new Thread(new Handler(socket))).start()
    }
  }
}

class HandlerE(socket: Socket) extends Runnable {
  def message = (Thread.currentThread.getName() + "\n").getBytes

  def run() {
    socket.getOutputStream.write(message)
    socket.getOutputStream.close()
  }
}


object SocketThread extends App {
  (new NetworkServiceE(2020, 2)).run
}
