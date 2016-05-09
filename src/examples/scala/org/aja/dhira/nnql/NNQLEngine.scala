package org.aja.dhira.nnql

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader
import java.lang.Error
import java.net.InetSocketAddress
;

import akka.actor.{Props, ActorSystem, Actor}
import akka.actor.Actor.Receive
import akka.util.ByteString

import scala.tools.scalap.scalax.rules.Error
import scala.util.Failure

/**
 * Created by mageswaran on 29/4/16.
 */

class NNQLParser extends Actor {
  var i = 0d
  var exit = false
  var manipulate: Thread = _
  override def receive: Receive = {
    case "create" | "CREATE" => println("...")//NNQLServer.start()
    case "start" | "START" => {
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
    case "stop" => exit = true
    case "exit" | "EXIT" => context stop self
    case "shutdown" | "SHUTDOWN" => System.exit(0)
  }
}

object NNQLEngine {

  implicit val consoleReader = new jline.console.ConsoleReader()
  //Creates a actor system
  val system =  ActorSystem("NNQLEngine")

  //Creates a actor or a actor handle to send message
  val nnqlParser = system.actorOf(Props[NNQLParser])

  sealed trait JLineEvent
  case class Line(value: String) extends JLineEvent
  case object EmptyLine extends JLineEvent
  case object EOF extends JLineEvent

  def console( handler: JLineEvent => Boolean ) {

    var finished = false
    while (!finished) {
      consoleReader.setPrompt("nnql> ");
      val line = consoleReader.readLine()
      if (line == null) {
        finished = handler(EOF)
      } else if (line.size == 0) {
        finished = handler(EmptyLine)
      } else if (line.size > 0) {
        finished = handler(Line(line))
      }
    }
  }

  object IntrupterThread extends Runnable {
    override def run(): Unit = {
        console {
          case EOF =>
            consoleReader.println("Ctrl-d")
            nnqlParser ! "exit"
            true
          case Line(s) if s == "q" =>
            nnqlParser ! "shutdown"
            true
          case Line(s) =>
            nnqlParser ! s
            false
          case _ =>
            false
        }


    }
  }

  def main(args: Array[String]) {
        val newThread = new Thread(IntrupterThread)
        newThread.run()
    //    val server = system.actorOf(Props(new NNQLServer("localhost", 9999)))
    //
    //    val clientUser = system.actorOf(Props(new ClientUser))
    //
    //    val client = system.actorOf(Props(new NNQLClient(new InetSocketAddress("localhost", 9999), clientUser)))



    //    scala.io.StdIn.readLine(s"Hit ENTER to exit ...${System.getProperty("line.separator")}")
    //    system.terminate()
  }
}


//https://github.com/p3t0r/scala-sql-dsl/blob/master/src/main/scala/com/log4p/sqldsl/AnsiSqlRenderer.scala
//https://ivanyu.me/blog/2015/10/18/type-safe-query-builders-in-scala/
//http://daily-scala.blogspot.in/2010/01/regular-expression-1-basics-and.html
//http://www.tutorialspoint.com/scala/scala_regular_expressions.htm