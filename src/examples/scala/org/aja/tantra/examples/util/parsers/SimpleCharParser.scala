package org.aja.tantra.examples.util.parsers

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.CharSequenceReader


/**
 * Created by mageswaran on 28/5/16.
 * Reference: http://henkelmann.eu/2011/01/13/an_introduction_to_scala_parser_combinators
 */

trait Ab01Parsers extends Parsers {
  type Elem = Char

  //though it is a function, we actually declare it like an
  //object, as we need to inherit all the other goodies
  //from the base Parser. We return a char wrapped in a
  //ParseResult if successful, so the type is Parser[Char]
  val aParser = new Parser[Char] {
    def apply(in:Input):ParseResult[Char] = {
    //"first" returns the first (duh!) element the reader
    //provides for us. If there are no more, the result depends on the
    //reader. Preferably some special token indicating the end of input
    //is returned. CharSequenceReaders return a
    val c = in.first
    //Hooray, we parsed a char. Now we need to provide input
    //for all following Parsers. "rest" does that for us. It
    //returns everything *but* first
    if (c == 'a') Success(c, in.rest)
    //                            ↑
    //Oh Noez, parsing failed. We provide a useful message and
    //the input the next Parser should continue with: the same
    //as we got, i.e. the next Parser will just try and pick off
    //where we failed.
    else Failure("Expected 'a' got '" + c + "'", in)
    //
    }
  }

  //note that this is now not a single object/function
  //but a function that returns a Parser based on the given
  //argument (a function returning a function)
  def charParser(expected:Char) = new Parser[Char] {
    def apply(in:Input):ParseResult[Char] = {
      val c = in.first
      if (c == expected) Success(c, in.rest)
      else Failure("Expected '" + expected + "' got '" + c + "'", in)
    }
  }

  abstract class Parser[T] extends super.Parser[T] {
    def or(right:Parser[T]):Parser[T] = {
      val left = this
      new Parser[T] {
        def apply(in:Input) =
          left(in) match {
            case s:Success[T] => s
            case _            => right(in)
          }
      }
    }
  }

  def ab01 = charParser('a') or charParser ('b') or charParser('0') or charParser ('1')

  def repeat[T](p:Parser[T]) = new Parser[List[T]] {
    def apply(in:Input):Success[List[T]] = {
      //apply the given Parser
      p (in) match {
        //if that succeeded, recurse and prepend the result
        case Success(t, next) => val s = apply(next)
          Success(t::s.get, s.next)
        //if it failed, end recursion and return the empty list
        case _                => Success(Nil, in)
      }
    }
  }


  val myParser = repeat(ab01)

  def run(s:String):Option[List[Char]] = {
    //wrap our input into a reader
    val input = new CharSequenceReader(s)
    //run the Parser on the input
    myParser(input) match {
      //if all input has been successfully consumed, return result
      //this checks if all input is gone   ↓
      case Success(list, next) if (next.atEnd) => Some(list)
      //either an error or there is still input left
      case _                                   => None
    }
  }
}

trait Ab01ProperParsers extends Parsers {
  type Elem = Char
  //"elem" parses exactly one element of the defined "Elem" type.
  //In our case this is a Char, just like our manual Parser.
  //The "|" Combinator is the same as our "or" Combinator
  //so this reads "element 'a' or element 'b' or ..."
  val ab01:Parser[Char] = elem('a') | elem('b') | elem('0') | elem('1')
  //The postfix "*" Combinator repeats the Parser it as applied to as often as possible.
  //The asterisk was chosen as this works like the Kleene Star
  val myParser:Parser[List[Char]] = ab01*
}

object SimpleCharParser extends Ab01Parsers {

  def main(args: Array[String]) {
    println (run("ab01"))

    println (run("ab012345"))
  }

}
