package org.aja.tantra.examples.patterns

/**
 * Created by mageswaran on 9/11/15.
 */

//https://en.wikipedia.org/wiki/Observer_pattern

abstract class Widget

class Button(name: String) extends Widget {
  def click() : Unit = {
    println("Button Clicked!")
  }
}

////////////////////////////////////////////////////////////////////////

trait Subject {
  //Type with notify method
  //No need of defining an interface/abstract class, jus say observer is on ewith notify methos in it
  type Observer = { def notify(subject: Any): Unit}

  private var observers: List[Observer] = List()
  def registerObservers(observer: Observer) = observers ::= observer
  //observers = observer :: observers
  //observers = observers.::(observer)
  def unregisterObserver(observer: Observer) = observers.filter(_ != observer)

  def notifyAllObservers = observers.foreach(_.notify(this))

}

////////////////////////////////////////////////////////////////////////

class ObservableButtons(name: String) extends Button(name) with Subject {
  //  Since the new click method overrides
  //  Button’s concrete implementation, the override keyword is required.
  override def click() : Unit = {
    super.click()
    notifyAllObservers
  }
}

////////////////////////////////////////////////////////////////////////

trait Clickable {
  def click()
}

class Button1(name: String) extends Widget with Clickable{
  def click() : Unit = {
    println("Button Clicked!")
  }
}

//Except for declaring abstract classes, the abstract keyword is only re-
//quired on a method in a trait when the method has a body, but it calls
//the super method that doesn’t have a concrete implementation in
//parents of the trait.
trait ObseravableClicks extends Clickable with Subject {
  abstract override def click() = {
    super.click()
    notifyAllObservers
  }
}

trait VetoableClick extends Clickable {
  var count = 0
  val maxAllowed = 1

  abstract override def click() = {
    if(count < maxAllowed) {
      count += 1
      super.click()
    }
  }
}
/////////////////////////////////////////////////////////////////////////

class ButtonCountObserver {
  var count = 0
  def notify(subject: Any) = count += 1
}

//////////////////////////////////////////////////////////////////////////

object ObserverPattern {

  def main(args: Array[String]) {

    val observableButton = new ObservableButtons("Okay")
    val buttonCountObserver = new ButtonCountObserver
    observableButton.registerObservers(buttonCountObserver)

    for (i <- 1 to 3)
      observableButton.click()

    println("0.Count should be 3: " + buttonCountObserver.count)

    println("///////////////////////////////////////////////////////////////////")

    val observableButton1 = new Button("Okay") with Subject { //Mixin the functionality while creating the instances
      override def click() = {
        super.click()
        notifyAllObservers
      }
    }
    val buttonCountObserver1 = new ButtonCountObserver
    observableButton1.registerObservers(buttonCountObserver1)

    for (i <- 1 to 3)
      observableButton1.click()

    println("1. Count should be 3: " + buttonCountObserver1.count)

    println("///////////////////////////////////////////////////////////////////")

    val observableButton2 = new Button1("Okay") with ObseravableClicks

    val buttonCountObserver2 = new ButtonCountObserver
    observableButton2.registerObservers(buttonCountObserver2)

    for (i <- 1 to 3)
      observableButton2.click()

    println("2. Count should be 3: " + buttonCountObserver2.count)

    println("///////////////////////////////////////////////////////////////////")

    val observableButton3 = new Button1("Okay") with ObseravableClicks with VetoableClick

    val buttonCountObserver3 = new ButtonCountObserver
    observableButton3.registerObservers(buttonCountObserver3)

    for (i <- 1 to 3)
      observableButton3.click()

    println("3. Count should be 3: " + buttonCountObserver3.count)

    println("///////////////////////////////////////////////////////////////////")

    val observableButton4 = new Button1("Okay")with VetoableClick  with ObseravableClicks

    val buttonCountObserver4 = new ButtonCountObserver
    observableButton4.registerObservers(buttonCountObserver4)

    for (i <- 1 to 3)
      observableButton4.click()

    println("4. Count should be 3: " + buttonCountObserver4.count)

  }
}
