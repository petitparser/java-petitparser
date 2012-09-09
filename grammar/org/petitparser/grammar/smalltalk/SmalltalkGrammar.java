package org.petitparser.grammar.smalltalk;

import static org.petitparser.Chars.any;
import static org.petitparser.Chars.anyOf;
import static org.petitparser.Chars.character;
import static org.petitparser.Chars.digit;
import static org.petitparser.Chars.letter;
import static org.petitparser.Chars.word;
import static org.petitparser.Parsers.epsilon;
import static org.petitparser.Parsers.string;

import java.util.List;

import org.petitparser.parser.CompositeParser;
import org.petitparser.parser.Parser;
import org.petitparser.utils.Functions;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Smalltalk grammar definition.
 *
 * @author Lukas Renggli (renggli@gmail.com)
 */
public class SmalltalkGrammar extends CompositeParser {

  public enum Kind {
    METHOD, EXPRESSION
  }

  private final Kind kind;

  public SmalltalkGrammar(Kind kind) {
    this.kind = kind;
  }

  private Parser token(Parser parser) {
    return parser.trim();
  }

  @Override
  protected void initialize() {
    def("start", ref(Kind.METHOD.equals(kind)
        ? "startMethod" : "startExpression"));
    def("startMethod", ref("sequence").end());
    def("startExpression", ref("method").end());
    initializeBasic();
    initializeBlock();
    initializeLiteral();
    initializeMessage();
    initializeMethod();
    initializePragma();
    initializePrimitive();
    initializeToken();
  }

  private void initializeBasic() {
    def("array", token(character('{'))
        .seq(ref("expression").delimitedBy(ref("periodToken")))
        .seq(token(character('}'))));
    def("assignment", ref("variable")
        .seq(ref("assignmentToken")));
    def("expression", ref("assignment").star()
        .seq(ref("cascadeExpression")));
    def("literal", ref("numberLiteral")
        .or(ref("stringLiteral"))
        .or(ref("charLiteral"))
        .or(ref("arrayLiteral"))
        .or(ref("byteLiteral"))
        .or(ref("symbolLiteral"))
        .or(ref("nilLiteral"))
        .or(ref("trueLiteral"))
        .or(ref("trueLiteral")));
    def("message", ref("keywordMessage")
        .or(ref("binaryMessage"))
        .or(ref("unaryMessage")));
    def("method", ref("methodDeclaration")
        .seq(ref("methodSequence")));
    def("methodDeclaration", ref("keywordMethod")
        .or(ref("unaryMethod"))
        .or(ref("binaryMethod")));
    def("methodSequence", ref("periodToken").star()
        .seq(ref("pragmas"))
        .seq(ref("periodToken").star())
        .seq(ref("temporaries"))
        .seq(ref("periodToken").star())
        .seq(ref("pragmas"))
        .seq(ref("periodToken").star())
        .seq(ref("statements")));
    def("parens", token(character('('))
        .seq(ref("expression"))
        .seq(token(character(')'))));
    def("pragma", token(character('('))
        .seq(ref("pragmaMessage"))
        .seq(token(character(')'))));
    def("pragmas", ref("pragma").star());
    def("primary", ref("literal")
        .or(ref("variable"))
        .or(ref("block"))
        .or(ref("parens"))
        .or(ref("array")));
    def("return", token(character('^'))
        .seq(ref("expression")));
    def("sequence", ref("temporaries")
        .seq(ref("periodToken").star())
        .seq(ref("statements")));
    def("statements", null);
  // !PPSmalltalkGrammar methodsFor: 'grammar' stamp: 'lr 6/29/2010 14:30'!
  // statements
  // ^ (expression wrapped , (($. asParser smalltalkToken plus , statements ==>
  // [ :nodes | nodes first , nodes last ])
  // / $. asParser smalltalkToken star)
  // ==> [ :nodes | (Array with: nodes first) , (nodes last) ])
  // / (return , $. asParser smalltalkToken star
  // ==> [ :nodes | (Array with: nodes first) , (nodes last) ])
  // / ($. asParser smalltalkToken star)! !
    def("temporaries", token(character('|'))
        .seq(ref("variable"))
        .seq(token(character('|')))
        .optional());
    def("variable", ref("identifierToken"));
  }

  private void initializeBlock() {
    def("block", token(character('['))
        .seq(ref("blockBody"))
        .seq(token(character(']'))));
    def("blockArgument", token(character(':'))
        .seq(ref("variable")));
    def("blockArguments", ref("blockArgumentsWith")
        .or(ref("blockArgumentsWithout")));
    def("blockArgumentsWith", ref("blockArgument").plus()
        .seq(token(character('|'))
            .or(token(character(']'))
                .and()
                .map(Functions.constant(null)))));
    def("blockArgumentsWithout", epsilon().map(Functions.constant(
        Lists.newArrayList(Lists.newArrayList(), null))));
    def("blockBody", ref("blockArguments")
        .seq(ref("sequence")));
  }

