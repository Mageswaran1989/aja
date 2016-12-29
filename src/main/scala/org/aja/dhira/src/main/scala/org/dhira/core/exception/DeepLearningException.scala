/*
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */
package org.dhira.core.exception

//case class DeepLearningException extends Exception
//
//
//case class DeepLearningException(message: String,
//                                 cause: Throwable,
//                                 enableSuppression: Boolean = true,
//                                 writableStackTrace: Boolean = false)
//  extends Exception(message, cause, enableSuppression, writableStackTrace)
//
//case class DeepLearningException(message: String, cause: Throwable)  extends Exception(message, cause)
//
//
//case class DeepLearningException(message: String)  extends Exception (message)
//
//
//case class DeepLearningException(cause: Throwable)  extends Exception(cause)

@SerialVersionUID(-7973589163269627293L)
case class DeepLearningException private(message: String = "",
                                    cause: Throwable = null,
                                    enableSuppression: Boolean = false,
                                    writableStackTrace: Boolean = false)
  extends Exception(message,
                     cause,
                     enableSuppression,
                     writableStackTrace)

// {

//  def this(message:String) = this(message, null, false, false)
//  def this(cause:Throwable) =this("", cause, false, false)
//  def this(message:String, throwable: Throwable) = this()
//  protected def this(message: String,
//                     cause: Throwable,
//                     enableSuppression: Boolean,
//                     writableStackTrace: Boolean) = this(message, cause,
//    enableSuppression, writableStackTrace)
//
//}
//
//object DeepLearningException {
//  def apply(message:String) = new DeepLearningException(message)
//  def apply(cause:Throwable) = new DeepLearningException(cause)
//  def apply(message:String, cause: Throwable) = new DeepLearningException(message, cause)
//  def apply(message: String,
//            cause: Throwable,
//            enableSuppression: Boolean,
//            writableStackTrace: Boolean) = new DeepLearningException(message, cause, enableSuppression, writableStackTrace)
//}

