package org.aja.tantra.examples.concurrency.threads

/**
 * Created by mageswaran on 27/3/16.
 */



object DeadLock extends App {
  val lock1: Object = new Object
  val lock2: Object = new Object

  class Person(var name: String) {
    def set(nm: String) = this.name = nm
  }

  class Runnable1(person: Person) extends Runnable {
    override def run(): Unit = {
      person.set("Thread1")
      lock1.synchronized{

      }
    }
  }

  class Runnable2(person: Person) extends Runnable {
    override def run(): Unit = {
      person.set("Thread2")
    }
  }


  val person = new Person("Aja")

  new Thread(new Runnable1(person)).start()
  new Thread(new Runnable2(person)).start()

  println(person.name)

}
