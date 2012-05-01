/**
 *
 */
package org.petitparser.grammar.smalltalk;


/**
 * Smalltalk parser definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SmalltalkParser extends SmalltalkGrammar {

  public SmalltalkParser(Kind kind) {
    super(kind);
    // TODO Auto-generated constructor stub
  }


// 'From Pharo1.3 of 16 June 2011 [Latest update: #13327] on 30 April 2012 at 9:45:04 am'!
// PPSmalltalkGrammar subclass: #PPSmalltalkParser
//   instanceVariableNames: ''
//   classVariableNames: ''
//   poolDictionaries: ''
//   category: 'PetitSmalltalk-Core'!
// !PPSmalltalkParser commentStamp: 'lr 11/29/2009 09:58' prior: 0!
// Enhances the Smalltalk grammar with production actions to build parse-tree nodes of the refactoring browser.!
//
//
// !PPSmalltalkParser methodsFor: 'accessing' stamp: 'lr 2/19/2010 08:07'!
// startExpression
//   "Make the sequence node has a method node as its parent and that the source is set."
//
//   ^ ([ :stream | stream collection ] asParser and , super startExpression) map: [ :source :node |
//     (RBMethodNode selector: #doIt body: node)
//       source: source.
//     (node statements size = 1 and: [ node temporaries isEmpty ])
//       ifTrue: [ node statements first ]
//       ifFalse: [ node ] ]! !
//
// !PPSmalltalkParser methodsFor: 'accessing' stamp: 'lr 2/19/2010 08:20'!
// startMethod
//   "Make sure the method node has the source code properly set."
//
//   ^ ([ :stream | stream collection ] asParser and , super startMethod)
//     map: [ :source :node | node source: source ]! !
//
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 8/14/2011 11:51'!
// array
//   ^ super array map: [ :openNode :statementNodes :closeNode |
//     (self buildArray: statementNodes)
//       left: openNode start;
//       right: closeNode start;
//       yourself ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 12/6/2009 10:40'!
// expression
//   ^ super expression map: [ :variableNodes :expressionNodes | self build: expressionNodes assignment: variableNodes ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 8/13/2011 21:09'!
// method
//   ^ super method map: [ :methodNode :bodyNode |
//     methodNode pragmas: bodyNode first.
//     methodNode body: bodyNode second.
//     self buildMethod: methodNode ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 3/1/2010 10:27'!
// methodDeclaration
//   ^ super methodDeclaration ==> [ :nodes |
//     RBMethodNode
//       selectorParts: nodes first
//       arguments: nodes second ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 8/14/2011 11:24'!
// methodSequence
//   ^ super methodSequence map: [ :periodNodes1 :pragmaNodes1 :periodNodes2 :tempNodes :periodNodes3 :pragmaNodes2 :periodNodes4 :statementNodes |
//     Array
//       with: pragmaNodes1 , pragmaNodes2
//       with: (self build: tempNodes sequence: periodNodes1 , periodNodes2 , periodNodes3 , periodNodes4 , statementNodes) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 12/6/2009 10:42'!
// parens
//   ^ super parens map: [ :openToken :expressionNode :closeToken | expressionNode addParenthesis: (openToken start to: closeToken start) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 3/1/2010 10:50'!
// pragma
//   ^ super pragma ==> [ :nodes |
//     (RBPragmaNode selectorParts: nodes second first arguments: nodes second second)
//       addComments: nodes first comments;
//       addComments: nodes last comments;
//       left: nodes first start;
//       right: nodes last start;
//       yourself ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 12/6/2009 10:42'!
// return
//   ^ super return map: [ :token :expressionNode | RBReturnNode return: token start value: expressionNode ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 8/14/2011 11:14'!
// sequence
//   ^ super sequence map: [ :tempNodes :periodNodes :statementNodes | self build: tempNodes sequence: periodNodes , statementNodes ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar' stamp: 'lr 9/27/2009 12:42'!
// variable
//   ^ super variable ==> [ :token | RBVariableNode identifierToken: token ]! !
//
//
// !PPSmalltalkParser methodsFor: 'grammar-blocks' stamp: 'lr 12/6/2009 10:37'!
// block
//   ^ super block map: [ :leftToken :blockNode :rightToken | blockNode left: leftToken start; right: rightToken start ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-blocks' stamp: 'lr 9/27/2009 12:42'!
// blockArgument
//   ^ super blockArgument ==> #second! !
//
// !PPSmalltalkParser methodsFor: 'grammar-blocks' stamp: 'lr 2/4/2010 11:42'!
// blockBody
//   ^ super blockBody ==> [ :nodes |
//     | result |
//     result := RBBlockNode arguments: nodes first first body: nodes last.
//     nodes first last isNil
//       ifFalse: [ result bar: nodes first last start ].
//     result ]! !
//
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:41'!
// arrayLiteral
//   ^ super arrayLiteral ==> [ :nodes | RBLiteralArrayNode startPosition: nodes first start contents: nodes second stopPosition: nodes last start isByteArray: false ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:41'!
// arrayLiteralArray
//   ^ super arrayLiteralArray ==> [ :nodes | RBLiteralArrayNode startPosition: nodes first start contents: nodes second stopPosition: nodes last start isByteArray: false ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:41'!
// byteLiteral
//   ^ super byteLiteral ==> [ :nodes | RBLiteralArrayNode startPosition: nodes first start contents: nodes second stopPosition: nodes last start isByteArray: true ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:41'!
// byteLiteralArray
//   ^ super byteLiteralArray ==> [ :nodes | RBLiteralArrayNode startPosition: nodes first start contents: nodes second stopPosition: nodes last start isByteArray: true ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:28'!
// charLiteral
//   ^ super charLiteral ==> [ :token | RBLiteralValueNode literalToken: (RBLiteralToken value: token value second start: token start stop: token stop) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:29'!
// falseLiteral
//   ^ super falseLiteral ==> [ :token | RBLiteralValueNode literalToken: (RBLiteralToken value: false start: token start stop: token stop) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:29'!
// nilLiteral
//   ^ super nilLiteral ==> [ :token | RBLiteralValueNode literalToken: (RBLiteralToken value: nil start: token start stop: token stop) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 13:33'!
// numberLiteral
//   ^ super numberLiteral ==> [ :token | RBLiteralValueNode literalToken: (RBNumberLiteralToken value: (Number readFrom: token value) start: token start stop: token stop source: token value) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:30'!
// stringLiteral
//   ^ super stringLiteral ==> [ :token | RBLiteralValueNode literalToken: (RBLiteralToken value: (self buildString: token value) start: token start stop: token stop) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'lr 8/14/2011 12:24'!
// symbolLiteral
//   ^ super symbolLiteral ==> [ :tokens | RBLiteralValueNode literalToken: (RBLiteralToken value: (self buildString: tokens last value) asSymbol start: tokens first start stop: tokens last stop) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:45'!
// symbolLiteralArray
//   ^ super symbolLiteralArray ==> [ :token | RBLiteralValueNode literalToken: (RBLiteralToken value: (self buildString: token value) asSymbol start: token start stop: token stop) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-literals' stamp: 'TestRunner 11/5/2009 11:29'!
// trueLiteral
//   ^ super trueLiteral ==> [ :token | RBLiteralValueNode literalToken: (RBLiteralToken value: true start: token start stop: token stop) ]! !
//
//
// !PPSmalltalkParser methodsFor: 'grammar-messages' stamp: 'lr 12/6/2009 10:39'!
// binaryExpression
//   ^ super binaryExpression map: [ :receiverNode :messageNodes | self build: receiverNode messages: messageNodes ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-messages' stamp: 'lr 12/6/2009 10:40'!
// cascadeExpression
//   ^ super cascadeExpression map: [ :receiverNode :messageNodes | self build: receiverNode cascade: messageNodes ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-messages' stamp: 'lr 12/6/2009 10:41'!
// keywordExpression
//   ^ super keywordExpression map: [ :receiveNode :messageNode | self build: receiveNode messages: (Array with: messageNode) ]! !
//
// !PPSmalltalkParser methodsFor: 'grammar-messages' stamp: 'lr 12/6/2009 10:43'!
// unaryExpression
//   ^ super unaryExpression map: [ :receiverNode :messageNodes | self build: receiverNode messages: messageNodes ]! !
//
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'lr 8/14/2011 12:04'!
// addStatements: aCollection into: aNode
//   aCollection isNil
//     ifTrue: [ ^ aNode ].
//   aCollection do: [ :each |
//     each class == PPSmalltalkToken
//       ifFalse: [ aNode addNode:  each ]
//       ifTrue: [
//         aNode statements isEmpty
//           ifTrue: [ aNode addComments: each comments ]
//           ifFalse: [ aNode statements last addComments: each comments ].
//         aNode periods: (aNode periods asOrderedCollection
//           addLast: each start;
//           yourself) ] ].
//   ^ aNode! !
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'TestRunner 11/5/2009 10:36'!
// build: aNode assignment: anArray
//   ^ anArray isEmpty
//     ifTrue: [ aNode ]
//     ifFalse: [
//       anArray reverse
//         inject: aNode
//         into: [ :result :each |
//           RBAssignmentNode
//             variable: each first
//             value: result
//             position: each second start ] ]! !
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'lr 9/11/2010 10:11'!
// build: aNode cascade: anArray
//   | messages semicolons |
//   ^ (anArray isNil or: [ anArray isEmpty ])
//     ifTrue: [ aNode ]
//     ifFalse: [
//       messages := OrderedCollection new: anArray size + 1.
//       messages addLast: aNode.
//       semicolons := OrderedCollection new.
//       anArray do: [ :each |
//         messages addLast: (self
//           build: aNode receiver
//           messages: (Array with: each second)).
//         semicolons addLast: each first start ].
//       RBCascadeNode messages: messages semicolons: semicolons ]! !
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'lr 11/5/2009 10:44'!
// build: aNode messages: anArray
//   ^ (anArray isNil or: [ anArray isEmpty ])
//     ifTrue: [ aNode ]
//     ifFalse: [
//       anArray
//         inject: aNode
//         into: [ :rec :msg |
//           msg isNil
//             ifTrue: [ rec ]
//             ifFalse: [
//               RBMessageNode
//                 receiver: rec
//                 selectorParts: msg first
//                 arguments: msg second ] ] ]! !
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'lr 8/14/2011 12:03'!
// build: aTempCollection sequence: aStatementCollection
//   | result |
//   result := self
//     addStatements: aStatementCollection
//     into: RBSequenceNode new.
//   aTempCollection isEmpty ifFalse: [
//     result
//       leftBar: aTempCollection first start
//       temporaries: aTempCollection second
//       rightBar: aTempCollection last start ].
//   ^ result! !
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'lr 8/14/2011 12:00'!
// buildArray: aStatementCollection
//   ^ self addStatements: aStatementCollection into: RBArrayNode new! !
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'lr 3/1/2010 10:51'!
// buildMethod: aMethodNode
//   aMethodNode selectorParts
//     do: [ :each | aMethodNode addComments: each comments ].
//   aMethodNode arguments
//     do: [ :each | aMethodNode addComments: each token comments ].
//   aMethodNode pragmas do: [ :pragma |
//     aMethodNode addComments: pragma comments.
//     pragma selectorParts
//       do: [ :each | aMethodNode addComments: each comments ].
//     pragma arguments do: [ :each |
//       each isLiteralArray
//         ifFalse: [ aMethodNode addComments: each token comments ] ].
//     pragma comments: nil ].
//   ^ aMethodNode! !
//
// !PPSmalltalkParser methodsFor: 'private' stamp: 'lr 8/14/2011 12:25'!
// buildString: aString
//   (aString isEmpty or: [ aString first ~= $' or: [ aString last ~= $' ] ])
//     ifTrue: [ ^ aString ].
//   ^ (aString
//     copyFrom: 2
//     to: aString size - 1)
//     copyReplaceAll: ''''''
//     with: ''''! !


}
