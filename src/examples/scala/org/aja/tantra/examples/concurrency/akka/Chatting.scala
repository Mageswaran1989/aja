package org.aja.tantra.examples.concurrency.akka

import akka.actor._

/**
 * Created by mageswaran on 28/3/16.
 */

abstract class Msg

case class Send(msg: String) extends Msg
case class NewMsg(from: String, msg: String) extends Msg
case class Info(msg: String) extends Msg

case class Connect(username: String) extends Msg
case class Broadcast(msg: String) extends Msg
case object Disconnect extends Msg


//Connect(username) - Cool, someone is trying to connect to our chat!

//The first thing we do is to notify all the other clients that a new client has connected.

//As with Erlang, we use the exclamation point to send messages to other actors.

//Next we add the new client to our client list. sender is defined in the Actor trait and contains the
// ActorRef of the message sender.

//The last thing we do is to make the server start monitoring the client actor by calling watch.
// Doing this, the server actor will receive a Terminated message if the client actor is terminated.
// Pretty cool right?

//Broadcast(msg) - A new message for the chatroom!

//This case doesn't contain anything special.
// It just finds the username of the sender based on the ActorRef and then sends a NewMsg to all the
// clients connected to the server.

//Terminated(client) - Aww, someone disconnected...

//Since we registered for Terminated messages from clients when they connected,
// we need to handle them. In this case the server simply notifies the other clients
// that a client disconnected, then removes the client from the client list.


class ChatServer extends Actor {
  var clients = List[(String, ActorRef)]();

  def receive = {
    case Connect(username) => {
      broadcast(Info(f"$username%s joined the chat"))
      clients = (username,sender) :: clients
      context.watch(sender)
    }
    case Broadcast(msg) => {
      val username = getUsername(sender)
      broadcast(NewMsg(username, msg))
    }
    case Terminated(client) => {
      val username = getUsername(client)
      clients = clients.filter(sender != _._2)
      broadcast(Info(f"$username%s left the chat"))
    }
  }

  def broadcast(msg: Msg) {
    clients.foreach(x => x._2 ! msg)
  }

  def getUsername(actor: ActorRef): String = {
    clients.filter(actor == _._2).head._1
  }
}

//There isn't much actor related to tell that hasn't been told in the server part.
// However, there are a few things worth mentioning.

//On creation, the client sends a Connect message to the server.

//On Disconnect, the client sends a PoisonPill message to itself to trigger the termination.
//A Terminated message will then be published to DeathWatch, which will then notify our server actor.


class Client(val username: String, server: ActorRef) extends Actor {

  server ! Connect(username)

  def receive = {
    case NewMsg(from, msg) => {
      println(f"[$username%s's client] - $from%s: $msg%s")
    }
    case Send(msg) => server ! Broadcast(msg)
    case Info(msg) => {
      println(f"[$username%s's client] - $msg%s")
    }
    case Disconnect => {
      self ! PoisonPill
    }
  }
}

object Chatting extends App {
  val system = ActorSystem("System")
  val server = system.actorOf(Props[ChatServer], name= "chat")

  val c1 = system.actorOf(Props(new Client("Sam", server)))
  c1 ! Send("Hi, anyone here?")

  val c2 = system.actorOf(Props(new Client("Mia", server)))
  val c3 = system.actorOf(Props(new Client("Luke", server)))

  c2 ! Send("Hello")
  c3 ! Disconnect
}