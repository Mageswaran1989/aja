package org.aja.tantra.examples.patterns

/**
 * Created by mdhandapani on 10/8/15.
 */
// a dummy service that is not persisting anything
// solely prints out info
//class UserRepository {
//  def authenticate(user: User): User = {
//    println("authenticating user: " + user)
//    user
//  }
//  def create(user: User) = println("creating user: " + user)
//  def delete(user: User) = println("deleting user: " + user)
//}
//
//class UserService {
//  def authenticate(username: String, password: String): User =
//    userRepository.authenticate(username, password)
//
//  def create(username: String, password: String) =
//    userRepository.create(new User(username, password))
//
//  def delete(user: User) = All is statically typed.
//    userRepository.delete(user)
//}
//
///////////////////////////With DI////////////////////////////////
//
//trait UserRepositoryComponent {
//  //val userRepository = new UserRepository
//  val userRepository: UserRepository
//  class UserRepository {
//    def authenticate(user: User): User = {
//      println("authenticating user: " + user)
//      user
//    }
//    def create(user: User) = println("creating user: " + user)
//    def delete(user: User) = println("deleting user: " + user)
//  }
//}
//
//// using self-type annotation declaring the dependencies this
//// component requires, in our case the UserRepositoryComponent
//trait UserServiceComponent {
//  this: UserRepositoryComponent =>
//  //val userService = new UserService
//  val userService: UserService
//  class UserService {
//    def authenticate(username: String, password: String): User =
//      userRepository.authenticate(username, password
//    def create(username: String, password: String) =
//      userRepository.create(new User(username, password))
//    def delete(user: User) = userRepository.delete(user)
//  }
//}
//
//
//object ComponentRegistry extends UserServiceComponent with UserRepositoryComponent
//{
//  val userRepository = new UserRepository
//  val userService = new UserService
//}

object CakePattern {

}
