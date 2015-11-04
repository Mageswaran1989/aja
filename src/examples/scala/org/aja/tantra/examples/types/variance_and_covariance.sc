

class GrandParent
class Parent extends GrandParent
class Child extends Parent 
class InvariantClass[A] //I am an orphan, always single man army
class CovariantClass[+A] //Let my childrens enjoy my property
class ContraVariantClass[-A] //I dont want any of child to bare my burden

def invariantMethod(obj: InvariantClass[Parent]) {}
def covariantClass(obj: CovariantClass[Parent]) {}
def contravariantClass(obj: ContraVariantClass[Parent]) {}

// invariantMethod(new InvariantClass[Child]) No Inherited class
invariantMethod(new InvariantClass[Parent])
//invariantMethod(new InvariantClass[GrandParent]) No parent class

covariantClass(new CovariantClass[Child])
covariantClass(new CovariantClass[Parent])
//covariantClass(new CovariantClass[GrandParent]) //No parent class

//contravariantClass(new ContraVariantClass[Child]) //No Inherited Class
contravariantClass(new ContraVariantClass[Parent])
contravariantClass(new ContraVariantClass[GrandParent])











println("//////////////////////////TODO: traits////////////////////////////")
trait Vehicle {
  val DIESEL: Int = 1
  val PETROL: Int = 2
  val WATER: Int = 3
  //Change the val -> var and see what happens?
  //Cannnot override mutable variables
  val numOfWheels: Int = 2
  val vehicleType: Int = DIESEL
  val canFly: Boolean = false
  val numPassengers: Int = 2
}

class Bullet extends Vehicle {
  override val numPassengers = 1
}
class Car extends Vehicle {
  override val numOfWheels = 4
  override val vehicleType = PETROL
  override val canFly = false
  override val numPassengers = 4
}

val bullet = new Bullet
bullet.numOfWheels
bullet vehicleType ; //added to remove misc error
bullet.numPassengers
bullet.canFly
val car = new Car
car.numOfWheels
car vehicleType ;
car.numPassengers
car.canFly
class AjaV extends Car {
  override val numOfWheels = 4
  override val vehicleType = WATER
  override val canFly = true
  override val numPassengers = 8
}
class Avenger extends Bullet {
  override val numPassengers = 2
  override val vehicleType = PETROL
}

//Now lets design a class that prints all the details for passed class object
