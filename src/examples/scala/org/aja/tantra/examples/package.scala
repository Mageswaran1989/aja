package org.aja.tantra

/**
 * Created by mageswaran on 12/11/15.
 */

//Scala version 2.8 introduces a new scoping construct called package objects. They are
//used to define types, variables, and methods that are visible at the level of the corre-
//sponding package

//One of the notable use case for this is when scala.List was moved to  scala.collection.immutable
//Despite the change, List is made visible in the scala package using package object scala, found
//in the src/library/scala/package.scala

//Finally, another benefit of package objects is the way they provide a clear separation
//between the abstractions exposed by a package and the implementations that should
//be hidden inside it. In a larger application, a package object could be used to expose
//all the public types, values, and operations (methods) for a “component,” while ev-
//erything else in the package and nested packages could be treated as internal imple-
//mentation details.

package object examples {

}