  private void initializeLiteral() {
    def("arrayItem", ref("literal")
        .or(ref("symbolLiteralArray"))
        .or(ref("arrayLiteralArray"))
        .or(ref("byteLiteralArray")));
    def("arrayLiteral", token(string("#("))
        .seq(ref("arrayItem").star())
        .seq(token(character(')'))));
    def("arrayLiteralArray", token(character('('))
        .seq(ref("arrayItem").star())
        .seq(token(character(')'))));
    def("byteLiteral", token(string("#["))
        .seq(ref("numberLiteral").star())
        .seq(token(character(']'))));
    def("byteLiteralArray", token(character('['))
        .seq(ref("numberLiteral").star())
        .seq(token(character(']'))));
    def("charLiteral", ref("charToken"));
    def("falseLiteral", ref("falseToken"));
    def("trueLiteral", ref("trueToken"));
    def("nilLiteral", ref("nilToken"));
    def("numberLiteral", ref("numberToken"));
    def("stringLiteral", ref("stringToken"));
    def("symbolLiteral", token(character('#')).plus()
        .seq(ref("symbolToken")));
  }

  private void initializeMessage() {
    def("cascadeExpression", ref("keywordExpression")
        .seq(ref("cascadeMessage").star()));
    def("cascadeMessage", token(character(';'))
        .seq(ref("message")));
    def("unaryExpression", ref("primary")
        .seq(ref("unaryMessage").star()));
    def("unaryMessage", ref("unaryToken")
        .map(new Function<Object, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(Object input) {
            return Lists.newArrayList(
                Lists.newArrayList(input),
                Lists.newArrayList());
          }
        }));
    def("binaryExpression", ref("unaryExpression")
        .seq(ref("binaryMessage").star()));
    def("binaryMessage", ref("binaryToken")
        .seq(ref("unaryExpression"))
        .map(new Function<List<Object>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<Object> input) {
            return Lists.newArrayList(
                Lists.newArrayList(input.get(0)),
                Lists.newArrayList(input.get(1)));
          }
        }));
    def("keywordExpression", ref("binaryExpression")
        .seq(ref("keywordMessage").optional()));
    def("keywordMessage", ref("keywordToken")
        .seq(ref("binaryExpression"))
        .plus()
        .map(new Function<List<List<Object>>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<List<Object>> input) {
            return Lists.newArrayList(
                Lists.transform(input, Functions.nthOfList(0)),
                Lists.transform(input, Functions.nthOfList(1)));
          }
        }));
  }

  private void initializeMethod() {
    def("unaryMethod", ref("identifierToken")
        .map(new Function<Object, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(Object input) {
            return Lists.newArrayList(
                Lists.newArrayList(input),
                Lists.newArrayList());
          }
        }));
    def("binaryMethod", ref("binaryToken")
        .seq(ref("variable"))
        .map(new Function<List<Object>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<Object> input) {
            return Lists.newArrayList(
                Lists.newArrayList(input.get(0)),
                Lists.newArrayList(input.get(1)));
          }
        }));
    def("keywordMethod", ref("keywordToken")
        .seq(ref("variable"))
        .plus()
        .map(new Function<List<List<Object>>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<List<Object>> input) {
            return Lists.newArrayList(
                Lists.transform(input, Functions.nthOfList(0)),
                Lists.transform(input, Functions.nthOfList(1)));
          }
        }));
  }

  private void initializePragma() {
    def("pragmaMessage", ref("keywordPragma")
        .or(ref("unaryPragma"))
        .or(ref("binaryPragma")));
    def("unaryPragma", ref("identifierToken")
        .map(new Function<Object, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(Object input) {
            return Lists.newArrayList(
                Lists.newArrayList(input),
                Lists.newArrayList());
          }
        }));
    def("binaryPragma", ref("binaryToken")
        .seq(ref("arrayItem"))
        .map(new Function<List<Object>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<Object> input) {
            return Lists.newArrayList(
                Lists.newArrayList(input.get(0)),
                Lists.newArrayList(input.get(1)));
          }
        }));
    def("keywordPragma", ref("keywordToken")
        .seq(ref("arrayItem"))
        .plus()
        .map(new Function<List<List<Object>>, Object>() {
          @Override
          @SuppressWarnings("unchecked")
          public Object apply(List<List<Object>> input) {
            return Lists.newArrayList(
                Lists.transform(input, Functions.nthOfList(0)),
                Lists.transform(input, Functions.nthOfList(1)));
          }
        }));
  }

  private void initializePrimitive() {
    def("binary", anyOf("!%&*+,-/<=>?@\\|~").plus());
    def("char", character('$').seq(any()));
    def("identifier", letter().or(word().star()));
    def("keyword", ref("identifier").seq(character(':')));
    def("multiword", ref("keyword").plus());
    def("number", character('-').optional()
        .seq(character('0').or(digit().plus()))
        .seq(character('.').seq(digit().plus()).optional())
        .seq(anyOf("eE").seq(anyOf("-+").optional()).seq(digit().plus()).optional()));
    def("period", character('.'));
    def("string", character('\'')
        .seq(string("''")
            .or(character('\'').negate())
            .star())
        .seq(character('\'')));
    def("symbol", ref("unary")
        .or(ref("binary"))
        .or(ref("multiword"))
        .or(ref("string")));
    def("unary", ref("identifier")
        .seq(character(':').not("identifier expected")));
  }

  private void initializeToken() {
    def("assignmentToken", token(string(":=")));
    def("binaryToken", token(ref("binary")));
    def("charToken", token(ref("char")));
    def("falseToken", token(string("false").seq(word().not("false expected"))));
    def("identifierToken", token(ref("identifier")));
    def("keywordToken", token(ref("keyword")));
    def("nilToken", token(string("nil").seq(word().not("nil expected"))));
    def("numberToken", token(ref("number")));
    def("periodToken", token(ref("period")));
    def("stringToken", token(ref("string")));
    def("trueToken", token(string("true").seq(word().not("true expected"))));
    def("unaryToken", token(ref("unary")));
  }

}
