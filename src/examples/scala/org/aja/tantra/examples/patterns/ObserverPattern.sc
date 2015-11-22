/**
 * Created by mageswaran on 9/11/15.
 *
 * This example starts with simple Observer pattern, then moves on to use mixins and to use abstract
 * subject with functional way
 */

//https://en.wikipedia.org/wiki/Observer_pattern


//Simple classes for simulating GUI components
abstract class Widget

class Button(name: String) extends Widget {
  def click() : Unit = {
    println("Button Clicked!")
  }
}

////////////////////////////////////////////////////////////////////////

//Trait/Interface defined for Subject in the Observer pattern

trait Subject {
  //Type with notify method
  //No need of defining an interface/abstract class, jus say observer is one with notify method in it
  type Observer = { def onReceiveUpdate(subject: Any): Unit}

  private var observers: List[Observer] = List()
  def registerObservers(observer: Observer) = observers ::= observer
                                          //observers = observer :: observers
                                         //observers = observers.::(observer)
  def unregisterObserver(observer: Observer) = observers.filter(_ != observer)

  def notifyAllObservers = observers.foreach(_.onReceiveUpdate(this))

}

////////////////////////////////////////////////////////////////////////

//Concrete implementation of the Subject
class ObservableButtons(name: String) extends Button(name) with Subject {
  //  Since the new click method overrides
  //  Button’s concrete implementation, the override keyword is required.
  override def click() : Unit = {
    super.click()
    notifyAllObservers
  }
}

//////////////////////////////////////////////////////////////////////////

//A concrete observer which does some work on receiving the data
class ButtonCountObserver {
  var count = 0
  def onReceiveUpdate(subject: Any) = count += 1
}

////////////////////////////////////////////////////////////////////////

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
/////////////////////////////////////////////////////////////////////////

//Why to keep the "click" events binded to the button, lets make it a module
trait Clickable {
  def click()
}
//Button1 that also has "click" event, but is not of its own!
class Button1(name: String) extends Widget with Clickable{
  //No override keyword required for abstract method
  def click() : Unit = {
    println("Button Clicked!")
  }
}

//Except for declaring abstract classes, the abstract keyword is only re-
//quired on a method in a trait when the method has a body, but it calls
//the super method that doesn’t have a concrete implementation in
//parents of the trait.
trait ObseravableClicks extends Clickable with Subject {
  //abstract here says, so far we don't have any concrete implementation yet
  abstract override def click() = {
    super.click()
    notifyAllObservers
  }
}

val observableButton2 = new Button1("Okay") with ObseravableClicks

val buttonCountObserver2 = new ButtonCountObserver
observableButton2.registerObservers(buttonCountObserver2)

for (i <- 1 to 3)
  observableButton2.click()

println("2. Count should be 3: " + buttonCountObserver2.count)

println("///////////////////////////////////////////////////////////////////")
/////////////////////////////////////////////////////////////////////////
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
println("///////////////////////////////////////////////////////////////////")

/////////////////////////////////////////////////////////////////////////
//AbstractSubject is more reusable than the original definition of Subject, because it
//  imposes fewer constraints on potential observers.

trait AbstractSubject {
  //Let user define how their observer should be
  type ObserverType
  private var observers: List[ObserverType] = List[ObserverType]()
  def registerObservers(observer: ObserverType) = observers ::= observer
  def notifyAllObservers = observers.foreach(notifyEach(_))
  //User needs to decide what to do with each observer
  def notifyEach(observer: ObserverType): Unit
}

trait SubjectWithMethod extends AbstractSubject {
  type ObserverType = { def onReceiveUpdate(subject: Any) }

  def notifyEach(observer: ObserverType): Unit = observer.onReceiveUpdate(this)
}
trait SubjectFuntionlaWay extends AbstractSubject {
  //Function literal that takes AbstractSubject as a argument and returns unit/void
  type ObserverType = (AbstractSubject) => Unit

  def notifyEach(observer: ObserverType) = observer(this)
}

/////////////////////////////////////////////////////////////////////////

val observableButton5 = new Button("Okay") with SubjectWithMethod { //Mixin the functionality while creating the instances
override def click() = {
  super.click()
  notifyAllObservers
}
}
val buttonCountObserver5 = new ButtonCountObserver
observableButton5.registerObservers(buttonCountObserver5)

for (i <- 1 to 3)
  observableButton5.click()

println("5. Count should be 3: " + buttonCountObserver5.count)

///////////////////
val observableButton6 = new Button("Okay") with SubjectFuntionlaWay { //Mixin the functionality while creating the instances
override def click() = {
  super.click()
  notifyAllObservers
}
}
var count = 0
//                                    closure
observableButton6.registerObservers((button) => count += 1)

for (i <- 1 to 3)
  observableButton6.click()
println("6. Count should be 3: " + count)