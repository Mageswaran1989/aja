#!/bin/sh
exec scala "$0" "$@"
!#

trait Friendly {
  def greet() = "Hi"
}

trait ExclamatoryGreeter extends Friendly {
  override def greet() = super.greet() + "!"
}

class Dog extends Friendly {
  override def greet() = "Woof"
}

class Cat extends Friendly {
  override def greet() = "Meow"
}

class HungryDog extends Friendly {
  override def greet() ="I would like eat my own dog food"
}

//Dynamic binding
val dog: Friendly = new Dog
val cat: Friendly = new Cat with ExclamatoryGreeter
val hungryDog = new HungryDog with ExclamatoryGreeter
println(dog.greet)
println(cat.greet())
println(hungryDog.greet())
