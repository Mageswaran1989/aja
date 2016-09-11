package org.dhira.nn.containers

/**
 * Created by mageswaran on 10/9/16.
 */

trait Factory[T] {
  def newInstance(args: AnyRef*): T
}

object Factory {

  class DefaultFactory[T] extends Factory[T] {
    private final var c: Class[_] = null

    def this(c: Class[_]) {
      this()
      this.c = c
    }

    def newInstance(args: AnyRef*): T = {
      try {
        return c.newInstance.asInstanceOf[T]
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }
      return null
    }
  }

}

