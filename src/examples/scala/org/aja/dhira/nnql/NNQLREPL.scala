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

  val consoleReader = new jline.console.ConsoleReader()
  val prettyPrinter = new PrettyPrinter
  val parser = new QLParser()

  def main(args: Array[String]) {
    while (true) {
      val input = consoleReader.readLine("λ>>>\n")
      consoleReader.setCopyPasteDetection(true)
        handleExpr(input)
    }
  }

  def handleExpr(input: String) =
    parseInput(parser.parse, input) { expr =>

    }

  def parseInput[T](p: String => parser.ParseResult[T], input: String)(success: T => Unit): Unit = {
    import parser.{ Success, NoSuccess }
    p(input) match {
      case Success(res, _) => success(res)
      case NoSuccess(err, _) => println(err)
    }
  }
}