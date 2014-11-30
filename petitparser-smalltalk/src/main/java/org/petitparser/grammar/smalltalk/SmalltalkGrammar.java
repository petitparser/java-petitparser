package org.petitparser.grammar.smalltalk;

import org.petitparser.parser.Parser;
import org.petitparser.parser.primitive.EpsilonParser;
import org.petitparser.tools.CompositeParser;

import static org.petitparser.parser.characters.CharacterParser.any;
import static org.petitparser.parser.characters.CharacterParser.digit;
import static org.petitparser.parser.characters.CharacterParser.of;
import static org.petitparser.parser.characters.CharacterParser.pattern;
import static org.petitparser.parser.characters.CharacterParser.whitespace;
import static org.petitparser.parser.characters.CharacterParser.word;
import static org.petitparser.parser.primitive.StringParser.of;

/**
 * Smalltalk grammar definition.
 */
public class SmalltalkGrammar extends CompositeParser {

  @Override
  protected void initialize() {
    other();
    number();
    smalltalk();
  }

  private void other() {
    // the original implementation uses a handwritten parser to
    // efficiently consume whitespace and comments
    def("whitespace", whitespace()
        .or(ref("comment")));
    def("comment", of('"')
        .seq(any().starLazy(of('"')))
        .seq(of('"')));
  }

  private void number() {
    // the original implementation uses the hand written number
    // parser of the system, this is the spec of the ANSI standard
    def("number", of('-').optional()
        .seq(ref("positiveNumber")));
    def("positiveNumber", ref("scaledDecimal")
        .or(ref("float"))
        .or(ref("integer")));

    def("integer", ref("radixInteger")
        .or(ref("decimalInteger")));
    def("decimalInteger", ref("digits"));
    def("digits", digit().plus());
    def("radixInteger", ref("radixSpecifier")
        .seq(of('r'))
        .seq(ref("radixDigits")));
    def("radixSpecifier", ref("digits"));
    def("radixDigits", pattern("0-9A-Z").plus());

    def("float", ref("mantissa")
        .seq(ref("exponentLetter")
            .seq(ref("exponent"))
            .optional()));
    def("mantissa", ref("digits")
        .seq(of('.'))
        .seq(ref("digits")));
    def("exponent", of('-')
        .seq(ref("decimalInteger")));
    def("exponentLetter", pattern("edq"));

    def("scaledDecimal", ref("scaledMantissa")
        .seq(of('s'))
        .seq(ref("fractionalDigits").optional()));
    def("scaledMantissa", ref("decimalInteger")
        .or(ref("mantissa")));
    def("fractionalDigits", ref("decimalInteger"));
  }

  private Parser token(Object input) {
    Parser parser;
    if (input instanceof Parser) {
      parser = (Parser) input;
    } else if (input instanceof Character) {
      parser = of((Character) input);
    } else if (input instanceof String) {
      parser = of((String) input);
    } else {
      throw new IllegalStateException("Object not parsable: " + input);
    }
    return parser.token().trim(ref("whitespace"));
  }

