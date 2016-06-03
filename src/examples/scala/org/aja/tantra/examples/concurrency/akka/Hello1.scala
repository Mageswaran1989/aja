package org.aja.tantra.examples.concurrency.akka

import akka.actor.{Actor, Props, ActorSystem}

/**
 * Created by mageswaran on 31/3/16.
 */
object Hello1 extends App {

  val system = ActorSystem("actor-demo-scala")
  val hello = system.actorOf(Props[Hello])
  hello ! "Bob"
  Thread sleep 1000
  system terminate()

  class Hello extends Actor {
    def receive = {
      case name: String => println(s"Hello $name")
    }
  }
}