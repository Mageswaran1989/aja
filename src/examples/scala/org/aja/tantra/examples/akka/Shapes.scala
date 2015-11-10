package org.aja.tantra.examples.akka

import akka.actor.{Props, ActorSystem, Actor}


/**
 * Created by mageswaran on 7/11/15.
 */

class Point(x: Int, y: Int)  {
  override def toString = "Point(" + x + "," + y + ")"
}

abstract class Shape {
  def draw(): String //abstract methods doesn't need explicit override
}

class Circle(p: Point, radius: Int) extends Shape {
  def draw = this.toString
  override def toString = "Circle(" + p + "," + radius +")"
}

class Rectangle(lowerPoint : Point, width: Int, height: Int) extends Shape {
  def draw = "" + this
  override def toString = "Rectangle(" + lowerPoint + "," + width + "," + height + ")"
}

class ShapesActor extends Actor {
   def receive: Receive = {
    case s: Shape => println("Drawing" + s.draw)
    case "exit"  => println("Bye bye..."); System.exit(0)
    case any: Any => println("Invalid object")
  }
}

object Shapes extends App {
 val actorSystem = ActorSystem("TestShapes")
  val shapeActor = actorSystem.actorOf(Props[ShapesActor], name= "shapes")

  shapeActor ! new Circle(new Point(1,2), 5)
  shapeActor ! new Rectangle(new Point(0,0), 5, 5)

  shapeActor ! 1231243
  shapeActor ! "exit"

}
