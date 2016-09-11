package org.dhira.nn.containers

/**
 * Created by mageswaran on 10/9/16.
 */
trait Filter[T] {
  def accept (t: T): Boolean
}
