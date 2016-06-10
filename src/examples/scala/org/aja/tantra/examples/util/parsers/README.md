There are several different parser traits and base classes for different purposes.

The main trait is scala.util.parsing.combinator.Parsers. This has most of the main combinators like opt, rep, elem, accept, etc. 
Definitely look over the documentation for this one, since this is most of what you need to know. The actual Parser class is 
defined as an inner class here, and that's important to know about, too.

Another important trait is scala.util.parsing.combinator.lexical.Scanners. This is the base trait for parsers which read a stream of 
characters and produce a stream of tokens (also known as lexers). In order to implement this trait, you need to implement a whitespace parser, 
which reads whitespace characters, comments, etc. You also need to implement a token method, which reads the next token. 
Tokens can be whatever you want, but they must be a subclass of Scanners.Token. Lexical extends Scanners and StdLexical extends Lexical. 
The former provides some useful basic operations (like digit, letter), while the latter actually defines and lexes common tokens 
(like numeric literals, identifiers, strings, reserved words). You just have to define delimiters and reserved, and you will get something 
useful for most languages. The token definitions are in scala.util.parsing.combinator.token.StdTokens.

Once you have a lexer, you can define a parser which reads a stream of tokens (produced by the lexer) and generates an abstract syntax tree. 
Separating the lexer and parser is a good idea since you won't need to worry about whitespace or comments or other complications in your syntax. 
If you use StdLexical, you may consider using scala.util.parsing.combinator.syntax.StdTokenPasers which has parsers built in to translate tokens 
into values (e.g., StringLit into String). I'm not sure what the difference is with StandardTokenParsers. If you define your own token classes, 
you should just use Parsers for simplicity.

You specifically asked about RegexParsers and JavaTokenParsers. RegexParsers is a trait which extends Parsers with one additional combinator: 
regex, which does exactly what you would expect. Mix in RegexParsers to your lexer if you want to use regular expressions to match tokens. 
JavaTokenParsers provides some parsers which lex tokens from Java syntax (like identifiers, integers) but without the token baggage of Lexical 
or StdLexical.

To summarise, you probably want two parsers: one which reads characters and produces tokens, and one which takes tokens and produces an AST. 
Use something based on Lexical or StdLexical for the first. Use something based on Parsers or StdTokenParsers for the second depending on 
whether you use StdLexical.