  void smalltalk() {
    def("array", token("{")
        .seq(ref("expression").separatedBy(ref("periodToken"))
            .seq(ref("periodToken").optional()).optional())
            .seq(token("}")));
    def("arrayItem", ref("literal")
        .or(ref("symbolLiteralArray"))
        .or(ref("arrayLiteralArray"))
        .or(ref("byteLiteralArray")));
    def("arrayLiteral", token("#(")
        .seq(ref("arrayItem").star())
        .seq(token(")")));
    def("arrayLiteralArray", token("(")
        .seq(ref("arrayItem").star())
        .seq(token(")")));
    def("assignment", ref("variable")
        .seq(ref("assignmentToken")));
    def("assignmentToken", token(":="));
    def("binary", pattern("!%&*+,-/<=>?@\\|~").plus());
    def("binaryExpression", ref("unaryExpression")
        .seq(ref("binaryMessage").star()));
    def("binaryMessage", ref("binaryToken")
        .seq(ref("unaryExpression")));
    def("binaryMethod", ref("binaryToken")
        .seq(ref("variable")));
    def("binaryPragma", ref("binaryToken")
        .seq(ref("arrayItem")));
    def("binaryToken", token(ref("binary")));
    def("block", token("[")
        .seq(ref("blockBody"))
        .seq(token("]")));
    def("blockArgument", token(":")
        .seq(ref("variable")));
    def("blockArguments", ref("blockArgumentsWith")
        .or(ref("blockArgumentsWithout")));
    def("blockArgumentsWith", ref("blockArgument").plus()
        .seq(token("|").or(token("]").and())));
    def("blockArgumentsWithout", new EpsilonParser());
    def("blockBody", ref("blockArguments")
        .seq(ref("sequence")));
    def("byteLiteral", token("#[")
        .seq(ref("numberLiteral").star())
        .seq(token("]")));
    def("byteLiteralArray", token("[")
        .seq(ref("numberLiteral").star())
        .seq(token("]")));
    def("cascadeExpression", ref("keywordExpression")
        .seq(ref("cascadeMessage").star()));
    def("cascadeMessage", token(";")
        .seq(ref("message")));
    def("char", of('$').seq(any()));
    def("charLiteral", ref("charToken"));
    def("charToken", token(ref("char")));
    def("expression", ref("assignment").star()
        .seq(ref("cascadeExpression")));
    def("falseLiteral", ref("falseToken"));
    def("falseToken", token("false")
        .seq(word().not()));
    def("identifier", pattern("a-zA-Z_")
        .seq(pattern("a-zA-Z0-9_").star()));
    def("identifierToken", token(ref("identifier")));
    def("keyword", ref("identifier")
        .seq(of(':')));
    def("keywordExpression", ref("binaryExpression")
        .seq(ref("keywordMessage").optional()));
    def("keywordMessage", ref("keywordToken")
        .seq(ref("binaryExpression")).plus());
    def("keywordMethod", ref("keywordToken")
        .seq(ref("variable")).plus());
    def("keywordPragma", ref("keywordToken")
        .seq(ref("arrayItem")).plus());
    def("keywordToken", token(ref("keyword")));
    def("literal", ref("numberLiteral")
        .or(ref("stringLiteral"))
        .or(ref("charLiteral"))
        .or(ref("arrayLiteral"))
        .or(ref("byteLiteral"))
        .or(ref("symbolLiteral"))
        .or(ref("nilLiteral"))
        .or(ref("trueLiteral"))
        .or(ref("falseLiteral")));
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
    def("multiword", ref("keyword").plus());
    def("nilLiteral", ref("nilToken"));
    def("nilToken", token("nil")
        .seq(word().not()));
    def("numberLiteral", ref("numberToken"));
    def("numberToken", token(ref("number")));
    def("parens", token("(")
        .seq(ref("expression"))
        .seq(token(")")));
    def("period", of('.'));
    def("periodToken", token(ref("period")));
    def("pragma", token("<")
        .seq(ref("pragmaMessage"))
        .seq(token(">")));
    def("pragmaMessage", ref("keywordPragma")
        .or(ref("unaryPragma"))
        .or(ref("binaryPragma")));
    def("pragmas", ref("pragma").star());
    def("primary", ref("literal")
        .or(ref("variable"))
        .or(ref("block"))
        .or(ref("parens"))
        .or(ref("array")));
    def("return", token("^")
        .seq(ref("expression")));
    def("sequence", ref("temporaries")
        .seq(ref("periodToken").star())
        .seq(ref("statements")));
    def("start", ref("startMethod"));
    def("startMethod", ref("method").end());
    def("statements", ref("expression")
        .seq(ref("periodToken").plus().seq(ref("statements"))
            .or(ref("periodToken").star()))
            .or(ref("return").seq(ref("periodToken").star()))
            .or(ref("periodToken").star()));
    def("string", of('\'')
        .seq(of("''").or(pattern("^'")).star())
        .seq(of('\'')));
    def("stringLiteral", ref("stringToken"));
    def("stringToken", token(ref("string")));
    def("symbol", ref("unary")
        .or(ref("binary"))
        .or(ref("multiword"))
        .or(ref("string")));
    def("symbolLiteral", token("#").plus()
        .seq(token(ref("symbol"))));
    def("symbolLiteralArray", token(ref("symbol")));
    def("temporaries", token("|")
        .seq(ref("variable").star())
        .seq(token("|"))
        .optional());
    def("trueLiteral", ref("trueToken"));
    def("trueToken", token("true")
        .seq(word().not()));
    def("unary", ref("identifier")
        .seq(of(':').not()));
    def("unaryExpression", ref("primary")
        .seq(ref("unaryMessage").star()));
    def("unaryMessage", ref("unaryToken"));
    def("unaryMethod", ref("identifierToken"));
    def("unaryPragma", ref("identifierToken"));
    def("unaryToken", token(ref("unary")));
    def("variable", ref("identifierToken"));
  }

}
