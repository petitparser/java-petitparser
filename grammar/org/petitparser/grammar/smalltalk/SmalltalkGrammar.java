/**
 *
 */
package org.petitparser.grammar.smalltalk;

import org.petitparser.parser.Parser;
import org.petitparser.tools.CompositeParser;
import org.petitparser.tools.Production;

/**
 * Smalltalk grammar definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SmalltalkGrammar extends CompositeParser {

  /**
   * The the kind of parser.
   */
  public enum Kind {
    METHOD, EXPRESSION
  }

  private final Kind kind;

  public SmalltalkGrammar(Kind kind) {
    this.kind = kind;
  }

  @Override
  protected Parser start() {

  }

  @Production
  Parser startMethod() {
    return null;
  }

  @Production
  Parser startExpression() {
    return null;
  }

  // !PPSmalltalkGrammar methodsFor: 'accessing' stamp: 'lr 2/19/2010 08:05'!
  // start
  // "Default start production."
  //
  // ^ startMethod! !
  //
  // !PPSmalltalkGrammar methodsFor: 'accessing' stamp: 'lr 2/19/2010 08:03'!
  // startExpression
  // "Start production for the expression."
  //
  // ^ sequence end! !
  //
  // !PPSmalltalkGrammar methodsFor: 'accessing' stamp: 'lr 2/19/2010 08:03'!
  // startMethod
  // "Start production for the method."
  //
  // ^ method end! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 8/14/2011 11:30'!
  // array
  // ^ ${ asParser smalltalkToken , (expression delimitedBy: periodToken)
  // optional , $} asParser smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
  // assignment
  // ^ variable , assignmentToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
  // expression
  // ^ assignment star , cascadeExpression! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 5/3/2010 21:46'!
  // literal
  // ^ numberLiteral / stringLiteral / charLiteral / arrayLiteral / byteLiteral
  // / symbolLiteral / nilLiteral / trueLiteral / falseLiteral! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
  // message
  // ^ keywordMessage / binaryMessage / unaryMessage! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
  // method
  // ^ methodDeclaration , methodSequence! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
  // methodDeclaration
  // ^ keywordMethod / unaryMethod / binaryMethod! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 8/14/2011 11:23'!
  // methodSequence
  // ^ periodToken star , pragmas , periodToken star , temporaries , periodToken
  // star , pragmas , periodToken star , statements! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 6/29/2010 14:30'!
  // parens
  // ^ $( asParser smalltalkToken , expression , $) asParser smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 6/29/2010 14:30'!
  // pragma
  // ^ $< asParser smalltalkToken , pragmaMessage , $> asParser smalltalkToken!
  // !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
  // pragmas
  // ^ pragma star! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 5/3/2010 21:39'!
  // primary
  // ^ literal / variable / block / parens / array! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 6/29/2010 14:30'!
  // return
  // ^ $^ asParser smalltalkToken , expression! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 8/14/2011 11:13'!
  // sequence
  // ^ temporaries , periodToken star , statements! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 6/29/2010 14:30'!
  // statements
  // ^ (expression wrapped , (($. asParser smalltalkToken plus , statements ==>
  // [ :nodes | nodes first , nodes last ])
  // / $. asParser smalltalkToken star)
  // ==> [ :nodes | (Array with: nodes first) , (nodes last) ])
  // / (return , $. asParser smalltalkToken star
  // ==> [ :nodes | (Array with: nodes first) , (nodes last) ])
  // / ($. asParser smalltalkToken star)! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 6/29/2010 14:30'!
  // temporaries
  // ^ ($| asParser smalltalkToken , variable star , $| asParser smalltalkToken)
  // optional ==> [ :nodes | nodes ifNil: [ #() ] ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
  // variable
  // ^ identifierToken! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-blocks' stamp: 'lr 6/29/2010
  // 14:30'!
  // block
  // ^ $[ asParser smalltalkToken , blockBody , $] asParser smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-blocks' stamp: 'lr 6/29/2010
  // 14:30'!
  // blockArgument
  // ^ $: asParser smalltalkToken , variable! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-blocks' stamp: 'lr 2/4/2010
  // 13:21'!
  // blockArguments
  // ^ blockArgumentsWith / blockArgumentsWithout! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-blocks' stamp: 'lr 6/29/2010
  // 14:30'!
  // blockArgumentsWith
  // ^ blockArgument plus , ($| asParser smalltalkToken / ($] asParser
  // smalltalkToken and ==> [ :node | nil ]))! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-blocks' stamp: 'lr 2/4/2010
  // 11:44'!
  // blockArgumentsWithout
  // ^ nil asParser ==> [ :nodes | Array with: #() with: nil ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-blocks' stamp: 'lr 2/4/2010
  // 11:39'!
  // blockBody
  // ^ blockArguments , sequence! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'TestRunner
  // 10/23/2009 17:39'!
  // arrayItem
  // ^ literal / symbolLiteralArray / arrayLiteralArray / byteLiteralArray! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 6/29/2010
  // 14:30'!
  // arrayLiteral
  // ^ '#(' asParser smalltalkToken , arrayItem star , $) asParser
  // smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 6/29/2010
  // 14:30'!
  // arrayLiteralArray
  // ^ $( asParser smalltalkToken , arrayItem star , $) asParser smalltalkToken!
  // !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 6/29/2010
  // 14:30'!
  // byteLiteral
  // ^ '#[' asParser smalltalkToken , numberLiteral star , $] asParser
  // smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 6/29/2010
  // 14:30'!
  // byteLiteralArray
  // ^ $[ asParser smalltalkToken , numberLiteral star , $] asParser
  // smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 9/27/2009
  // 12:42'!
  // charLiteral
  // ^ charToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 9/27/2009
  // 12:42'!
  // falseLiteral
  // ^ falseToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 9/27/2009
  // 12:42'!
  // nilLiteral
  // ^ nilToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 9/27/2009
  // 12:42'!
  // numberLiteral
  // ^ numberToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 9/27/2009
  // 12:42'!
  // stringLiteral
  // ^ stringToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 8/14/2011
  // 12:27'!
  // symbolLiteral
  // "This is totally fucked up: The Pharo compiler allows multiple #, arbitrary spaces between the # and the symbol, as well as comments inbetween. And yes, it is used."
  //
  // ^ $# asParser smalltalkToken plus , symbol smalltalkToken ==> [ :tokens |
  // tokens first copyWith: tokens last ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 6/29/2010
  // 14:30'!
  // symbolLiteralArray
  // ^ symbol smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-literals' stamp: 'lr 9/27/2009
  // 12:42'!
  // trueLiteral
  // ^ trueToken! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 9/27/2009
  // 12:42'!
  // binaryExpression
  // ^ unaryExpression , binaryMessage star! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 12/6/2009
  // 10:38'!
  // binaryMessage
  // ^ (binaryToken , unaryExpression) ==> [ :nodes |
  // Array
  // with: (Array with: nodes first)
  // with: (Array with: nodes second) ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 9/27/2009
  // 12:42'!
  // cascadeExpression
  // ^ keywordExpression , cascadeMessage star! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 6/29/2010
  // 14:30'!
  // cascadeMessage
  // ^ $; asParser smalltalkToken , message! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 9/27/2009
  // 12:42'!
  // keywordExpression
  // ^ binaryExpression , keywordMessage optional! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 12/6/2009
  // 10:38'!
  // keywordMessage
  // ^ (keywordToken , binaryExpression) plus ==> [ :nodes |
  // Array
  // with: (nodes collect: [ :each | each first ])
  // with: (nodes collect: [ :each | each second ]) ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 9/27/2009
  // 12:42'!
  // unaryExpression
  // ^ primary , unaryMessage star! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-messages' stamp: 'lr 12/6/2009
  // 10:39'!
  // unaryMessage
  // ^ unaryToken ==> [ :node |
  // Array
  // with: (Array with: node)
  // with: Array new ]! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-methods' stamp: 'lr 12/6/2009
  // 10:38'!
  // binaryMethod
  // ^ (binaryToken , variable) ==> [ :nodes |
  // Array
  // with: (Array with: nodes first)
  // with: (Array with: nodes second) ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-methods' stamp: 'lr 12/6/2009
  // 10:38'!
  // keywordMethod
  // ^ (keywordToken , variable) plus ==> [ :nodes |
  // Array
  // with: (nodes collect: [ :each | each first ])
  // with: (nodes collect: [ :each | each second ]) ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-methods' stamp: 'lr 12/6/2009
  // 10:39'!
  // unaryMethod
  // ^ identifierToken ==> [ :node |
  // Array
  // with: (Array with: node)
  // with: Array new ]! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-pragmas' stamp: 'TestRunner
  // 10/23/2009 17:34'!
  // binaryPragma
  // ^ (binaryToken , arrayItem) ==> [ :nodes |
  // Array
  // with: (Array with: nodes first)
  // with: (Array with: nodes second) ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-pragmas' stamp: 'TestRunner
  // 10/23/2009 17:35'!
  // keywordPragma
  // ^ (keywordToken , arrayItem) plus ==> [ :nodes |
  // Array
  // with: (nodes collect: [ :each | each first ])
  // with: (nodes collect: [ :each | each second ]) ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-pragmas' stamp: 'lr 9/27/2009
  // 12:42'!
  // pragmaMessage
  // ^ keywordPragma / unaryPragma / binaryPragma! !
  //
  // !PPSmalltalkGrammar methodsFor: 'grammar-pragmas' stamp: 'TestRunner
  // 10/23/2009 16:35'!
  // unaryPragma
  // ^ identifierToken ==> [ :node |
  // Array
  // with: (Array with: node)
  // with: (Array new) ]! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'parsing' stamp: 'lr 2/19/2010 08:00'!
  // parseExpression: aString
  // ^ self parseExpression: aString onError: [ :msg :pos | self error: msg ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'parsing' stamp: 'lr 2/19/2010 08:04'!
  // parseExpression: aString onError: aBlock
  // ^ startExpression parse: aString onError: aBlock! !
  //
  // !PPSmalltalkGrammar methodsFor: 'parsing' stamp: 'lr 2/19/2010 08:01'!
  // parseMethod: aString
  // ^ self parseMethod: aString onError: [ :msg :pos | self error: msg ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'parsing' stamp: 'lr 2/19/2010 08:04'!
  // parseMethod: aString onError: aBlock
  // ^ startMethod parse: aString onError: aBlock! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 8/11/2011 23:29'!
  // binary
  // ^ (PPPredicateObjectParser anyOf: '!!%&*+,-/<=>?@\|~') plus! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 9/27/2009 12:42'!
  // char
  // ^ $$ asParser , #any asParser! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 7/11/2011 11:32'!
  // identifier
  // ^ self class allowUnderscoreAssignment
  // ifTrue: [ #letter asParser , #word asParser star ]
  // ifFalse: [
  // (PPPredicateObjectParser
  // on: [ :each | each isLetter or: [ each = $_ ] ]
  // message: 'letter expected') ,
  // (PPPredicateObjectParser
  // on: [ :each | each isAlphaNumeric or: [ each = $_ ] ]
  // message: 'letter or digit expected') star ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 9/27/2009 12:42'!
  // keyword
  // ^ identifier , $: asParser! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 9/27/2009 12:42'!
  // multiword
  // ^ keyword plus! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 8/13/2011 20:33'!
  // number
  // ^ ($- asParser optional , #digit asParser) and , [ :stream |
  // [ Number readFrom: stream ]
  // on: Error
  // do: [ :err | PPFailure message: err messageText at: stream position ] ]
  // asParser! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 8/13/2011 20:51'!
  // period
  // ^ $. asParser! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 1/8/2010 15:38'!
  // string
  // ^ $' asParser , ('''''' asParser / $' asParser negate) star , $' asParser!
  // !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 9/27/2009 12:42'!
  // symbol
  // ^ unary / binary / multiword / string! !
  //
  // !PPSmalltalkGrammar methodsFor: 'primitives' stamp: 'lr 9/27/2009 12:42'!
  // unary
  // ^ identifier , $: asParser not! !
  //
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 7/11/2011 11:27'!
  // assignmentToken
  // ^ self class allowUnderscoreAssignment
  // ifTrue: [ (':=' asParser / '_' asParser) smalltalkToken ]
  // ifFalse: [ ':=' asParser smalltalkToken ]! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // binaryToken
  // ^ binary smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // charToken
  // ^ char smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // falseToken
  // ^ ('false' asParser , #word asParser not) smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // identifierToken
  // ^ identifier smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // keywordToken
  // ^ keyword smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // nilToken
  // ^ ('nil' asParser , #word asParser not) smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // numberToken
  // ^ number smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 8/13/2011 20:52'!
  // periodToken
  // ^ period smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // stringToken
  // ^ string smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // trueToken
  // ^ ('true' asParser , #word asParser not) smalltalkToken! !
  //
  // !PPSmalltalkGrammar methodsFor: 'token' stamp: 'lr 6/29/2010 14:30'!
  // unaryToken
  // ^ unary smalltalkToken! !
  //
  // "-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- "!
  //
  // PPSmalltalkGrammar class
  // instanceVariableNames: ''!
  //
  // !PPSmalltalkGrammar class methodsFor: 'accessing' stamp: 'lr 2/19/2010
  // 08:08'!
  // parseExpression: aString
  // ^ self new parseExpression: aString! !
  //
  // !PPSmalltalkGrammar class methodsFor: 'accessing' stamp: 'lr 2/19/2010
  // 08:09'!
  // parseExpression: aString onError: aBlock
  // ^ self new parseExpression: aString onError: aBlock! !
  //
  // !PPSmalltalkGrammar class methodsFor: 'accessing' stamp: 'lr 2/19/2010
  // 08:09'!
  // parseMethod: aString
  // ^ self new parseMethod: aString! !
  //
  // !PPSmalltalkGrammar class methodsFor: 'accessing' stamp: 'lr 2/19/2010
  // 08:09'!
  // parseMethod: aString onError: aBlock
  // ^ self new parseMethod: aString onError: aBlock! !
  //
  //
  // !PPSmalltalkGrammar class methodsFor: 'testing' stamp: 'lr 7/11/2011
  // 11:27'!
  // allowUnderscoreAssignment
  // ^ Scanner allowUnderscoreAsAssignment! !

}
