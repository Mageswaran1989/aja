package org.aja.dhira.nnql


/**
 * Created by mageswaran on 29/4/16.
 */


import scala.language.implicitConversions

import scala.util.parsing.combinator.syntactical.{StandardTokenParsers, StdTokenParsers}
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.{JavaTokenParsers, PackratParsers}
import scala.util.parsing.input.CharArrayReader.EofCh
import org.aja.dhira.nnql.NNQLCommands._


trait DataTypeParser extends StandardTokenParsers {
}


/**
  * Abstract class covering the basic parser logic, inspired by AbstractSparkSQLParser
  */
abstract class NNQLParserBase extends StandardTokenParsers with PackratParsers {

  def parse(input: String): NNQlExpr = synchronized {
    // Initialize the Keywords.
    initLexical
    phrase(start)(new lexical.Scanner(input)) match {
      case Success(command, _) => command
      case failureOrError => sys.error(failureOrError.toString)
    }
  }
  // Set the keywords as empty by default, will change that later.
  override val lexical = new NNQLLexer

  /* One time initialization of lexical.This avoid reinitialization of  lexical in parse method */
  protected lazy val initLexical: Unit = lexical.initialize(reservedWords)

  protected case class Keyword(str: String) {
    def normalize: String = lexical.normalizeKeyword(str)
    def parser: Parser[String] = normalize
  }

  protected implicit def asParser(k: Keyword): Parser[String] = k.parser

  // By default, use Reflection to find the reserved words defined in the sub class.
  // NOTICE, Since the Keyword properties defined by sub class, we couldn't call this
  // method during the parent class instantiation, because the sub class instance
  // isn't created yet.
  protected lazy val reservedWords: Seq[String] =
    this
      .getClass
      .getMethods
      .filter(_.getReturnType == classOf[Keyword])
      .map(_.invoke(this).asInstanceOf[Keyword].normalize)

  //DSL grammer is defined here by inherited class
  protected def start: Parser[NNQlExpr]

  //Following snippet is Ctrl+ C & Ctrl + V from Spark code base
  //TODO: Understand the following code!
  // Returns the whole input string
  protected lazy val wholeInput: Parser[String] = new Parser[String] {
    def apply(in: Input): ParseResult[String] =
      Success(in.source.toString, in.drop(in.source.length()))
  }

  // Returns the rest of the input string that are not parsed yet
  protected lazy val restInput: Parser[String] = new Parser[String] {
    def apply(in: Input): ParseResult[String] =
      Success(
        in.source.subSequence(in.offset, in.source.length()).toString,
        in.drop(in.source.length()))
  }
}


class NNQLLexer extends StdLexical {

  /* This is a work around to support the lazy setting */
  def initialize(keywords: Seq[String]): Unit = {
    reserved.clear()
    reserved ++= keywords
  }

  /* Normal the keyword string */
  def normalizeKeyword(str: String): String = str.toLowerCase

  delimiters += (
    "@", "*", "+", "-", "<", "=", "<>", "!=", "<=", ">=", ">", "/", "(", ")",
    ",", ";", "%", "{", "}", ":", "[", "]", ".", "&", "|", "^", "~", "<=>"
    )

  protected override def processIdent(name: String) = {
    val token = normalizeKeyword(name)
    if (reserved contains token) Keyword(token) else Identifier(name)
  }

  //Following snippet is Ctrl+ C & Ctrl + V from Spark code base
  //TODO: Understand the following code!

  case class DecimalLit(chars: String) extends Token {
    override def toString: String = chars
  }

