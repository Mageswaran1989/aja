package org.aja

import org.scalatest.FunSuite

/**
 * Created by mageswaran on 20/10/15.
 */
class FirstTestSuit extends FunSuite {

  test("very basic") {
    assert (1 == 1)
  }

  test ("another very basic test") {
    assert ("Hello" == "Hello")
  }

}
