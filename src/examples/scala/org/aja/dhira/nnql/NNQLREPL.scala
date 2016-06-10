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


import scala.sys.SystemProperties
import scala.tools.scalap.scalax.rules.Error
import scala.util.Failure

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop

/**
 * Created by mageswaran on 29/4/16.
 */

object NNQLREPL {

  implicit val consoleReader = new jline.console.ConsoleReader()
  //Creates a actor system
  val system =  ActorSystem("NNQLEngine")

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
    System.exit(0)
  }

  object IntrupterThread extends Runnable {
    override def run(): Unit = {
      console {
        case EOF =>
          consoleReader.println("Ctrl-d")
          System.exit(0)// "stop"
          true
        case Line(s) if s == "quit" =>
          System.exit(0)// "quit"
          true
        case Line(s) =>
          println("Processing...  " + s)
          NNQLParser.parseLine(s)
          false
        case _ =>
          false
      }
    }
  }

  def main(args: Array[String]) {
    val newThread = new Thread(IntrupterThread)
    newThread.run()
  }
}