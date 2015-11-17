package org.aja.tantra.examples.classes

import org.aja.tantra.examples.classes.scopeA.PublicClass1
import org.aja.tantra.examples.classes.scopeB.DoesSomething

/**
 * Created by mageswaran on 10/11/15.
 */

package scopeA {
  class PublicClass1 {
    val publicField = 1

    class NestedClass {
      val nestedField = 1
    }
  }

  class PublicClass2 extends PublicClass1{
    val publicField2 = publicField  + 1
    val nestedField = new NestedClass().nestedField
  }
}

package scopeB {
  class PublicClass1B extends scopeA.PublicClass1

  class DoesSomething(val publicClass: scopeA.PublicClass1) {
    def method: String = "In class DoesSomething \n" + "publicFiled :" + publicClass.publicField +
                 "\nnested field: " + new publicClass.NestedClass().nestedField
  }
}
object PublicVisibility extends App{

  println(new DoesSomething(new PublicClass1).method)
}
