package org.aja.dhira.nnql

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader
import java.lang.Error
;

import akka.actor.{Props, ActorSystem, Actor}
import akka.actor.Actor.Receive

import scala.tools.scalap.scalax.rules.Error
import scala.util.Failure

/**
 * Created by mageswaran on 29/4/16.
 */

class NNQLParser extends Actor {
  var i = 0d
  var exit = false
  var manipulate: Thread = null
  override def receive: Receive = {
    case "create" | "CREATE" => NNQLServer.start()
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
    case "exit" | "EXIT" => System.exit(0)
  }
}

object NNQLEngine {
  val system =  ActorSystem("actor-demo-scala")
  val nnqlParser = system.actorOf(Props[NNQLParser])

  object IntrupterThread extends Runnable {
    override def run(): Unit = {
      while (true) {
        System.out.print("nnql-server >> ")
        val br: BufferedReader = new BufferedReader(new InputStreamReader(System.in));
        val input: String = br.readLine();
        nnqlParser ! input

      }
    }
  }

  def main(args: Array[String]) {
    val newThread = new Thread(IntrupterThread)
    newThread.run()
  }
}


//https://github.com/p3t0r/scala-sql-dsl/blob/master/src/main/scala/com/log4p/sqldsl/AnsiSqlRenderer.scala
//https://ivanyu.me/blog/2015/10/18/type-safe-query-builders-in-scala/
//http://daily-scala.blogspot.in/2010/01/regular-expression-1-basics-and.html
//http://www.tutorialspoint.com/scala/scala_regular_expressions.htm