  override lazy val token: Parser[Token] =
    ( rep1(digit) ~ scientificNotation ^^ { case i ~ s => DecimalLit(i.mkString + s) }
      | '.' ~> (rep1(digit) ~ scientificNotation) ^^
      { case i ~ s => DecimalLit("0." + i.mkString + s) }
      | rep1(digit) ~ ('.' ~> digit.*) ~ scientificNotation ^^
      { case i1 ~ i2 ~ s => DecimalLit(i1.mkString + "." + i2.mkString + s) }
      | digit.* ~ identChar ~ (identChar | digit).* ^^
      { case first ~ middle ~ rest => processIdent((first ++ (middle :: rest)).mkString) }
      | rep1(digit) ~ ('.' ~> digit.*).? ^^ {
      case i ~ None => NumericLit(i.mkString)
      case i ~ Some(d) => DecimalLit(i.mkString + "." + d.mkString)
    }
      | '\'' ~> chrExcept('\'', '\n', EofCh).* <~ '\'' ^^
      { case chars => StringLit(chars mkString "") }
      | '"' ~> chrExcept('"', '\n', EofCh).* <~ '"' ^^
      { case chars => StringLit(chars mkString "") }
      | '`' ~> chrExcept('`', '\n', EofCh).* <~ '`' ^^
      { case chars => Identifier(chars mkString "") }
      | EofCh ^^^ EOF
      | '\'' ~> failure("unclosed string literal")
      | '"' ~> failure("unclosed string literal")
      | delim
      | failure("illegal character")
      )

  override def identChar: Parser[Elem] = letter | elem('_')

  private lazy val scientificNotation: Parser[String] =
    (elem('e') | elem('E')) ~> (elem('+') | elem('-')).? ~ rep1(digit) ^^ {
      case s ~ rest => "e" + s.mkString + rest.mkString
    }

  override def whitespace: Parser[Any] =
    ( whitespaceChar
      | '/' ~ '*' ~ comment
      | '/' ~ '/' ~ chrExcept(EofCh, '\n').*
      | '#' ~ chrExcept(EofCh, '\n').*
      | '-' ~ '-' ~ chrExcept(EofCh, '\n').*
      | '/' ~ '*' ~ failure("unclosed comment")
      ).*
}

/**
 * Parser using StdTokenParsers
 */
class NNQLParser extends NNQLParserBase  {

  // Keyword is a convention with NNQLParserBase, which will scan all of the `Keyword`
  // properties via reflection the class in runtime for constructing the SqlLexical object
  protected val CREATE = Keyword("CREATE")
  protected val NEURONS = Keyword("NEURONS")
  protected val LAYER = Keyword("LAYER")
  protected val AS = Keyword("AS")
  protected val INTERCONNECTION = Keyword("INTERCONNECTION")
  protected val WITH = Keyword("WITH")
  protected val LOAD = Keyword("LOAD")
  protected val DATA = Keyword("DATA")
  protected val FROM = Keyword("FROM")
  protected val PRINT = Keyword("PRINT")


  def start: Parser[NNQlExpr] = createNeuronWithInterConnection | createLayer | createNeuron | loadData

  //To parse line like: CREATE 10 NEURONS
  lazy val createNeuron: Parser[NNQlExpr] = CREATE ~> numericLit <~ NEURONS ^^  {
    case number => CreateNeurons(number.toLong,false)
  }

  //To parse line like: CREATE 10 NEURONS WITH INTERCONNECTION
  lazy val createNeuronWithInterConnection: Parser[NNQlExpr] = CREATE ~> numericLit <~ NEURONS ~ WITH ~ INTERCONNECTION ^^  {
    case number => CreateNeurons(number.toLong,true)
  }

  //To parse line like: CREATE LAYER WITH 10 NEURONS
  lazy val createLayer: Parser[NNQlExpr] = CREATE ~ LAYER ~ WITH ~> numericLit <~ NEURONS ^^ {
    case numNeurons => CreateLayer(numNeurons.toLong)
  }

  //To parse line like: LOAD DATA FROM path_to_csv AS name
  lazy val loadData: Parser[NNQlExpr] = LOAD ~ DATA ~ FROM ~> ident ~ opt(AS ~> ident) ^^ {
    case path ~ refName => LoadData(path, refName)
  }

}


object NNQLParser {
  val nnqlParser = new NNQLParser
  def parseLine(s: String) = {
    println(nnqlParser.parse(s))
  }
}


